package br.com.vidaplus.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final Key key;
    private final long expirationMillis;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration}") long expirationMillis) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String username, Set<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMillis);
        return Jwts.builder()
                .setSubject(username)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return getParser().parseClaimsJws(token).getBody().getSubject();
    }

    public Set<String> extractRoles(String token) {
        try {
            Object roles = getParser().parseClaimsJws(token).getBody().get("roles");
            if (roles instanceof List<?>) {
                return ((List<?>) roles).stream().map(Object::toString).collect(Collectors.toSet());
            }
        } catch (JwtException e) {
            // logar e tratar erro de token inválido
            return Collections.emptySet();
        }
        return Collections.emptySet();
    }


    public boolean isValid(String token, String username) {
        String subject = extractUsername(token);
        Date exp = getParser().parseClaimsJws(token).getBody().getExpiration();
        return subject.equals(username) && exp.after(new Date());
    }

    private JwtParser getParser(){
        return Jwts.parserBuilder().setSigningKey(key).build();
    }
}
