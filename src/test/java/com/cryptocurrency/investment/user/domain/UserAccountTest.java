package com.cryptocurrency.investment.user.domain;

import com.cryptocurrency.investment.config.JpaConfig;
import com.cryptocurrency.investment.config.RedisConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.UUID;

@DataJpaTest
@Import({RedisConfig.class, JpaConfig.class})
class UserAccountTest {
    @Autowired
    private TestEntityManager testEntityManager;
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

        // when
        UserAccount storedUserAccountEntity = testEntityManager.persistAndFlush(userAccount);

        // then
        Assertions.assertEquals(id,storedUserAccountEntity.getId());
    }
}