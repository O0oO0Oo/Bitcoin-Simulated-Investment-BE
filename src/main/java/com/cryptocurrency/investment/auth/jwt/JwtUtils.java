package com.cryptocurrency.investment.auth.jwt;

import com.cryptocurrency.investment.user.domain.UserAccount;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    private static final long EXPIRE_DURATION = 60 * 60 * 1000 * 24; // 1 hour

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(getAccessToken(token));
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("JWT expired", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.error("JWT is invalid", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("JWT is not supported", ex.getMessage());
        } catch (SignatureException ex) {
            LOGGER.error("Signature validation failed");
        }
        return false;
    }

    public String generateAccessToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject("Json Web Token")
                .claim("id", authentication.getName())
                .claim("authorities",
                        authentication.getAuthorities().stream()
                                .map(authority -> "ROLE_" + authority.getAuthority().toString())
                                .collect(Collectors.joining(","))
                )
                .setIssuer("Frin Coin")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String getSubject(String token) {
        return parseClaim(token).getSubject();
    }

    public Claims parseClaim(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(getAccessToken(token))
                .getBody();
    }

    private String getAccessToken(String token) {
        return token.split(" ")[1].trim();
    }
}