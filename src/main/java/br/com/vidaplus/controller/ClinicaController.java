package br.com.vidaplus.controller;

import br.com.vidaplus.model.Clinica;
import br.com.vidaplus.repository.ClinicaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/clinicas")
public class ClinicaController {
    private final ClinicaRepository repo;
    public ClinicaController(ClinicaRepository repo){ this.repo = repo; }

    @PostMapping
    public ResponseEntity<Clinica> criar(@RequestBody Clinica c){ return ResponseEntity.ok(repo.save(c)); }

    @GetMapping
    public List<Clinica> listar(){ return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Clinica> obter(@PathVariable Long id){
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Clinica> atualizar(@PathVariable Long id, @RequestBody Clinica dados){
        return repo.findById(id).map(c -> {
            c.setNome(dados.getNome());
            c.setCnpj(dados.getCnpj());
            c.setEndereco(dados.getEndereco());
            c.setTotalLeitos(dados.getTotalLeitos());
            c.setLeitosOcupados(dados.getLeitosOcupados());
            return ResponseEntity.ok(repo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id){
        if(repo.existsById(id)){ repo.deleteById(id); return ResponseEntity.noContent().build(); }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/leitos/total/{total}")
    public ResponseEntity<?> definirTotalLeitos(@PathVariable Long id, @PathVariable Integer total){
        return repo.findById(id).map(c -> {
            c.setTotalLeitos(total);
            if(c.getLeitosOcupados() > total) c.setLeitosOcupados(total);
            return ResponseEntity.ok(repo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/leitos/ocupar/{qtd}")
    public ResponseEntity<?> ocuparLeitos(@PathVariable Long id, @PathVariable Integer qtd){
        return repo.findById(id).map(c -> {
            int novo = Math.min(c.getTotalLeitos(), c.getLeitosOcupados() + qtd);
            c.setLeitosOcupados(novo);
            return ResponseEntity.ok(repo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/leitos/liberar/{qtd}")
    public ResponseEntity<?> liberarLeitos(@PathVariable Long id, @PathVariable Integer qtd){
        return repo.findById(id).map(c -> {
            int novo = Math.max(0, c.getLeitosOcupados() - qtd);
            c.setLeitosOcupados(novo);
            return ResponseEntity.ok(repo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/leitos/status")
    public ResponseEntity<?> statusLeitos(@PathVariable Long id){
        return repo.findById(id).<ResponseEntity<?>>map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("clinicaId", c.getId());
            m.put("nome", c.getNome());
            m.put("totalLeitos", c.getTotalLeitos());
            m.put("leitosOcupados", c.getLeitosOcupados());
            m.put("leitosDisponiveis", c.getTotalLeitos() - c.getLeitosOcupados());
            return ResponseEntity.ok(m);
        }).orElse(ResponseEntity.notFound().build());
    }
}
