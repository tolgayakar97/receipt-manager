package com.tolgayakar.receipt_manager.Service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${secret.key}") // read secret.key from environment
    private String secretKey;

    public String generateJwt(UserDetails userDetails) {
        // Creation time and expiration time are needed.
        Date current = new Date();
        Date expire = new Date(current.getTime() + 600000); // Expires after 10 mins (as ms);

        // JJWT needeed to use Jwts
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(current)
                .expiration(expire)
                .signWith(getSecretKey()) // Needs secret key as SecretKey. 
                .compact();
    }

    private SecretKey getSecretKey() {
        // Generate SecretKey object from given string
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
