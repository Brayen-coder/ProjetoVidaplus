package br.com.vidaplus.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Internacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "clinica_id")
    private Clinica clinica;

    @ManyToOne
    @JoinColumn(name = "leito_id")
    private Leito leito;

    private LocalDateTime dataEntrada = LocalDateTime.now();

    private LocalDateTime dataAlta;

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Clinica getClinica() { return clinica; }
    public void setClinica(Clinica clinica) { this.clinica = clinica; }

    public Leito getLeito() { return leito; }
    public void setLeito(Leito leito) { this.leito = leito; }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDateTime getDataAlta() { return dataAlta; }
    public void setDataAlta(LocalDateTime dataAlta) { this.dataAlta = dataAlta; }
}
