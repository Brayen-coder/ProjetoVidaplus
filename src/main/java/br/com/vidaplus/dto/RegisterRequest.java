package br.com.vidaplus.dto;

import java.util.Set;
import br.com.vidaplus.model.RoleName;

public class RegisterRequest {
    public String username;
    public String password;
    public Set<RoleName> roles;
    public Long pacienteId;
    public Long medicoId;
}
