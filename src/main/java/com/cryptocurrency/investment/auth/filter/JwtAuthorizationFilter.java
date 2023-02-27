package com.cryptocurrency.investment.auth.filter;

import com.cryptocurrency.investment.auth.jwt.JwtUtils;
import com.cryptocurrency.investment.config.security.CustomAccessDeniedHandler;
import com.cryptocurrency.investment.user.domain.UserAccount;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@NoArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private JwtUtils jwtUtils;
    private AuthenticationManager authenticationManager;

    @Autowired
    public JwtAuthorizationFilter(JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    /**
     * TODO : 잘못된 트큰, AccessDeniedHandler 처리하기
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && jwtUtils.validateAccessToken(token)) {
            Claims claims = jwtUtils.parseClaim(token);

            String email = claims.get("email").toString();
            String authorities = claims.get("authorities").toString();

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
                    );
            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String servletPath = request.getServletPath();
        String method = request.getMethod();

        if (servletPath.equals("/user") && method.equals("POST")) {
            return true;
        }

        if ((servletPath.equals("/user/username") || servletPath.equals("/user/email"))
                && method.equals("GET")) {
            return true;
        }

        String[] paths = {"/crypto/price","/user/login"};
        return Arrays.stream(paths).anyMatch(
                path -> request.getServletPath().startsWith(path)
        );
    }
}
