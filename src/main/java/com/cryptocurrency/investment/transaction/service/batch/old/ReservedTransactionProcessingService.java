package com.cryptocurrency.investment.transaction.service.batch.old;

import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoInnerDto;
import com.cryptocurrency.investment.transaction.dto.processing.ReservedTransactionJpaDto;
import com.cryptocurrency.investment.transaction.repository.mysql.TransactionMysqlRepository;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.UserRepository;
import com.cryptocurrency.investment.wallet.domain.Wallet;
import com.cryptocurrency.investment.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReservedTransactionProcessingService {

    private final TransactionMysqlRepository transactionMysqlRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;


    /**
     * TODO : 적용 후 상당히 느려졌다. 거래정보가 0.x 초마다 200개가 넘는
     * @param fields
     * @return
     */
    public int reservedTransactionProcessing(ConcurrentHashMap<String, RequestPriceInfoInnerDto> fields) {

        // 처리된 트랜잭션의 수 카운트 -> JobExecutionContext 에 넣기 만약 처리된값들의 갯수가 일치하지 않으면 Retry
        AtomicInteger count = new AtomicInteger(0);

        // -------------------------- Reader -------------------------- //
        List<ReservedTransactionJpaDto> reservedTx = new ArrayList<>();
        ArrayList<String> keys = Collections.list(fields.keys());
        // 이름과 가격이 일치하는 거래목록들 불러옴
        keys.stream().parallel().forEach(
                key -> reservedTx.addAll(
                        transactionMysqlRepository.findByNameAndPriceAndStatusIsReservedForDto(
                                key,
                                Double.parseDouble(fields.get(key).getClosing_price())
                        )
                )
        );
        // -------------------------- Reader -------------------------- //

        ConcurrentHashMap<UUID, Wallet> wallets = new ConcurrentHashMap<>();
        ConcurrentHashMap<UUID, UserAccount> userAccounts = new ConcurrentHashMap<>();

        // -------------------------- Processor -------------------------- //
        // 거래들을 병렬적으로 처리
        reservedTx.stream().parallel().forEach(
                transaction -> {
                    if (transaction.getType().equals("RESERVE_BUY")) {
                        ByteBuffer byteBuffer = ByteBuffer.wrap(transaction.getUser_account_id());
                        UUID id = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
                        Wallet wallet = wallets
                                .computeIfAbsent(id, k ->
                                        walletRepository.
                                                findByUserAccount_IdAndName(
                                                        k,
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
                        count.incrementAndGet();
                    } else if(transaction.getType().equals("RESERVE_SELL")){
                        ByteBuffer byteBuffer = ByteBuffer.wrap(transaction.getUser_account_id());
                        UUID id = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
                        UserAccount userAccount = userAccounts
                                .computeIfAbsent(id, k ->
                                        userRepository.findById(k).get()
                                );

                        synchronized (userAccount){
                            userAccount.setMoney(userAccount.getMoney() + transaction.getAmount() * transaction.getPrice());
                        }
                        count.incrementAndGet();
                    }

                    // 처리된 트랜잭션의 수 증가
                }
        );
        // -------------------------- Processor -------------------------- //


        // -------------------------- Writer -------------------------- //
        CompletableFuture.runAsync(
                () -> walletRepository.saveAll(new ArrayList<>(wallets.values()))
        );
        CompletableFuture.runAsync(
                () -> userRepository.saveAll(new ArrayList<>(userAccounts.values()))
        );
        CompletableFuture.runAsync(
                () -> transactionMysqlRepository.updateStatusByIdIn(reservedTx.stream().map(tx -> tx.getId()).collect(Collectors.toList()))
        );
        // -------------------------- Writer -------------------------- //

        return count.get();
    }
}