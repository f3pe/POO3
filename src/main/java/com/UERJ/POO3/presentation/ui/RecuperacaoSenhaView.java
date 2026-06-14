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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("recuperar-senha")
@PageTitle("Recuperar Senha - Petshop ERP")
@AnonymousAllowed
public class RecuperacaoSenhaView extends VerticalLayout {

    private final UsuarioService usuarioService;

    public RecuperacaoSenhaView(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H2 titulo = new H2("Recuperação de Senha");
        Paragraph instrucao = new Paragraph("Informe seu e-mail cadastrado para receber o token de recuperação.");

        EmailField emailField = new EmailField("E-mail");
        emailField.setWidth("300px");
        emailField.setRequiredIndicatorVisible(true);

        Button btnEnviar = new Button("Enviar Token por E-mail");
        btnEnviar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnVoltar = new Button("Voltar para o Login", e -> UI.getCurrent().navigate(LoginView.class));
        btnVoltar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        btnEnviar.addClickListener(event -> {
            if (emailField.isEmpty() || emailField.isInvalid()) {
                Notification.show("Por favor, insira um e-mail válido.").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                // Chama o nosso back-end para gerar o token e enviar o e-mail via Mailtrap
                usuarioService.gerarTokenRecuperacaoSenha(emailField.getValue());

                Notification sucesso = Notification.show("E-mail enviado! Verifique sua caixa de entrada.");
                sucesso.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // Redireciona o usuário para a tela onde ele vai digitar o token e a nova senha
                UI.getCurrent().navigate(RedefinirSenhaView.class);

            } catch (IllegalArgumentException ex) {
                Notification.show(ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        VerticalLayout container = new VerticalLayout(titulo, instrucao, emailField, btnEnviar, btnVoltar);
        container.setAlignItems(Alignment.CENTER);
        container.setMaxWidth("400px");

        add(container);
    }
}