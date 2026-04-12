package com.pharmacy.backend.security;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    private final String SECRET_STRING = "SecretStringForPharmacyManagementSystem2026";
    private final Key key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    private final long expiration_time = 86400000;

    // when log in
    public String generateToken(String sdt, String vaitro) {
        Date nowDate = new Date();
        Date expiryDate = new Date(nowDate.getTime() + expiration_time);
        return Jwts.builder()
            .setSubject(sdt)
            .claim("vaitro", vaitro)
            .setIssuedAt(nowDate)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    // when call API
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) { return null; }
    }
}
