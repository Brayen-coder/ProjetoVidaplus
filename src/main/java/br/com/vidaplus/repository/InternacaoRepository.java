package br.com.vidaplus.repository;
import br.com.vidaplus.model.Internacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternacaoRepository extends JpaRepository<Internacao, Long> {
}
