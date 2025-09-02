package br.com.vidaplus.service;

import br.com.vidaplus.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repo;
    public UserDetailsServiceImpl(UserRepository repo){ this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
