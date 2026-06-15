package com.UERJ.POO3.presentation.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;

@PermitAll
public class MainLayout extends AppLayout {

    private final AuthenticationContext authContext;

    public MainLayout(AuthenticationContext authContext) {
        this.authContext = authContext;
        criarCabecalho();
        criarMenuLateral();
    }

    private void criarCabecalho() {
        H1 logo = new H1("Petshop ERP");
        logo.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        DrawerToggle toggle = new DrawerToggle();

        Button btnLogout = new Button("Sair", VaadinIcon.SIGN_OUT.create());
        btnLogout.addClickListener(e -> authContext.logout());

        HorizontalLayout cabecalho = new HorizontalLayout(toggle, logo, btnLogout);
        cabecalho.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        cabecalho.expand(logo); // Faz o logo ocupar o espaço vazio, empurrando o botão Sair para a direita
        cabecalho.setWidthFull();
        cabecalho.getStyle().set("padding", "0 var(--lumo-space-m)");

        addToNavbar(cabecalho);
    }

    private void criarMenuLateral() {
        // Os RouterLinks ligam os textos às telas que já construímos
        RouterLink linkDashboard = new RouterLink("Dashboard", MainView.class);
        RouterLink linkVendas = new RouterLink("Ponto de Venda", VendaView.class);
        RouterLink linkEstoque = new RouterLink("Gestão de Estoque", ProdutoView.class);
        RouterLink linkCompras = new RouterLink("Fornecedores e Compras", FornecedorView.class);
        RouterLink linkRelatorios = new RouterLink("Relatórios Financeiros", RelatorioView.class);
        RouterLink linkUsuarios = new RouterLink("Gestão de Utilizadores", GestaoUsuariosView.class);

        VerticalLayout menu = new VerticalLayout(linkDashboard, linkVendas, linkEstoque, linkCompras, linkRelatorios, linkUsuarios);
        addToDrawer(menu);
    }
}