package com.UERJ.POO3.presentation.ui;

import com.UERJ.POO3.modules.seguranca.service.UsuarioService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("redefinir-senha")
@PageTitle("Nova Palavra-passe - Petshop ERP")
@AnonymousAllowed // Tem de estar acessível para quem não tem o login feito
public class RedefinirSenhaView extends VerticalLayout {

    private final UsuarioService usuarioService;

    public RedefinirSenhaView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H2 titulo = new H2("Criar Nova Palavra-passe");
        Paragraph instrucao = new Paragraph("Cole o token recebido por e-mail e defina a sua nova palavra-passe.");

        // Campo para o Token
        TextField tokenField = new TextField("Token de Segurança");
        tokenField.setWidth("300px");
        tokenField.setRequiredIndicatorVisible(true);

        // Campos para a Palavra-passe
        PasswordField novaSenhaField = new PasswordField("Nova Palavra-passe");
        novaSenhaField.setWidth("300px");
        novaSenhaField.setRequiredIndicatorVisible(true);

        PasswordField confirmarSenhaField = new PasswordField("Confirmar Nova Palavra-passe");
        confirmarSenhaField.setWidth("300px");
        confirmarSenhaField.setRequiredIndicatorVisible(true);

        // Botões de Ação
        Button btnSalvar = new Button("Salvar e Entrar");
        btnSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnVoltar = new Button("Voltar para o Login", e -> UI.getCurrent().navigate(LoginView.class));
        btnVoltar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Lógica de validação e submissão
        btnSalvar.addClickListener(event -> {
            String token = tokenField.getValue();
            String senha = novaSenhaField.getValue();
            String confirmacao = confirmarSenhaField.getValue();

            if (token.isBlank() || senha.isBlank() || confirmacao.isBlank()) {
                Notification.show("Por favor, preencha todos os campos.")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (!senha.equals(confirmacao)) {
                Notification.show("As palavras-passe não coincidem!")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                // Chama o back-end para validar o token e atualizar o hash no banco de dados
                usuarioService.redefinirSenha(token, senha);

                Notification sucesso = Notification.show("Palavra-passe alterada com sucesso! Faça o login.");
                sucesso.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // Redireciona de volta para o login
                UI.getCurrent().navigate(LoginView.class);

            } catch (IllegalArgumentException ex) {
                // Captura os erros do nosso serviço (ex: "Token expirado" ou "Token inválido")
                Notification.show(ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        // Agrupa tudo num contentor centralizado
        VerticalLayout formLayout = new VerticalLayout(titulo, instrucao, tokenField, novaSenhaField, confirmarSenhaField, btnSalvar, btnVoltar);
        formLayout.setAlignItems(Alignment.CENTER);
        formLayout.setMaxWidth("400px");
        formLayout.getStyle().set("border", "1px solid #ccc").set("padding", "20px").set("border-radius", "8px");

        add(formLayout);
    }
}