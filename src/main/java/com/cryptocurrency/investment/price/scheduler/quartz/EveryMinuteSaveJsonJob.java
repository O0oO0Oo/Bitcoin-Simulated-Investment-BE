package com.cryptocurrency.investment.price.scheduler.quartz;

import com.cryptocurrency.investment.price.domain.mysql.PriceInfoMysql;
import com.cryptocurrency.investment.price.dto.scheduler.PricePerMinuteDto;
import com.cryptocurrency.investment.price.repository.mysql.PriceInfoMysqlRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EveryMinuteSaveJsonJob implements Job {

    @Autowired
    PricePerMinuteDto pricePerMinuteDto;
    @Autowired
    PriceInfoMysqlRepository mysqlRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        mysqlRepository.saveAll(pricePerMinuteDto.getPriceHashMap().entrySet().stream()
                .map(entry -> new PriceInfoMysql(
                                entry.getKey(),
                                context.getFireTime().getTime() - context.getFireTime().getTime() % 1000,
                                entry.getValue().getCurPrice(),
                                entry.getValue().getMaxPrice(),
                                entry.getValue().getMinPrice()
                        )
                )
                .collect(Collectors.toList())
        );
    }
}