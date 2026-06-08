package com.UERJ.POO3.modules.seguranca.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean ativo = true;

    // Campos adicionados para a recuperação de palavra-passe com token temporário
    @Column(name = "reset_token", length = 255)
    private String resetToken;

    @Column(name = "token_expiracao")
    private LocalDateTime tokenExpiracao;

    // Relacionamento N:M para o Controlo de Acesso Baseado em Papéis (RBAC)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_papeis",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "papel_id")
    )
    private Set<Papel> papeis = new HashSet<>();

    // Construtores
    public Usuario() {}

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public LocalDateTime getTokenExpiracao() { return tokenExpiracao; }
    public void setTokenExpiracao(LocalDateTime tokenExpiracao) { this.tokenExpiracao = tokenExpiracao; }

    public Set<Papel> getPapeis() { return papeis; }
    public void setPapeis(Set<Papel> papeis) { this.papeis = papeis; }
}