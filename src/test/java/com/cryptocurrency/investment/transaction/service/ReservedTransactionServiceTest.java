package com.cryptocurrency.investment.transaction.service;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.crypto.repository.CryptoRepository;
import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.transaction.domain.TransactionStatus;
import com.cryptocurrency.investment.transaction.domain.TransactionType;
import com.cryptocurrency.investment.transaction.dto.request.DeleteReservedTransactionRequestDto;
import com.cryptocurrency.investment.transaction.dto.request.ReservedTransactionRequestDto;
import com.cryptocurrency.investment.transaction.dto.response.TransactionListResponseDto;
import com.cryptocurrency.investment.transaction.dto.response.TransactionResponseDto;
import com.cryptocurrency.investment.transaction.exception.EntityNotFoundException;
import com.cryptocurrency.investment.transaction.exception.InsufficientAmountException;
import com.cryptocurrency.investment.transaction.exception.InsufficientFundException;
import com.cryptocurrency.investment.transaction.repository.mysql.TransactionMysqlRepository;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.UserRepository;
import com.cryptocurrency.investment.wallet.domain.Wallet;
import com.cryptocurrency.investment.wallet.repository.WalletRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ReservedTransactionServiceTest {

    /**
     * 예약 거래 기능 종류
     * - 조회
     * - 등록 - 구매
     * - 등록 - 판매
     * - 삭제

     * 예역 거래 조회 성공

     * 예약 거래 구매 등록 성공
     * 예약 거래 구매 등록 실패
     * - 유효하지 않은 유저
     * - 잔고 부족
     * - 유효하지 않은 코인 이름

     * 예약 거래 판매 등록 성공
     * 예약 거래 판매 등록 실패
     * - 유효하지 않은 유저
     * - 보유 코인 부족
     * - 유효하지 않은 코인 이름

     * 예약 거래 삭제 성공
     * 예약 거래 삭제 실패
     * - 유효하지 않은 거래
     */

    // 테스트 유저의 UUID
    private UUID id = UUID.fromString("0bf81996-e28f-338a-bd55-c540176329c5");
    @Mock
    private TransactionMysqlRepository transactionMysqlRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CryptoRepository cryptoRepository;
    @Mock
    private WalletRepository walletRepository;
    @InjectMocks
    private ReservedTransactionService reservedTransactionService;

    /**
     * 예역 거래 조회 성공
     */
    @Test
    @DisplayName("예약 거래 기능 - 조회 - 성공")
    void Given_UserIsAuthenticated_When_FindReservedTransactionList_Then_ReturnReservedTransactionDto() {
        // given
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(id, "password");

        // when
        Mockito.when(transactionMysqlRepository.findAllReservedTxByUserAccount_Id(id))
                .thenReturn(Collections.emptyList());
        List<TransactionListResponseDto> reservedTx = reservedTransactionService.findReservedTx(authentication);

        // then
        Assertions.assertThat(reservedTx).isEqualTo(Collections.emptyList());
    }

    /**
     * 예약 거래 구매 등록 성공
     * 예약 거래 구매 등록 실패
     * - 유요하지 않은 유저
     * - 잔고 부족
     * - 유효하지 않은 코인 이름
     */
    @Test
    @DisplayName("예약 거래 기능 - 구매 등록 - 성공")
    void Given_UserIsAuthenticatedAndReservedTransactionDto_When_AddReservedBuyTransaction_Then_AddReservedTransaction() throws Exception {
        // given
        ReservedTransactionRequestDto requestDto =
                ReservedTransactionRequestDto.of(
                        "BBQ",
                        1000,
                        1);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(id, "password");

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("test");
        userAccount.setEmail("test@frincoin.com");
        userAccount.setId(id);
        userAccount.setMoney(1000);

        Crypto crypto = new Crypto();
        crypto.setId(4000L);
        crypto.setName("BBQ");

        Transaction transaction = new Transaction();
        transaction.setName("BBQ");
        transaction.setPrice(1000);
        transaction.setAmount(1);
        transaction.setType(TransactionType.RESERVE_BUY);
        transaction.setStatus(TransactionStatus.RESERVED);
        transaction.setUserAccount(userAccount);
        transaction.setCrypto(crypto);

        TransactionResponseDto responseDto = TransactionResponseDto.of(
                transaction,
                userAccount
        );
        // when
        Mockito.when(userRepository.findById(UUID.fromString(authentication.getName())))
                .thenReturn(Optional.of(userAccount));
        Mockito.when(cryptoRepository.findByNameExceptStatus("BBQ"))
                .thenReturn(Optional.of(crypto));
        Mockito.when(transactionMysqlRepository.save(Mockito.any(Transaction.class)))
                .thenReturn(transaction);

        TransactionResponseDto returnDto = reservedTransactionService.addReservedBuyTx(authentication, requestDto);

        // then
        Assertions.assertThat(returnDto.name()).isEqualTo(responseDto.name());
        Assertions.assertThat(returnDto.amount()).isEqualTo(responseDto.amount());
        Assertions.assertThat(returnDto.price()).isEqualTo(responseDto.price());
        Assertions.assertThat(returnDto.type()).isEqualTo(responseDto.type());
        Assertions.assertThat(returnDto.timestamp()).isEqualTo(responseDto.timestamp());
        Assertions.assertThat(returnDto.money()).isZero();
    }

    // JWT 는 유요하지만 탈퇴한 유저일 경우 유요하지 않은 유저
    @Test
    @DisplayName("에약 거래 기능 - 구매 등록 - 실패 - 유효하지 않은 유저")
    void Given_UserIsAuthenticatedAndReservedTransactionDtd_When_AddReservedBuyTransactionAndUserNotFound_Then_ThrowException() {
        // given
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        UUID.nameUUIDFromBytes("DELETED_ID".getBytes()),
                        "password");
        ReservedTransactionRequestDto requestDto =
                ReservedTransactionRequestDto.of(
                        "BBQ",
                        1000,
                        1);

        // when
        Mockito.when(userRepository.findById(UUID.fromString(authentication.getName())))
                .thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> {
            reservedTransactionService.addReservedBuyTx(authentication, requestDto);
        });
    }

    @Test
    @DisplayName("예약 거래 기능 - 구매 등록 - 실패 - 잔고 부족")
    void Given_UserIsAuthenticatedAndReservedTransactionDto_When_AddReservedBuyTransactionAndInsufficientMoney_Then_ThrowException() {
        // given
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        id,
                        "password");
        ReservedTransactionRequestDto requestDto =
                ReservedTransactionRequestDto.of(
                        "BBQ",
                        1,
                        1);
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("test");
        userAccount.setMoney(0);

        // when
        Mockito.when(userRepository.findById(UUID.fromString(authentication.getName())))
                .thenReturn(Optional.of(userAccount));

        // then
        assertThrows(InsufficientFundException.class, () -> {
                    reservedTransactionService.addReservedBuyTx(authentication, requestDto);
                }
        );
    }

    @Test
    @DisplayName("에약 거래 기능 - 구매 등록 - 실패 - 유효하지 않은 코인 이름")
    void Given_UserIdAuthenticatedAndReservedTransactionDto_When_AddReservedBuyTransactionAndCryptoNotFound_Then_ThrowException() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        UUID.nameUUIDFromBytes("test".getBytes()),
                        "password");
        ReservedTransactionRequestDto requestDto =
                ReservedTransactionRequestDto.of(
                        "BBQ",
                        1000,
                        1);
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("test");
        userAccount.setMoney(1000);

        // when
        Mockito.when(userRepository.findById(UUID.fromString(authentication.getName())))
                .thenReturn(Optional.of(userAccount));
        Mockito.when(cryptoRepository.findByNameExceptStatus(requestDto.name()))
                .thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> {
            reservedTransactionService.addReservedBuyTx(authentication, requestDto);
        });
    }

    /**
     * 예약 거래 판매 등록 성공
     * 예약 거래 판매 등록 실패
     * - 유요하지 않은 유저
     * - 보유 코인 부족
     * - 유효하지 않은 코인 이름
     */
    @Test
    @DisplayName("에약 거래 기능 - 판매 등록 - 성공")
    void Given_UserIsAuthenticatedAndReservedTransactionDtd_When_AddReservedSellTransaction_Then_AddReservedTransaction() {
        // given
        ReservedTransactionRequestDto requestDto =
                ReservedTransactionRequestDto.of(
                        "BBQ",
                        1000,
                        1);
        Authentication authentication = new UsernamePasswordAuthenticationToken(id, "password");

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("test");
        userAccount.setEmail("test@frincoin.com");
        userAccount.setId(id);

        Crypto crypto = new Crypto();
        crypto.setId(4000L);
        crypto.setName("BBQ");

        Wallet wallet = new Wallet();
        wallet.setUserAccount(userAccount);
        wallet.setCrypto(crypto);
        wallet.setName("BBQ");
        wallet.setAmount(1);

        Transaction transaction = new Transaction();
        transaction.setName("BBQ");
        transaction.setPrice(1000);
        transaction.setAmount(1);
        transaction.setType(TransactionType.RESERVE_SELL);
        transaction.setStatus(TransactionStatus.RESERVED);
        transaction.setUserAccount(userAccount);
        transaction.setCrypto(crypto);

        TransactionResponseDto responseDto = TransactionResponseDto.of(
                transaction,
                userAccount
        );

        // when
        Mockito.when(userRepository.findById(UUID.fromString(authentication.getName())))
                .thenReturn(Optional.of(userAccount));
        Mockito.when(cryptoRepository.findByNameExceptStatus(requestDto.name()))
                .thenReturn(Optional.of(crypto));
        Mockito.when(walletRepository.findByUserAccount_IdAndName(id, "BBQ"))
                .thenReturn(Optional.of(wallet));
        Mockito.when(transactionMysqlRepository.save(Mockito.any(Transaction.class)))
                .thenReturn(transaction);

        TransactionResponseDto returnDto = reservedTransactionService.addReservedSellTx(authentication, requestDto);

        // then
        Assertions.assertThat(returnDto.name()).isEqualTo(responseDto.name());
        Assertions.assertThat(returnDto.price()).isEqualTo(responseDto.price());
        Assertions.assertThat(returnDto.amount()).isEqualTo(responseDto.amount());
        Assertions.assertThat(returnDto.type()).isEqualTo(responseDto.type());
        Assertions.assertThat(returnDto.timestamp()).isEqualTo(responseDto.timestamp());
        Assertions.assertThat(returnDto.money()).isEqualTo(responseDto.money());
    }

    @Test
    @DisplayName("에약 거래 기능 - 판매 등록 - 실패 - 유효하지 않은 유저")
    void Given_UserIsAuthenticatedAndReservedTransactionDtd_When_AddReservedSellTransactionAndUserNotFound_Then_ThrowException() {
        // given
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        UUID.nameUUIDFromBytes("DELETED_ID".getBytes()),
                        "password");
        ReservedTransactionRequestDto requestDto =
                ReservedTransactionRequestDto.of(
                        "BBQ",
                        1000,
                        1);

        // when
        Mockito.when(userRepository.findById(UUID.fromString(authentication.getName())))
                .thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> {
            reservedTransactionService.addReservedSellTx(authentication, requestDto);
        });
    }

    @Test
    @DisplayName("에약 거래 기능 - 구매 등록 - 실패 - 유효하지 않은 코인 이름")
    void Given_UserIdAuthenticatedAndReservedTransactionDto_When_AddReservedSellTransactionAndCryptoNotFound_Then_ThrowException() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        UUID.nameUUIDFromBytes("test".getBytes()),
                        "password");
        ReservedTransactionRequestDto requestDto =
                ReservedTransactionRequestDto.of(
                        "BBQ",
                        1000,
                        1);
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("test");
        userAccount.setMoney(1000);

        // when
        Mockito.when(userRepository.findById(UUID.fromString(authentication.getName())))
                .thenReturn(Optional.of(userAccount));
        Mockito.when(cryptoRepository.findByNameExceptStatus(requestDto.name()))
                .thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> {
            reservedTransactionService.addReservedSellTx(authentication, requestDto);
        });
    }


    @Test
    @DisplayName("예약 거래 기능 - 판매 등록 - 실패 - 보유 코인 부족")
    void Given_UserIsAuthenticatedAndReservedTransactionDto_When_AddReservedBuyTransactionAndInsufficientAmount_Then_ThrowException() {
        // given
        ReservedTransactionRequestDto requestDto =
                ReservedTransactionRequestDto.of(
                        "BBQ",
                        1000,
                        2);
        Authentication authentication = new UsernamePasswordAuthenticationToken(id, "password");

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername("test");
        userAccount.setEmail("test@frincoin.com");
        userAccount.setId(id);

        Crypto crypto = new Crypto();
        crypto.setId(4000L);
        crypto.setName("BBQ");

        Wallet wallet = new Wallet();
        wallet.setUserAccount(userAccount);
        wallet.setCrypto(crypto);
        wallet.setName("BBQ");
        wallet.setAmount(1);

        // when
        Mockito.when(userRepository.findById(UUID.fromString(authentication.getName())))
                .thenReturn(Optional.of(userAccount));
        Mockito.when(cryptoRepository.findByNameExceptStatus(requestDto.name()))
                .thenReturn(Optional.of(crypto));
        Mockito.when(walletRepository.findByUserAccount_IdAndName(id, "BBQ"))
                .thenReturn(Optional.of(wallet));

        // then
        assertThrows(InsufficientAmountException.class, () -> {
                    reservedTransactionService.addReservedSellTx(authentication, requestDto);
                }
        );
    }

    /**
     * 예약 거래 삭제 성공
     * 예약 거래 삭제 실패
     * - 유요하지 않은 유저
     */
    @Test
    @DisplayName("예약 거래 기능 - 삭제 - 성공")
    void Given_UserIdAuthenticatedAndDeleteReservedTransactionDto_When_DeleteReservedTx_Then_ReturnReservedTransactionDto() {
        // given
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        id,
                        "password");
        UserAccount userAccount = new UserAccount();
        userAccount.setId(id);
        userAccount.setMoney(0);

        List<Long> deleteTxList = new ArrayList<>();
        DeleteReservedTransactionRequestDto deleteReservedTransactionRequestDto = DeleteReservedTransactionRequestDto.of(deleteTxList);

        // when
        Mockito.when(userRepository.findById(UUID.fromString(authentication.getName())))
                .thenReturn(Optional.of(userAccount));
        Mockito.when(transactionMysqlRepository.findAllReservedTxByIdsAndUserAccount_Id(
                deleteReservedTransactionRequestDto.ids(),
                id
        )).thenReturn(Collections.emptyList());
        Mockito.when(transactionMysqlRepository.deleteAllReservedTxByIdAndUserAccount_Id(
                deleteTxList, id
        )).thenReturn(deleteTxList.size());
        List<TransactionListResponseDto> deletedReservedTx = reservedTransactionService.deleteReservedTx(authentication, deleteReservedTransactionRequestDto);

        // then
        Assertions.assertThat(deletedReservedTx.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("예약 거래 기능 - 삭제 - 실패 - 유효하지 않은 유저")
    void Given_UserIdAuthenticatedAndDeleteReservedTransactionDto_When_DeleteTxReservedTxAndUserNotFound_Then_ThrowException() {
        // given
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        UUID.nameUUIDFromBytes("DELETED_ID".getBytes()),
                        "password");

        DeleteReservedTransactionRequestDto deleteReservedTransactionRequestDto = DeleteReservedTransactionRequestDto.of(
                Collections.emptyList()
        );

        // when
        Mockito.when(userRepository.findById(UUID.fromString(authentication.getName())))
                .thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> {
            reservedTransactionService.deleteReservedTx(authentication, deleteReservedTransactionRequestDto);
        });
    }
}