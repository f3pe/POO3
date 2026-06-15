package com.UERJ.POO3.presentation.ui;

import com.UERJ.POO3.modules.estoque.domain.Produto;
import com.UERJ.POO3.modules.estoque.service.ProdutoService;
import com.UERJ.POO3.modules.seguranca.domain.Usuario;
import com.UERJ.POO3.modules.seguranca.repository.UsuarioRepository;
import com.UERJ.POO3.modules.vendas.domain.ItemVenda;
import com.UERJ.POO3.modules.vendas.domain.Venda;
import com.UERJ.POO3.modules.vendas.service.VendaService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Route(value = "vendas", layout = MainLayout.class)
@PageTitle("Ponto de Venda (Caixa) - Petshop ERP")
@RolesAllowed({"ADMIN", "GERENTE", "FUNCIONARIO"})
public class VendaView extends VerticalLayout {

    private final ProdutoService produtoService;
    private final VendaService vendaService;
    private final UsuarioRepository usuarioRepository; // Novo: Repositório de Usuários

    private Venda carrinhoAtual = new Venda();
    private final Grid<ItemVenda> gridCarrinho = new Grid<>(ItemVenda.class, false);
    private final H3 labelTotal = new H3("Total: R$ 0.00");

    // Novo: Caixa de seleção de Clientes
    private final ComboBox<Usuario> comboCliente = new ComboBox<>("Selecionar Cliente");

    // Adicionamos o UsuarioRepository no construtor
    public VendaView(ProdutoService produtoService, VendaService vendaService, UsuarioRepository usuarioRepository) {
        this.produtoService = produtoService;
        this.vendaService = vendaService;
        this.usuarioRepository = usuarioRepository;

        setSizeFull();
        H2 titulo = new H2("Ponto de Venda (PDV)");

        HorizontalLayout layoutPrincipal = new HorizontalLayout();
        layoutPrincipal.setWidthFull();
        layoutPrincipal.setHeightFull();

        VerticalLayout painelProdutos = criarPainelProdutos();
        painelProdutos.setWidth("50%");

        VerticalLayout painelCarrinho = criarPainelCarrinho();
        painelCarrinho.setWidth("50%");

        layoutPrincipal.add(painelProdutos, painelCarrinho);
        add(titulo, layoutPrincipal);
    }

    private VerticalLayout criarPainelProdutos() {
        VerticalLayout layout = new VerticalLayout();
        layout.add(new H3("Adicionar Produtos"));

        ComboBox<Produto> comboProdutos = new ComboBox<>("Selecionar Produto");
        comboProdutos.setItems(produtoService.listarProdutos());
        comboProdutos.setItemLabelGenerator(Produto::getNome);
        comboProdutos.setWidthFull();

        IntegerField campoQuantidade = new IntegerField("Quantidade");
        campoQuantidade.setValue(1);
        campoQuantidade.setMin(1);

        Button btnAdicionar = new Button("Adicionar ao Carrinho", new Icon(VaadinIcon.CART));
        btnAdicionar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAdicionar.addClickListener(e -> {
            Produto produtoSelecionado = comboProdutos.getValue();
            if (produtoSelecionado != null && campoQuantidade.getValue() != null) {
                if (produtoSelecionado.getQuantidadeAtual() < campoQuantidade.getValue()) {
                    Notification.show("Erro: Stock insuficiente!").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }
                ItemVenda novoItem = new ItemVenda(produtoSelecionado, campoQuantidade.getValue(), produtoSelecionado.getPreco());
                carrinhoAtual.adicionarItem(novoItem);
                atualizarCarrinho();
                Notification.show("Produto adicionado!");
            }
        });

        layout.add(comboProdutos, campoQuantidade, btnAdicionar);
        return layout;
    }

