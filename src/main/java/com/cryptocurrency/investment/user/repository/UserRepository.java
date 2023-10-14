package com.cryptocurrency.investment.user.repository;

import com.cryptocurrency.investment.user.domain.UserAccount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByUsername(String name);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<UserAccount> findById(UUID uuid);

    @Modifying
    @Transactional
    @Query(value =
            "UPDATE user_account ua " +
                    "SET ua.username = :username, ua.password = :password " +
                    "WHERE ua.id = :id", nativeQuery = true)
    int updateUser(@Param("id") UUID id,
                   @Param("username") String username,
                   @Param("password") String password);

    @Modifying
    @Transactional
    int deleteById(UUID id);
}
