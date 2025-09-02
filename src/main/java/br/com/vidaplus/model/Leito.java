package br.com.vidaplus.model;

import jakarta.persistence.*;

@Entity
public class Leito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    @Enumerated(EnumType.STRING)
    private LeitoStatus status = LeitoStatus.LIVRE; // por padrão sempre livre

    @ManyToOne
    @JoinColumn(name = "clinica_id")
    private Clinica clinica;

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public LeitoStatus getStatus() { return status; }
    public void setStatus(LeitoStatus status) { this.status = status; }

    public Clinica getClinica() { return clinica; }
    public void setClinica(Clinica clinica) { this.clinica = clinica; }
}
