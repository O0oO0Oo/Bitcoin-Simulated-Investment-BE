package com.cryptocurrency.investment.wallet.service;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.price.domain.redis.PriceInfoRedis;
import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.transaction.domain.TransactionType;
import com.cryptocurrency.investment.transaction.dto.request.TransactionRequestDto;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.wallet.domain.Wallet;
import com.cryptocurrency.investment.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    public List<Wallet> findWallet(UUID id) {
        return walletRepository.findByUserAccount_Id(id);
    }

    public Optional<Wallet> findWallet(UUID id, String name) {
        return walletRepository.findByUserAccount_IdAndName(id, name);
    }

    public Wallet addWallet(Wallet wallet,
                             UserAccount user,
                             Double price,
                             TransactionRequestDto txDto) {
        if (txDto.type().equals(TransactionType.BUY)) {
            wallet.setTotalCost(
                    wallet.getTotalCost() + price * txDto.amount()
            );
            wallet.setAmount(
                    wallet.getAmount() + txDto.amount()
            );
            user.setMoney(
                    user.getMoney() - price * txDto.amount()
            );
        }

        if (txDto.type().equals(TransactionType.SELL)) {
            wallet.setTotalCost(
                    wallet.getTotalCost() -  price * txDto.amount()
            );
            wallet.setAmount(
                    wallet.getAmount() - txDto.amount()
            );

            user.setMoney(
                    user.getMoney() + price * txDto.amount()
            );
        }

        return walletRepository.save(wallet);
    }
}
