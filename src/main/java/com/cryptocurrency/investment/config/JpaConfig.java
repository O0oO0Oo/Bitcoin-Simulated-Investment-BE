package com.cryptocurrency.investment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.cryptocurrency.investment.price.repository.mysql",
                "com.cryptocurrency.investment.user.repository",
                "com.cryptocurrency.investment.crypto.repository"
        })
public class JpaConfig {
}