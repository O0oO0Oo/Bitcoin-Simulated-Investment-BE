package com.cryptocurrency.investment.transaction.repository.redis;

import com.cryptocurrency.investment.config.JpaConfig;
import com.cryptocurrency.investment.config.RedisConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * redis 에 주문 데이터를 올릴때
 * List, Zset 둘 중 어느것을 선택할지를 위한 테스트
 */
@DataJpaTest
@Import({RedisConfig.class, JpaConfig.class, TransactionRedisRepository.class})
@ActiveProfiles("test")
public class TransactionRedisRepositoryPerformanceTest {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    static StopWatch zsetIncr = new StopWatch();
    static StopWatch zsetDesc = new StopWatch();
    static StopWatch list = new StopWatch();
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    final int addCount = 10_000;
    final int addRepeat = 10;

    @AfterEach
    void afterEach() {
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.flushAll();
        connection.close();

        if(0<zsetIncr.getTotalTimeMillis())
            System.out.println("증가: " + zsetIncr.getTotalTimeMillis()/(addRepeat+1));
        if(0<zsetDesc.getTotalTimeMillis())
            System.out.println("감소:" + zsetDesc.getTotalTimeMillis()/(addRepeat+1));
        if(0<list.getTotalTimeMillis())
            System.out.println("리스트:" + list.getTotalTimeMillis()/(addRepeat+1));
    }

    /**
     * 파이프 라인 없이 addCount 수 추가 addRepeat 반복
     *
     * 저장시 메모리의 차이가 있어
     * zset 이 ziplist 구조로 변경했을때 추가 속도가 느려짐
     * zset-max-listpack-entries 100000000
     * zset-max-listpack-value 100000000
     * 10_000 개 테스트 - 평균시간
     * zset : 8076(score 증가), 7216(score 감소)
     * list : 6324
     *
     * 처음부터 skiplist 구조 사용
     * zset-max-listpack-entries 0
     * zset-max-listpack-value 0
     * 10_000 개 테스트 - 평균시간
     * ZSet : 6547(score 증가), 6465(score 감소)
     * List : 위와 비슷
     *
     * list-max-listpack-size -2 -> -1 변경
     * list : 6865
     *
     * list-compress-depth 0 -> 1, 2, 3
     * list : 6374, 6228, 6369
     *
     * ZADD : O(log(N))
     * rpush : O(1)
     * 이여서 차이가 있을거라 예상했지만 큰 차이가 없음.
     */
    @Test
    @RepeatedTest(addRepeat)
    @DisplayName("ZSET, List 추가 성능 비교 - zset score 증가 "+ addCount +"개")
    void performance_comp_zset_incr_add() {
        // given
        RedisConnection connection = redisConnectionFactory.getConnection();

        // when
        // zset score 증가 삽입
        zsetIncr.start();
        IntStream.range(0, addCount)
                .forEach(i ->
                        connection.zSetCommands().zAdd(
                                "testzset".getBytes(),
                                i,
                                ("member:" + i).getBytes()
                        )
                );
        zsetIncr.stop();

        Long zsetL = connection.zSetCommands().zCard("testzset".getBytes());
        connection.close();

        // then
        Assertions.assertEquals(addCount,zsetL);
    }

    @Test
    @RepeatedTest(addRepeat)
    @DisplayName("ZSET, List 추가 성능 비교 - zset score 감소 "+ addCount +" 개")
    void performance_comp_zset_desc_add() {
        // given
        RedisConnection connection = redisConnectionFactory.getConnection();

        // zset score 감소 삽입
        zsetDesc.start();
        IntStream.range(0,addCount)
                .forEach(i ->
                        connection.zSetCommands().zAdd(
                                "testzset".getBytes(),
                                -i,
                                ("member:" + i).getBytes()
                        )
                );
        zsetDesc.stop();

        Long zsetL = connection.zSetCommands().zCard("testzset".getBytes());
        connection.close();

        // then
        Assertions.assertEquals(addCount,zsetL);
    }

