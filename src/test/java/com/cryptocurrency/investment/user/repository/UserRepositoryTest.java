package com.cryptocurrency.investment.user.repository;

import com.cryptocurrency.investment.config.JpaConfig;
import com.cryptocurrency.investment.config.RedisConfig;
import com.cryptocurrency.investment.user.domain.Role;
import com.cryptocurrency.investment.user.domain.UserAccount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({RedisConfig.class, JpaConfig.class})
class UserRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private UserRepository userRepository;
    @Test
    void testUserAccountEntity_whenValidUserDetailsProvided_thenReturnsStoredUserDetails() {
        // given
        UserAccount userAccount = new UserAccount();
        UUID id = UUID.randomUUID();
        userAccount.setId(id);
        userAccount.setMoney(0);
        userAccount.setRole(Role.USER);
        userAccount.setDeleted(false);
        userAccount.setPassword("asdasd");
        userAccount.setEmail("test@test.com");
        userAccount.setUsername("test_user");
        userAccount.setJoinDate(LocalDate.now());
        testEntityManager.persistAndFlush(userAccount);

        // when
        Optional<UserAccount> storedUserAccountEntity = userRepository.findById(id);

        // then
        Assertions.assertEquals("test@test.com",storedUserAccountEntity.get().getEmail());
    }
}