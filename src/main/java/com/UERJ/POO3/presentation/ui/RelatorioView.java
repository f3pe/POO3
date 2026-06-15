package com.UERJ.POO3.presentation.ui;

import com.UERJ.POO3.modules.compras.domain.Compra;
import com.UERJ.POO3.modules.compras.repository.CompraRepository;
import com.UERJ.POO3.modules.estoque.domain.Produto;
import com.UERJ.POO3.modules.estoque.repository.ProdutoRepository;
import com.UERJ.POO3.modules.vendas.domain.Venda;
import com.UERJ.POO3.modules.vendas.repository.VendaRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Route(value = "relatorios", layout = MainLayout.class)
@PageTitle("Relatórios Financeiros - Petshop ERP")
@RolesAllowed({"ADMIN", "GERENTE"}) // Apenas gestores podem ver dados financeiros
public class RelatorioView extends VerticalLayout {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final CompraRepository compraRepository;

    public RelatorioView(VendaRepository vendaRepository, ProdutoRepository produtoRepository, CompraRepository compraRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
        this.compraRepository = compraRepository;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H2 titulo = new H2("Central de Relatórios Financeiros");

        // Painel 1: Vendas por Período
        VerticalLayout painelVendas = criarPainelRelatorioVendas();

        // Painel 2: Produtos com Baixo Estoque
        VerticalLayout painelEstoque = criarPainelRelatorioEstoque();

        // Painel 3: Compras Realizadas
        VerticalLayout painelCompras = criarPainelRelatorioCompras();

        HorizontalLayout paineisSuperiores = new HorizontalLayout(painelVendas, painelEstoque);
        paineisSuperiores.setWidthFull();
        paineisSuperiores.setJustifyContentMode(JustifyContentMode.CENTER);

        add(titulo, paineisSuperiores, painelCompras);
    }

    private VerticalLayout criarPainelRelatorioVendas() {
        VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("border", "1px solid #ccc").set("border-radius", "8px").set("padding", "16px");
        layout.setWidth("45%");

        H3 titulo = new H3("Vendas por Período");
        DatePicker dataInicio = new DatePicker("Data Inicial");
        DatePicker dataFim = new DatePicker("Data Final");

        Button btnExportarCSV = new Button("Exportar CSV", new Icon(VaadinIcon.FILE_TABLE));
        btnExportarCSV.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout botoes = new HorizontalLayout();

        // Lógica de Geração do CSV
        btnExportarCSV.addClickListener(e -> {
            if (dataInicio.getValue() != null && dataFim.getValue() != null) {
                LocalDateTime inicio = dataInicio.getValue().atStartOfDay();
                LocalDateTime fim = dataFim.getValue().atTime(23, 59, 59);
                List<Venda> vendas = vendaRepository.findVendasComClientePorPeriodo(inicio, fim);

                StreamResource resource = gerarCsvVendas(vendas);
                Anchor link = new Anchor(resource, "Baixar CSV Gerado");
                link.getElement().setAttribute("download", true);
                botoes.removeAll();
                botoes.add(link);
            }
        });

        layout.add(titulo, new HorizontalLayout(dataInicio, dataFim), btnExportarCSV, botoes);
        return layout;
    }

    private VerticalLayout criarPainelRelatorioEstoque() {
        VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("border", "1px solid #ccc").set("border-radius", "8px").set("padding", "16px");
        layout.setWidth("45%");

        H3 titulo = new H3("Produtos com Baixo Estoque");

        Button btnExportarPDF = new Button("Gerar PDF", new Icon(VaadinIcon.FILE_O));
        btnExportarPDF.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout areaDownload = new HorizontalLayout();

        btnExportarPDF.addClickListener(e -> {
            List<Produto> produtos = produtoRepository.findProdutosComEstoqueBaixo();
            StreamResource resource = gerarPdfEstoqueBaixo(produtos);
            Anchor link = new Anchor(resource, "Baixar PDF de Estoque");
            link.getElement().setAttribute("download", true);
            areaDownload.removeAll();
            areaDownload.add(link);
        });

        layout.add(titulo, btnExportarPDF, areaDownload);
        return layout;
    }

    private VerticalLayout criarPainelRelatorioCompras() {
        VerticalLayout layout = new VerticalLayout();
        layout.getStyle().set("border", "1px solid #ccc").set("border-radius", "8px").set("padding", "16px");
        layout.setWidth("45%");

        H3 titulo = new H3("Fornecedores e Compras Realizadas");

        Button btnExportarCSV = new Button("Exportar Histórico (CSV)", new Icon(VaadinIcon.FILE_TABLE));
        btnExportarCSV.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        HorizontalLayout areaDownload = new HorizontalLayout();

        btnExportarCSV.addClickListener(e -> {
            List<Compra> compras = compraRepository.findAllComFornecedores();
            StreamResource resource = gerarCsvCompras(compras);
            Anchor link = new Anchor(resource, "Baixar CSV de Compras");
            link.getElement().setAttribute("download", true);
            areaDownload.removeAll();
            areaDownload.add(link);
        });

        layout.add(titulo, btnExportarCSV, areaDownload);
        return layout;
    }

