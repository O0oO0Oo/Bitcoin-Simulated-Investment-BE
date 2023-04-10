package com.cryptocurrency.investment.auth.filter;

import com.cryptocurrency.investment.auth.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

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

            UUID id = UUID.fromString(claims.get("id").toString());
            String authorities = claims.get("authorities").toString();

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            id,
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

        String[] getPaths = {
                "/users/email",
                "/users/username",
                "/cryptos/price",
                "/cryptos/list",
        };

        if(method.equals("GET") && Arrays.stream(getPaths).anyMatch(
                path -> servletPath.startsWith(path)
        )){
            return true;
        }

        String[] getMatchPaths = {
                "/cryptos/[^/]+/list"
        };

        if(method.equals("GET") && Arrays.stream(getMatchPaths).anyMatch(
                path -> servletPath.matches(path)
        )){
            return true;
        }

        String[] postPaths = {
                "/users/login",
                "/users/email",
                "/users"
        };

        if(method.equals("POST") && Arrays.stream(postPaths).anyMatch(
                path -> servletPath.equals(path)
        )){
            return true;
        }

        return false;
    }
}
