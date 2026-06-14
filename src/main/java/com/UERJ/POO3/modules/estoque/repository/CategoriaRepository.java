package com.UERJ.POO3.modules.estoque.repository;

import com.UERJ.POO3.modules.estoque.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // O Spring cria o SQL automaticamente só de lermos o nome do método!
    Optional<Categoria> findByNome(String nome);
}