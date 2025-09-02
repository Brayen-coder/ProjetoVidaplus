package br.com.vidaplus.service;

import br.com.vidaplus.model.RefreshToken;
import br.com.vidaplus.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final long refreshMillis;

    public RefreshTokenService(RefreshTokenRepository repo, @Value("${app.jwt.refreshExpiration}") long refreshMillis){
        this.repo = repo; 
        this.refreshMillis = refreshMillis;
    }

    public RefreshToken create(String username){
        RefreshToken rt = new RefreshToken();
        rt.setUsername(username);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiresAt(Instant.now().plusMillis(refreshMillis));
        return repo.save(rt);
    }

    public Optional<RefreshToken> find(String token){ 
        return repo.findByToken(token); 
    }

    public void revoke(String token){
        repo.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);  // Marca como revogado
            repo.save(rt);
        });
    }

    public void revokeAllTokensForUser(String username) {
        repo.deleteByUsername(username);  // Revoga todos os tokens do usuário
    }

    public void logoutUser(String username, String token){
        repo.deleteByUsername(username);  // Remove todos os refresh tokens associados ao usuário
        repo.findByToken(token).ifPresent(rt -> { 
            rt.setRevoked(true); 
            repo.save(rt); 
        });
    }
}
