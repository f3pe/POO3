package com.UERJ.POO3.modules.compras.repository;

import com.UERJ.POO3.modules.compras.domain.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {
    // Método útil para garantir que não vamos cadastrar dois fornecedores com o mesmo CNPJ
    Optional<Fornecedor> findByCnpj(String cnpj);
}