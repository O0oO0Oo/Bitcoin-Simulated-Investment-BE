package com.cryptocurrency.investment.transaction.service.performance;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.crypto.repository.CryptoRepository;
import com.cryptocurrency.investment.price.repository.redis.PriceInfoRedisRepository;
import com.cryptocurrency.investment.transaction.dto.processing.ReservedTransactionJpaDto;
import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.transaction.domain.TransactionStatus;
import com.cryptocurrency.investment.transaction.domain.TransactionType;
import com.cryptocurrency.investment.transaction.repository.mysql.TransactionMysqlRepository;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.UserRepository;
import com.cryptocurrency.investment.wallet.domain.Wallet;
import com.cryptocurrency.investment.wallet.repository.WalletRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * DB 간 속도 비교
 */
@SpringBootTest
@ActiveProfiles("test")
class ReservedTransactionProcessing_Mysql_PerformanceTest {
    
    @Autowired
    private TransactionMysqlRepository transactionMysqlRepository;
    @Autowired
    private PriceInfoRedisRepository priceInfoRedisRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private CryptoRepository cryptoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    private static List<UserAccount> userAccounts;
    private static List<Transaction> transactions;

    // 시간 측정
    private StopWatch getTransactionStopWatch = new StopWatch();
    private StopWatch processingStopWatch = new StopWatch();
    private StopWatch reservedTransactionSaveStopWatch = new StopWatch();
    private StopWatch updateTransactionStopWatch = new StopWatch();
    private StopWatch updateWalletStopWatch = new StopWatch();
    @BeforeAll
    static void beforeAll() throws IOException {
        ClassPathResource resource = new ClassPathResource("test_user_data_1.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        String line;
        userAccounts = new ArrayList<>();
        transactions = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            String[] split = line.split(",");
            UserAccount userAccount = new UserAccount();
            userAccount.setId(UUID.nameUUIDFromBytes(split[0].getBytes()));
            userAccount.setPassword("1234");
            userAccount.setEmail(split[0]);
            userAccount.setUsername(split[1]);

            userAccounts.add(userAccount);

            Crypto crypto = new Crypto();
            crypto.setId(38L);
            crypto.setName("BTC");

            for (int i = 1; i < 21; i++) {

                Transaction transaction = new Transaction();
                transaction.setUserAccount(userAccount);
                transaction.setAmount(1);
                transaction.setPrice(i * 1000.0);
                transaction.setName(crypto.getName());
                transaction.setCrypto(crypto);
                transaction.setStatus(TransactionStatus.RESERVED);
                transaction.setType(TransactionType.RESERVE_BUY);

                userAccount.getTransactions().add(transaction);
                transactions.add(transaction);
            }
        }
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("Before Each " + System.currentTimeMillis());
        userRepository.saveAll(userAccounts);
        // 유저들에게 BTC 지갑 만들어줌
        List<Wallet> wallets = new ArrayList<>();
        userAccounts.stream().parallel().forEach(user -> {
            Wallet wallet = new Wallet();
            Crypto crypto = new Crypto();
            crypto.setId(38L);
            crypto.setName("BTC");

            wallet.setCrypto(crypto);
            wallet.setUserAccount(user);
            wallet.setName(crypto.getName());
            wallets.add(wallet);
        });
        walletRepository.saveAll(wallets);

        reservedTransactionSaveStopWatch.start();
        transactionMysqlRepository.saveAll(transactions);
        reservedTransactionSaveStopWatch.stop();
        System.out.println("start " + System.currentTimeMillis());
        processingStopWatch.start();
    }

