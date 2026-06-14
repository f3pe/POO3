package com.UERJ.POO3.modules.estoque.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity // 1. Diz ao Spring/Hibernate: "Transforme esta classe numa tabela no banco de dados".
@Table(name = "categorias") // 2. Dá o nome exato da tabela no PostgreSQL.
public class Categoria {

    @Id // 3. Define que este campo é a Chave Primária (PK).
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 4. Faz o banco gerar o ID automaticamente (1, 2, 3...).
    private Integer id;

    @Column(nullable = false, length = 100, unique = true) // 5. Regras: não pode ser nulo, máx 100 caracteres, e não podem existir duas categorias com o mesmo nome.
    private String nome;

    // 6. O NOVO ENTENDIMENTO: Mapeamento Bidirecional
    // Como mapeamos @ManyToOne lá na classe Produto, aqui fazemos o inverso (@OneToMany).
    // O 'mappedBy = "categoria"' diz: "A regra deste relacionamento já foi definida lá na classe Produto, na variável 'categoria'".
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<Produto> produtos = new ArrayList<>();

    public Categoria() {}

    public Categoria(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<Produto> getProdutos() { return produtos; }
    public void setProdutos(List<Produto> produtos) { this.produtos = produtos; }
}