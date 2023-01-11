package com.cryptocurrency.investment.config;

import com.cryptocurrency.investment.price.scheduler.quartz.PeriodicGetJsonDataTask;
import org.quartz.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException {
        Scheduler scheduler = factory.getScheduler();
        scheduler.start();
        return scheduler;
    }

    @Bean
    public CommandLineRunner run(Scheduler scheduler) {
        return (String[] args) -> {
            JobDetail job = JobBuilder.newJob(PeriodicGetJsonDataTask.class)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("everySecondsRequestJsonData","requestGroup")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(500)
                        .repeatForever())
                    .build();

            scheduler.scheduleJob(job, trigger);
        };
    }
}