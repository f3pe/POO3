package com.UERJ.POO3.modules.compras.repository;

import com.UERJ.POO3.modules.compras.domain.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {

    // Cumpre a exigência do edital: Histórico de compras por fornecedor
    // O Spring gera automaticamente a consulta SQL (ORDER BY data_compra DESC)
    // para trazer as compras mais recentes primeiro.
    List<Compra> findByFornecedorIdOrderByDataCompraDesc(Integer fornecedorId);

    @Query("SELECT c FROM Compra c JOIN FETCH c.fornecedor")
    List<Compra> findAllComFornecedores();
}