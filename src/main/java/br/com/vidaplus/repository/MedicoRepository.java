package br.com.vidaplus.repository;
import br.com.vidaplus.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MedicoRepository extends JpaRepository<Medico, Long> { }
