package br.com.vidaplus.dto;

import java.util.List;

import br.com.vidaplus.model.Paciente;

public class PacienteResumoDTO {
    private Long id;
    private String nome;
    private String cpf;
    private List<String> resumoConsultas;

    public PacienteResumoDTO(Paciente paciente) {
        this.id = paciente.getId();
        this.nome = paciente.getNome();
        this.cpf = paciente.getCpf();

        this.resumoConsultas = paciente.getConsultas()
                .stream()
                .map(c -> "Consulta em " + c.getDataHora()
                        + " com " + c.getMedico().getNome()
                        + " [" + c.getStatus() + "]")
                .toList();
    }

    // getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public List<String> getResumoConsultas() { return resumoConsultas; }
}