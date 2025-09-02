package br.com.vidaplus.dto;

import java.util.Set;

public class AuthResponse {
    public String token;
    public String username;
    public Set<String> roles;
    public String refreshToken; 


    public AuthResponse(String token, String username, Set<String> roles, String refreshToken){
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.refreshToken = refreshToken;
    }

    // Opcional: construtor  sem refreshToken
    public AuthResponse(String token, String username, Set<String> roles){
        this(token, username, roles, null);
    }
}