    // --- MÉTODOS GERADORES DE ARQUIVO ---

    private StreamResource gerarCsvVendas(List<Venda> vendas) {
        return new StreamResource("relatorio_vendas.csv", () -> {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                PrintWriter writer = new PrintWriter(out);
                writer.println("ID Venda,Data,Cliente,Forma Pagamento,Total (R$)");

                for (Venda v : vendas) {
                    // Proteção contra dados nulos na base de dados
                    String data = v.getDataVenda() != null ? v.getDataVenda().toString() : "Data Indisponível";
                    String cliente = (v.getCliente() != null && v.getCliente().getUsername() != null)
                            ? v.getCliente().getUsername() : "Cliente Desconhecido";
                    String formaPagamento = v.getFormaPagamento() != null ? v.getFormaPagamento() : "N/A";
                    Double total = v.getValorTotal() != null ? v.getValorTotal() : 0.0;

                    writer.printf("%d,%s,%s,%s,%.2f\n",
                            v.getId(), data, cliente, formaPagamento, total);
                }
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
                // Se der um erro crítico, geramos um CSV com a mensagem de erro em vez de devolver null!
                PrintWriter errorWriter = new PrintWriter(out);
                errorWriter.println("Erro ao gerar relatorio: " + e.getMessage());
                errorWriter.flush();
            }
            return new ByteArrayInputStream(out.toByteArray());
        });
    }

    private StreamResource gerarCsvCompras(List<Compra> compras) {
        return new StreamResource("relatorio_compras.csv", () -> {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                PrintWriter writer = new PrintWriter(out);
                writer.println("ID Compra,Data,Fornecedor,Status,Total (R$)");

                for (Compra c : compras) {
                    // Proteção contra dados nulos na base de dados
                    String data = c.getDataCompra() != null ? c.getDataCompra().toString() : "Data Indisponível";
                    String fornecedor = (c.getFornecedor() != null && c.getFornecedor().getRazaoSocial() != null)
                            ? c.getFornecedor().getRazaoSocial() : "Fornecedor Desconhecido";
                    String status = c.getStatus() != null ? c.getStatus() : "N/A";
                    Double total = c.getValorTotal() != null ? c.getValorTotal() : 0.0;

                    writer.printf("%d,%s,%s,%s,%.2f\n",
                            c.getId(), data, fornecedor, status, total);
                }
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
                PrintWriter errorWriter = new PrintWriter(out);
                errorWriter.println("Erro ao gerar relatorio: " + e.getMessage());
                errorWriter.flush();
            }
            return new ByteArrayInputStream(out.toByteArray());
        });
    }

    private StreamResource gerarPdfEstoqueBaixo(List<Produto> produtos) {
        return new StreamResource("relatorio_estoque_baixo.pdf", () -> {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, out);
                document.open();

                document.add(new Paragraph("=== RELATÓRIO DE ESTOQUE CRÍTICO ==="));
                document.add(new Paragraph("Data de Geração: " + LocalDate.now().toString()));
                document.add(new Paragraph(" "));

                if (produtos.isEmpty()) {
                    document.add(new Paragraph("Todos os produtos estão com estoque regular."));
                } else {
                    for (Produto p : produtos) {
                        // Proteção contra dados nulos na base de dados
                        String nomeProduto = p.getNome() != null ? p.getNome() : "Produto Desconhecido";
                        String nomeFornecedor = (p.getFornecedor() != null && p.getFornecedor().getRazaoSocial() != null)
                                ? p.getFornecedor().getRazaoSocial() : "Fornecedor Desconhecido";

                        document.add(new Paragraph("- Produto: " + nomeProduto +
                                " | Qtd Atual: " + p.getQuantidadeAtual() +
                                " | Qtd Mínima: " + p.getQuantidadeMinima() +
                                " | Fornecedor: " + nomeFornecedor));
                    }
                }
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
                // Em caso de erro crítico, devolvemos um PDF de texto simples a avisar do erro
                try {
                    Document errorDoc = new Document();
                    PdfWriter.getInstance(errorDoc, out);
                    errorDoc.open();
                    errorDoc.add(new Paragraph("Erro crítico ao gerar o relatório de stock: " + e.getMessage()));
                    errorDoc.close();
                } catch (Exception ex) {
                    // Ignora
                }
            }
            return new ByteArrayInputStream(out.toByteArray());
        });
    }
}