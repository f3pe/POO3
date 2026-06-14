package com.UERJ.POO3.modules.compras.domain;

import com.UERJ.POO3.modules.estoque.domain.Produto;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fornecedores")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String razaoSocial;

    // Cumprindo a exigência do edital para o campo CNPJ
    @Column(nullable = false, length = 18, unique = true)
    private String cnpj;

    // Cumprindo a exigência do edital para "dados de contato"
    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefone;

    // Cumprindo a exigência do edital para mostrar os "produtos fornecidos"
    // Um fornecedor entrega MUITOS (@OneToMany) produtos.
    @OneToMany(mappedBy = "fornecedor", fetch = FetchType.LAZY)
    private List<Produto> produtosFornecidos = new ArrayList<>();

    public Fornecedor() {}

    public Fornecedor(String razaoSocial, String cnpj, String email, String telefone) {
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.email = email;
        this.telefone = telefone;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public List<Produto> getProdutosFornecidos() { return produtosFornecidos; }
    public void setProdutosFornecidos(List<Produto> produtosFornecidos) { this.produtosFornecidos = produtosFornecidos; }
}