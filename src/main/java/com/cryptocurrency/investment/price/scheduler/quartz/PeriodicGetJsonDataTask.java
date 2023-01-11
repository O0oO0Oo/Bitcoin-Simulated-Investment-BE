package com.cryptocurrency.investment.price.scheduler.quartz;

import com.cryptocurrency.investment.price.domain.redis.PriceInfoRedis;
import com.cryptocurrency.investment.price.domain.redis.request.CryptoJson;
import com.cryptocurrency.investment.price.domain.redis.request.CryptoPriceJson;
import com.cryptocurrency.investment.price.repository.redis.PriceInfoRedisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PeriodicGetJsonDataTask implements Job {

    @Autowired
    PriceInfoRedisRepository redisRepository;

    @Autowired
    RedisTemplate<String, PriceInfoRedis> redisTemplate;

    /**
     * Todo: 현재는 request 패키지의 Class들에 ObjectMapper를 통해 매핑 후 다시 CurrencyPriceRedis 생성자를 통해 저장하고 있다.
     * Todo: 한 단계 거치지 않고 바로 redis에 저장할수있는 매핑방법을 찾아보기
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        URL url;
        HttpURLConnection conn;
        InputStream responseBody;
        ObjectMapper mapper = new ObjectMapper();
        CryptoJson readValue;
        try {
            url = new URL("https://api.bithumb.com/public/ticker/ALL");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            responseBody = conn.getInputStream();
            readValue = mapper.readValue(responseBody, CryptoJson.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HashMap<String, CryptoPriceJson> fields = readValue.getCryptocurrencyJsonInnerInfo().getFields();
        Long localDateTime = readValue.getCryptocurrencyJsonInnerInfo().getTimestamp();
        localDateTime = localDateTime - localDateTime % 1000;

        for (Map.Entry<String, CryptoPriceJson> entry : fields.entrySet()) {
            PriceInfoRedis priceRedis = new PriceInfoRedis(
                    entry.getKey() + localDateTime,
                    entry.getKey(),
                    localDateTime,
                    entry.getValue().getClosing_price(),
                    300);
            redisRepository.save(priceRedis);
        }
    }
}
