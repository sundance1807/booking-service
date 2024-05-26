package com.booking_service.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    private static final String ISSUER = "booking-service";

    @Value("${spring.security.jwt.expirationHours}")
    private Long jwtExpirationHours;
    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;
    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    public void init() {
        try {
            this.algorithm = Algorithm.HMAC256(jwtSecret);
            this.verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("Error initializing JWT algorithm: {}", e.getMessage());
        }
    }

    public String generateToken(Authentication authentication) {
        Instant expirationTime = Instant.now().plus(jwtExpirationHours, ChronoUnit.HOURS);
        Date expirationDate = Date.from(expirationTime);
        String username = authentication.getName();

        return JWT.create()
                .withSubject(username)
                .withIssuer(ISSUER)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getExpiresAt().after(new Date());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }
}
