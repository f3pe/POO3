package com.UERJ.POO3.modules.vendas.repository;

import com.UERJ.POO3.modules.vendas.domain.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Integer> {

    // Método útil para os futuros relatórios financeiros e dashboard
    // Busca todas as vendas realizadas num determinado período
    List<Venda> findByDataVendaBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    // NOVA CONSULTA PARA RESOLVER O ERRO NO DASHBOARD
    // O DISTINCT evita linhas duplicadas. Trazemos a Venda, os seus itens e o produto associado a cada item numa única viagem ao banco!
    @Query("SELECT DISTINCT v FROM Venda v LEFT JOIN FETCH v.itens i LEFT JOIN FETCH i.produto")
    List<Venda> findAllComItensEProdutos();

    @Query("SELECT v FROM Venda v JOIN FETCH v.cliente WHERE v.dataVenda BETWEEN :dataInicio AND :dataFim")
    List<Venda> findVendasComClientePorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}