package com.cryptocurrency.investment.price.scheduler.quartz;

import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoDto;
import com.cryptocurrency.investment.price.dto.scheduler.PricePerMinuteDto;
import com.cryptocurrency.investment.price.repository.redis.PriceInfoRedisRepository;
import com.cryptocurrency.investment.price.util.HttpJsonRequest;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class EverySecondRequestJsonJob implements Job {
    private final PricePerMinuteDto pricePerMinuteDto;
    private final PriceInfoRedisRepository priceInfoRedisRepository;

    private final HttpJsonRequest httpJsonRequest;
    /**
     * TODO : SortedSet 으로 저장하면서 expire 옵션을 사용안함,
     */
    @Value("${crypto.redis.time}")
    private int TIME;

    /**
     * Todo: 현재는 request 패키지의 Class들에 ObjectMapper를 통해 매핑 후 다시 CurrencyPriceRedis 생성자를 통해 저장하고 있다.
     * Todo: 한 단계 거치지 않고 바로 redis에 저장할수있는 매핑방법을 찾아보기
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RequestPriceInfoDto readValue;
        try {
            readValue = httpJsonRequest.sendRequest("https://api.bithumb.com/public/ticker/ALL", RequestPriceInfoDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Long localDateTime = readValue.getInnerData().getTimestamp();
        Long finalLocalDateTime = localDateTime - localDateTime % 1000;

        // TODO : 키 일정시간 마다 제거해야함.
        // 레디스에 데이터 저장
        priceInfoRedisRepository.saveAll(readValue.getFields(), finalLocalDateTime);

        // 분당 가격 데이터를 저장하기 위해 갱신
        pricePerMinuteDto.setPriceHashMap(readValue);
    }
}