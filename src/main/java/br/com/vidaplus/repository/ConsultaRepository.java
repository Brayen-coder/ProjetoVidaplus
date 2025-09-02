package br.com.vidaplus.repository;
import br.com.vidaplus.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
//No seu ConsultaRepository.java
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
 List<Consulta> findByPacienteId(Long pacienteId);
 List<Consulta> findByMedicoId(Long medicoId); // Adicione esta linha
 List<Consulta> findByDataHoraBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
