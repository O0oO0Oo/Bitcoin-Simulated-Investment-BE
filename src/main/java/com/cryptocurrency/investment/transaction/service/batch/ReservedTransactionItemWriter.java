package com.cryptocurrency.investment.transaction.service.batch;

import com.cryptocurrency.investment.wallet.domain.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class ReservedTransactionItemWriter implements ItemWriter<Object> {

    private final ReservedTransactionItemProcessor reservedTransactionItemProcessor;
    private final DataSource dataSource;
//    private final walletSql

    private JdbcBatchItemWriter<Wallet> walletJdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<Wallet>()
                .dataSource(dataSource)
                .build();
    }

    private JdbcBatchItemWriter<Object> userAccountJdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<>()
                .build();
    }

    private JdbcBatchItemWriter<Object> reservedTransactionJdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<>()
                .build();
    }

    @Override
    public void write(Chunk<?> chunk) throws Exception {
    }
}
