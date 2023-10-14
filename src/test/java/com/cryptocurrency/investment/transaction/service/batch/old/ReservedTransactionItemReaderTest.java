package com.cryptocurrency.investment.transaction.service.batch.old;

import com.cryptocurrency.investment.config.JpaConfig;
import com.cryptocurrency.investment.config.RedisConfig;
import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoDto;
import com.cryptocurrency.investment.price.util.HttpJsonRequest;
import com.cryptocurrency.investment.price.util.PriceMessageBlockingQueue;
import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.transaction.domain.TransactionStatus;
import com.cryptocurrency.investment.transaction.domain.TransactionType;
import com.cryptocurrency.investment.transaction.dto.processing.ReservedTransactionJdbcDto;
import com.cryptocurrency.investment.transaction.repository.mysql.TransactionMysqlRepository;
import com.cryptocurrency.investment.transaction.service.batch.ReservedTransactionItemReader;
import com.cryptocurrency.investment.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import({RedisConfig.class, JpaConfig.class})
@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@Commit
class ReservedTransactionItemReaderTest {

    @InjectMocks
    private static PriceMessageBlockingQueue priceMessageBlockingQueue;
    @InjectMocks
    private HttpJsonRequest httpJsonRequest;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private TransactionMysqlRepository transactionMysqlRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("한번의 가격정보와 한개의 예약된 거래, reservedTransactionItemReader 테스트")
    public void givenMockedStep_whenReaderCalled_thenSuccess() throws Exception {
        // given
        ExecutionContext context = new ExecutionContext();
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(context);
        ReservedTransactionItemReader<ReservedTransactionJdbcDto> reader = reservedTransactionItemReader();

        // given - Json 데이터 -> 가격정보 큐에 저장
        RequestPriceInfoDto readValue = httpJsonRequest.sendRequest("https://api.bithumb.com/public/ticker/ALL", RequestPriceInfoDto.class);
        priceMessageBlockingQueue.produce(readValue.getFields(),readValue.getInnerData().getTimestamp());

        // given - Transaction 1개 설정
        Connection connection = dataSource.getConnection();
        Transaction transaction = new Transaction();
        transaction.setTimestamp(readValue.getInnerData().getTimestamp() - 1000);
        transaction.setType(TransactionType.RESERVE_BUY);
        transaction.setStatus(TransactionStatus.RESERVED);
        transaction.setUserAccount(
                userRepository.findByUsername("test").get()
        );
        transaction.setName("BTC");
        transaction.setAmount(10.0);
        transaction.setPrice(Double.parseDouble(readValue.getFields().get("BTC").getClosing_price()));
        Crypto btc = new Crypto();
        btc.setId(38L);
        transaction.setCrypto(btc);

        transactionMysqlRepository.saveAndFlush(transaction);

        // when - read
        List<ReservedTransactionJdbcDto> result = new ArrayList<>();
        ReservedTransactionJdbcDto item;
        reader.open(stepExecution.getExecutionContext());
        while ((item = reader.read()) != null) {
            result.add(item);
        }
        reader.close();

        // then
        Assertions.assertEquals(
                1, result.size()
        );
    }
    private ReservedTransactionItemReader<ReservedTransactionJdbcDto> reservedTransactionItemReader() throws Exception {
        ReservedTransactionItemReader<ReservedTransactionJdbcDto> reservedTransactionItemReader
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
                    setMappedClass(ReservedTransactionJdbcDto.class);
                }}
        );

        reservedTransactionItemReader.setPageSize(1000);
        reservedTransactionItemReader.afterPropertiesSet();

        return reservedTransactionItemReader;
    }
}