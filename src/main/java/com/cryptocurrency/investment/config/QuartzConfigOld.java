package com.cryptocurrency.investment.config;

import com.cryptocurrency.investment.price.Service.quartz.EveryMinuteSaveJsonJob;
import com.cryptocurrency.investment.price.Service.quartz.EverySecondRequestJsonJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

//@Configuration
public class QuartzConfigOld {

    @Bean("everySecondRequestJsonSchedulerFactory")
    public SchedulerFactoryBean everySecondRequestJsonSchedulerFactory(DataSource dataSource){
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setDataSource(dataSource);
        return factoryBean;
    }

    @Bean("everySecondRequestJsonScheduler")
    public Scheduler everySecondRequestJsonScheduler(
            @Qualifier("everySecondRequestJsonSchedulerFactory") SchedulerFactoryBean factoryBean)
            throws SchedulerException {
        Scheduler scheduler = factoryBean.getScheduler();
        scheduler.start();
        return scheduler;
    }

    @Bean("everyMinuteSaveJsonSchedulerFactory")
    public SchedulerFactoryBean everyMinuteSaveJsonSchedulerFactory(DataSource dataSource){
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setDataSource(dataSource);
        return factoryBean;
    }

    @Bean("everyMinuteSaveJsonScheduler")
    public Scheduler everyMinuteSaveJsonScheduler(
            @Qualifier("everyMinuteSaveJsonSchedulerFactory") SchedulerFactoryBean factoryBean)
            throws SchedulerException {
        Scheduler scheduler = factoryBean.getScheduler();
        scheduler.start();
        return scheduler;
    }

    @Bean
    public CommandLineRunner run(
            @Qualifier("everySecondRequestJsonScheduler") Scheduler requestScheduler,
            @Qualifier("everyMinuteSaveJsonScheduler") Scheduler saveScheduler) {
        return (String[] args) -> {

            JobDetail requestJob = JobBuilder.newJob().ofType(EverySecondRequestJsonJob.class)
                    .build();
            Trigger requestTrigger = TriggerBuilder.newTrigger()
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder
                            .simpleSchedule()
                            .withIntervalInMilliseconds(500)
                            .repeatForever())
                    .build();
            requestScheduler.scheduleJob(requestJob, requestTrigger);


            JobDetail saveJob = JobBuilder.newJob().ofType(EveryMinuteSaveJsonJob.class)
                    .build();
            Trigger saveTrigger = TriggerBuilder.newTrigger()
                    .startNow()
                    .withSchedule(CronScheduleBuilder
                            .cronSchedule("0 0/1 * * * ?"))
                    .build();
            saveScheduler.scheduleJob(saveJob, saveTrigger);
        };
    }
}