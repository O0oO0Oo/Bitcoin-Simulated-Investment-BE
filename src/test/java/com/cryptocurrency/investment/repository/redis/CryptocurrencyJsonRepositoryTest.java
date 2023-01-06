package com.cryptocurrency.investment.repository.redis;

import com.cryptocurrency.investment.Service.redis.CurrencyPriceRedisService;
import com.cryptocurrency.investment.domain.redis.CurrencyPriceRedis;
import com.cryptocurrency.investment.domain.redis.request.CryptocurrencyJson;
import com.cryptocurrency.investment.domain.redis.request.CryptocurrencyJsonPriceData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class CryptocurrencyJsonRepositoryTest {

    @Autowired
    CurrencyPriceRedisService redisService;

    @Test
    void givenJsonData_whenthenPrintKeyPrice() throws IOException {
        //given
        URL url = new URL("https://api.bithumb.com/public/ticker/ALL");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

        InputStream responseBody = conn.getInputStream();
        ObjectMapper mapper = new ObjectMapper();

        CryptocurrencyJson readValue = mapper.readValue(responseBody, CryptocurrencyJson.class);

        CryptocurrencyJson.CryptocurrencyJsonInnerInfo innerInfo = readValue.getCryptocurrencyJsonInnerInfo();

        HashMap<String, CryptocurrencyJsonPriceData> fields = readValue.getCryptocurrencyJsonInnerInfo().getFields();

        Long idl = 1L;
        //when && then
        for (Map.Entry<String, CryptocurrencyJsonPriceData> entry : fields.entrySet()
        ) {
            System.out.println(entry.getKey() + " = " + entry.getValue().getClosing_price() + " Date : " + readValue.getCryptocurrencyJsonInnerInfo().getTimestamp());
            CurrencyPriceRedis priceRedis = new CurrencyPriceRedis(++idl,entry.getKey(),LocalDateTime.now(),entry.getValue().getClosing_price());
            redisService.savePriceInfo(priceRedis);
        }
    }
}


