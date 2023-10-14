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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservedTransactionService {

    /**
     * TODO : 수만명이 같은가격에 예약, 매초마다 현재가격에 예약한 유저가 있는지 확인, 있다면 수만개의 입력이 일어남 이것을 어떻게 처리할지
     */
    private final TransactionMysqlRepository transactionMysqlRepository;
    private final CryptoRepository cryptoRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    public List<TransactionListResponseDto> findReservedTx(Authentication authentication) {
        return transactionMysqlRepository.findAllReservedTxByUserAccount_Id(UUID.fromString(authentication.getName())).stream()
                .map(tx -> TransactionListResponseDto.of(tx)).collect(Collectors.toList());
    }

    public TransactionResponseDto addReservedBuyTx(Authentication authentication, ReservedTransactionRequestDto reservedTransactionRequestDto) throws Exception {
        UserAccount userAccount = userRepository.findById(UUID.fromString(authentication.getName()))
                .orElseThrow(() -> new EntityNotFoundException("User"));

        if (userAccount.getMoney() < reservedTransactionRequestDto.price() * reservedTransactionRequestDto.amount()) {
            throw new InsufficientFundException("Insufficient money.");
        }

        Crypto crypto = cryptoRepository.findByNameExceptStatus(reservedTransactionRequestDto.name())
                .orElseThrow(() -> new EntityNotFoundException("Crypto"));

        userAccount.setMoney(userAccount.getMoney() - reservedTransactionRequestDto.price() * reservedTransactionRequestDto.amount());

        Transaction transaction = new Transaction();
        transaction.setName(reservedTransactionRequestDto.name());
        transaction.setPrice(reservedTransactionRequestDto.price());
        transaction.setAmount(reservedTransactionRequestDto.amount());
        transaction.setType(TransactionType.RESERVE_BUY);
        transaction.setStatus(TransactionStatus.RESERVED);
        transaction.setUserAccount(userAccount);
        transaction.setCrypto(crypto);

        return TransactionResponseDto.of(
                transactionMysqlRepository.save(transaction),
                userAccount);
    }

    public TransactionResponseDto addReservedSellTx(Authentication authentication, ReservedTransactionRequestDto reservedTransactionRequestDto) {
        UserAccount userAccount = userRepository.findById(UUID.fromString(authentication.getName()))
                .orElseThrow(() -> new EntityNotFoundException("User"));

        Crypto crypto = cryptoRepository.findByNameExceptStatus(reservedTransactionRequestDto.name())
                .orElseThrow(() -> new EntityNotFoundException("Crypto"));

        Wallet wallet = walletRepository.findByUserAccount_IdAndName(UUID.fromString(authentication.getName()), crypto.getName())
                .orElseThrow(() -> new InsufficientAmountException("Insufficient amount."));

        if (wallet.getAmount() < reservedTransactionRequestDto.amount()) {
            throw new InsufficientAmountException("Insufficient amount.");
        }

        wallet.setAmount(wallet.getAmount() - reservedTransactionRequestDto.amount());

        Transaction transaction = new Transaction();
        transaction.setName(reservedTransactionRequestDto.name());
        transaction.setPrice(reservedTransactionRequestDto.price());
        transaction.setAmount(reservedTransactionRequestDto.amount());
        transaction.setType(TransactionType.RESERVE_SELL);
        transaction.setStatus(TransactionStatus.RESERVED);
        transaction.setUserAccount(userAccount);
        transaction.setCrypto(crypto);

        return TransactionResponseDto.of(
                transactionMysqlRepository.save(transaction),
                userAccount);
    }

    public List<TransactionListResponseDto> deleteReservedTx(Authentication authentication, DeleteReservedTransactionRequestDto deleteReservedTransactionRequestDto) {
        UserAccount userAccount = userRepository.findById(UUID.fromString(authentication.getName()))
                .orElseThrow(() -> new EntityNotFoundException("User"));

        List<Transaction> transactions = transactionMysqlRepository.findAllReservedTxByIdsAndUserAccount_Id(
                deleteReservedTransactionRequestDto.ids(),
                userAccount.getId()
        );

        transactions.forEach(
                tx -> {
                    if(tx.getType().equals(TransactionType.RESERVE_BUY)) {
                        userAccount.setMoney(
                                userAccount.getMoney() + tx.getAmount() * tx.getPrice()
                        );
                    }
                    else{
                        Optional<Wallet> walletOpt = walletRepository.findByUserAccount_IdAndName(userAccount.getId(), tx.getName());
                        if (walletOpt.isPresent()) {
                            Wallet wallet = walletOpt.get();
                            wallet.setAmount(wallet.getAmount() + tx.getAmount());
                        }
                    }
                }
        );

        transactionMysqlRepository.deleteAllReservedTxByIdAndUserAccount_Id(
                deleteReservedTransactionRequestDto.ids(),
                userAccount.getId());

        return transactions.stream()
                .map(TransactionListResponseDto::of).collect(Collectors.toList());
    }
}