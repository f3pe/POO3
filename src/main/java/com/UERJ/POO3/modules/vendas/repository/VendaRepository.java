package com.UERJ.POO3.modules.vendas.repository;

import com.UERJ.POO3.modules.vendas.domain.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Integer> {

    // Método útil para os futuros relatórios financeiros e dashboard
    // Busca todas as vendas realizadas num determinado período
    List<Venda> findByDataVendaBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
}