package com.cryptocurrency.investment.price.scheduler.quartz;

import com.cryptocurrency.investment.config.JpaConfig;
import com.cryptocurrency.investment.config.RedisConfig;
import com.cryptocurrency.investment.price.domain.redis.PriceInfoRedis;
import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoDto;
import com.cryptocurrency.investment.price.repository.redis.PriceInfoRedisJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DataJpaTest(properties = "spring.config.location=classpath:/application-test.yaml")
@Import({RedisConfig.class, JpaConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // TODO : 실행 순서가 제일 앞에 오는 테스트메서드는 시간이 더 걸림
class EverySecondRequestJsonJob_PerformanceTest {

    private final int testRepeat = 10;
    private Long savePerformanceTime;
    private static Long localDateTime;
    private static RequestPriceInfoDto readValue;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PriceInfoRedisJpaRepository priceInfoRedisJpaRepository;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @BeforeAll
    static void beforeAll() {
        URL url;
        HttpURLConnection conn;
        InputStream responseBody;
        ObjectMapper mapper = new ObjectMapper();
        try {
            url = new URL("https://api.bithumb.com/public/ticker/ALL");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            responseBody = conn.getInputStream();
            readValue = mapper.readValue(responseBody, RequestPriceInfoDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        localDateTime = readValue.getInnerData().getTimestamp() - readValue.getInnerData().getTimestamp() % 1000;
    }

    @BeforeEach
    void beforeEach() {
        savePerformanceTime = System.currentTimeMillis();
    }

    @AfterEach
    void afterEach() {
        System.out.println("Time : " + (System.currentTimeMillis() - savePerformanceTime));

        // Redis Connection Initialize
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.commands().flushAll();
        connection.close();
    }

    /**
     * 저장 속도 비교
     */

    /**
     * Using Redis JpaRepository
     * - 기존의 방법 -
     */
    @Test
    @DisplayName("Redis Jpa Repository")
    @RepeatedTest(testRepeat)
    void givenPriceJsonData_whenSaveRedisRepository_thenSaveJsonData() {
        List<PriceInfoRedis> priceInfoRedisList = new ArrayList<>();
        readValue.getFields().forEach((k, v) -> {
            priceInfoRedisList.add(
                    new PriceInfoRedis(
                            k + localDateTime,
                            k,
                            localDateTime,
                            Double.parseDouble(v.getClosing_price()),
                            600
                    )
            );
        });
        priceInfoRedisJpaRepository.saveAll(priceInfoRedisList);
    }

    /**
     * TODO: JpaRepository 가 index 같은 데이터를 포함해서 2배가량 많이 저장하지만 StringRedisTemplate 보다 8배 느리다. 원인 찾아보기
     * Using StringRedisTemplate SortedSet
     */
    @Test
    @DisplayName("Redis RedisTemplate")
    @RepeatedTest(testRepeat)
    void givenPriceJsonData_whenSaveStringRedisTemplate_thenSaveJsonData() {
        readValue.getFields().forEach((k, v) -> {
            stringRedisTemplate.opsForZSet().add(
                    "price:" + k,
                    v.getClosing_price().toString() + ":" + localDateTime,
                    localDateTime);
        });
    }

    /**
     * Using RedisConnection Multi - Exec
     */
    @Test
    @DisplayName("Redis Multi - Exec RedisConnection")
    @RepeatedTest(testRepeat)
    void givenPriceJsonData_whenSaveRedisConnectionMultiExec_thenSaveJsonData() {
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.multi();
        readValue.getFields().forEach((k, v) -> {
            connection.zAdd(
                    ("price:" + k).getBytes(),
                    localDateTime,
                    (v.getClosing_price() + ":" + localDateTime).getBytes());
        });
        connection.exec();
    }

    /**
     * Using StringRedisTemplate Pipeline
     */
    @Test
    @DisplayName("Redis Pipeline RedisTemplate")
    @RepeatedTest(testRepeat)
    void givenPriceJsonData_whenSaveStringRedisTemplatePipeline_thenSaveJsonData() {
        stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                readValue.getFields().forEach((k, v) -> {
                    connection.zAdd(
                            ("price:" + k).getBytes(),
                            localDateTime,
                            (v.getClosing_price() + ":" + localDateTime).getBytes());
                });
                return null;
            }
        });
    }

    /**
     * Using RedisConnection Pipeline
     */
    @Test
    @DisplayName("Redis Pipeline RedisConnection")
    @RepeatedTest(testRepeat)
    void givenPriceJsonData_whenSaveRedisConnectionPipeline_thenSaveJsonData() {
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.openPipeline();
        readValue.getFields().forEach((k, v) -> {
            connection.zAdd(
                    ("price:" + k).getBytes(),
                    localDateTime,
                    (v.getClosing_price() + ":" + localDateTime).getBytes());
        });
        connection.closePipeline();
    }
}