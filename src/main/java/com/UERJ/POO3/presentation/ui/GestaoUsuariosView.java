package com.UERJ.POO3.presentation.ui;

import com.UERJ.POO3.modules.seguranca.domain.Papel;
import com.UERJ.POO3.modules.seguranca.domain.Usuario;
import com.UERJ.POO3.modules.seguranca.repository.PapelRepository;
import com.UERJ.POO3.modules.seguranca.repository.UsuarioRepository;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Route(value = "usuarios", layout = MainLayout.class)
@PageTitle("Gestão de Usuários - Petshop ERP")
@RolesAllowed({"ADMIN"})
public class GestaoUsuariosView extends VerticalLayout {

    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;
    private final PasswordEncoder passwordEncoder;

    private final Grid<Usuario> gridUsuarios = new Grid<>(Usuario.class, false);

    public GestaoUsuariosView(UsuarioRepository usuarioRepository, PapelRepository papelRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.papelRepository = papelRepository;
        this.passwordEncoder = passwordEncoder;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H2 titulo = new H2("Gestão de Utilizadores e Perfis");

        Button btnNovoUsuario = new Button("Novo Utilizador", new Icon(VaadinIcon.PLUS));
        btnNovoUsuario.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNovoUsuario.addClickListener(e -> abrirDialogoCadastro());

        configurarGrid();
        atualizarGrid();

        HorizontalLayout toolbar = new HorizontalLayout(btnNovoUsuario);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.END);

        add(titulo, toolbar, gridUsuarios);
    }

    private void configurarGrid() {
        gridUsuarios.setWidthFull();
        gridUsuarios.addColumn(Usuario::getUsername).setHeader("Nome de Utilizador").setSortable(true);
        gridUsuarios.addColumn(Usuario::getEmail).setHeader("E-mail");

        // Mostra os papéis atribuídos ao utilizador
        gridUsuarios.addColumn(usuario -> {
            StringBuilder papeis = new StringBuilder();
            usuario.getPapeis().forEach(p -> papeis.append(p.getNome()).append(" "));
            return papeis.toString();
        }).setHeader("Perfil (RBAC)");
    }

    private void atualizarGrid() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        gridUsuarios.setItems(usuarios);
    }

    private void abrirDialogoCadastro() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Cadastrar Novo Utilizador");

        TextField campoUsername = new TextField("Nome de Utilizador");
        EmailField campoEmail = new EmailField("E-mail");
        PasswordField campoSenha = new PasswordField("Palavra-passe");

        ComboBox<Papel> comboPapel = new ComboBox<>("Perfil de Acesso");
        comboPapel.setItems(papelRepository.findAll());
        comboPapel.setItemLabelGenerator(Papel::getNome);

        VerticalLayout layoutForm = new VerticalLayout(campoUsername, campoEmail, campoSenha, comboPapel);
        layoutForm.setPadding(false);

        Button btnSalvar = new Button("Salvar", e -> {
            if (campoUsername.isEmpty() || campoEmail.isEmpty() || campoSenha.isEmpty() || comboPapel.isEmpty()) {
                Notification.show("Preencha todos os campos!").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Verifica se o username já existe
            if (usuarioRepository.findByUsername(campoUsername.getValue()).isPresent()) {
                Notification.show("Erro: Este nome de utilizador já está em uso!").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                Usuario novoUsuario = new Usuario();
                novoUsuario.setUsername(campoUsername.getValue());
                novoUsuario.setEmail(campoEmail.getValue());

                // Encripta a palavra-passe com BCrypt antes de guardar na base de dados
                novoUsuario.setPassword(passwordEncoder.encode(campoSenha.getValue()));

                // Associa o papel (RBAC)
                novoUsuario.getPapeis().add(comboPapel.getValue());

                usuarioRepository.save(novoUsuario);

                Notification.show("Utilizador cadastrado com sucesso!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                atualizarGrid();
                dialog.close();

            } catch (Exception ex) {
                Notification.show("Erro ao salvar utilizador.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.add(layoutForm);
        dialog.getFooter().add(btnCancelar, btnSalvar);
        dialog.open();
    }
}