package com.UERJ.POO3.presentation.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login - Petshop ERP")
@AnonymousAllowed // Permite acesso sem estar logado
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 titulo = new H1("Petshop ERP");

        // Configura o formulário padrão do Vaadin para enviar os dados para o Spring Security
        loginForm.setAction("login");

        // Redireciona para a nossa tela de recuperação de senha quando o usuário clica em "Esqueci a senha"
        loginForm.addForgotPasswordListener(e -> UI.getCurrent().navigate(RecuperacaoSenhaView.class));

        // Botão visual para o requisito de OAuth2 (Google)
        Button btnGoogle = new Button("Entrar com Google", new Icon(VaadinIcon.GOOGLE_PLUS));
        btnGoogle.addThemeVariants(ButtonVariant.LUMO_ERROR); // Deixa o botão vermelho (estilo Google)
        btnGoogle.setWidthFull();
        // O clique redirecionará para a rota padrão do Spring Security OAuth2
        btnGoogle.addClickListener(e -> UI.getCurrent().getPage().setLocation("/oauth2/authorization/google"));

        // Cria um layout de coluna para agrupar o formulário e o botão do Google
        VerticalLayout formLayout = new VerticalLayout(titulo, loginForm, btnGoogle);
        formLayout.setAlignItems(Alignment.CENTER);
        formLayout.setMaxWidth("400px");
        formLayout.getStyle().set("border", "1px solid #ccc").set("padding", "20px").set("border-radius", "8px");

        add(formLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Se o Spring Security retornar um erro na URL (ex: /login?error), exibe a mensagem de erro no form
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