    @AfterEach
    void afterEach() throws InterruptedException {
        processingStopWatch.stop();
        System.out.println("예약 거래 20000건 저장 시간 : " + reservedTransactionSaveStopWatch.getTotalTimeMillis());
        System.out.println("예약 거래 20000건 Transaction 불러오기 시간 : " + (getTransactionStopWatch.getTotalTimeMillis()));
        System.out.println("예약 거래에 관련된 20000건 Wallet / Transaction 업데이트 시간 : " + updateWalletStopWatch.getTotalTimeMillis() + " / " + updateTransactionStopWatch.getTotalTimeMillis());
        // 5000 은 250 * 20 인 대기시간
        System.out.println("예약 거래 20000건 처리 시간 : " + (processingStopWatch.getTotalTimeMillis()-5000));
        System.out.println("예약 거래 1000건 처리 평균 시간 : " + (processingStopWatch.getTotalTimeMillis() - 5000) / 20);
        Thread.sleep(5000);
    }

    /**
     * 테스트 H2DB 기준
     * - 0.25초 마다 가격이 1000 -> 20000 으로 1000 씩 오름
     * - 1000명의 유저들이 1000 ~ 20000 사이의 가격으로 BTC, RESERVE_BUY Transaction 을 20개씩 신청해놓은 상태, 총 20000건
     * - 0.25 초마다 BTC 코인의 가격을 받아옴 (0.25초 마다 빗썸에 요청해서 새로운 값을 받아오기 때문에)
     * - 가격이 일치하는 Reserved Transaction 을 처리, 유저 Wallet 에 추가
     * <p>
     * - Redis 에서 BTC 의 실시간 데이터를 받아온다
     * - MySQL 에서 BTC 의 가격과 일치하는 RESERVE_SELL, RESERVE_BUY Transaction 이 있다면 실행한다.
     * <p>
     * 거래정보 20000건 저장 시간           5000 ~ 10000
     * <p>
     * 1000건 당 평균 처리 시간
     * - 초기                            1401 ~ 1567
     * 1. Stream Parallel               864 ~ 1112  
     * 2. 트랙잰션을 모아서 처리
     *      - 5000건                     562 ~ 724
     *      - 10000건                    512 ~ 617 
     *      - 20000건                    454 ~ 581
     *      - Wallet, Transaction 의 업데이트 시간 성능이 개선됨.
     * 3. 필요한 컬럼만 받기                228 ~ 368 
     *      - Wallet, Transaction 의 업데이트 시간 성능이 개선됨.
     * 4. WHERE 조건문 재정렬             유의미한 결과 ?
     * 5. Index 설정                     유의미한 결과 ?
     * 6. 저장 비동기 처리                  100 ~ 126 
     *      - Wallet, Transaction 의 업데이트 시간이 비동기로 처리되면서 대기 시간이 없어져 시간은 줄어들었지만 성능은?
     * 7. batch insert/update 설정
     *      - 오히려 느려지거나 비슷했다. 왜 ?
     */
    @Test
    @DisplayName("Mysql - 10000건에 대한 저장속도 batch insert/update 속도 비교")
    void mysql_saveAll_test() throws InterruptedException {
        Double price = 1000.0;
        for(int i = 0;i < 1;i++) {

            List<ReservedTransactionJpaDto> btc = new ArrayList<>();
            int num = 0;
            while (num < 4) {
                // 해당 가격에 맞는 예약된 거래들을 가져온다.
                getTransactionStopWatch.start();
                btc.addAll(transactionMysqlRepository.findByNameAndPriceAndStatusIsReservedForDto("BTC", price));
                getTransactionStopWatch.stop();

                // 0.25 초마다 새로운 가격을 받아옴
                Thread.sleep(250);
                num++;
                price += 1000.0;
            }

            // 거래들을 병렬적으로 처리
            ConcurrentHashMap<UUID, Wallet> wallets = new ConcurrentHashMap<>();

            // 거래들을 병렬적으로 처리
            btc.stream().parallel().forEach(
                    transaction -> {
                        ByteBuffer byteBuffer = ByteBuffer.wrap(transaction.getUser_account_id());
                        UUID id = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
                        Wallet wallet = wallets
                                .computeIfAbsent(id, k ->
                                        walletRepository.
                                                findByUserAccount_IdAndName(
                                                        id,
                                                        transaction.getName()
                                                ).orElse(
                                                        // wallet 을 미리 만들어도 batch insert / update 시 입력이 안될수있음
                                                        null
                                                )
                                );

                        synchronized (wallet) {
                            wallet.setAmount(wallet.getAmount() + transaction.getAmount());
                            wallet.setTotalCost(wallet.getTotalCost() + transaction.getAmount() * transaction.getPrice());
                        }
                    }
            );
            List<Wallet> walletList = new ArrayList<>(wallets.values());

            updateWalletStopWatch.start();
            walletRepository.saveAll(walletList);
            updateWalletStopWatch.stop();

            updateTransactionStopWatch.start();
            transactionMysqlRepository.updateStatusByIdIn(btc.stream().map(tx -> tx.getId()).collect(Collectors.toList()));
            updateTransactionStopWatch.stop();
        }
    }


