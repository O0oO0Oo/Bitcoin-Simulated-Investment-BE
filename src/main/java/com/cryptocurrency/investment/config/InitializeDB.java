package com.cryptocurrency.investment.config;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.crypto.domain.CryptoStatus;
import com.cryptocurrency.investment.crypto.repository.CryptoRepository;
import com.cryptocurrency.investment.user.domain.Role;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InitializeDB implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CryptoRepository cryptoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(!userRepository.existsByEmail("Admin@frincoin.com")) {
            UserAccount admin = new UserAccount();
            UserAccount test = new UserAccount();

            admin.setEmail("Admin@frincoin.com");
            admin.setUsername("Admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setId(
                    UUID.nameUUIDFromBytes(admin.getEmail().getBytes())
            );
            admin.setRole(Role.ADMIN);

            test.setEmail("test@frincoin.com");
            test.setUsername("test");
            test.setPassword(passwordEncoder.encode("test"));
            test.setId(
                    UUID.nameUUIDFromBytes(test.getEmail().getBytes())
            );

            userRepository.save(admin);
            userRepository.save(test);
        }

        // 기본 코인 리스트
        if (cryptoRepository.count() == 0) {
            try {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/BaseCryptoList.txt");
                List<String> lines = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines().collect(Collectors.toList());
                cryptoRepository.saveAll(
                        lines.stream()
                                .map(name -> new Crypto(name, CryptoStatus.NORMAL))
                                .collect(Collectors.toList())
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
