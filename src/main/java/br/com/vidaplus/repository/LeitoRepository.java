package br.com.vidaplus.repository;

import br.com.vidaplus.model.Leito;
import br.com.vidaplus.model.LeitoStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeitoRepository extends JpaRepository<Leito, Long> {
    List<Leito> findByClinicaId(Long clinicaId);
	List<Leito> findByClinicaIdAndStatus(Long clinicaId, LeitoStatus livre);
}
