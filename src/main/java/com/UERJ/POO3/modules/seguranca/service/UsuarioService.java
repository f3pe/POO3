package com.UERJ.POO3.modules.seguranca.service;

import com.UERJ.POO3.modules.seguranca.domain.Papel;
import com.UERJ.POO3.modules.seguranca.domain.Usuario;
import com.UERJ.POO3.modules.seguranca.repository.PapelRepository;
import com.UERJ.POO3.modules.seguranca.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Injeção de dependência via construtor (Melhor prática do Spring)
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PapelRepository papelRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.papelRepository = papelRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public Usuario cadastrarUsuario(Usuario novoUsuario, String nomePapel) {
        // 1. Verifica se o username ou email já existem
        if (usuarioRepository.findByUsername(novoUsuario.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username já está em uso.");
        }
        if (usuarioRepository.findByEmail(novoUsuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        // 2. Criptografa a senha com BCrypt antes de salvar
        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getPassword());
        novoUsuario.setPassword(senhaCriptografada);

        // 3. Atribui o papel (perfil) ao usuário
        Papel papel = papelRepository.findByNome(nomePapel)
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado: " + nomePapel));
        novoUsuario.getPapeis().add(papel);

        // 4. Salva no banco de dados
        return usuarioRepository.save(novoUsuario);
    }

    @Transactional
    public String gerarTokenRecuperacaoSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com este e-mail."));

        // Gera um UUID único para o token e define a expiração para 1 hora a partir de agora
        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setTokenExpiracao(LocalDateTime.now().plusHours(1));

        usuarioRepository.save(usuario);

        emailService.enviarEmailRecuperacao(usuario.getEmail(), token);
        return token;
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        Usuario usuario = usuarioRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou não encontrado."));

        // Verifica se o token já passou do prazo de validade
        if (usuario.getTokenExpiracao().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("O token de recuperação expirou.");
        }

        // Atualiza a senha (criptografando novamente) e invalida o token
        usuario.setPassword(passwordEncoder.encode(novaSenha));
        usuario.setResetToken(null);
        usuario.setTokenExpiracao(null);

        usuarioRepository.save(usuario);
    }
}
