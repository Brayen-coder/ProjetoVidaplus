package br.com.vidaplus.dto;

import java.time.LocalDateTime;

public class ConsultaResumoDTO {
    public Long id;
    public LocalDateTime dataHora;
    public String nomePaciente;
    public String nomeMedico;
    public String clinica;
    public String status;
    public ConsultaResumoDTO(Long id, LocalDateTime dh, String np, String nm, String cl, String st) {
        this.id = id; this.dataHora = dh; this.nomePaciente = np; this.nomeMedico = nm; this.clinica = cl; this.status = st;
    }
}
