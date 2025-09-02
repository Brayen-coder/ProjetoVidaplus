package br.com.vidaplus.controller;

import br.com.vidaplus.dto.AgendamentoConsultaDTO;
import br.com.vidaplus.dto.ConsultaResumoDTO;
import br.com.vidaplus.model.*;
import br.com.vidaplus.repository.*;
import br.com.vidaplus.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {
    private final ConsultaRepository consultas;
    private final PacienteRepository pacientes;
    private final MedicoRepository medicos;
    private final ClinicaRepository clinicas;

    public ConsultaController(ConsultaRepository consultas, PacienteRepository pacientes,
                              MedicoRepository medicos, ClinicaRepository clinicas) {
        this.consultas = consultas;
        this.pacientes = pacientes;
        this.medicos = medicos;
        this.clinicas = clinicas;
    }
    // Paciente solicita consulta
    @PostMapping("/solicitar")
    public ResponseEntity<?> solicitar(@RequestBody AgendamentoConsultaDTO dto) {
        UserAccount user = SecurityUtil.currentUser();
        boolean isPaciente = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"));

        if (!isPaciente) {
            return ResponseEntity.status(403).body("Apenas pacientes podem solicitar consulta.");
        }

        if (user.getPaciente() == null || !user.getPaciente().getId().equals(dto.pacienteId)) {
            return ResponseEntity.status(403).body("Paciente só pode solicitar para si próprio.");
        }

        Optional<Paciente> p = pacientes.findById(dto.pacienteId);
        Optional<Medico> m = medicos.findById(dto.medicoId);
        Optional<Clinica> c = clinicas.findById(dto.clinicaId);
        if (p.isEmpty() || m.isEmpty() || c.isEmpty()) {
            return ResponseEntity.badRequest().body("Paciente/Medico/Clinica inválidos");
        }

        Consulta cons = new Consulta();
        cons.setPaciente(p.get());
        cons.setMedico(m.get());
        cons.setClinica(c.get());
        cons.setDataHora(dto.dataHora);
        cons.setObservacoes(dto.observacoes);
        cons.setStatus("SOLICITADA");

        return ResponseEntity.ok(consultas.save(cons));
    }
    
 // Médico aprova agendamento
    @PutMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovar(@PathVariable Long id) {
        UserAccount user = SecurityUtil.currentUser();
        if (user.getMedico() == null) {
            return ResponseEntity.status(403).body("Apenas médicos podem aprovar consultas.");
        }

        return consultas.findById(id).map(c -> {
            if (!c.getMedico().getId().equals(user.getMedico().getId())) {
                return ResponseEntity.status(403).body("Você não é o médico desta consulta.");
            }
            if (!"SOLICITADA".equals(c.getStatus())) {
                return ResponseEntity.badRequest().body("Somente consultas solicitadas podem ser aprovadas.");
            }
            c.setStatus("AGENDADA");
            return ResponseEntity.ok(consultas.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }
    
    

    @GetMapping
    public ResponseEntity<?> listar() {
        UserAccount user = SecurityUtil.currentUser();
        boolean isAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isPaciente = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"));
        boolean isProf = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFISSIONAL"));

        if (isAdmin) return ResponseEntity.ok(consultas.findAll());
        if (isPaciente && user.getPaciente() != null) {
            return ResponseEntity.ok(consultas.findByPacienteId(user.getPaciente().getId()));
        }
        if (isProf && user.getMedico() != null) {
            return ResponseEntity.ok(consultas.findByMedicoId(user.getMedico().getId()));
        }
        return ResponseEntity.status(403).body("Sem permissão para visualizar consultas");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obter(@PathVariable Long id) {
        UserAccount user = SecurityUtil.currentUser();
        return consultas.findById(id).map(c -> {
            boolean allowed = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) ||
                    (user.getPaciente() != null && c.getPaciente().getId().equals(user.getPaciente().getId())) ||
                    (user.getMedico() != null && c.getMedico().getId().equals(user.getMedico().getId()));
            return allowed ? ResponseEntity.ok(c) : ResponseEntity.status(403).body("Sem permissão para acessar esta consulta");
        }).orElse(ResponseEntity.notFound().build());
    }

 // Marcar como realizada (médico)
    @PutMapping("/{id}/realizar")
    public ResponseEntity<?> realizar(@PathVariable Long id) {
        UserAccount user = SecurityUtil.currentUser();
        if (user.getMedico() == null) {
            return ResponseEntity.status(403).body("Apenas médicos podem concluir consultas.");
        }

        return consultas.findById(id).map(c -> {
            if (!c.getMedico().getId().equals(user.getMedico().getId())) {
                return ResponseEntity.status(403).body("Você não é o médico desta consulta.");
            }
            if (!"AGENDADA".equals(c.getStatus())) {
                return ResponseEntity.badRequest().body("Somente consultas agendadas podem ser marcadas como realizadas.");
            }
            c.setStatus("REALIZADA");
            return ResponseEntity.ok(consultas.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }
 // Cancelar consulta (Paciente, Médico ou Admin)
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        UserAccount user = SecurityUtil.currentUser();

        return consultas.findById(id).map(c -> {
            boolean isAdmin = user.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isPaciente = user.getPaciente() != null && 
                                 c.getPaciente().getId().equals(user.getPaciente().getId());
            boolean isMedico = user.getMedico() != null &&
                               c.getMedico().getId().equals(user.getMedico().getId());

            if (!(isAdmin || isPaciente || isMedico)) {
                return ResponseEntity.status(403).body("Sem permissão para cancelar esta consulta.");
            }

            if ("REALIZADA".equals(c.getStatus())) {
                return ResponseEntity.badRequest().body("Consultas realizadas não podem ser canceladas.");
            }

            c.setStatus("CANCELADA");
            return ResponseEntity.ok(consultas.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/resumo")
    public ResponseEntity<?> resumo(@RequestParam(required = false) String inicio,
                                    @RequestParam(required = false) String fim) {
        UserAccount user = SecurityUtil.currentUser();
        LocalDateTime i;
        LocalDateTime f;
        try { 
            i = inicio != null ? LocalDateTime.parse(inicio) : LocalDateTime.now().minusMonths(1);
            f = fim != null ? LocalDateTime.parse(fim) : LocalDateTime.now().plusMonths(1);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de data/hora inválido. Use o formato ISO (ex: 'YYYY-MM-DDTHH:MM:SS').");
        }

        List<Consulta> lista = consultas.findByDataHoraBetween(i, f);
        List<ConsultaResumoDTO> saida = new ArrayList<>();
        for (Consulta c : lista) {
            boolean allowed = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) ||
                    (user.getPaciente() != null && c.getPaciente().getId().equals(user.getPaciente().getId())) ||
                    (user.getMedico() != null && c.getMedico().getId().equals(user.getMedico().getId()));
            if (allowed) {
                saida.add(new ConsultaResumoDTO(
                        c.getId(),
                        c.getDataHora(),
                        c.getPaciente().getNome(),
                        c.getMedico().getNome(),
                        c.getClinica().getNome(),
                        c.getStatus()
                ));
            }
        }
        return ResponseEntity.ok(saida);
    }
}