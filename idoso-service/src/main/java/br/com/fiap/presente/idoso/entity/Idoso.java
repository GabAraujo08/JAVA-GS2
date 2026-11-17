package br.com.fiap.presente.idoso.entity;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "idoso")
public class Idoso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false)
    private LocalTime horarioAviso;

    @Column(nullable = false)
    private Integer tempoEsperaMinutos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusDia statusDia;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalTime getHorarioAviso() { return horarioAviso; }
    public void setHorarioAviso(LocalTime horarioAviso) { this.horarioAviso = horarioAviso; }

    public Integer getTempoEsperaMinutos() { return tempoEsperaMinutos; }
    public void setTempoEsperaMinutos(Integer tempoEsperaMinutos) { this.tempoEsperaMinutos = tempoEsperaMinutos; }

    public StatusDia getStatusDia() { return statusDia; }
    public void setStatusDia(StatusDia statusDia) { this.statusDia = statusDia; }
}
