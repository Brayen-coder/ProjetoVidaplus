package br.com.vidaplus.model;

import jakarta.persistence.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class UserAccount implements UserDetails {
	private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<RoleName> roles = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente; // vínculo opcional

    @OneToOne
    @JoinColumn(name = "medico_id")
    private Medico medico; // vínculo opcional

    public UserAccount(){}

    public UserAccount(String username, String password, Set<RoleName> roles){
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    @Override public String getUsername(){ return username; }
    public void setUsername(String username){ this.username = username; }

    @Override public String getPassword(){ return password; }
    public void setPassword(String password){ this.password = password; }

    public Set<RoleName> getRoles(){ return roles; }
    public void setRoles(Set<RoleName> roles){ this.roles = roles; }

    public Paciente getPaciente(){ return paciente; }
    public void setPaciente(Paciente paciente){ this.paciente = paciente; }

    public Medico getMedico(){ return medico; }
    public void setMedico(Medico medico){ this.medico = medico; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities(){
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.name())).collect(Collectors.toSet());
    }

    @Override public boolean isAccountNonExpired(){ return true; }
    @Override public boolean isAccountNonLocked(){ return true; }
    @Override public boolean isCredentialsNonExpired(){ return true; }
    @Override public boolean isEnabled(){ return true; }
}
