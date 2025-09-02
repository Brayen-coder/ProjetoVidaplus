package br.com.vidaplus.controller;

import br.com.vidaplus.model.Medico;
import br.com.vidaplus.repository.MedicoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {
    private final MedicoRepository repo;
    public MedicoController(MedicoRepository repo){ this.repo = repo; }

    @PostMapping
    public ResponseEntity<Medico> criar(@RequestBody Medico m){ return ResponseEntity.ok(repo.save(m)); }

    @GetMapping
    public List<Medico> listar(){ return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Medico> obter(@PathVariable Long id){
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medico> atualizar(@PathVariable Long id, @RequestBody Medico dados){
        return repo.findById(id).map(m -> {
            m.setNome(dados.getNome());
            m.setCrm(dados.getCrm());
            m.setEspecialidade(dados.getEspecialidade());
            return ResponseEntity.ok(repo.save(m));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id){
        if(repo.existsById(id)){ repo.deleteById(id); return ResponseEntity.noContent().build(); }
        return ResponseEntity.notFound().build();
    }
}
