package com.cryptocurrency.investment.config.security;

import com.cryptocurrency.investment.auth.filter.JwtAuthenticationFilter;
import com.cryptocurrency.investment.auth.filter.JwtAuthorizationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity(
//        debug = true
) // 스프링 시큐리티 필터가 스프링 필터체인에 등록
@EnableMethodSecurity(
        prePostEnabled = true
)
@NoArgsConstructor
public class SecurityConfig{

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private JwtAuthorizationFilter jwtAuthorizationFilter;
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthenticationFilter,
                          @Lazy JwtAuthorizationFilter jwtAuthorizationFilter,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /**
         * Filter
         */
        http.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        /**
         * CORS
         */
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors().configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setExposedHeaders(Arrays.asList("Authorization"));
                        configuration.setMaxAge(3600L);
                        return configuration;
                    }
                });

        /**
         * CSRF 설정
         */
//        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
//        http.csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers("/crypto/price**","/user/login")
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        http.csrf().disable();

        /**
         * Exception Handler
         */
        http.exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler);

        /**
         * Request Match
         */
        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.GET, "/crypto/price/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user", "/user/login").permitAll()
                        .requestMatchers("/user/email", "/user/username").permitAll()
                        .requestMatchers(HttpMethod.GET, "/crypto/comment").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }
}