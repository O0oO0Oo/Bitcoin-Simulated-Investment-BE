package com.cryptocurrency.investment.price.scheduler.quartz;

import com.cryptocurrency.investment.price.domain.redis.PriceInfoRedis;
import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoDto;
import com.cryptocurrency.investment.price.dto.scheduler.PricePerMinuteDataDto;
import com.cryptocurrency.investment.price.dto.scheduler.PricePerMinuteDto;
import com.cryptocurrency.investment.price.repository.redis.PriceInfoRedisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
@RequiredArgsConstructor
public class EverySecondRequestJsonJob implements Job {
    private final PriceInfoRedisRepository redisRepository;
    private final PricePerMinuteDto pricePerMinuteDto;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${crypto.redis.time}")
    private int TIME;

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
        RequestPriceInfoDto readValue;

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


        Long localDateTime = readValue.getInnerData().getTimestamp();
        Long finalLocalDateTime = localDateTime - localDateTime % 1000;
        LocalDateTime start = LocalDateTime.now();

//        readValue.getFields().forEach((k, v) -> {
//            if (!pricePerMinuteDto.getPriceHashMap().containsKey(k)) {
//                pricePerMinuteDto.getPriceHashMap().put(
//                        k,
//                        new PricePerMinuteDataDto(
//                                v.getClosing_price()
//                        )
//                );
//            } else {
//                pricePerMinuteDto.getPriceHashMap().get(k)
//                        .setPrice(v.getClosing_price());
//            }
//
//            redisRepository.save(new PriceInfoRedis(
//                    k + finalLocalDateTime,
//                    k,
//                    finalLocalDateTime,
//                    Double.parseDouble(v.getClosing_price()),
//                    TIME));
//        });

//        List<PriceInfoRedis> priceInfoRedis = new ArrayList<>();
//
//        readValue.getFields().forEach((k, v) -> {
//            if (!pricePerMinuteDto.getPriceHashMap().containsKey(k)) {
//                pricePerMinuteDto.getPriceHashMap().put(
//                        k,
//                        new PricePerMinuteDataDto(
//                                v.getClosing_price()
//                        )
//                );
//            } else {
//                pricePerMinuteDto.getPriceHashMap().get(k)
//                        .setPrice(v.getClosing_price());
//            }
//
//            priceInfoRedis.add(
//                    new PriceInfoRedis(
//                            k + finalLocalDateTime,
//                            k,
//                            finalLocalDateTime,
//                            Double.parseDouble(v.getClosing_price()),
//                            TIME
//                    ));
//        });
//        redisRepository.saveAll(priceInfoRedis);

        // TODO : JpaRepository 를 사용하면 1~2초 걸림, redisTemplate 사용할 것
        readValue.getFields().forEach((k, v) -> {
            if (!pricePerMinuteDto.getPriceHashMap().containsKey(k)) {
                pricePerMinuteDto.getPriceHashMap().put(
                        k,
                        new PricePerMinuteDataDto(
                                v.getClosing_price()
                        )
                );
            } else {
                pricePerMinuteDto.getPriceHashMap().get(k)
                        .setPrice(v.getClosing_price());
            }

            stringRedisTemplate.opsForValue().set(
                            k + finalLocalDateTime,
                    (new PriceInfoRedis(k + finalLocalDateTime, k, finalLocalDateTime, Double.parseDouble(v.getClosing_price()), TIME )).toString()
                    );
        });

        System.out.println(MessageFormat.format("Thread: {0}, id : {1} , Time : {2} end ",
                Thread.currentThread().getName(),Thread.currentThread().getId(), Duration.between(start, LocalDateTime.now()).toMillis()));
    }
}
