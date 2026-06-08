package com.UERJ.POO3.modules.seguranca.repository;

import com.UERJ.POO3.modules.seguranca.domain.Papel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PapelRepository extends JpaRepository<Papel, Integer> {

    // Busca um perfil específico no banco de dados para vincular a um usuário
    Optional<Papel> findByNome(String nome);
}