    @Test
    @DisplayName("Mysql - 병렬 + 벌크(4000건) + 필요한 컬럼 save, update + 비동기 처리(Save, Update 쿼리)")
    void mysql_async_test() throws InterruptedException {
        Double price = 1000.0;
        for(int i = 0;i < 5;i++) {

            List<ReservedTransactionJpaDto> btc = new ArrayList<>();
            int num = 0;
            while (num < 4) {
                // 해당 가격에 맞는 예약된 거래들을 가져온다.
                getTransactionStopWatch.start();
                btc.addAll(transactionMysqlRepository.findByNameAndPriceAndStatusIsReservedForDto("BTC", price));
                getTransactionStopWatch.stop();

                // 0.25 초마다 새로운 가격을 받아옴
                Thread.sleep(250);
                num++;
                price += 1000.0;
            }

            // 거래들을 병렬적으로 처리
            ConcurrentHashMap<UUID, Wallet> wallets = new ConcurrentHashMap<>();

            // 거래들을 병렬적으로 처리
            btc.stream().parallel().forEach(
                    transaction -> {
                        ByteBuffer byteBuffer = ByteBuffer.wrap(transaction.getUser_account_id());
                        UUID id = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
                        Wallet wallet = wallets
                                .computeIfAbsent(id, k ->
                                        walletRepository.
                                                findByUserAccount_IdAndName(
                                                        id,
                                                        transaction.getName()
                                                )
                                                .orElse(
                                                        null
                                                )
                                );

                        synchronized (wallet) {
                            wallet.setAmount(wallet.getAmount() + transaction.getAmount());
                            wallet.setTotalCost(wallet.getTotalCost() + transaction.getAmount() * transaction.getPrice());
                        }
                    }
            );
            List<Wallet> walletList = new ArrayList<>(wallets.values());

            updateWalletStopWatch.start();
            CompletableFuture.runAsync(
                    () -> walletRepository.saveAll(walletList)
            );
            updateWalletStopWatch.stop();

            updateTransactionStopWatch.start();
            CompletableFuture.runAsync(
                    () -> transactionMysqlRepository.updateStatusByIdIn(btc.stream().map(tx -> tx.getId()).collect(Collectors.toList()))
            );
            updateTransactionStopWatch.stop();
        }
    }

