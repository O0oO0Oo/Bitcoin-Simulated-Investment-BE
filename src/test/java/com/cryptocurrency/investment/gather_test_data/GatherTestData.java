package com.cryptocurrency.investment.gather_test_data;

import com.cryptocurrency.investment.config.JpaConfig;
import com.cryptocurrency.investment.config.RedisConfig;
import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoDto;
import com.cryptocurrency.investment.price.repository.redis.PriceInfoRedisJpaRepository;
import com.cryptocurrency.investment.price.repository.redis.PriceInfoRedisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Test 에서 사용될 초당 가격 데이터 수집
 */
@ExtendWith(MockitoExtension.class)
@DataJpaTest(properties = "spring.config.location=classpath:/application-test.yaml")
@Import({RedisConfig.class, JpaConfig.class, PriceInfoRedisRepository.class})
@Disabled
public class GatherTestData {
    private final int testRepeat = 3600;
    private Long localDateTime;
    private RequestPriceInfoDto readValue;
    @Autowired
    private PriceInfoRedisRepository priceInfoRedisRepository;

    @BeforeEach
    void beforeEach() {
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

    @Test
    @DisplayName("Redis Multi - Exec RedisConnection")
    @RepeatedTest(testRepeat)
    void givenPriceJsonData_whenRepeatSavePriceInfoRedisRepository_thenSavePriceData() throws InterruptedException {
        priceInfoRedisRepository.saveAll(readValue.getFields(), localDateTime);
        TimeUnit.MILLISECONDS.sleep(200);
    }
}
