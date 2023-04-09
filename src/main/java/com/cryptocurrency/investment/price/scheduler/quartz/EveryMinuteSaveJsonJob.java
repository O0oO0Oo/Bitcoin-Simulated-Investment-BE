package com.cryptocurrency.investment.price.scheduler.quartz;

import com.cryptocurrency.investment.price.dto.scheduler.PricePerMinuteDto;
import com.cryptocurrency.investment.price.repository.mysql.PriceInfoMysqlRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EveryMinuteSaveJsonJob implements Job {

    private final PricePerMinuteDto pricePerMinuteDto;
    private final PriceInfoMysqlRepository mysqlRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        mysqlRepository.saveAll(pricePerMinuteDto.getPriceInfoMysqlList());
    }
}