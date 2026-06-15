package com.UERJ.POO3.modules.vendas.service;

import com.UERJ.POO3.modules.estoque.domain.Produto;
import com.UERJ.POO3.modules.estoque.repository.ProdutoRepository;
import com.UERJ.POO3.modules.vendas.domain.ItemVenda;
import com.UERJ.POO3.modules.vendas.domain.Venda;
import com.UERJ.POO3.modules.vendas.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
    }

    /**
     * Processa o checkout do carrinho de compras.
     * Atualiza o stock automaticamente, conforme exigido no edital.
     */
    @Transactional
    public Venda finalizarVenda(Venda carrinho, String formaPagamento) {
        if (carrinho.getItens().isEmpty()) {
            throw new IllegalArgumentException("O carrinho de compras está vazio!");
        }

        carrinho.setFormaPagamento(formaPagamento);
        carrinho.setDataVenda(LocalDateTime.now());
        carrinho.setStatus("CONCLUIDA");

        // Regra de Negócio: Atualização automática do stock
        for (ItemVenda item : carrinho.getItens()) {
            Produto produto = item.getProduto();

            // Verifica se há stock suficiente antes de vender
            if (produto.getQuantidadeAtual() < item.getQuantidade()) {
                throw new IllegalStateException("Stock insuficiente para o produto: " + produto.getNome());
            }

            // Subtrai a quantidade vendida do stock atual
            produto.setQuantidadeAtual(produto.getQuantidadeAtual() - item.getQuantidade());

            // Guarda a atualização do produto na base de dados
            produtoRepository.save(produto);
        }

        // Guarda a venda com todos os itens e o valor total calculado
        return vendaRepository.save(carrinho);
    }
}