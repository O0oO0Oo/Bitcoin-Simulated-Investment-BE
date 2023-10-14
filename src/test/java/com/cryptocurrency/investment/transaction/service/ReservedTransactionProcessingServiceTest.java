package com.cryptocurrency.investment.transaction.service;

import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoDto;
import com.cryptocurrency.investment.price.util.HttpJsonRequest;
import com.cryptocurrency.investment.transaction.dto.processing.ReservedTransactionJpaDto;
import com.cryptocurrency.investment.transaction.repository.mysql.TransactionMysqlRepository;
import com.cryptocurrency.investment.transaction.service.batch.old.ReservedTransactionProcessingService;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.UserRepository;
import com.cryptocurrency.investment.wallet.domain.Wallet;
import com.cryptocurrency.investment.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ReservedTransactionProcessingServiceTest {

    // 테스트 유저의 UUID
    private UUID id = UUID.fromString("0bf81996-e28f-338a-bd55-c540176329c5");
    @Mock
    private TransactionMysqlRepository transactionMysqlRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private HttpJsonRequest httpJsonRequest;
    @InjectMocks
    private ReservedTransactionProcessingService reservedTransactionProcessingService;
    /**
     * 예약된 거래 처리 기능
     *
     * 예약된 거래 처리 성공
     * - 구매 성공
     * - 판매 성공
     * 예약된 거래 처리 실패
     * - ? 어떤 경우 ?
     * - 처음 검색했던 현재 가격과 일치하는 예약된 트랙잭션의 수 != 실제 처리된 트랜잭션의 수
     * - AtomicInteger 를 사용해서 카운트를 하지만 어떻게 처리?
     * TODO : 스프링 배치 반복 실패
     */

    @Test
    @DisplayName("예약된 거래 처리 기능 - 구매 - 성공")
    void Given_RequestPriceInfoDto_When_ProcessReservedTxBuy_Then_ProcessReservedTx() throws IOException {
        // given
        RequestPriceInfoDto readValue = httpJsonRequest.sendRequest("https://api.bithumb.com/public/ticker/ALL", RequestPriceInfoDto.class);
        List<ReservedTransactionJpaDto> transactions = new ArrayList<>();
        transactions.add(new ReservedTransactionJpaDto() {
            @Override
            public Long getId() {
                return -1L;
            }

            @Override
            public Double getPrice() {
                return 1000.0;
            }

            @Override
            public Double getAmount() {
                return 1000.0;
            }

            @Override
            public String getName() {
                return "Test";
            }

            @Override
            public byte[] getUser_account_id() {
                return UUID.randomUUID().toString().getBytes();
            }

            @Override
            public String getType() {
                return "RESERVE_BUY";
            }
        });
        Wallet wallet = new Wallet();

        // when
        Mockito.when(transactionMysqlRepository.findByNameAndPriceAndStatusIsReservedForDto(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyDouble()))
                .thenReturn(transactions);
        Mockito.when(walletRepository.findByUserAccount_IdAndName(
                ArgumentMatchers.any(UUID.class),
                ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(wallet));
        Mockito.when(walletRepository.saveAll(List.of(wallet)))
                .thenReturn(List.of(wallet));
        Mockito.doNothing().when(transactionMysqlRepository).updateStatusByIdIn(
                transactions.stream().map(tx -> tx.getId()).collect(Collectors.toList())
        );
        reservedTransactionProcessingService.reservedTransactionProcessing(readValue.getFields());

        // then
    }

    @Test
    @DisplayName("예약된 거래 처리 기능 - 판매 - 성공")
    void Given_RequestPriceInfoDto_When_ProcessReservedTxSell_Then_ProcessReservedTx() throws IOException {
        // given
        RequestPriceInfoDto readValue = httpJsonRequest.sendRequest("https://api.bithumb.com/public/ticker/ALL", RequestPriceInfoDto.class);
        List<ReservedTransactionJpaDto> transactions = new ArrayList<>();
        transactions.add(new ReservedTransactionJpaDto() {
            @Override
            public Long getId() {
                return -1L;
            }

            @Override
            public Double getPrice() {
                return 1000.0;
            }

            @Override
            public Double getAmount() {
                return 1000.0;
            }

            @Override
            public String getName() {
                return "Test";
            }

            @Override
            public byte[] getUser_account_id() {
                return UUID.randomUUID().toString().getBytes();
            }

            @Override
            public String getType() {
                return "RESERVE_SELL";
            }
        });
        Wallet wallet = new Wallet();
        UserAccount userAccount = new UserAccount();
        // when
        Mockito.when(transactionMysqlRepository.findByNameAndPriceAndStatusIsReservedForDto(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyDouble()))
                .thenReturn(transactions);
        Mockito.when(userRepository.findById(
                        ArgumentMatchers.any(UUID.class)))
                .thenReturn(Optional.of(userAccount));
        Mockito.when(userRepository.saveAll(List.of(userAccount)))
                .thenReturn(List.of(userAccount));
        Mockito.doNothing().when(transactionMysqlRepository).updateStatusByIdIn(
                transactions.stream().map(tx -> tx.getId()).collect(Collectors.toList())
        );
        int ret = reservedTransactionProcessingService.reservedTransactionProcessing(readValue.getFields());

        // then
        Assertions.assertEquals(ret,0);
    }

    @Test
    void runAsync_Test() {
        CompletableFuture.runAsync(() -> {
            System.out.println("out : " + Thread.currentThread().getName() + " " + runAsync_Method_Test());
            CompletableFuture.runAsync(() -> {
                System.out.println("inner 1 : " + Thread.currentThread().getName() + " " + runAsync_Method_Test());
                CompletableFuture.runAsync(() -> {
                    System.out.println("inner 1 inner 1 : " + Thread.currentThread().getName() + " " + runAsync_Method_Test());
                });
            });
            CompletableFuture.runAsync(() -> {
                System.out.println("inner 2 : " + Thread.currentThread().getName() + " " + runAsync_Method_Test());
            });
        });
    }

    int runAsync_Method_Test() {
        int a = 0;
        for (int i = 0; i < 100000; i++) {
            a++;
        }
        return a;
    }
}