    @Test
    @RepeatedTest(addRepeat)
    @DisplayName("ZSET, List 추가 성능 비교 - list "+ addCount +" 개")
    void performance_comp_list_add() {
        // given
        RedisConnection connection = redisConnectionFactory.getConnection();

        // list 삽입
        list.start();
        IntStream.range(0,addCount)
                .forEach(i ->
                        connection.listCommands().rPush(
                                "testlist".getBytes(),
                                ("member:" + i).getBytes()
                        )
                );
        list.stop();

        Long listL = connection.listCommands().lLen("testlist".getBytes());
        connection.close();

        // then
        Assertions.assertEquals(addCount,listL);
    }


    /**
     * 값이 매칭이 될때, 앞에서부터 데이터를 불러와서 처리하 후 삭제할때 사용
     * ZSET : 51
     *
     * list-compress-depth 0 -> 1, 2, 3
     * LIST : 1 -> 3, 3, 4
     */
    @Test
    @DisplayName("ZSET, List 삭제 성능 비교 - 1_000_000개 추가 후 100_000개 앞에서부터 index 삭제")
    void performance_comp_indexDelete() {
        // given
        int count = 1_000_000;
        StopWatch zset = new StopWatch();
        StopWatch list = new StopWatch();

        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.openPipeline();
        IntStream.range(0,count)
                .forEach(i ->
                        connection.zSetCommands().zAdd(
                                "testzset".getBytes(),
                                i,
                                ("member:" + i).getBytes()
                        )
                );
        connection.closePipeline();

        connection.openPipeline();
        IntStream.range(0,count)
                .forEach(i ->
                        connection.listCommands().lPush(
                                "testlist".getBytes(),
                                ("member:" + i).getBytes()
                        )
                );
        connection.closePipeline();

        // when
        zset.start();
        redisTemplate.opsForZSet().removeRange(
                "testzset",
                0,
                100_000
        );
        zset.stop();
        Long zsetL = connection.zSetCommands().zCard(
                "testzset".getBytes()
        );

        list.start();
        connection.listCommands().lTrim(
                "testlist".getBytes(),
                100_001,
                -1
        );
        list.stop();
        Long listL = connection.listCommands().lLen(
                "testlist".getBytes()
        );

        connection.close();

        System.out.println("zset 인덱스 삭제 : "+zset.getTotalTimeMillis());
        System.out.println("list 인덱스 삭제 : "+list.getTotalTimeMillis());

        // then
        // 남아있는 갯수가 같아야함
        Assertions.assertEquals(zsetL,listL);
    }

    /**
     * 유저가 주문을 취소할때 사용.
     * ZSET : 3
     *
     * list-compress-depth 0 -> 1, 2, 3
     * LIST : 31 -> 52, 53, 51
     *
     * ZSET 이 빠르지만, 너무 미미하다.
     */
    @Test
    @DisplayName("ZSET, List 삭제 성능 비교 - 1_000_000개 추가 후 100_000개 랜덤 값(Member, Value)을 찾아서 삭제")
    void performance_comp_randomDelete() {
        // given
        int count = 1_000_000;
        StopWatch zset = new StopWatch();
        StopWatch list = new StopWatch();

        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.openPipeline();
        IntStream.range(0,count)
                .forEach(i ->
                        connection.zSetCommands().zAdd(
                                "testzset".getBytes(),
                                i,
                                ("member:" + i).getBytes()
                        )
                );
        connection.closePipeline();

        connection.openPipeline();
        IntStream.range(0,count)
                .forEach(i ->
                        connection.listCommands().lPush(
                                "testlist".getBytes(),
                                ("member:" + i).getBytes()
                        )
                );
        connection.closePipeline();

        // when
        int bound = 100_000;
        int[] randoms = new int[bound];
        for(int i = 0;i < bound;i++)
            randoms[i] = new Random().nextInt(count);

        zset.start();
        Stream.of(randoms)
                .forEach(i ->
                        connection.zSetCommands().zRem(
                                "testzset".getBytes(),
                                ("member:" + i).getBytes()
                        )
                );
        zset.stop();
        Long zsetL = connection.zSetCommands().zCard(
                "testzset".getBytes()
        );

        list.start();
        Stream.of(randoms)
                .forEach(i ->
                        connection.listCommands().lRem(
                                "testlist".getBytes(),
                                1,
                                ("member:" + i).getBytes()
                        )
                );
        list.stop();
        Long listL = connection.listCommands().lLen(
                "testlist".getBytes()
        );

        connection.close();

        System.out.println(zset.getTotalTimeMillis());
        System.out.println(list.getTotalTimeMillis());

        // then
        // 남아있는 갯수가 같아야함
        Assertions.assertEquals(zsetL,listL);
    }

