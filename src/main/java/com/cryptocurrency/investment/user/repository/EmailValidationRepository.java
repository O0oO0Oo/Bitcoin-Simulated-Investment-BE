package com.cryptocurrency.investment.user.repository;

import com.cryptocurrency.investment.user.domain.EmailValidation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmailValidationRepository extends JpaRepository<EmailValidation, Long> {

    boolean existsByEmailAndCode(String email, Integer code);

    @Query(value =
            "SELECT :timeout - (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(timestamp)) " +
                    "FROM email_validation " +
                    "WHERE email = :email", nativeQuery = true)
    Integer findByEmailSent(@Param("email") String email, @Param("timeout") int timeout);

    @Modifying
    @Transactional
    @Query(value =
            "UPDATE email_validation ev " +
                    "SET ev.timestamp = NOW(), ev.code = :code " +
                    "WHERE ev.email = :email", nativeQuery = true)
    int updateByEmail(@Param("email") String email, @Param("code") Integer code);

    @Modifying
    @Transactional
    int deleteByEmail(String email);
}