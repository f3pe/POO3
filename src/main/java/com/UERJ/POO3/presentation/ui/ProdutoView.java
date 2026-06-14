package com.UERJ.POO3.presentation.ui;

import com.UERJ.POO3.modules.compras.domain.Fornecedor;
import com.UERJ.POO3.modules.compras.repository.FornecedorRepository;
import com.UERJ.POO3.modules.estoque.domain.Categoria;
import com.UERJ.POO3.modules.estoque.domain.Produto;
import com.UERJ.POO3.modules.estoque.repository.CategoriaRepository;
import com.UERJ.POO3.modules.estoque.service.ProdutoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route("estoque")
@PageTitle("Gestão de Estoque - Petshop ERP")
@RolesAllowed({"ADMIN", "GERENTE"})
public class ProdutoView extends VerticalLayout {

    private final ProdutoService produtoService;
    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;

    private final Grid<Produto> gridProdutos = new Grid<>(Produto.class, false);

    // Construtor atualizado para injetar os repositórios necessários para as listas suspensas
    public ProdutoView(ProdutoService produtoService,
                       CategoriaRepository categoriaRepository,
                       FornecedorRepository fornecedorRepository) {
        this.produtoService = produtoService;
        this.categoriaRepository = categoriaRepository;
        this.fornecedorRepository = fornecedorRepository;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H2 titulo = new H2("Controle de Estoque");

        Button btnNovoProduto = new Button("Adicionar Novo Produto", new Icon(VaadinIcon.PLUS));
        btnNovoProduto.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Liga o botão ao método que abre o formulário
        btnNovoProduto.addClickListener(e -> abrirDialogoNovoProduto());

        HorizontalLayout toolbar = new HorizontalLayout(btnNovoProduto);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.END);

        configurarGrid();
        atualizarGrid();

        add(titulo, toolbar, gridProdutos);
    }

    private void configurarGrid() {
        gridProdutos.setWidthFull();

        gridProdutos.addColumn(Produto::getNome).setHeader("Produto").setSortable(true);
        gridProdutos.addColumn(produto -> "R$ " + String.format("%.2f", produto.getPreco())).setHeader("Preço");
        gridProdutos.addColumn(Produto::getQuantidadeAtual).setHeader("Qtd Atual").setSortable(true);
        gridProdutos.addColumn(Produto::getQuantidadeMinima).setHeader("Qtd Mínima");
        gridProdutos.addColumn(produto -> produto.getCategoria().getNome()).setHeader("Categoria");
        gridProdutos.addColumn(produto -> produto.getFornecedor().getRazaoSocial()).setHeader("Fornecedor");

        gridProdutos.addComponentColumn(produto -> {
            Button btnExcluir = new Button(new Icon(VaadinIcon.TRASH));
            btnExcluir.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            btnExcluir.setTooltipText("Desativar Produto");

            btnExcluir.addClickListener(e -> {
                produtoService.excluirProduto(produto.getId());
                Notification.show("Produto removido com sucesso!")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                atualizarGrid();
            });
            return btnExcluir;
        }).setHeader("Ações").setAutoWidth(true).setFlexGrow(0);
    }

    private void atualizarGrid() {
        List<Produto> produtos = produtoService.listarProdutos();
        gridProdutos.setItems(produtos);
    }

    // --- O NOVO FORMULÁRIO DE CADASTRO ---
    private void abrirDialogoNovoProduto() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Registar Novo Produto");

        // Campos de Texto e Números
        TextField nomeField = new TextField("Nome do Produto");
        nomeField.setWidthFull();

        NumberField precoField = new NumberField("Preço (R$)");
        precoField.setWidthFull();

        IntegerField qtdAtualField = new IntegerField("Quantidade Atual");
        qtdAtualField.setWidthFull();

        IntegerField qtdMinimaField = new IntegerField("Quantidade Mínima");
        qtdMinimaField.setWidthFull();

        // Listas Suspensas (ComboBox) para os Relacionamentos
        ComboBox<Categoria> categoriaBox = new ComboBox<>("Categoria");
        categoriaBox.setItems(categoriaRepository.findAll());
        categoriaBox.setItemLabelGenerator(Categoria::getNome);
        categoriaBox.setWidthFull();

        ComboBox<Fornecedor> fornecedorBox = new ComboBox<>("Fornecedor");
        fornecedorBox.setItems(fornecedorRepository.findAll());
        fornecedorBox.setItemLabelGenerator(Fornecedor::getRazaoSocial);
        fornecedorBox.setWidthFull();

        // Layout do Formulário
        VerticalLayout formLayout = new VerticalLayout(
                nomeField, precoField, qtdAtualField, qtdMinimaField, categoriaBox, fornecedorBox
        );
        formLayout.setPadding(false);
        formLayout.setSpacing(true);
        formLayout.setWidth("400px");

        // Botões de Ação do Dialog
        Button btnGuardar = new Button("Guardar", e -> {
            // Validação simples
            if (nomeField.isEmpty() || precoField.isEmpty() || categoriaBox.isEmpty() || fornecedorBox.isEmpty()) {
                Notification.show("Preencha todos os campos obrigatórios!").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Criação do novo objeto Produto
            Produto novoProduto = new Produto();
            novoProduto.setNome(nomeField.getValue());
            novoProduto.setPreco(precoField.getValue());
            novoProduto.setQuantidadeAtual(qtdAtualField.getValue() != null ? qtdAtualField.getValue() : 0);
            novoProduto.setQuantidadeMinima(qtdMinimaField.getValue() != null ? qtdMinimaField.getValue() : 0);
            novoProduto.setCategoria(categoriaBox.getValue());
            novoProduto.setFornecedor(fornecedorBox.getValue());

            // Guarda na base de dados usando o nosso serviço
            produtoService.salvarProduto(novoProduto);

            Notification.show("Produto guardado com sucesso!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            atualizarGrid(); // Atualiza a tabela principal
            dialog.close();  // Fecha a janela
        });
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.add(formLayout);
        dialog.getFooter().add(btnCancelar, btnGuardar);

        dialog.open();
    }
}