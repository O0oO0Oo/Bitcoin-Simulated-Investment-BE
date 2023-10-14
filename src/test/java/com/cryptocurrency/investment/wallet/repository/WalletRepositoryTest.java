package com.cryptocurrency.investment.wallet.repository;

import com.cryptocurrency.investment.config.JpaConfig;
import com.cryptocurrency.investment.config.RedisConfig;
import com.cryptocurrency.investment.wallet.domain.Wallet;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ActiveProfiles("test")
@SpringBootTest
@Import({RedisConfig.class, JpaConfig.class})
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private EntityManager entityManager;

    StopWatch stopWatch;

    @Test
    void findByUserAccount_Id() {
    }
    @Test
    void findByUserAccount_IdAndName() {
    }
    @Test
    void saveAll() {
        List<Wallet> wallets = walletRepository.findAll();
        System.out.println("wallets.size() = " + wallets.size());
        wallets.stream().forEach(item -> {
            item.setTotalCost(item.getTotalCost() + 100);
            item.setAmount(item.getAmount() + 100);
        });

        walletRepository.saveAll(wallets);
        entityManager.flush();
    }

    @Test
    void te() throws InterruptedException {
        Thread.sleep(10000);
    }
}