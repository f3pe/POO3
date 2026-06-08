package com.UERJ.POO3.modules.seguranca.repository;

import com.UERJ.POO3.modules.seguranca.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Utilizado pelo Spring Security para fazer o login
    Optional<Usuario> findByUsername(String username);

    // Pode ser útil caso permitam login por e-mail ou validações de cadastro
    Optional<Usuario> findByEmail(String email);

    // Essencial para o requisito de "Recuperação de senha com token temporário"
    Optional<Usuario> findByResetToken(String resetToken);

    // Exemplo de JPQL explícito exigido nos critérios técnicos do projeto.
    // O "JOIN FETCH" carrega os papéis do usuário numa única consulta, otimizando a performance.
    @Query("SELECT u FROM Usuario u JOIN FETCH u.papeis WHERE u.username = :username")
    Optional<Usuario> findByUsernameWithPapeis(@Param("username") String username);
}