package br.com.vidaplus.controller;

import br.com.vidaplus.model.Leito;
import br.com.vidaplus.model.LeitoStatus;
import br.com.vidaplus.model.UserAccount;
import br.com.vidaplus.model.Clinica; // Importe a classe Clinica
import br.com.vidaplus.repository.LeitoRepository;
import br.com.vidaplus.repository.ClinicaRepository;
import br.com.vidaplus.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; // Importe esta anotação

import java.util.*;

@RestController
@RequestMapping("/api/leitos")
public class LeitoController {

    private final LeitoRepository leitos;
    private final ClinicaRepository clinicas;

    public LeitoController(LeitoRepository leitos, ClinicaRepository clinicas) {
        this.leitos = leitos;
        this.clinicas = clinicas;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> criar(@RequestBody Leito leito) {
        UserAccount user = SecurityUtil.currentUser();
        boolean isAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Somente administradores podem criar leitos.");
        }

        Leito novoLeito = leitos.save(leito);

        // Atualiza a contagem total de leitos na clínica
        if (novoLeito.getClinica() != null) {
            Clinica clinica = clinicas.findById(novoLeito.getClinica().getId()).orElse(null);
            if (clinica != null) {
                clinica.setTotalLeitos(clinica.getTotalLeitos() + 1);
                clinicas.save(clinica);
            }
        }
        return ResponseEntity.ok(novoLeito);
    }

    @GetMapping
    public List<Leito> listar() {
        UserAccount user = SecurityUtil.currentUser();
        boolean isPaciente = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"));

        if (isPaciente) {
            // Paciente só pode ver leitos livres
            return leitos.findAll().stream()
                    .filter(l -> LeitoStatus.LIVRE.equals(l.getStatus()))
                    .toList();
        }
        return leitos.findAll();
    }

    @GetMapping("/clinica/{clinicaId}/disponiveis")
    public List<Leito> listarDisponiveis(@PathVariable Long clinicaId) {
        return leitos.findByClinicaIdAndStatus(clinicaId, LeitoStatus.LIVRE);
    }

    @PutMapping("/{id}/ocupar")
    @Transactional
    public ResponseEntity<?> ocupar(@PathVariable Long id) {
        return leitos.findById(id).map(l -> {
            if (LeitoStatus.OCUPADO.equals(l.getStatus())) {
                return ResponseEntity.badRequest().body("Leito já está ocupado.");
            }
            l.setStatus(LeitoStatus.OCUPADO);
            Leito leitoOcupado = leitos.save(l);

            // Atualiza a contagem de leitos ocupados na clínica
            if (leitoOcupado.getClinica() != null) {
                Clinica clinica = clinicas.findById(leitoOcupado.getClinica().getId()).orElse(null);
                if (clinica != null) {
                    clinica.setLeitosOcupados(clinica.getLeitosOcupados() + 1);
                    clinicas.save(clinica);
                }
            }
            return ResponseEntity.ok(leitoOcupado);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/liberar")
    @Transactional
    public ResponseEntity<?> liberar(@PathVariable Long id) {
        return leitos.findById(id).map(l -> {
            l.setStatus(LeitoStatus.LIVRE);
            Leito leitoLiberado = leitos.save(l);

            // Atualiza a contagem de leitos ocupados na clínica
            if (leitoLiberado.getClinica() != null) {
                Clinica clinica = clinicas.findById(leitoLiberado.getClinica().getId()).orElse(null);
                if (clinica != null) {
                    if (clinica.getLeitosOcupados() > 0) {
                        clinica.setLeitosOcupados(clinica.getLeitosOcupados() - 1);
                        clinicas.save(clinica);
                    }
                }
            }
            return ResponseEntity.ok(leitoLiberado);
        }).orElse(ResponseEntity.notFound().build());
    }
}