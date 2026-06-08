package com.UERJ.POO3.modules.seguranca.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "papeis")
public class Papel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // O nome do papel (ex: ROLE_ADMIN, ROLE_GERENTE, ROLE_VETERINARIO)
    @Column(nullable = false, length = 50, unique = true)
    private String nome;

    // Mapeamento bidirecional: aponta para a coleção 'papeis' na classe Usuario
    @ManyToMany(mappedBy = "papeis")
    private Set<Usuario> usuarios = new HashSet<>();

    public Papel() {}

    public Papel(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Set<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(Set<Usuario> usuarios) { this.usuarios = usuarios; }
}