package br.com.vidaplus.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Consulta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    @ManyToOne(optional = false)
    @JsonBackReference
    private Paciente paciente;


    @ManyToOne(optional = false)
    private Medico medico;

    @ManyToOne(optional = false)
    private Clinica clinica;

    private String status; // AGENDADA, REALIZADA, CANCELADA

    private String observacoes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }
    public Clinica getClinica() { return clinica; }
    public void setClinica(Clinica clinica) { this.clinica = clinica; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
