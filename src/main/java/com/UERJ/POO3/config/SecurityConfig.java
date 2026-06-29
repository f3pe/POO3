package com.UERJ.POO3.config;

import com.UERJ.POO3.presentation.ui.LoginView;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1. Libera o acesso público às telas de recuperação de palavra-passe
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers("/recuperar-senha", "/redefinir-senha").permitAll()
        );

        // 2. Aplica as configurações padrão de segurança do Vaadin e define a nossa LoginView
        http.with(VaadinSecurityConfigurer.vaadin(), vaadinSecurity ->
                vaadinSecurity.loginView(LoginView.class)
        );

        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/", true) // Redireciona para o Dashboard após logar com Google
        );

        return http.build();
    }

    // Mantemos o nosso codificador BCrypt aqui!
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}