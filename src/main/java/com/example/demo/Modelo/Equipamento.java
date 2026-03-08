package com.example.demo.Modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "equipamento")
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String codigo;

    private String descricao;

    private LocalDateTime dataCadastro = LocalDateTime.now();

    @ManyToMany
    @JoinTable(name = "equipamento_subconjunto", joinColumns = @JoinColumn(name = "equipamento_id"), inverseJoinColumns = @JoinColumn(name = "subconjunto_id"))
    private List<Subconjunto> subconjuntos = new ArrayList<>();

    private Integer producaoPorHora;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public List<Subconjunto> getSubconjuntos() {
        return subconjuntos;
    }

    public void setSubconjuntos(List<Subconjunto> subconjuntos) {
        this.subconjuntos = subconjuntos;
    }

       public Integer getProducaoPorHora() {
        return producaoPorHora;
    }

    public void setProducaoPorHora(Integer producaoPorHora) {
        this.producaoPorHora = producaoPorHora;
    }

}
