package com.example.teoriadosgrafosfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // Carrega a tela principal (hello-view.fxml)
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("/com/example/teoriadosgrafosfx/hello-view.fxml")
        );
        BorderPane root = fxmlLoader.load();

        // Cria o menu para navegação
        MenuBar menuBar = new MenuBar();
        Menu menuOpcoes = new Menu("Opções");

        MenuItem itemPrincipal = new MenuItem("Tela Principal");
        MenuItem itemColoracao = new MenuItem("Coloração de Grafos");

        menuOpcoes.getItems().addAll(itemPrincipal, itemColoracao);
        menuBar.getMenus().add(menuOpcoes);

        // Adiciona o menu no topo da tela
        root.setTop(menuBar);

        // Cria a cena principal
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/teoriadosgrafosfx/style.css").toExternalForm()
        );

        stage.setTitle("Analisador de Pontos de Articulação de Grafos");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        stage.show();

        // ===============================
        //  AÇÕES DOS ITENS DO MENU
        // ===============================

        // Voltar para tela principal
        itemPrincipal.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        HelloApplication.class.getResource("/com/example/teoriadosgrafosfx/hello-view.fxml")
                );
                Scene novaCena = new Scene(loader.load(), 1200, 800);
                novaCena.getStylesheets().add(
                        getClass().getResource("/com/example/teoriadosgrafosfx/style.css").toExternalForm()
                );
                stage.setScene(novaCena);
                stage.setTitle("Analisador de Pontos de Articulação de Grafos");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Abrir tela de Coloração de Grafos
        itemColoracao.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        HelloApplication.class.getResource("/com/example/teoriadosgrafosfx/coloracao-view.fxml")
                );
                Scene novaCena = new Scene(loader.load(), 1200, 800);
                novaCena.getStylesheets().add(
                        getClass().getResource("/com/example/teoriadosgrafosfx/style.css").toExternalForm()
                );
                stage.setScene(novaCena);
                stage.setTitle("Coloração de Grafos - Algoritmo Maior Primeiro");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
