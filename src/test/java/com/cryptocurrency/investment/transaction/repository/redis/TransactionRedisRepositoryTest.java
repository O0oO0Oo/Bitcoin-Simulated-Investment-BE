package com.cryptocurrency.investment.transaction.repository.redis;

import com.cryptocurrency.investment.config.JpaConfig;
import com.cryptocurrency.investment.config.RedisConfig;
import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.price.repository.mysql.PriceInfoMysqlRepository;
import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.transaction.domain.TransactionStatus;
import com.cryptocurrency.investment.transaction.domain.TransactionType;
import com.cryptocurrency.investment.transaction.repository.mysql.TransactionMysqlRepository;
import com.cryptocurrency.investment.user.domain.UserAccount;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@DataJpaTest
@Import({RedisConfig.class, JpaConfig.class, TransactionRedisRepository.class})
@ActiveProfiles("test")
class TransactionRedisRepositoryTest {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private TransactionRedisRepository transactionRedisRepository;
    @Autowired
    private TransactionMysqlRepository transactionMysqlRepository;
    private static DecimalFormat decimalFormat;
    @Autowired
    private PriceInfoMysqlRepository priceInfoMysqlRepository;

    @BeforeAll
    static void beforeAll() {
        decimalFormat = new DecimalFormat("#");
        decimalFormat.setMaximumFractionDigits(340);
    }

    @AfterEach
    void beforeEach(){
        RedisConnection connection = redisConnectionFactory.getConnection();
//        connection.serverCommands().flushAll();
        connection.close();
    }

    @Test
    @DisplayName("예약거래 모두 저장 - 10000.123 1개, 20000.0 2개")
    void givenReservedTransactionList_whenSaveAll_thenSaveAll() {
        // given
        List<Transaction> transactions = new ArrayList<>();

        Transaction transaction1 = new Transaction();
        transaction1.setName("BTC");
        transaction1.setPrice(10000.123);
        transaction1.setAmount(10);
        transaction1.setType(TransactionType.RESERVE_BUY);
        transaction1.setCrypto(
                new Crypto(){{
                    setId(1L);
                }}
        );
        transaction1.setUserAccount(
                new UserAccount(){{
                    setId(
                            UUID.randomUUID()
                    );
                }}
        );
        transaction1.setStatus(
                TransactionStatus.RESERVED
        );

        Transaction transaction2 = new Transaction();
        transaction2.setName("BTC");
        transaction2.setPrice(20000.0);
        transaction2.setAmount(10);
        transaction2.setType(TransactionType.RESERVE_BUY);
        transaction2.setCrypto(
                new Crypto(){{
                    setId(1L);
                }}
        );
        transaction2.setUserAccount(
                new UserAccount(){{
                    setId(
                            UUID.randomUUID()
                    );
                }}
        );
        transaction2.setStatus(
                TransactionStatus.RESERVED
        );

        Transaction transaction3 = new Transaction();
        transaction3.setName("BTC");
        transaction3.setPrice(20000.0);
        transaction3.setAmount(10);
        transaction3.setType(TransactionType.RESERVE_BUY);
        transaction3.setCrypto(
                new Crypto(){{
                    setId(1L);
                }}
        );
        transaction3.setUserAccount(
                new UserAccount(){{
                    setId(
                            UUID.randomUUID()
                    );
                }}
        );
        transaction3.setStatus(
                TransactionStatus.RESERVED
        );

        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);

        // when
        transactionRedisRepository.saveAll(transactions);

        // then
        RedisConnection connection = redisConnectionFactory.getConnection();
        Boolean exists1 = connection.keyCommands().exists(
                (transaction1.getName() + ":" + decimalFormat.format(transaction1.getPrice())).getBytes()
        );

        Boolean exists2 = connection.keyCommands().exists(
                (transaction2.getName() + ":" + decimalFormat.format(transaction2.getPrice())).getBytes()
        );
        Long len = connection.listCommands().lLen(
                (transaction2.getName() + ":" + decimalFormat.format(transaction2.getPrice())).getBytes()
        );
        connection.close();

