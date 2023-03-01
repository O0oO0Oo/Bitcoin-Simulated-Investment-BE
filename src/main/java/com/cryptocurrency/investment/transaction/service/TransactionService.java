package com.cryptocurrency.investment.transaction.service;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.transaction.domain.TransactionStatus;
import com.cryptocurrency.investment.transaction.dto.request.TransactionRequestDto;
import com.cryptocurrency.investment.transaction.dto.response.TransactionListResponseDto;
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
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    /**
     * TODO: Jwt 에 user id를 포함시킨다면 유저에대한 조회쿼리 없이 아래 쿼리만 나가면 된다. 이거 물어볼것
     *     select
     *         t1_0.id,
     *         t1_0.amount,
     *         t1_0.crypto_id,
     *         t1_0.name,
     *         t1_0.price,
     *         t1_0.status,
     *         t1_0.timestamp,
     *         t1_0.type,
     *         t1_0.user_account_id 
     *     from
     *         transaction t1_0 
     *     where
     *         t1_0.user_account_id=?
     * @param authentication
     * @return
     */
    public List<TransactionListResponseDto> findTx(Authentication authentication) {
        UUID id = UUID.fromString(authentication.getName());
        return transactionRepository.findByUserAccount_Id(id)
                .stream()
                .map(
                        tx -> TransactionListResponseDto.of(tx)
                ).collect(Collectors.toList());
    }

    public List<TransactionListResponseDto> findNameTx(Authentication authentication, String name) {
        UUID id = UUID.fromString(authentication.getName());
        return transactionRepository.findByUserAccount_IdAndName(id, name.toUpperCase())
                .stream()
                .map(
                        tx -> TransactionListResponseDto.of(tx)
                ).collect(Collectors.toList());
    }

    public Transaction addTx(
            Crypto crypto,
            UserAccount user,
            Double price,
            TransactionRequestDto txDto) {

        Transaction tx = new Transaction();
        tx.setName(crypto.getName());
        tx.setPrice(price);
        tx.setAmount(txDto.amount());
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setType(txDto.type());

        tx.setUserAccount(user);
        tx.setCrypto(crypto);

        user.getTransactions().add(tx);
        crypto.getTransactions().add(tx);

        return transactionRepository.save(tx);
    }
}
