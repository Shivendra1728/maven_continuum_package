package com.di.commons.helper;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

 

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;

 

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

 

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

 

@Component
public class JwtTokenUtil {

 

    
    @Value("${jwt.expirationTime}")
    private long expirationTimeMillis;

    @Value("${jwt.secretKey}")
    private String secretKey;

 

    private Key signingKey;

 


    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        signingKey = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.getJcaName());
    }

 

    public String generateToken(String username) {
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeMillis);

 

        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expirationDate)
                .claim("role", "USER")
                .signWith(signingKey)
                .compact();
    }

 

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

 

        return claims.getSubject();
    }

 

    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

 

        return claims.getExpiration();
    }

 

    public Object getClaimFromToken(String token, String claimName) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

 

        return claims.get(claimName);
    }

 

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}