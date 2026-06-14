package com.UERJ.POO3.modules.seguranca.service;

import com.UERJ.POO3.modules.seguranca.domain.Usuario;
import com.UERJ.POO3.modules.seguranca.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Busca o nosso usuário no banco de dados do PostgreSQL
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // 2. Converte o nosso 'Usuario' para o 'UserDetails' que o Spring Security entende
        return new User(
                usuario.getUsername(),
                usuario.getPassword(), // A senha já está com o hash BCrypt aqui
                // 3. Mapeia os nossos papéis (ex: ROLE_ADMIN) para as autoridades do Spring
                usuario.getPapeis().stream()
                        .map(papel -> new SimpleGrantedAuthority(papel.getNome()))
                        .collect(Collectors.toList())
        );
    }
}