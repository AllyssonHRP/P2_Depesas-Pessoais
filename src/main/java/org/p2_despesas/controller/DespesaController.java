package org.p2_despesas.controller;

import org.p2_despesas.dao.DespesaDAO;
import org.p2_despesas.model.Despesa;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DespesaController {

    private final DespesaDAO dao = new DespesaDAO();
    private final ObservableList<Despesa> despesasData = FXCollections.observableArrayList();

    // Componentes da Interface
    private VBox view;
    private TableView<Despesa> tabelaDespesas;
    private TextField txtDescricao;
    private TextField txtValor;
    private DatePicker dpData;
    private ComboBox<String> cmbCategoria;
    private Label lblTotal;

    private Button btnAdicionar;
    private Button btnAtualizar;
    private Button btnExcluir;
    private Button btnLimpar;

    // Objeto selecionado na tabela
    private Despesa despesaSelecionada = null;

    // Formatador de Data para exibição brasileira (dd/MM/yyyy)
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DespesaController() {
        configurarGUI();
        carregarDadosDoBanco(); // Carrega os dados assim que a tela abre
    }

    public VBox getView() {
        return view;
    }

    // --- Configuração da Interface (GUI) ---

    private void configurarGUI() {
        // 1. Título
        Label lblTitulo = new Label("💰 Controle de Despesas");
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        // 2. Formulário
        GridPane formGrid = criarFormulario();

        // 3. Tabela
        tabelaDespesas = criarTabela();

        // 4. Rodapé (Total)
        lblTotal = new Label("Total: R$ 0,00");
        lblTotal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        HBox footer = new HBox(lblTotal);
        footer.setAlignment(Pos.CENTER_RIGHT);

        // 5. Layout Principal
        view = new VBox(15);
        view.setPadding(new Insets(20));
        view.getChildren().addAll(lblTitulo, new Separator(), formGrid, tabelaDespesas, footer);
    }

    private GridPane criarFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Campos
        txtDescricao = new TextField();
        txtDescricao.setPromptText("Ex: Almoço no shopping");

        txtValor = new TextField();
        txtValor.setPromptText("0.00");

        dpData = new DatePicker(LocalDate.now());

        cmbCategoria = new ComboBox<>();
        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Alimentação", "Transporte", "Lazer", "Moradia", "Educação", "Saúde", "Outros"
        ));
        cmbCategoria.getSelectionModel().selectFirst();

        // Botões
        btnAdicionar = new Button("Adicionar");
        btnAtualizar = new Button("Atualizar");
        btnExcluir = new Button("Excluir");
        btnLimpar = new Button("Limpar");

        // Estilizando Botões (Opcional)
        btnAdicionar.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnExcluir.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");

        // Estado inicial dos botões
        btnAtualizar.setDisable(true);
        btnExcluir.setDisable(true);

        // Ações dos Botões
        btnAdicionar.setOnAction(e -> salvarDespesa(true)); // true = nova despesa
        btnAtualizar.setOnAction(e -> salvarDespesa(false)); // false = atualizar existente
        btnExcluir.setOnAction(e -> excluirDespesa());
        btnLimpar.setOnAction(e -> limparCampos());

        // Adicionando ao Grid
        grid.add(new Label("Descrição:"), 0, 0); grid.add(txtDescricao, 1, 0);
        grid.add(new Label("Valor (R$):"), 2, 0); grid.add(txtValor, 3, 0);

        grid.add(new Label("Data:"), 0, 1); grid.add(dpData, 1, 1);
        grid.add(new Label("Categoria:"), 2, 1); grid.add(cmbCategoria, 3, 1);

        HBox botoesBox = new HBox(10, btnAdicionar, btnAtualizar, btnExcluir, btnLimpar);
        botoesBox.setAlignment(Pos.CENTER_LEFT);
        grid.add(botoesBox, 0, 2, 4, 1);

        return grid;
    }

    private TableView<Despesa> criarTabela() {
        TableView<Despesa> table = new TableView<>();
        table.setItems(despesasData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- Colunas (Usando PropertyValueFactory para POJO JPA) ---

        TableColumn<Despesa, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMinWidth(40);
        colId.setMaxWidth(60);

        TableColumn<Despesa, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        TableColumn<Despesa, Double> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        // Formatar Célula de Valor (R$)
        colValor.setCellFactory(tc -> new TableCell<Despesa, Double>() {
            @Override
            protected void updateItem(Double valor, boolean empty) {
                super.updateItem(valor, empty);
                if (empty || valor == null) {
                    setText(null);
                } else {
                    setText(String.format("R$ %.2f", valor));
                    setStyle("-fx-alignment: CENTER-RIGHT;"); // Alinhar à direita
                }
            }
        });

        TableColumn<Despesa, LocalDate> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        // Formatar Célula de Data (dd/MM/yyyy)
        colData.setCellFactory(tc -> new TableCell<Despesa, LocalDate>() {
            @Override
            protected void updateItem(LocalDate data, boolean empty) {
                super.updateItem(data, empty);
                if (empty || data == null) {
                    setText(null);
                } else {
                    setText(dtf.format(data));
                }
            }
        });

        TableColumn<Despesa, String> colCat = new TableColumn<>("Categoria");
        colCat.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        table.getColumns().addAll(colId, colDesc, colValor, colData, colCat);

        // Evento de Seleção na Tabela
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                preencherCampos(newVal);
            }
        });

        return table;
    }

    // --- Lógica de Negócio (Interação com DAO) ---

    private void carregarDadosDoBanco() {
        try {
            despesasData.clear();
            despesasData.addAll(dao.listarDespesas());
            atualizarTotal();
        } catch (Exception e) {
            mostrarAlerta("Erro ao carregar dados", "Falha ao conectar com o banco: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void salvarDespesa(boolean isNova) {
        try {
            // 1. Validar campos
            validarCampos();

            // 2. Criar objeto
            Despesa d = new Despesa();
            d.setDescricao(txtDescricao.getText());
            d.setData(dpData.getValue());
            d.setCategoria(cmbCategoria.getValue());

            // Tratamento do valor (troca vírgula por ponto)
            String valorStr = txtValor.getText().replace(",", ".");
            d.setValor(Double.parseDouble(valorStr));

            if (!isNova) {
                // Se for atualização, precisa setar o ID do objeto selecionado
                if (despesaSelecionada == null) return;
                d.setId(despesaSelecionada.getId());
            } else {
                d.setId(d.getId()); // ID nulo para o Hibernate criar novo
            }

            // 3. Chamar DAO
            dao.salvarDespesa(d);

            // 4. Feedback e Limpeza
            mostrarInfo("Sucesso", isNova ? "Despesa criada!" : "Despesa atualizada!");
            limparCampos();
            carregarDadosDoBanco(); // Recarrega do banco para garantir sincronia

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro de Formato", "O valor digitado é inválido.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Campos Obrigatórios", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta("Erro de Banco de Dados", "Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void excluirDespesa() {
        if (despesaSelecionada == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir Despesa");
        alert.setHeaderText("Deseja realmente excluir: " + despesaSelecionada.getDescricao() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                dao.excluirDespesa(despesaSelecionada.getId());
                mostrarInfo("Sucesso", "Despesa excluída.");
                limparCampos();
                carregarDadosDoBanco();
            } catch (Exception e) {
                mostrarAlerta("Erro", "Não foi possível excluir: " + e.getMessage());
            }
        }
    }

    // --- Métodos Auxiliares ---

    private void validarCampos() {
        if (txtDescricao.getText().isEmpty() || txtValor.getText().isEmpty() ||
                dpData.getValue() == null || cmbCategoria.getValue() == null) {
            throw new IllegalArgumentException("Preencha todos os campos!");
        }
    }

    private void preencherCampos(Despesa d) {
        despesaSelecionada = d;
        txtDescricao.setText(d.getDescricao());
        txtValor.setText(String.format("%.2f", d.getValor())); // Mostra com 2 casas
        dpData.setValue(d.getData());
        cmbCategoria.setValue(d.getCategoria());

        // Ajusta estado dos botões
        btnAdicionar.setDisable(true);
        btnAtualizar.setDisable(false);
        btnExcluir.setDisable(false);
    }

    private void limparCampos() {
        despesaSelecionada = null;
        txtDescricao.clear();
        txtValor.clear();
        dpData.setValue(LocalDate.now());
        cmbCategoria.getSelectionModel().selectFirst();
        tabelaDespesas.getSelectionModel().clearSelection();

        btnAdicionar.setDisable(false);
        btnAtualizar.setDisable(true);
        btnExcluir.setDisable(true);
    }

    private void atualizarTotal() {
        // Opção 1: Calcular somando a lista da memória (mais rápido p/ interface)
        double total = despesasData.stream().mapToDouble(Despesa::getValor).sum();

        // Opção 2: Pegar do DAO (garante cálculo do banco)
        // double total = dao.calcularTotalDespesas();

        lblTotal.setText(String.format("Total: R$ %.2f", total));
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}