    /**
     *
     * ZSET : 4
     *
     * list-compress-depth 0 -> 1, 2, 3
     * List : 33 -> 67, 54, 53
     */
    @Test
    @DisplayName("ZSET, List 조회 성능 비교 - 1_000_000 개 에서 100_000 개 조회 속도")
    void performance_comp_find() {
        // given
        int count = 1_000_000;
        StopWatch zset = new StopWatch();
        StopWatch list = new StopWatch();

        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.openPipeline();
        IntStream.range(0,count)
                .forEach(i ->
                        connection.zSetCommands().zAdd(
                                "testzset".getBytes(),
                                i,
                                ("member:" + i).getBytes()
                        )
                );
        connection.closePipeline();

        connection.openPipeline();
        IntStream.range(0,count)
                .forEach(i ->
                        connection.listCommands().lPush(
                                "testlist".getBytes(),
                                ("member:" + i).getBytes()
                        )
                );
        connection.closePipeline();

        // then
        int bound = 100_000;
        int[] randoms = new int[bound];
        for(int i = 0;i < bound;i++)
            randoms[i] = new Random().nextInt(count);

        zset.start();
        List<Long> zsetIndex = Stream.of(randoms)
                .map(i ->
                        connection.zSetCommands().zRank(
                                "testzset".getBytes(),
                                ("member:" + i).getBytes()
                        )
                ).collect(Collectors.toList());
        zset.stop();


        list.start();
        List<Long> listIndex = Stream.of(randoms)
                .map(i ->
                        connection.listCommands().lPos(
                                "testlist".getBytes(),
                                ("member:" + i).getBytes()
                        )
                ).collect(Collectors.toList());
        list.stop();
        connection.close();

        System.out.println(zset.getTotalTimeMillis());
        System.out.println(list.getTotalTimeMillis());

        // then
        Assertions.assertEquals(zsetIndex, listIndex);
    }

    /**
     * list-max-listpack-size -2
     * 100_000 개 저장
     * zset : 9369376
     * list : 1364720
     *
     * list-max-listpack-size -1
     * list : 1375553
     *
     * list-compress-depth 0 -> 1, 2, 3
     * list : 1364720 -> 668400, 842480, 1016560
     */
    @Test
    @DisplayName("ZSET, List 조회 메모리 비교 - 100_000개 저장 메모리 차이 테스트")
    void performance_comp_memoryUsage() {
        // given
        int count = 100_000;

        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.openPipeline();
        IntStream.range(0,count)
                .forEach(i ->
                        connection.zSetCommands().zAdd(
                                "testzset".getBytes(),
                                i,
                                ("member:" + i).getBytes()
                        )
                );
        connection.closePipeline();
        Long zsetL = connection.zSetCommands().zCard(
                "testzset".getBytes()
        );

        connection.openPipeline();
        IntStream.range(0,count)
                .forEach(i ->
                        connection.listCommands().lPush(
                                "testlist".getBytes(),
                                ("member:" + i).getBytes()
                        )
                );
        connection.closePipeline();
        Long listL = connection.listCommands().lLen(
                "testlist".getBytes()
        );
        
        // when
        RedisScript<Long> memoryUsageZsetScript = RedisScript.of(
                "return redis.call('MEMORY', 'USAGE', KEYS[1])", Long.class);
        Long zsetMemory = redisTemplate.execute(memoryUsageZsetScript, Collections.singletonList("testzset"));

        RedisScript<Long> memoryUsageListScript = RedisScript.of(
                "return redis.call('MEMORY', 'USAGE', KEYS[1])", Long.class);
        Long listMemory = redisTemplate.execute(memoryUsageListScript, Collections.singletonList("testlist"));

        System.out.println("zsetMemory = " + zsetMemory);
        System.out.println("listMemory = " + listMemory);
        // then
        Assertions.assertEquals(listL,zsetL);
    }
}
