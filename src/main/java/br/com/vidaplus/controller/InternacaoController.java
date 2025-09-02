package br.com.vidaplus.controller;

import br.com.vidaplus.model.*;
import br.com.vidaplus.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional; 

import java.util.Optional;

@RestController
@RequestMapping("/api/internacoes")
public class InternacaoController {

    private final InternacaoRepository internacaoRepository;
    private final PacienteRepository pacienteRepository;
    private final ClinicaRepository clinicaRepository;
    private final LeitoRepository leitoRepository;

    public InternacaoController(InternacaoRepository internacaoRepository,
                                PacienteRepository pacienteRepository,
                                ClinicaRepository clinicaRepository,
                                LeitoRepository leitoRepository) {
        this.internacaoRepository = internacaoRepository;
        this.pacienteRepository = pacienteRepository;
        this.clinicaRepository = clinicaRepository;
        this.leitoRepository = leitoRepository;
    }

    // Internar paciente
    @PostMapping("/internar/{pacienteId}/{clinicaId}/{leitoId}")
    @Transactional
    public ResponseEntity<?> internar(@PathVariable Long pacienteId,
                                      @PathVariable Long clinicaId,
                                      @PathVariable Long leitoId) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
        Optional<Clinica> clinicaOpt = clinicaRepository.findById(clinicaId);
        Optional<Leito> leitoOpt = leitoRepository.findById(leitoId);

        if (pacienteOpt.isEmpty() || clinicaOpt.isEmpty() || leitoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Paciente, clínica ou leito inválidos");
        }

        Leito leito = leitoOpt.get();
        if (LeitoStatus.OCUPADO.equals(leito.getStatus())) {
            return ResponseEntity.badRequest().body("Leito já está ocupado");
        }

        Internacao internacao = new Internacao();
        internacao.setPaciente(pacienteOpt.get());
        internacao.setClinica(clinicaOpt.get());
        internacao.setLeito(leito);

        // Atualiza o status do leito e salva a internação
        leito.setStatus(LeitoStatus.OCUPADO);
        leitoRepository.save(leito);
        Internacao novaInternacao = internacaoRepository.save(internacao);

        // Atualiza a contagem de leitos ocupados na clínica
        Clinica clinica = clinicaOpt.get();
        clinica.setLeitosOcupados(clinica.getLeitosOcupados() + 1);
        clinicaRepository.save(clinica);

        return ResponseEntity.ok(novaInternacao);
    }

    // Dar alta ao paciente
    @PutMapping("/alta/{internacaoId}")
    @Transactional
    public ResponseEntity<?> darAlta(@PathVariable Long internacaoId) {
        Optional<Internacao> internacaoOpt = internacaoRepository.findById(internacaoId);

        if (internacaoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Internação não encontrada");
        }

        Internacao internacao = internacaoOpt.get();
        internacao.setDataAlta(java.time.LocalDateTime.now());
        internacaoRepository.save(internacao);

        Leito leito = internacao.getLeito();
        leito.setStatus(LeitoStatus.LIVRE);
        leitoRepository.save(leito);

        // Atualiza a contagem de leitos ocupados na clínica
        Clinica clinica = internacao.getClinica();
        if (clinica.getLeitosOcupados() > 0) {
            clinica.setLeitosOcupados(clinica.getLeitosOcupados() - 1);
            clinicaRepository.save(clinica);
        }

        return ResponseEntity.ok(internacao);
    }

    // Listar internações
    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(internacaoRepository.findAll());
    }
}