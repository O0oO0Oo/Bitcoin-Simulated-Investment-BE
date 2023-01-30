package com.cryptocurrency.investment.config;

import com.cryptocurrency.investment.price.dto.scheduler.PricePerMinuteDto;
import com.cryptocurrency.investment.user.domain.Role;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;

@Configuration
public class InitializationDefaultConfig {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public void initializeAccount(){
        if(!userRepository.existsByEmail("Admin@princoin.com")) {
            userRepository.save(new UserAccount("Admin", "Admin@princoin.com", passwordEncoder.encode("Admin1234!"), Role.ADMIN));
            userRepository.save(new UserAccount("test", "test@test.com", passwordEncoder.encode("test"), Role.USER));
        }
    }

    @Bean
    public PricePerMinuteDto pricePerMinuteDto() {
        PricePerMinuteDto pricePerMinuteDto = new PricePerMinuteDto();
        pricePerMinuteDto.setPriceHashMap(new HashMap<>());
        return pricePerMinuteDto;
    }
}
