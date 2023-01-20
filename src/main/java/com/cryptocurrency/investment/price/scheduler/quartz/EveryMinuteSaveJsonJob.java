package com.cryptocurrency.investment.price.scheduler.quartz;

import com.cryptocurrency.investment.price.domain.mysql.PriceInfoMysql;
import com.cryptocurrency.investment.price.dto.scheduler.PricePerMinuteDto;
import com.cryptocurrency.investment.price.repository.mysql.PriceInfoMysqlRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EveryMinuteSaveJsonJob implements Job {

    @Autowired
    PricePerMinuteDto pricePerMinuteDto;

    @Autowired
    PriceInfoMysqlRepository mysqlRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        pricePerMinuteDto.getPriceHashMap().forEach((k,v) -> {
            mysqlRepository.save(
                    new PriceInfoMysql(
                            k,
                            context.getFireTime().getTime() - context.getFireTime().getTime() % 1000,
                            v.getCurPrice(),
                            v.getMaxPrice(),
                            v.getMinPrice()
                    )
            );
        });
    }
}