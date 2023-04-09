package com.cryptocurrency.investment.transaction.service;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.crypto.repository.CryptoRepository;
import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.transaction.domain.TransactionStatus;
import com.cryptocurrency.investment.transaction.dto.request.DeleteReservedTransactionDto;
import com.cryptocurrency.investment.transaction.dto.request.ReservedTransactionRequestDto;
import com.cryptocurrency.investment.transaction.dto.response.TransactionListResponseDto;
import com.cryptocurrency.investment.transaction.dto.response.TransactionResponseDto;
import com.cryptocurrency.investment.transaction.exception.EntityNotFoundException;
import com.cryptocurrency.investment.transaction.exception.InsufficientFundException;
import com.cryptocurrency.investment.transaction.repository.TransactionRepository;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservedTransactionService {

    /**
     * TODO : 수만명이 같은가격에 예약, 매초마다 현재가격에 예약한 유저가 있는지 확인, 있다면 수만개의 입력이 일어남 이것을 어떻게 처리할지
     */
    private final TransactionRepository transactionRepository;
    private final CryptoRepository cryptoRepository;
    private final UserRepository userRepository;
    public List<TransactionListResponseDto> findReservedTx(Authentication authentication) {
        return transactionRepository.findAllReservedTxByUserAccount_Id(UUID.fromString(authentication.getName())).stream()
                .map(TransactionListResponseDto::of).collect(Collectors.toList());
    }

    public TransactionResponseDto addReservedTx(Authentication authentication, ReservedTransactionRequestDto reservedTransactionRequestDto) throws Exception {
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
        transaction.setType(reservedTransactionRequestDto.type());
        transaction.setStatus(TransactionStatus.RESERVED);
        transaction.setUserAccount(userAccount);
        transaction.setCrypto(crypto);

        return TransactionResponseDto.of(
                transactionRepository.save(transaction),
                userAccount);
    }

    public List<TransactionListResponseDto> deleteReservedTx(Authentication authentication, DeleteReservedTransactionDto deleteReservedTransactionDto) {
        UserAccount userAccount = userRepository.findById(UUID.fromString(authentication.getName()))
                .orElseThrow(() -> new EntityNotFoundException("User"));

        List<Transaction> allReservedTxByIdsAndUserAccountId = transactionRepository.findAllReservedTxByIdsAndUserAccount_Id(
                deleteReservedTransactionDto.id(),
                userAccount.getId()
        );
        allReservedTxByIdsAndUserAccountId.forEach(
                tx -> userAccount.setMoney(
                        userAccount.getMoney() + tx.getAmount() * tx.getPrice()
                )
        );

        transactionRepository.deleteAllReservedTxByIdAndUserAccount_Id(
                deleteReservedTransactionDto.id(),
                userAccount.getId());

        return allReservedTxByIdsAndUserAccountId.stream()
                .map(TransactionListResponseDto::of).collect(Collectors.toList());
    }
}