        Assertions.assertEquals(true,exists1);
        Assertions.assertEquals(true,exists2);
        Assertions.assertEquals(2,len);
    }

    @Test
    @DisplayName("예약거래 저장")
    void givenReservedTransaction_whenSave_thenSave() {
        // given
        Transaction transaction = new Transaction();
        transaction.setName("BTC");
        transaction.setPrice(3000.0);
        transaction.setAmount(10);
        transaction.setType(TransactionType.RESERVE_BUY);
        transaction.setCrypto(
                new Crypto(){{
                    setId(1L);
                }}
        );
        transaction.setUserAccount(
                new UserAccount(){{
                    setId(
                            UUID.randomUUID()
                    );
                }}
        );
        transaction.setStatus(
                TransactionStatus.RESERVED
        );

        // when
        Long saved = transactionRedisRepository.save(transaction);

        // then
        Assertions.assertEquals(1,saved);
    }

    @Test
    @DisplayName("예약거래 - 예약 판매 - 등록한 가격이 현재가보다 낮음 - 저장이 아닌 현재가에 처리")
    void givenReservedTransaction_whenReservedSellLowerThenCurrenPrice_thenSellCurrentPrice() {
        // given
        String name = "BTC";
        Double price = 1000.0;

        Transaction transaction = new Transaction();
        transaction.setName(name);
        transaction.setPrice(price);
        transaction.setAmount(10);
        transaction.setType(TransactionType.RESERVE_BUY);
        transaction.setCrypto(
                new Crypto(){{
                    setId(1L);
                }}
        );
        transaction.setUserAccount(
                new UserAccount(){{
                    setId(
                            UUID.randomUUID()
                    );
                }}
        );
        transaction.setStatus(
                TransactionStatus.RESERVED
        );


        // when
        Double currentPrice = 2000.0;

        transaction.setStatus(
                TransactionStatus.COMPLETED
        );

        Transaction save = transactionMysqlRepository.save(transaction);

        Assertions.assertEquals(transaction,save);
    }

    @Test
    @DisplayName("예약거래 삭제")
    void givenTransaction_whenDelete_thenDeleted() {
        // given
        Long id = 1L;
        String name = "BTC";
        Double price = 3000.0;

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setName(name);
        transaction.setPrice(price);
        transaction.setAmount(10);
        transaction.setType(TransactionType.RESERVE_BUY);
        transaction.setCrypto(
                new Crypto(){{
                    setId(1L);
                }}
        );
        transaction.setUserAccount(
                new UserAccount(){{
                    setId(
                            UUID.randomUUID()
                    );
                }}
        );
        transaction.setStatus(
                TransactionStatus.RESERVED
        );

        Long saved = transactionRedisRepository.save(transaction);

        // when
        Long deleted = transactionRedisRepository.delete(transaction);

        RedisConnection connection = redisConnectionFactory.getConnection();
        Long len = connection.listCommands().lLen(
                (transaction.getName() + ":" + transaction.getType() + ":" + decimalFormat.format(transaction.getPrice())).getBytes()
        );
        // then
        Assertions.assertEquals(0,len);
        Assertions.assertEquals(1,saved);
        Assertions.assertEquals(1,deleted);
    }

    @Test
    @DisplayName("거래 처리하기위해 조회")
    void givenTransaction_whenDelete_thenDelete() {
        // given
        List<Transaction> transactions = new ArrayList<>();

        Transaction transaction1 = new Transaction();
        transaction1.setName("BTC");
        transaction1.setPrice(10000.123);
        transaction1.setAmount(10);
        transaction1.setType(TransactionType.RESERVE_BUY);
        transaction1.setCrypto(
                new Crypto(){{
                    setId(1L);
                }}
        );
        transaction1.setUserAccount(
                new UserAccount(){{
                    setId(
                            UUID.randomUUID()
                    );
                }}
        );
        transaction1.setStatus(
                TransactionStatus.RESERVED
        );

        Transaction transaction2 = new Transaction();
        transaction2.setName("BTC");
        transaction2.setPrice(20000.0);
        transaction2.setAmount(10);
        transaction2.setType(TransactionType.RESERVE_BUY);
        transaction2.setCrypto(
                new Crypto(){{
                    setId(1L);
                }}
        );
        transaction2.setUserAccount(
                new UserAccount(){{
                    setId(
                            UUID.randomUUID()
                    );
                }}
        );
        transaction2.setStatus(
                TransactionStatus.RESERVED
        );

        Transaction transaction3 = new Transaction();
        transaction3.setName("BTC");
        transaction3.setPrice(20000.0);
        transaction3.setAmount(10);
        transaction3.setType(TransactionType.RESERVE_BUY);
        transaction3.setCrypto(
                new Crypto(){{
                    setId(1L);
                }}
        );
        transaction3.setUserAccount(
                new UserAccount(){{
                    setId(
                            UUID.randomUUID()
                    );
                }}
        );
        transaction3.setStatus(
                TransactionStatus.RESERVED
        );

        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);

    }

//    @Test
//    @DisplayName("거래하기 위해 예약거래 조회")
//    void givenNameAndPrice_whenMatching_thenGet() {
//
//    }
}