package com.cryptocurrency.investment.config;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.crypto.domain.CryptoStatus;
import com.cryptocurrency.investment.crypto.repository.CryptoRepository;
import com.cryptocurrency.investment.price.dto.scheduler.PricePerMinuteDto;
import com.cryptocurrency.investment.user.domain.Role;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class InitializationDefaultConfig {
    private final UserRepository userRepository;
    private final CryptoRepository cryptoRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public void initializeDB(){
        // 기본 유저 등록
        if(!userRepository.existsByEmail("Admin@frincoin.com")) {
            UserAccount admin = new UserAccount();
            UserAccount test = new UserAccount();

            admin.setEmail("Admin@frincoin.com");
            admin.setUsername("Admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN);
            admin.setJoinDate(LocalDate.now());
            admin.setDeleted(false);

            test.setEmail("test@frincoin.com");
            test.setUsername("test");
            test.setPassword(passwordEncoder.encode("test"));
            test.setRole(Role.USER);
            test.setJoinDate(LocalDate.now());
            test.setDeleted(false);

            userRepository.save(admin);
            userRepository.save(test);
        }
        
        // 기본 코인 리스트
        if (cryptoRepository.count() == 0) {
            try {
                ClassPathResource resource = new ClassPathResource("data/BaseCryptoList.txt");
                cryptoRepository.saveAll(
                        Files.readAllLines(
                                Paths.get(resource.getURI())
                                )
                                .stream()
                                .map(
                                        name -> {
                                            return new Crypto(name, CryptoStatus.NORMAL);
                                        }
                                ).collect(Collectors.toList())
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Bean
    public PricePerMinuteDto pricePerMinuteDto() {
        PricePerMinuteDto pricePerMinuteDto = new PricePerMinuteDto();
        pricePerMinuteDto.setPriceHashMap(new HashMap<>());
        return pricePerMinuteDto;
    }
}
