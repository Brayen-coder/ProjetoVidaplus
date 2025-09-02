package br.com.vidaplus.controller;

import br.com.vidaplus.model.Paciente;
import br.com.vidaplus.repository.PacienteRepository;
import br.com.vidaplus.dto.PacienteResumoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteRepository repo;

    public PacienteController(PacienteRepository repo) {
        this.repo = repo;
    }

    // Criar paciente
    @PostMapping
    public ResponseEntity<Paciente> criar(@RequestBody Paciente p) {
        return ResponseEntity.ok(repo.save(p));
    }

    // Listar todos
    @GetMapping
    public List<Paciente> listar() {
        return repo.findAll();
    }

    // Obter por ID
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obter(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizar(@PathVariable Long id, @RequestBody Paciente dados) {
        return repo.findById(id).map(p -> {
            p.setNome(dados.getNome());
            p.setCpf(dados.getCpf());
            p.setDataNasc(dados.getDataNasc());
            p.setPeso(dados.getPeso());
            p.setAltura(dados.getAltura());
            return ResponseEntity.ok(repo.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Remover
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // histórico de consultas
    @GetMapping("/{id}/resumo")
    public ResponseEntity<PacienteResumoDTO> resumo(@PathVariable Long id) {
        return repo.findById(id)
                .map(p -> ResponseEntity.ok(new PacienteResumoDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }
}
