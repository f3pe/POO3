package com.UERJ.POO3.modules.compras.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_compra", nullable = false)
    private LocalDateTime dataCompra;

    @Column(name = "valor_total", nullable = false)
    private Double valorTotal = 0.0;

    @Column(nullable = false, length = 50)
    private String status; // Ex: "PENDENTE", "CONCLUIDA", "CANCELADA"

    // Relacionamento: Muitas compras pertencem a 1 Fornecedor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    // Relacionamento: 1 Compra possui Muitos Itens (Cascade ALL para salvar tudo junto)
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCompra> itens = new ArrayList<>();

    public Compra() {
        this.dataCompra = LocalDateTime.now();
        this.status = "PENDENTE";
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDateTime dataCompra) { this.dataCompra = dataCompra; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }

    public List<ItemCompra> getItens() { return itens; }
    public void setItens(List<ItemCompra> itens) { this.itens = itens; }

    // Método utilitário para adicionar item e já somar o valor total
    public void adicionarItem(ItemCompra item) {
        itens.add(item);
        item.setCompra(this);
        this.valorTotal += (item.getPrecoCusto() * item.getQuantidade());
    }
}