    private VerticalLayout criarPainelCarrinho() {
        VerticalLayout layout = new VerticalLayout();
        layout.add(new H3("Carrinho de Compras"));

        // Novo: Preenchemos o ComboBox com os usuários do sistema
        comboCliente.setItems(usuarioRepository.findAll());
        comboCliente.setItemLabelGenerator(Usuario::getUsername);
        comboCliente.setWidthFull();

        gridCarrinho.addColumn(item -> item.getProduto().getNome()).setHeader("Produto");
        gridCarrinho.addColumn(ItemVenda::getQuantidade).setHeader("Qtd");
        gridCarrinho.addColumn(item -> "R$ " + String.format("%.2f", item.getPrecoUnitario())).setHeader("Preço Unit.");
        gridCarrinho.addColumn(item -> "R$ " + String.format("%.2f", item.getQuantidade() * item.getPrecoUnitario())).setHeader("Subtotal");

        ComboBox<String> comboPagamento = new ComboBox<>("Forma de Pagamento");
        comboPagamento.setItems("Pix (Simulação)", "Cartão de Crédito", "Cartão de Débito", "Boleto");
        comboPagamento.setValue("Pix (Simulação)");

        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.setJustifyContentMode(JustifyContentMode.BETWEEN);
        footer.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Button btnFinalizar = new Button("Finalizar Venda", new Icon(VaadinIcon.CHECK));
        btnFinalizar.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout areaRecibo = new HorizontalLayout();

        btnFinalizar.addClickListener(e -> {
            // Nova Validação: Impede a venda se não houver cliente selecionado
            if (comboCliente.isEmpty()) {
                Notification.show("Selecione um cliente para prosseguir com a venda!").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            if (carrinhoAtual.getItens().isEmpty()) {
                Notification.show("O carrinho está vazio!").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                // Vinculamos o cliente escolhido ao carrinho antes de salvar no banco!
                carrinhoAtual.setCliente(comboCliente.getValue());

                Venda vendaConcluida = vendaService.finalizarVenda(carrinhoAtual, comboPagamento.getValue());
                Notification.show("Venda finalizada com sucesso!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                Anchor linkDownload = criarBotaoDownloadPdf(vendaConcluida);
                areaRecibo.removeAll();
                areaRecibo.add(linkDownload);

                carrinhoAtual = new Venda();
                atualizarCarrinho();
                comboCliente.clear(); // Limpa o cliente para a próxima venda

            } catch (Exception ex) {
                Notification.show("Erro ao finalizar: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        // Adicionamos o comboCliente na tela
        footer.add(labelTotal, comboPagamento, btnFinalizar);
        layout.add(comboCliente, gridCarrinho, footer, areaRecibo);
        return layout;
    }

    private void atualizarCarrinho() {
        gridCarrinho.setItems(carrinhoAtual.getItens());
        labelTotal.setText("Total: R$ " + String.format("%.2f", carrinhoAtual.getValorTotal()));
    }

    private Anchor criarBotaoDownloadPdf(Venda venda) {
        StreamResource resource = new StreamResource("recibo_venda_" + venda.getId() + ".pdf", () -> {
            try {
                Document document = new Document();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PdfWriter.getInstance(document, out);
                document.open();

                document.add(new Paragraph("=== PETSHOP ERP - RECIBO DE VENDA ==="));
                document.add(new Paragraph("Venda ID: " + venda.getId()));
                document.add(new Paragraph("Cliente: " + venda.getCliente().getUsername())); // Mostra o cliente no PDF!
                document.add(new Paragraph("Data: " + venda.getDataVenda().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
                document.add(new Paragraph("Forma de Pagamento: " + venda.getFormaPagamento()));
                document.add(new Paragraph(" "));
                document.add(new Paragraph("ITENS:"));

                for (ItemVenda item : venda.getItens()) {
                    document.add(new Paragraph("- " + item.getQuantidade() + "x " + item.getProduto().getNome() +
                            " | R$ " + String.format("%.2f", item.getPrecoUnitario())));
                }

                document.add(new Paragraph(" "));
                document.add(new Paragraph("TOTAL: R$ " + String.format("%.2f", venda.getValorTotal())));
                document.add(new Paragraph("Obrigado pela preferência!"));

                document.close();
                return new ByteArrayInputStream(out.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        Anchor link = new Anchor(resource, "Descarregar Recibo (PDF)");
        link.getElement().setAttribute("download", true);
        Button btnDownload = new Button("Descarregar Recibo (PDF)", new Icon(VaadinIcon.DOWNLOAD));
        link.add(btnDownload);
        return link;
    }
}