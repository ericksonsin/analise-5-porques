package com.example.demo.Modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "analise_5_porques")
public class Analise5Porques {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "equipamento_id")
    private Equipamento equipamento;

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    @Column(columnDefinition = "TEXT")
    private String porque1;

    @Column(columnDefinition = "TEXT")
    private String porque2;

    @Column(columnDefinition = "TEXT")
    private String porque3;

    @Column(columnDefinition = "TEXT")
    private String porque4;

    @Column(columnDefinition = "TEXT")
    private String porque5;

    @Column(columnDefinition = "TEXT")
    private String causaRaiz;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "usuario_analise_id")
    private User usuarioAnalise;

    @Column(name = "caminho_imagem")
    private String caminhoImagem;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataInicioAvaria;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataFimAvaria;

    @ManyToOne
    @JoinColumn(name = "mecanico_id")
    private Mecanico mecanico;

    @ManyToOne
    @JoinColumn(name = "subconjunto_id")
    private Subconjunto subconjunto;

    private LocalDateTime dataAnalise = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPorque1() {
        return porque1;
    }

    public void setPorque1(String porque1) {
        this.porque1 = porque1;
    }

    public String getPorque2() {
        return porque2;
    }

    public void setPorque2(String porque2) {
        this.porque2 = porque2;
    }

    public String getPorque3() {
        return porque3;
    }

    public void setPorque3(String porque3) {
        this.porque3 = porque3;
    }

    public String getPorque4() {
        return porque4;
    }

    public void setPorque4(String porque4) {
        this.porque4 = porque4;
    }

    public String getPorque5() {
        return porque5;
    }

    public void setPorque5(String porque5) {
        this.porque5 = porque5;
    }

    public String getCausaRaiz() {
        return causaRaiz;
    }

    public void setCausaRaiz(String causaRaiz) {
        this.causaRaiz = causaRaiz;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public User getUsuarioAnalise() {
        return usuarioAnalise;
    }

    public void setUsuarioAnalise(User usuarioAnalise) {
        this.usuarioAnalise = usuarioAnalise;
    }

    public LocalDateTime getDataAnalise() {
        return dataAnalise;
    }

    public void setDataAnalise(LocalDateTime dataAnalise) {
        this.dataAnalise = dataAnalise;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }

    public LocalDateTime getDataInicioAvaria() {
        return dataInicioAvaria;
    }

    public void setDataInicioAvaria(LocalDateTime dataInicioAvaria) {
        this.dataInicioAvaria = dataInicioAvaria;
    }

    public LocalDateTime getDataFimAvaria() {
        return dataFimAvaria;
    }

    public void setDataFimAvaria(LocalDateTime dataFimAvaria) {
        this.dataFimAvaria = dataFimAvaria;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public void setMecanico(Mecanico mecanico) {
        this.mecanico = mecanico;
    }

    public Subconjunto getSubconjunto() {
        return subconjunto;
    }

    public void setSubconjunto(Subconjunto subconjunto) {
        this.subconjunto = subconjunto;
    }

}
