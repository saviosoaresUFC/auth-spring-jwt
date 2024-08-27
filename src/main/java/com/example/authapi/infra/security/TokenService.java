package com.example.authapi.infra.security;

import com.example.authapi.domain.user.LoginAttempt;
import com.example.authapi.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class TokenService {
    private static final String ISSUER = "auth-api"; // Emissor do token

    public final ConcurrentMap<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 7; // Numero maximo de tentativas de login
    private static final long LOCK_TIME_DURATION = 15; // 15 minutos

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
//            Implementation of JWT token generation
            Algorithm algorithm = Algorithm.HMAC256(secret);
//            The token is generated with the issuer[emissor], the subject (user login),
//            the expiration date and the encryption algorithm
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getEmail())
                    .withExpiresAt(getExpirationTokenDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new TokenGenerationException(
                    "Error generating token: ", exception
            );
        }
    }

    public String generateRefreshToken(User user) {
        try {
//            Implementation of JWT token generation
            Algorithm algorithm = Algorithm.HMAC256(secret);
//            The token is generated with the issuer[emissor], the subject (user login),
//            the expiration date and the encryption algorithm
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getEmail())
                    .withExpiresAt(getExpirationTokenRefreshDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new TokenGenerationException(
                    "Error generating token: ", exception
            );
        }
    }

    public String getNameFromToken(String token) {
        try {
            return JWT.decode(token).getSubject();
        } catch (JWTVerificationException exception) {
            return new TokenGenerationException(
                    "Error decoding token: ", exception
            ).getMessage();
        }
    }

    //    Metodo para validar o token
//    Retorna o login do User se o token for valido
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return new TokenGenerationException(
                    "Error validating token: ", exception
            ).getMessage();
        }
    }

    private Date getExpirationTokenDate() {
//        Implementacao da data de expiracao do token
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        return calendar.getTime();
    }

    private Date getExpirationTokenRefreshDate() {
//        Implementacao da data de expiracao do token
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 168);
        return calendar.getTime();
    }

    public boolean isAccountLocked(String email) {
        LoginAttempt attempt = loginAttempts.get(email);
        if (attempt == null) {
            return false;
        }

        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            Duration lockDuration = Duration.between(attempt.getLastAttemptTime(), LocalDateTime.now());
            if (lockDuration.toMinutes() < LOCK_TIME_DURATION) {
                return true;
            } else {
                // Resetar tentativas após o período de bloqueio expirar
                loginAttempts.remove(email);
                return false;
            }
        }
        return false;
    }

    public void registerFailedAttempt(String email) {
        LoginAttempt attempts = loginAttempts.get(email);
        if (attempts == null) {
            attempts = new LoginAttempt(1, LocalDateTime.now());
        } else {
            attempts.incrementAttempts();
            attempts.setLastAttemptTime(LocalDateTime.now());
        }
        loginAttempts.put(email, attempts);
    }

    // Classe de excecao especifiva para erros de geracao de tokens
    private static class TokenGenerationException extends RuntimeException {
        public TokenGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