    @Test
    @DisplayName("Mysql - 병렬 + 벌크(10000건) + 필요한 컬럼만 받기, 업데이트")
    void mysql_dto_test() throws InterruptedException {
        Double price = 1000.0;
        for(int i = 0;i < 2;i++) {

            List<ReservedTransactionJpaDto> btc = new ArrayList<>();
            int num = 0;
            while (num < 10) {
                // 가격 데이터를 가져온다
                RedisConnection connection = redisConnectionFactory.getConnection();
                Set<byte[]> bytes = connection.zSetCommands().zRevRange("price:BTC".getBytes(), 0, 0);
                connection.close();

                // 해당 가격에 맞는 예약된 거래들을 가져온다.
                getTransactionStopWatch.start();
                btc.addAll(transactionMysqlRepository.findByNameAndPriceAndStatusIsReservedForDto("BTC", price));
                getTransactionStopWatch.stop();

                // 0.25 초마다 새로운 가격을 저장하므로
                Thread.sleep(250);
                num++;
                price += 1000.0;
            }

            // 거래들을 병렬적으로 처리
            ConcurrentHashMap<UUID, Wallet> wallets = new ConcurrentHashMap<>();

            // 거래들을 병렬적으로 처리
            btc.stream().parallel().forEach(
                    transaction -> {
                        ByteBuffer byteBuffer = ByteBuffer.wrap(transaction.getUser_account_id());
                        UUID id = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
                        Wallet wallet = wallets
                                .computeIfAbsent(id, k ->
                                        walletRepository.
                                                findByUserAccount_IdAndName(
                                                        id,
                                                        transaction.getName()
                                                )
                                                .orElse(
                                                        null
                                                )
                                );

                        synchronized (wallet) {
                            wallet.setAmount(wallet.getAmount() + transaction.getAmount());
                            wallet.setTotalCost(wallet.getTotalCost() + transaction.getAmount() * transaction.getPrice());
                        }
                    }
            );
            List<Wallet> walletList = new ArrayList<>(wallets.values());

            updateWalletStopWatch.start();
            walletRepository.saveAll(walletList);
            updateWalletStopWatch.stop();

            updateTransactionStopWatch.start();
            transactionMysqlRepository.updateStatusByIdIn(btc.stream().map(tx -> tx.getId()).collect(Collectors.toList()));
            updateTransactionStopWatch.stop();
        }
    }

    @Test
    @DisplayName("Mysql - 병렬 + 10000건씩 모아서 처리")
    void mysql_bulk_test() throws InterruptedException {
        Double price = 1000.0;
        for(int i = 0;i < 2;i++) {

            List<Transaction> btc = new ArrayList<>();
            int num = 0;
            while (num < 10) {
                // 가격 데이터를 가져온다
                RedisConnection connection = redisConnectionFactory.getConnection();
                Set<byte[]> bytes = connection.zSetCommands().zRevRange("price:BTC".getBytes(), 0, 0);
                connection.close();

                // 해당 가격에 맞는 예약된 거래들을 가져온다.
                getTransactionStopWatch.start();
                btc.addAll(transactionMysqlRepository.findByNameAndPriceAndStatusIsReserved("BTC", price));
                getTransactionStopWatch.stop();

                // 0.25 초마다 새로운 가격을 저장하므로
                Thread.sleep(250);
                num++;
                price += 1000.0;
            }

            ConcurrentHashMap<UUID, Wallet> wallets = new ConcurrentHashMap<>();

            // 거래들을 병렬적으로 처리
            btc.stream().parallel().forEach(transaction -> {
                        transaction.setStatus(TransactionStatus.COMPLETED);
                        UUID id = transaction.getUserAccount().getId();

                        Wallet wallet = wallets
                                .computeIfAbsent(id, k ->
                                        walletRepository.
                                                findByUserAccount_IdAndName(
                                                        id,
                                                        transaction.getName()
                                                )
                                                .orElse(
                                                        null
                                                )
                                );
                        synchronized (wallet) {
                            wallet.setAmount(wallet.getAmount() + transaction.getAmount());
                            wallet.setTotalCost(wallet.getTotalCost() + transaction.getAmount() * transaction.getPrice());
                        }
                    }
            );
            List<Wallet> walletList = new ArrayList<>(wallets.values());

            updateWalletStopWatch.start();
            walletRepository.saveAll(walletList);
            updateWalletStopWatch.stop();

            updateTransactionStopWatch.start();
            transactionMysqlRepository.saveAll(btc);
            updateTransactionStopWatch.stop();
            }
    }

