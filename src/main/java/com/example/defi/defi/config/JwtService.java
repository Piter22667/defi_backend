package com.example.defi.defi.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRET_KEY = "9a47d2e54a7f08359c1bfc52a8643beaa2c1d5f40860726e92e2f8e8ac107915";

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }


    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }


    private Claims extractAllClaims(String jwt) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        //Jwts.parser() створює парсер для розбору JWT-токена
        //.setSigningKey(getSigningKey()) — встановлює ключ для перевірки підпису токена
        //.parseClaimsJws(jwt) — розбирає токен і повертає об'єкт Jws<Claims>, який містить заголовок, підпис і claims
        //.getBody() — отримує тіло токена (claims) з об'єкта Jws<Claims>, яке містить інформацію про користувача та інші дані, закодовані в токені
    }


    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 60))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    } //Метод generateTokens() генерує токен, використовуючи порожню карту claims і деталі користувача.



    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUsername(jwt);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }


    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    } // метод, який отримує ключ для підпису токена.
    // (ключ для перевірки підпису токена)
}
