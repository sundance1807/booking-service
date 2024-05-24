package com.booking_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtGenerator {

    public static final long JWT_EXPIRATION = 60000 * 60;
    public static final String JWT_SECRET = "1()n2oxn$1309%82j1!@#r0nra9n1243123";
    private static final Logger log = LoggerFactory.getLogger(JwtGenerator.class);
    private final Algorithm algorithm;

    public JwtGenerator() {
        this.algorithm = Algorithm.HMAC256(JWT_SECRET);
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        long expirationMs = System.currentTimeMillis() + JWT_EXPIRATION;
        Date expireDate = new Date(expirationMs);

        return JWT.create()
                .withSubject(username)
                .withIssuer("booking-service")
                .withExpiresAt(expireDate)
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            Algorithm secret = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT.require(secret).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            Date now = new Date();
            Date expireDate = decodedJWT.getExpiresAt();

            if (expireDate != null && now.before(expireDate)) {
                return true;
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        return false;
    }
}
