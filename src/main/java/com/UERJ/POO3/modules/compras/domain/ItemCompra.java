package com.UERJ.POO3.modules.compras.domain;

import com.UERJ.POO3.modules.estoque.domain.Produto;
import jakarta.persistence.*;

@Entity
@Table(name = "item_compra")
public class ItemCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_custo", nullable = false)
    private Double precoCusto;

    // Relacionamento com a Compra "Pai"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    // Relacionamento apontando para qual Produto do estoque estamos comprando
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    public ItemCompra() {}

    public ItemCompra(Produto produto, Integer quantidade, Double precoCusto) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoCusto = precoCusto;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(Double precoCusto) { this.precoCusto = precoCusto; }

    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
}