package org.p2_despesas.Principal;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region; // Ou VBox
import javafx.stage.Stage;
import org.p2_despesas.controller.DespesaController;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Instancia o Controlador (Isso conecta ao banco e cria a tela)
            DespesaController controller = new DespesaController();

            // 2. Pega a visualização (o VBox que criamos no controller)
            Region root = controller.getView();

            // 3. Cria a Cena (Scene) definindo o tamanho da janela
            Scene scene = new Scene(root, 800, 600);

            // 4. Configura o Palco (Stage/Janela)
            primaryStage.setTitle("Sistema de Despesas Pessoais");
            primaryStage.setScene(scene);

            // Opcional: Fecha a conexão do Hibernate ao fechar a janela
            primaryStage.setOnCloseRequest(e -> {
                org.p2_despesas.dao.JPAUtil.close(); // Se tiver criado esse método estático
                System.exit(0);
            });

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace(); // Mostra erros no console se falhar ao abrir
        }
    }

    public static void main(String[] args) {
        // Inicia o ciclo de vida do JavaFX
        launch(args);
    }
}
