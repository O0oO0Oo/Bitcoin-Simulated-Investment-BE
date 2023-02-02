package com.cryptocurrency.investment.user.repository;

import com.cryptocurrency.investment.user.domain.UserAccount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);

    Optional<UserAccount> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query(value =
            "UPDATE user_account ua" +
                    "SET ua.username = :username, ua.password = :password " +
                    "WHERE ua.email = :email", nativeQuery = true)
    int updateUser(@Param("email") String email,
                   @Param("username") String username,
                   @Param("password") String password);

    @Modifying
    @Transactional
    int deleteByEmail(String email);
}
