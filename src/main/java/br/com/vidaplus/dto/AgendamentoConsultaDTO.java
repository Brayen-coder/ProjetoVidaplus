package br.com.vidaplus.dto;

import java.time.LocalDateTime;

public class AgendamentoConsultaDTO {
    public Long pacienteId;
    public Long medicoId;
    public Long clinicaId;
    public LocalDateTime dataHora;
    public String observacoes;
}
