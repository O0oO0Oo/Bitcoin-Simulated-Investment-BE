package com.cryptocurrency.investment.transaction.service.batch;

import com.cryptocurrency.investment.price.util.PriceMessageBlockingQueue;
import com.cryptocurrency.investment.transaction.dto.processing.ReservedTransactionJpaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReservedTransactionBatch {
    // Setting
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    // Message Queue
    private final PriceMessageBlockingQueue priceMessageBlockingQueue;

    // Processor, Writer
    private final ReservedTransactionItemProcessor reservedTransactionItemProcessor;
    private final ReservedTransactionItemWriter reservedTransactionItemWriter;

    public Job reservedTransactionJob() throws Exception {
        return new JobBuilder("Reserved Transaction Job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(reservedTrasactionStep())
                .build();
    }

    private Step reservedTrasactionStep() throws Exception {
        return new StepBuilder("Reserved Transaction Step", jobRepository)
                .chunk(1000, platformTransactionManager)
                .reader(reservedTransactionItemReader())
                .processor(reservedTransactionItemProcessor)
                .writer(reservedTransactionItemWriter) // TODO : 구현
                .build();
    }

    @Bean
    private ReservedTransactionItemReader<ReservedTransactionJpaDto> reservedTransactionItemReader() throws Exception {
        ReservedTransactionItemReader<ReservedTransactionJpaDto> reservedTransactionItemReader
                = new ReservedTransactionItemReader<>();

        Map<String, Order> sortKey = new HashMap<>();
        sortKey.put("id", Order.ASCENDING);

        // Iterator 설정
        reservedTransactionItemReader.setIteratorSql("SELECT c.name FROM crypto c WHERE c.status != 'NOT_USED' or c.status != 'DELETED'");
        reservedTransactionItemReader.setIteratorColumnName("name");

        // Price MessageQueue 설정
        reservedTransactionItemReader.setPriceMessageQueue(priceMessageBlockingQueue);

        // 일반 설정
        reservedTransactionItemReader.setDataSource(dataSource);
        reservedTransactionItemReader.setQueryProvider(
                new MySqlPagingQueryProvider(){{
                    setSelectClause("t.id, t.price, t.amount, t.name, t.user_account_id, t.type");
                    setFromClause("FROM transaction t");
                    setWhereClause("WHERE t.name = ? and t.price = ? and t.timestamp <= ? and t.status = 'RESERVED'");
                    setSortKeys(sortKey);
                }}
        );

        reservedTransactionItemReader.setRowMapper(
                new BeanPropertyRowMapper<>(){{
                    setMappedClass(ReservedTransactionJpaDto.class);
                }}
        );

        reservedTransactionItemReader.setPageSize(1000);
        reservedTransactionItemReader.afterPropertiesSet();

        return reservedTransactionItemReader;
    }
}