package com.UERJ.POO3.modules.estoque.repository;

import com.UERJ.POO3.modules.estoque.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    // Lista todos os produtos que atingiram ou estão abaixo da quantidade mínima
    // Ideal para o alerta automático de estoque que o edital exige!
    // NOVA CONSULTA PARA O RELATÓRIO DE STOCK (COM JOIN FETCH)
    @Query("SELECT p FROM Produto p JOIN FETCH p.fornecedor JOIN FETCH p.categoria WHERE p.quantidadeAtual <= p.quantidadeMinima")
    List<Produto> findProdutosComEstoqueBaixo();

    // ADICIONE ESTA NOVA CONSULTA:
    // O "JOIN FETCH" força o Hibernate a trazer as relações na mesma ida ao banco, resolvendo o erro "no session".
    @Query("SELECT p FROM Produto p JOIN FETCH p.categoria JOIN FETCH p.fornecedor")
    List<Produto> findAllComRelacionamentos();


}