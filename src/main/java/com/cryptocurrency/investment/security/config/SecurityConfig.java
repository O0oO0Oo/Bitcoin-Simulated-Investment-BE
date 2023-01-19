package com.cryptocurrency.investment.security.config;

import com.cryptocurrency.investment.security.jwt.JwtAuthenticationFilter;
import com.cryptocurrency.investment.security.service.AuthenticationUserDetailsService;
import com.cryptocurrency.investment.user.domain.Role;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록
@EnableMethodSecurity(
        prePostEnabled = true
)
@RequiredArgsConstructor
public class SecurityConfig{

    private final AuthenticationUserDetailsService authenticationUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    /**
     * @return PasswordEncode
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * DaoAuthenticationProvider
     * @return
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();


        authenticationProvider.setUserDetailsService(authenticationUserDetailsService);

        /**
         * Set Password Encoder
         */
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /**
         * JwtAuthTokenFilter 추가
         */
        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        /**
         * 인증 프로세스중 오류 발생시 401 반환
         */
        http.exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, authException) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    authException.getMessage()
                            );
                        }
                );

        /**
         * Url 매칭
         */
        http.cors().and().csrf().disable();
        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.GET, "/api/crypto/*/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user", "/user/login").permitAll()
                        .requestMatchers("/admin/*").hasRole(Role.ADMIN.toString())
                        .anyRequest().authenticated()
                )
                .logout(LogoutConfigurer::permitAll);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        /**
         * DaoAuthenticationProvider 세팅
         */
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}