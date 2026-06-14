package com.UERJ.POO3.presentation.ui;

import com.UERJ.POO3.modules.compras.domain.Compra;
import com.UERJ.POO3.modules.compras.domain.Fornecedor;
import com.UERJ.POO3.modules.compras.domain.ItemCompra;
import com.UERJ.POO3.modules.compras.repository.FornecedorRepository;
import com.UERJ.POO3.modules.compras.service.CompraService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route("fornecedores")
@PageTitle("Gestão de Fornecedores - Petshop ERP")
// Proteção de Rota: Apenas Admin e Gerente podem gerir compras
@RolesAllowed({"ADMIN", "GERENTE"})
public class FornecedorView extends VerticalLayout {

    private final FornecedorRepository fornecedorRepository;
    private final CompraService compraService;
    private final Grid<Fornecedor> gridFornecedores = new Grid<>(Fornecedor.class, false);

    public FornecedorView(FornecedorRepository fornecedorRepository, CompraService compraService) {
        this.fornecedorRepository = fornecedorRepository;
        this.compraService = compraService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H2 titulo = new H2("Gestão de Fornecedores e Compras");

        configurarGrid();
        atualizarGrid();

        add(titulo, gridFornecedores);
    }

    private void configurarGrid() {
        gridFornecedores.setWidthFull();

        // Colunas com os dados exigidos pelo edital
        gridFornecedores.addColumn(Fornecedor::getRazaoSocial).setHeader("Fornecedor").setSortable(true);
        gridFornecedores.addColumn(Fornecedor::getCnpj).setHeader("CNPJ");
        gridFornecedores.addColumn(Fornecedor::getEmail).setHeader("E-mail");
        gridFornecedores.addColumn(Fornecedor::getTelefone).setHeader("Telefone");

        // Coluna de Ações: A Simulação de Pedidos Automáticos
        gridFornecedores.addComponentColumn(fornecedor -> {
            Button btnSimular = new Button("Simular Pedido", new Icon(VaadinIcon.MAGIC));
            btnSimular.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            btnSimular.setTooltipText("Gera sugestão de compra baseada nos últimos 30 dias");

            btnSimular.addClickListener(e -> abrirDialogoSimulacao(fornecedor));
            return btnSimular;
        }).setHeader("Inteligência de Compras").setAutoWidth(true).setFlexGrow(0);
    }

    private void atualizarGrid() {
        List<Fornecedor> fornecedores = fornecedorRepository.findAll();
        gridFornecedores.setItems(fornecedores);
    }

    // --- A JANELA DE SIMULAÇÃO DE PEDIDO AUTOMÁTICO ---
    private void abrirDialogoSimulacao(Fornecedor fornecedor) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeaderTitle("Simulação Automática: " + fornecedor.getRazaoSocial());

        // 1. Chama o nosso Service para executar o algoritmo matemático
        Compra sugestaoCompra = compraService.simularPedidoAutomatico(fornecedor.getId());

        if (sugestaoCompra.getItens().isEmpty()) {
            dialog.add(new Span("O sistema analisou a demanda e o stock atual. Não há necessidade de reposição para este fornecedor no momento."));
            Button btnFechar = new Button("Fechar", e -> dialog.close());
            dialog.getFooter().add(btnFechar);
            dialog.open();
            return;
        }

        // 2. Apresenta o resultado num Grid interno caso existam produtos em falta
        Grid<ItemCompra> gridItens = new Grid<>(ItemCompra.class, false);
        gridItens.addColumn(item -> item.getProduto().getNome()).setHeader("Produto").setAutoWidth(true);
        gridItens.addColumn(ItemCompra::getQuantidade).setHeader("Qtd Sugerida");
        gridItens.addColumn(item -> "R$ " + String.format("%.2f", item.getPrecoCusto())).setHeader("Preço Custo (Est.)");
        gridItens.addColumn(item -> "R$ " + String.format("%.2f", item.getQuantidade() * item.getPrecoCusto())).setHeader("Subtotal");
        gridItens.setItems(sugestaoCompra.getItens());
        gridItens.setAllRowsVisible(true);

        H3 totalLabel = new H3("Valor Total Estimado: R$ " + String.format("%.2f", sugestaoCompra.getValorTotal()));

        VerticalLayout layout = new VerticalLayout(
                new Span("Sugestão baseada na demanda média dos últimos 30 dias e stock atual."),
                gridItens,
                totalLabel
        );
        layout.setPadding(false);

        // 3. Botões de ação para o Gerente decidir se aprova o algoritmo ou não
        Button btnEfetivar = new Button("Efetivar Pedido", new Icon(VaadinIcon.CHECK));
        btnEfetivar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEfetivar.addClickListener(e -> {
            compraService.efetivarCompra(sugestaoCompra);
            Notification.show("Pedido de compra registado com sucesso no histórico!")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            dialog.close();
        });

        Button btnCancelar = new Button("Cancelar Simulação", e -> dialog.close());
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.add(layout);
        dialog.getFooter().add(btnCancelar, btnEfetivar);
        dialog.open();
    }
}