package com.example.authapi.infra.security;

import com.example.authapi.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.util.Calendar;
import java.util.Date;

@Service
public class TokenService {
    private static final String ISSUER = "auth-api"; // Emissor do token

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
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new TokenGenerationException(
                    "Error generating token: ", exception
            );
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

    private Date getExpirationDate() {
//        Implementacao da data de expiracao do token
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 2);
        return calendar.getTime();
    }
}

// Classe de excecao especifiva para erros de geracao de tokens
class TokenGenerationException extends RuntimeException {
    public TokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
