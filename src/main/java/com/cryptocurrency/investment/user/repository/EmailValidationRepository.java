package com.cryptocurrency.investment.user.repository;

import com.cryptocurrency.investment.user.domain.EmailValidation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailValidationRepository extends JpaRepository<EmailValidation, Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndValidation(String email, String validation);
}