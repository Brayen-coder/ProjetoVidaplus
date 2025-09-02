package br.com.vidaplus.model;

import jakarta.persistence.*;

@Entity
public class Clinica {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cnpj;
    private String endereco;

    private Integer totalLeitos = 0;
    private Integer leitosOcupados = 0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public Integer getTotalLeitos(){ return totalLeitos; }
    public void setTotalLeitos(Integer totalLeitos){ this.totalLeitos = totalLeitos; }
    public Integer getLeitosOcupados(){ return leitosOcupados; }
    public void setLeitosOcupados(Integer leitosOcupados){ this.leitosOcupados = leitosOcupados; }
}
