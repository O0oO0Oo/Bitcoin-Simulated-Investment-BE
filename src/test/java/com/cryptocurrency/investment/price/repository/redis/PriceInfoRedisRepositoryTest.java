package com.cryptocurrency.investment.price.repository.redis;

import com.cryptocurrency.investment.config.JpaConfig;
import com.cryptocurrency.investment.config.RedisConfig;
import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoDto;
import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoInnerDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@DataJpaTest(properties = "spring.config.location=classpath:/application-test.yaml")
@Import({RedisConfig.class, JpaConfig.class, PriceInfoRedisRepository.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PriceInfoRedisRepositoryTest {

    private static RequestPriceInfoDto readValue;
    private static Long localDateTime;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private PriceInfoRedisRepository priceInfoRedisRepository;
    private Long checkPerformanceTime;

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
        checkPerformanceTime = System.currentTimeMillis();
    }

    @AfterEach
    void afterEach() {
        System.out.println("Time : " + (System.currentTimeMillis() - checkPerformanceTime));
    }
    @Test
    @DisplayName("Save Multi - Exec RedisConnection")
    @Order(2)
    void givenPriceData_whenSaveMultiExecRedisConnection_thenSavePriceData() {
        // given
        RedisConnection connection = redisConnectionFactory.getConnection();
        ConcurrentHashMap<String, RequestPriceInfoInnerDto> fields = readValue.getFields();

        // when
        priceInfoRedisRepository.saveAll(fields, localDateTime);

        // then
        fields.forEach((k,v) ->{
            long score = connection.zSetCommands().zScore(
                    ("price:" + k).getBytes(),
                    (v.getClosing_price() + ":" + localDateTime).getBytes()
            ).longValue();
            Assertions.assertThat(score).isEqualTo(localDateTime);
        });
    }

    @Test
    @DisplayName("Get Price Data RedisConnection")
    @Order(3)
    void givenPriceData_whenFindByNameRedisConnection_thenReturnPriceDataList() {
        // given
        ConcurrentHashMap<String, RequestPriceInfoInnerDto> fields = readValue.getFields();

        // when
        List<String> btcList = priceInfoRedisRepository.findByName("BTC");

        // then
        String[] split = btcList.stream().findFirst().get().split(":");
        // Price
        Assertions.assertThat(split[0]).isEqualTo(fields.get("BTC").getClosing_price());
        // localDateTime
        Assertions.assertThat(Long.parseLong(split[1])).isEqualTo(localDateTime);
    }

    @Test
    @DisplayName("Get Price Data RedisConnection")
    @Order(1)
    void givenPriceData_whenFindByNameAndLocalDateTimeRedisConnection_thenReturnPriceDataList() {
        // given
        List<String> givenBtcList = priceInfoRedisRepository.findByName("BTC");
        String[] givenSplit = givenBtcList.get(10).split(":");
        Long findLocalDateTime = Long.parseLong(givenSplit[1]);

        // when
        List<String> whenBtcList = priceInfoRedisRepository.findByNameAndLocalDateTime("BTC", findLocalDateTime);
        String[] whenSplit = whenBtcList.get(0).split(":");

        // then
        Assertions.assertThat(whenSplit).isEqualTo(givenSplit);
        Assertions.assertThat(whenBtcList.size()).isEqualTo(300);
    }
}