package com.UERJ.POO3.modules.seguranca.repository;

import com.UERJ.POO3.modules.seguranca.domain.LogAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogAcessoRepository extends JpaRepository<LogAcesso, Integer> {

    // Caso precisem exibir no Vaadin o histórico de acessos de um usuário específico
    List<LogAcesso> findByUsuarioIdOrderByDataHoraDesc(Integer usuarioId);
}
