package com.cryptocurrency.investment.price.scheduler.quartz;

import com.cryptocurrency.investment.price.domain.mysql.PriceInfoMysql;
import com.cryptocurrency.investment.price.dto.scheduler.PricePerMinuteDto;
import com.cryptocurrency.investment.price.repository.mysql.PriceInfoMysqlRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EveryMinuteSaveJsonJob implements Job {

    private final PricePerMinuteDto pricePerMinuteDto;
    private final PriceInfoMysqlRepository mysqlRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        mysqlRepository.saveAll(pricePerMinuteDto.getPriceHashMap().entrySet().stream()
                .map(entry -> new PriceInfoMysql(
                                entry.getKey(),
                                context.getFireTime().getTime() - context.getFireTime().getTime() % 1000,
                                Double.parseDouble(entry.getValue().getCurPrice()),
                                Double.parseDouble(entry.getValue().getMaxPrice()),
                                Double.parseDouble(entry.getValue().getMinPrice())
                        )
                )
                .collect(Collectors.toList())
        );
    }
}