    @Test
    @DisplayName("Mysql - 병렬 실행")
    void mysql_parallel_test() throws InterruptedException {
        int num = 0;
        Double price = 1000.0;
        while (num < 20) {
            // 가격 데이터를 가져온다
            RedisConnection connection = redisConnectionFactory.getConnection();
            Set<byte[]> bytes = connection.zSetCommands().zRevRange("price:BTC".getBytes(), 0, 0);
            connection.close();

            // 해당 가격에 맞는 예약된 거래들을 가져온다.
            getTransactionStopWatch.start();
            List<Transaction> btc = transactionMysqlRepository.findByNameAndPriceAndStatusIsReserved("BTC", price);
            getTransactionStopWatch.stop();

            List<Wallet> wallets = Collections.synchronizedList(new ArrayList<>());

            // 거래들을 병렬적으로 처리
            btc.stream().parallel().forEach(transaction -> {
                        transaction.setStatus(TransactionStatus.COMPLETED);
                        UUID id = transaction.getUserAccount().getId();

                        Optional<Wallet> walletOpt = walletRepository.findByUserAccount_IdAndName(id, transaction.getName());

                        Wallet wallet;
                        if (walletOpt.isPresent()) {
                            wallet = walletOpt.get();
                            synchronized (wallet) {
                                wallet.setAmount(wallet.getAmount() + transaction.getAmount());
                                wallet.setTotalCost(wallet.getTotalCost() + transaction.getAmount() * transaction.getPrice());
                            }
                            wallets.add(wallet);
                        }
                    }
            );

            updateWalletStopWatch.start();
            walletRepository.saveAll(wallets);
            updateWalletStopWatch.stop();
            updateTransactionStopWatch.start();
            transactionMysqlRepository.saveAll(btc);
            updateTransactionStopWatch.stop();

            price += 1000.0;
            // 0.25 초마다 새로운 가격을 저장하므로
            Thread.sleep(250);
            num++;
        }
    }
    @Test
    @DisplayName("Mysql - 초기")
    void mysql_test() throws InterruptedException {
        int num = 0;
        Double price = 1000.0;
        while (num < 20) {
            // 가격 데이터를 가져온다
            RedisConnection connection = redisConnectionFactory.getConnection();
            Set<byte[]> bytes = connection.zSetCommands().zRevRange("price:BTC".getBytes(), 0, 0);
            connection.close();

            // 해당 가격에 맞는 예약된 거래들을 가져온다.
            getTransactionStopWatch.start();
            List<Transaction> btc = transactionMysqlRepository.findByNameAndPriceAndStatusIsReserved("BTC", price);
            getTransactionStopWatch.stop();

            List<Wallet> wallets = Collections.synchronizedList(new ArrayList<>());

            // 거래 순차적
            btc.stream().forEach(transaction -> {
                        transaction.setStatus(TransactionStatus.COMPLETED);
                        UUID id = transaction.getUserAccount().getId();

                        Optional<Wallet> walletOpt = walletRepository.findByUserAccount_IdAndName(id, transaction.getName());

                        Wallet wallet;
                        if (walletOpt.isPresent()) {
                            wallet = walletOpt.get();
                            wallet.setAmount(wallet.getAmount() + transaction.getAmount());
                            wallet.setTotalCost(wallet.getTotalCost() + transaction.getAmount() * transaction.getPrice());
                            wallets.add(wallet);
                        }
                    }
            );

            updateWalletStopWatch.start();
            walletRepository.saveAll(wallets);
            updateWalletStopWatch.stop();

            updateTransactionStopWatch.start();
            transactionMysqlRepository.saveAll(btc);
            updateTransactionStopWatch.stop();

            price += 1000.0;
            // 0.25 초마다 새로운 가격을 저장하므로
            Thread.sleep(250);
            num++;
        }
    }
}