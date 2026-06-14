package com.UERJ.POO3.modules.estoque.service;

import com.UERJ.POO3.modules.estoque.domain.Produto;
import com.UERJ.POO3.modules.estoque.repository.ProdutoRepository;
// Importamos o serviço de e-mail que já criámos no módulo de segurança
import com.UERJ.POO3.modules.seguranca.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EmailService emailService;

    public ProdutoService(ProdutoRepository produtoRepository, EmailService emailService) {
        this.produtoRepository = produtoRepository;
        this.emailService = emailService;
    }

    /**
     * Guarda ou atualiza um produto e verifica imediatamente se o stock está baixo.
     */
    @Transactional
    public Produto salvarProduto(Produto produto) {
        Produto produtoSalvo = produtoRepository.save(produto);

        // Regra de Negócio Exigida: Alerta de Stock Mínimo
        verificarEAlertarStock(produtoSalvo);

        return produtoSalvo;
    }

    /**
     * Retorna a lista de todos os produtos ativos (o Hibernate ignora os excluídos logicamente).
     */
    public List<Produto> listarProdutos() {
        return produtoRepository.findAllComRelacionamentos();
    }

    /**
     * Exclui um produto. Graças à anotação @SQLDelete na entidade,
     * isto fará um UPDATE para excluido = true em vez de apagar o registo.
     */
    @Transactional
    public void excluirProduto(Integer id) {
        produtoRepository.deleteById(id);
    }

    /**
     * Lógica interna para verificar se o produto atingiu a quantidade mínima.
     */
    private void verificarEAlertarStock(Produto produto) {
        if (produto.getQuantidadeAtual() <= produto.getQuantidadeMinima()) {

            String mensagem = String.format(
                    "ALERTA DE STOCK: O produto '%s' atingiu o nível mínimo! Quantidade atual: %d. Quantidade mínima: %d.",
                    produto.getNome(),
                    produto.getQuantidadeAtual(),
                    produto.getQuantidadeMinima()
            );

            // Aqui disparamos o e-mail para o gerente/administrador da loja
            // (Assumindo que adicionou um método genérico no seu EmailService)
            emailService.enviarEmailAlertaEstoque("gerencia@petshop.com", "Alerta de Stock Baixo", mensagem);

            System.out.println("⚠️ " + mensagem);
        }
    }

    /**
     * Método que pode ser chamado por uma rotina agendada (Cron Job)
     * para verificar todo o stock da loja de uma só vez no final do dia.
     */
    public void varreduraDiariaDeStock() {
        List<Produto> produtosEmBaixa = produtoRepository.findProdutosComEstoqueBaixo();
        for (Produto p : produtosEmBaixa) {
            verificarEAlertarStock(p);
        }
    }
}