package br.com.fiap.presente.checkin.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "checkin")
public class Checkin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idosoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCheckin tipo;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(length = 255)
    private String observacao;

    @Column(nullable = false)
    private boolean resolvido;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdosoId() { return idosoId; }
    public void setIdosoId(Long idosoId) { this.idosoId = idosoId; }

    public TipoCheckin getTipo() { return tipo; }
    public void setTipo(TipoCheckin tipo) { this.tipo = tipo; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public boolean isResolvido() { return resolvido; }
    public void setResolvido(boolean resolvido) { this.resolvido = resolvido; }
}
