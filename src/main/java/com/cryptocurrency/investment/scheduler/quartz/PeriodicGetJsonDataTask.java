package com.cryptocurrency.investment.scheduler.quartz;

import com.cryptocurrency.investment.domain.redis.CurrencyPriceRedis;
import com.cryptocurrency.investment.domain.redis.request.CryptocurrencyJson;
import com.cryptocurrency.investment.domain.redis.request.CryptocurrencyJsonPriceData;
import com.cryptocurrency.investment.repository.redis.CurrencyPriceInfoRedisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PeriodicGetJsonDataTask implements Job {

    @Autowired
    CurrencyPriceInfoRedisRepository redisRepository;

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
        CryptocurrencyJson readValue;
        try {
            url = new URL("https://api.bithumb.com/public/ticker/ALL");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            responseBody = conn.getInputStream();
            readValue = mapper.readValue(responseBody, CryptocurrencyJson.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HashMap<String, CryptocurrencyJsonPriceData> fields = readValue.getCryptocurrencyJsonInnerInfo().getFields();
        Long localDateTime = readValue.getCryptocurrencyJsonInnerInfo().getTimestamp();

        for (Map.Entry<String, CryptocurrencyJsonPriceData> entry : fields.entrySet()) {
            CurrencyPriceRedis priceRedis = new CurrencyPriceRedis(
                    entry.getKey() + readValue.getCryptocurrencyJsonInnerInfo().getTimestamp(),
                    entry.getKey(),
                    localDateTime,
                    entry.getValue().getClosing_price());
            redisRepository.save(priceRedis);
        }
    }
}
