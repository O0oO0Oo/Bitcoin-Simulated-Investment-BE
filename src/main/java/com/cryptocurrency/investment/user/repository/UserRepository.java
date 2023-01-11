package com.cryptocurrency.investment.user.repository;

import com.cryptocurrency.investment.user.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, String> {
}
