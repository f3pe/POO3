package com.UERJ.POO3.modules.vendas.domain;

import com.UERJ.POO3.modules.seguranca.domain.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda;

    @Column(name = "valor_total", nullable = false)
    private Double valorTotal = 0.0;

    // Atende à exigência do edital para múltiplas formas de pagamento, incluindo simulação de Pix
    @Column(name = "forma_pagamento", nullable = false, length = 50)
    private String formaPagamento;

    @Column(nullable = false, length = 50)
    private String status; // Ex: PENDENTE, CONCLUIDA, CANCELADA

    // Relacionamento com o Cliente (Vinculado à nossa tabela de usuários com ROLE_CLIENTE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    // Relacionamento com os itens do carrinho
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    public Venda() {
        this.dataVenda = LocalDateTime.now();
        this.status = "PENDENTE";
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }

    public List<ItemVenda> getItens() { return itens; }
    public void setItens(List<ItemVenda> itens) { this.itens = itens; }

    // Método utilitário para adicionar produtos ao carrinho
    public void adicionarItem(ItemVenda item) {
        itens.add(item);
        item.setVenda(this);
        this.valorTotal += (item.getPrecoUnitario() * item.getQuantidade());
    }
}