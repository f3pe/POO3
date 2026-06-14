package com.UERJ.POO3.modules.compras.repository;

import com.UERJ.POO3.modules.compras.domain.ItemCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCompraRepository extends JpaRepository<ItemCompra, Integer> {

    // Método útil caso precisemos de listar apenas os itens de uma compra específica
    List<ItemCompra> findByCompraId(Integer compraId);
}