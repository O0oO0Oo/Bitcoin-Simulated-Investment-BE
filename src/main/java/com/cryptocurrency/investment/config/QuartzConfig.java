package com.cryptocurrency.investment.config;

import com.cryptocurrency.investment.price.scheduler.quartz.EveryMinuteSaveJsonJob;
import com.cryptocurrency.investment.price.scheduler.quartz.EverySecondRequestJsonJob;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.*;

import javax.sql.DataSource;

@Configuration
public class QuartzConfig{

    @Autowired
    ApplicationContext applicationContext;
    @Bean("springBeanJobFactory")
    public SpringBeanJobFactory springBeanJobFactory() {
        SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /*------------------------------------------------------*/
    /**
     * EverySecondRequestJsonJob
     */
    @Bean("everySecondRequestJsonJobDetail")
    public JobDetailFactoryBean everySecondRequestJsonJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(EverySecondRequestJsonJob.class);
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean("everySecondRequestJsonTrigger")
    public SimpleTriggerFactoryBean everySecondRequestJsonTrigger(
            @Qualifier("everySecondRequestJsonJobDetail") JobDetail requestJob){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(requestJob);
        factoryBean.setStartDelay(0);
        factoryBean.setRepeatInterval(500);
        return factoryBean;
    }

    @Bean("everySecondRequestJsonScheduler")
    public SchedulerFactoryBean everySecondRequestJsonScheduler(
            @Qualifier("everySecondRequestJsonJobDetail") JobDetail requestJob,
            @Qualifier("everySecondRequestJsonTrigger") Trigger requestTrigger,
            @Qualifier("springBeanJobFactory") SpringBeanJobFactory springBeanJobFactory,
            DataSource dataSource) {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobDetails(requestJob);
        factoryBean.setTriggers(requestTrigger);
        factoryBean.setJobFactory(springBeanJobFactory);
        factoryBean.setDataSource(dataSource);
        return factoryBean;
    }

    /*------------------------------------------------------*/
    /**
     * EveryMinuteSaveJsonJob
     */
    @Bean("everyMinuteSaveJsonJobDetail")
    public JobDetailFactoryBean everyMinuteSaveJsonJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(EveryMinuteSaveJsonJob.class);
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean("everyMinuteSaveJsonTrigger")
    public CronTriggerFactoryBean everyMinuteSaveJsonTrigger(
            @Qualifier("everyMinuteSaveJsonJobDetail") JobDetail saveJob){
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(saveJob);
        factoryBean.setStartDelay(0);
        factoryBean.setCronExpression("0 0/1 * * * ?");
        return factoryBean;
    }

    @Bean("everyMinuteSaveJsonScheduler")
    public SchedulerFactoryBean everyMinuteSaveJsonScheduler(
            @Qualifier("everyMinuteSaveJsonJobDetail") JobDetail saveJob,
            @Qualifier("everyMinuteSaveJsonTrigger") Trigger saveTrigger,
            @Qualifier("springBeanJobFactory") SpringBeanJobFactory springBeanJobFactory,
            DataSource dataSource) {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobDetails(saveJob);
        factoryBean.setTriggers(saveTrigger);
        factoryBean.setJobFactory(springBeanJobFactory);
        factoryBean.setDataSource(dataSource);
        return factoryBean;
    }
}