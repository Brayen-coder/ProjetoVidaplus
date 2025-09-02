package br.com.vidaplus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Paciente {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String nome;
    @NotBlank
    @Column(unique = true, length = 14)
    private String cpf;
    @NotBlank
    private LocalDate dataNasc;
    @NotBlank
    private Double peso;
    @NotBlank
    private Double altura;
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Consulta> consultas;

    public Paciente() {}

    public Paciente(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public List<Consulta> getConsultas() { return consultas; }
    public void setConsultas(List<Consulta> consultas) { this.consultas = consultas; }
	public LocalDate getDataNasc() {		return dataNasc;	}
	public void setDataNasc(LocalDate dataNasc) {		this.dataNasc = dataNasc;	}
	public Double getPeso() {		return peso;	}
	public void setPeso(Double peso) {		this.peso = peso;	}
	public Double getAltura() {		return altura;	}
	public void setAltura(Double altura) {		this.altura = altura;	}
    
}





