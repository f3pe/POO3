package com.UERJ.POO3.modules.compras.service;

import com.UERJ.POO3.modules.compras.domain.Compra;
import com.UERJ.POO3.modules.compras.domain.Fornecedor;
import com.UERJ.POO3.modules.compras.domain.ItemCompra;
import com.UERJ.POO3.modules.compras.repository.CompraRepository;
import com.UERJ.POO3.modules.compras.repository.FornecedorRepository;
import com.UERJ.POO3.modules.estoque.domain.Produto;
import com.UERJ.POO3.modules.vendas.repository.ItemVendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ItemVendaRepository itemVendaRepository;

    public CompraService(CompraRepository compraRepository,
                         FornecedorRepository fornecedorRepository,
                         ItemVendaRepository itemVendaRepository) {
        this.compraRepository = compraRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.itemVendaRepository = itemVendaRepository;
    }

    /**
     * O ALGORITMO DE SIMULAÇÃO EXIGIDO NO EDITAL
     * Analisa o histórico de vendas dos últimos 30 dias e o stock atual
     * para sugerir um pedido de compra otimizado a um fornecedor.
     */
    @Transactional(readOnly = true)
    public Compra simularPedidoAutomatico(Integer fornecedorId) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado"));

        // Cria uma compra virtual (não guardada na base de dados)
        Compra sugestaoCompra = new Compra();
        sugestaoCompra.setFornecedor(fornecedor);
        sugestaoCompra.setStatus("SIMULACAO");

        // Define a data limite para a análise da demanda (30 dias atrás)
        LocalDateTime trintaDiasAtras = LocalDateTime.now().minusDays(30);

        // Analisa cada produto que este fornecedor nos vende
        for (Produto produto : fornecedor.getProdutosFornecidos()) {

            // 1. Calcula a Demanda (Usa a nossa consulta JPQL personalizada)
            Integer demanda30Dias = itemVendaRepository.somarQuantidadeVendidaPorProdutoAposData(
                    produto.getId(), trintaDiasAtras);

            // 2. Lógica Matemática de Sugestão
            int estoqueAtual = produto.getQuantidadeAtual();
            int quantidadeSugerida = 0;

            // Se o que vendemos no mês passado é maior que o stock atual, precisamos de repor a diferença
            if (estoqueAtual < demanda30Dias) {
                quantidadeSugerida = demanda30Dias - estoqueAtual;
            }

            // Garantia de Segurança: Se a simulação deixar o stock abaixo do mínimo, força a compra até ao mínimo
            if ((estoqueAtual + quantidadeSugerida) < produto.getQuantidadeMinima()) {
                quantidadeSugerida = produto.getQuantidadeMinima() - estoqueAtual;
            }

            // 3. Se o algoritmo detetar necessidade de compra, adiciona ao carrinho virtual
            if (quantidadeSugerida > 0) {
                ItemCompra item = new ItemCompra();
                item.setProduto(produto);
                item.setQuantidade(quantidadeSugerida);

                // Assumimos o preço de custo como 60% do preço de venda para fins de MVP
                Double precoCustoEstimado = produto.getPreco() * 0.60;
                item.setPrecoCusto(precoCustoEstimado);

                sugestaoCompra.adicionarItem(item);
            }
        }

        return sugestaoCompra;
    }

    /**
     * Transforma uma simulação num pedido real e guarda no histórico
     */
    @Transactional
    public Compra efetivarCompra(Compra compraSimulada) {
        compraSimulada.setStatus("CONCLUIDA");
        compraSimulada.setDataCompra(LocalDateTime.now());

        // Aqui também poderíamos adicionar lógica para atualizar o stock dos produtos
        // aumentando a 'quantidade_atual' com base no que acabou de chegar.

        return compraRepository.save(compraSimulada);
    }

    /**
     * Cumpre o requisito do edital: "Histórico de compras por fornecedor"
     */
    @Transactional(readOnly = true)
    public List<Compra> listarHistoricoPorFornecedor(Integer fornecedorId) {
        return compraRepository.findByFornecedorIdOrderByDataCompraDesc(fornecedorId);
    }
}