package br.com.vidaplus.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private String username;

    private Instant expiresAt;
    private boolean revoked = false;

    public Long getId(){ return id; }
    public String getToken(){ return token; }
    public void setToken(String token){ this.token = token; }
    public String getUsername(){ return username; }
    public void setUsername(String username){ this.username = username; }
    public Instant getExpiresAt(){ return expiresAt; }
    public void setExpiresAt(Instant expiresAt){ this.expiresAt = expiresAt; }
    public boolean isRevoked(){ return revoked; }
    public void setRevoked(boolean revoked){ this.revoked = revoked; }
}
