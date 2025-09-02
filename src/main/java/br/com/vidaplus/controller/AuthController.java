package br.com.vidaplus.controller;
import br.com.vidaplus.dto.*;
import br.com.vidaplus.model.*;
import br.com.vidaplus.repository.*;
import br.com.vidaplus.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository users;
    private final PacienteRepository pacientes;
    private final MedicoRepository medicos;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokens;

    public AuthController(UserRepository users, PacienteRepository pacientes, MedicoRepository medicos,
                          PasswordEncoder encoder, AuthenticationManager authManager, JwtService jwtService,
                          RefreshTokenService refreshTokens){
        this.users = users; this.pacientes = pacientes; this.medicos = medicos;
        this.encoder = encoder; this.authManager = authManager; this.jwtService = jwtService;
        this.refreshTokens = refreshTokens;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req){
        if(users.existsByUsername(req.username)){
            return ResponseEntity.badRequest().body("Usuário já existe");
        }
        if(req.roles == null || req.roles.isEmpty()){
            req.roles = Set.of(RoleName.ROLE_USER);
        }
        UserAccount u = new UserAccount();
        u.setUsername(req.username);
        u.setPassword(encoder.encode(req.password));
        u.setRoles(req.roles);

        if(req.pacienteId != null){
            var p = pacientes.findById(req.pacienteId).orElse(null);
            if(p == null) return ResponseEntity.badRequest().body("PacienteId inválido");
            u.setPaciente(p);
        }
        if(req.medicoId != null){
            var m = medicos.findById(req.medicoId).orElse(null);
            if(m == null) return ResponseEntity.badRequest().body("MedicoId inválido");
            u.setMedico(m);
        }

        users.save(u);
        return ResponseEntity.ok("Registrado com sucesso");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req){
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username, req.password));
        UserAccount user = (UserAccount) auth.getPrincipal();
        String token = jwtService.generateToken(user.getUsername(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        var rt = refreshTokens.create(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()), rt.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenRequest request){
        String refreshToken = request.getRefreshToken();
        var opt = refreshTokens.find(refreshToken);
        
        // Verifica se o refresh token é inválido ou revogado
        if (opt.isEmpty() || opt.get().isRevoked() || opt.get().getExpiresAt().isBefore(java.time.Instant.now())) {
            return ResponseEntity.status(401).body("Refresh token inválido ou expirado");
        }

        String username = opt.get().getUsername();
        var user = users.findByUsername(username).orElse(null);
        
        if (user == null) {
            return ResponseEntity.status(401).body("Usuário não encontrado");
        }

        // Gera um novo access token
        String token = jwtService.generateToken(user.getUsername(), 
                    user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));

        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), 
                    user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()), refreshToken));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody TokenRequest request){
        String refreshToken = request.getRefreshToken();

        // Revoga o refresh token no banco de dados
        refreshTokens.revoke(refreshToken);
        
        // Revoga todos os refresh tokens do usuário (opcional)
        String username = jwtService.extractUsername(refreshToken);
        refreshTokens.revokeAllTokensForUser(username);

        return ResponseEntity.ok("Logout efetuado com sucesso.");
    }

}