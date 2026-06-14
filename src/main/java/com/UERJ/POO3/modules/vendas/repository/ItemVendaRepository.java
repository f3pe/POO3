package com.UERJ.POO3.modules.vendas.repository;

import com.UERJ.POO3.modules.vendas.domain.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Integer> {

    // A MÁGICA DA DEMANDA: Esta consulta soma a quantidade de um produto específico
    // que foi vendido a partir de uma data limite (ex: 30 dias atrás)
    @Query("SELECT COALESCE(SUM(i.quantidade), 0) FROM ItemVenda i " +
            "WHERE i.produto.id = :produtoId " +
            "AND i.venda.dataVenda >= :dataLimite " +
            "AND i.venda.status = 'CONCLUIDA'")
    Integer somarQuantidadeVendidaPorProdutoAposData(
            @Param("produtoId") Integer produtoId,
            @Param("dataLimite") LocalDateTime dataLimite
    );
}