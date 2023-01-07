package com.cryptocurrency.investment.config;

import org.quartz.*;
import com.cryptocurrency.investment.scheduler.quartz.PeriodicGetJsonDataTask;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
                        .withIntervalInSeconds(1)
                        .repeatForever())
                    .build();

            scheduler.scheduleJob(job, trigger);
        };
    }
}