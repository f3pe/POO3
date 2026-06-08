package com.UERJ.POO3.modules.seguranca.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_acessos")
public class LogAcesso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relacionamento N:1 - Muitos logs podem pertencer a um único usuário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false, length = 255)
    private String acao;

    // Tamanho 45 é o padrão para suportar endereços IPv6
    @Column(nullable = false, length = 45)
    private String ip;

    @Column(nullable = false, length = 10)
    private String resultado;

    public LogAcesso() {}

    public LogAcesso(Usuario usuario, LocalDateTime dataHora, String acao, String ip, String resultado) {
        this.usuario = usuario;
        this.dataHora = dataHora;
        this.acao = acao;
        this.ip = ip;
        this.resultado = resultado;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
}