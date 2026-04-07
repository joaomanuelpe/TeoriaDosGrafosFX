package com.example.teoriadosgrafosfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.ResourceBundle;

public class ColoracaoController implements Initializable {

    @FXML
    private VBox resultsVBox;

    @FXML
    private TextArea fileContentArea;

    @FXML
    private Label fileInfoLabel;

    @FXML
    private Label statusLabel;

    private int[][] matrizAdjacencia;
    private String[] rotulos;
    private Coloracao coloracao;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileContentArea.setPromptText("Carregue um arquivo de matriz de adjacência...\n\nFormato esperado:\nMA\nF1 F2 F3 F4\n0 1 1 0\n1 0 1 1\n1 1 0 1\n0 1 1 0");
        statusLabel.setText("Pronto");
        statusLabel.getStyleClass().add("status-success");
    }

    @FXML
    protected void abrirArquivo() {
        resultsVBox.getChildren().clear();
        statusLabel.setText("Carregando...");
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().add("status-processing");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Matriz de Adjacência");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos de Texto", "*.txt"));
        File arquivo = fileChooser.showOpenDialog(new Stage());

        if (arquivo != null) { // Processa apenas se houver arquivo
            try {
                List<String> linhas = Files.readAllLines(arquivo.toPath());
                fileContentArea.setText(String.join("\n", linhas));

                if (linhas.isEmpty()) {
                    throw new IllegalArgumentException("Arquivo vazio.");
                }

                linhas.removeIf(l -> l.trim().isEmpty());

                int inicioAnalise = linhas.get(0).trim().equals("MA") ? 1 : 0;
                rotulos = linhas.get(inicioAnalise).trim().split("\\s+");
                int ordem = rotulos.length;

                matrizAdjacencia = new int[ordem][ordem];
                for (int i = 0; i < ordem; i++) {
                    String[] valores = linhas.get(inicioAnalise + 1 + i).trim().split("\\s+");
                    for (int j = 0; j < ordem; j++) {
                        matrizAdjacencia[i][j] = Integer.parseInt(valores[j]);
                    }
                }

                fileInfoLabel.setText("Arquivo: " + arquivo.getName() + " (" + ordem + " vértices)");
                statusLabel.setText("Arquivo carregado com sucesso");
                statusLabel.getStyleClass().clear();
                statusLabel.getStyleClass().add("status-success");

                criarCardInfo("Informações do Grafo",
                        "Tipo: Matriz de Adjacência\n" +
                                "Ordem: " + ordem + " vértices\n" +
                                "Arquivo carregado e pronto para coloração");

                exibirMatrizAdjacencia();

            } catch (Exception e) {
                statusLabel.setText("Erro no processamento");
                statusLabel.getStyleClass().clear();
                statusLabel.getStyleClass().add("status-error");
                criarCardErro("Erro ao processar arquivo: " + e.getMessage());
                e.printStackTrace();
            }
        } else { // Caso o usuário não selecione arquivo
            statusLabel.setText("Cancelado");
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().add("status-error");
            criarCardErro("Nenhum arquivo selecionado.");
        }
    }


    @FXML
    protected void executarColoracao() {
        if (matrizAdjacencia != null) {
            resultsVBox.getChildren().clear();
            statusLabel.setText("Processando coloração...");
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().add("status-processing");

            try {
                int ordem = rotulos.length;
                coloracao = new Coloracao(null, ordem);
                coloracao.constroiListaDaMatriz(matrizAdjacencia, ordem);
                List<FilaInfo> ordemFila = capturarOrdemFila();

                coloracao.colorirGrafo();
                int qtdCores = coloracao.contaCores();

                statusLabel.setText("Coloração concluída com sucesso");
                statusLabel.getStyleClass().clear();
                statusLabel.getStyleClass().add("status-success");
                criarCardResumo(ordem, qtdCores);
                criarCardFilaProcessamento(ordemFila);
                criarCardTabelaColoracao();
                criarCardMatrizColoracao();

            } catch (Exception e) {
                statusLabel.setText("Erro na coloração");
                statusLabel.getStyleClass().clear();
                statusLabel.getStyleClass().add("status-error");
                criarCardErro("Erro ao colorir grafo: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            criarCardErro("Nenhum arquivo carregado! Carregue um arquivo primeiro.");
        }
    }


    private List<FilaInfo> capturarOrdemFila() {
        List<FilaInfo> listaFila = new ArrayList<>();
        PriorityQueue<NoMA> fila = new PriorityQueue<>(
                (a, b) -> Integer.compare(b.getLigacoes().size(), a.getLigacoes().size())
        );

        Set<NoMA> visitados = new HashSet<>(); // Evita duplicidade

        // Começa pelo nó de maior grau
        NoMA atual = coloracao.pegaNoMaior();

        while (atual != null) {
            if (!visitados.contains(atual)) {
                visitados.add(atual);
                fila.add(atual);
            }

            for (NoMA ligacao : atual.getLigacoes()) {
                if (!visitados.contains(ligacao)) {
                    visitados.add(ligacao);
                    fila.add(ligacao);
                }
            }

            atual = atual.getProx();
        }

        int posicao = 1;
        while (!fila.isEmpty()) {
            NoMA no = fila.poll();
            listaFila.add(new FilaInfo(posicao++, no.getRotulo(), no.getLigacoes().size()));
        }

        return listaFila;
    }


    private void exibirMatrizAdjacencia() {
        VBox card = new VBox();
        card.getStyleClass().add("result-card");

        Label titulo = new Label("Matriz de Adjacência");
        titulo.getStyleClass().add("card-title");

        GridPane matriz = new GridPane();
        matriz.getStyleClass().add("analysis-table");
        matriz.setHgap(2);
        matriz.setVgap(2);

        // Cabeçalho vazio
        Label empty = new Label("");
        empty.getStyleClass().add("table-header");
        empty.setMinWidth(60);
        matriz.add(empty, 0, 0);

        // Cabeçalhos de coluna
        for (int i = 0; i < rotulos.length; i++) {
            Label header = new Label(rotulos[i]);
            header.getStyleClass().add("table-header");
            header.setMinWidth(60);
            header.setAlignment(Pos.CENTER);
            matriz.add(header, i + 1, 0);
        }

        // Linhas da matriz
        for (int i = 0; i < rotulos.length; i++) {
            Label rowHeader = new Label(rotulos[i]);
            rowHeader.getStyleClass().add("table-header");
            rowHeader.setMinWidth(60);
            rowHeader.setAlignment(Pos.CENTER);
            matriz.add(rowHeader, 0, i + 1);

            for (int j = 0; j < rotulos.length; j++) {
                Label cell = new Label(String.valueOf(matrizAdjacencia[i][j]));
                cell.getStyleClass().add("table-cell");
                cell.setMinWidth(60);
                cell.setAlignment(Pos.CENTER);
                if (matrizAdjacencia[i][j] == 1) {
                    cell.setStyle("-fx-background-color: #cce5ff; -fx-font-weight: bold;");
                }
                matriz.add(cell, j + 1, i + 1);
            }
        }

        card.getChildren().addAll(titulo, matriz);
        resultsVBox.getChildren().add(card);
    }

    private void criarCardResumo(int ordem, int qtdCores) {
        VBox card = new VBox();
        card.getStyleClass().add("result-card");
        card.setStyle("-fx-border-color: #28a745; -fx-background-color: #d4edda;");

        Label titulo = new Label("Resumo da Coloração");
        titulo.getStyleClass().add("card-title");
        titulo.setStyle("-fx-text-fill: #155724;");

        Label info = new Label(
                "Vértices: " + ordem + "\n" +
                        "Cores utilizadas: " + qtdCores + "\n" +
                        "Algoritmo: Maior Grau Primeiro\n" +
                        "Status: Concluído com sucesso ✓"
        );
        info.setStyle("-fx-text-fill: #155724; -fx-font-size: 14px;");
        info.setWrapText(true);

        card.getChildren().addAll(titulo, info);
        resultsVBox.getChildren().add(card);
    }

    private void criarCardFilaProcessamento(List<FilaInfo> fila) {
        VBox card = new VBox();
        card.getStyleClass().add("result-card");

        Label titulo = new Label("Ordem de Processamento (Fila de Prioridade)");
        titulo.getStyleClass().add("card-title");

        GridPane tabela = new GridPane();
        tabela.getStyleClass().add("analysis-table");

        String[] cabecalhos = {"Posição", "Vértice", "Grau"};
        for (int i = 0; i < cabecalhos.length; i++) {
            Label header = new Label(cabecalhos[i]);
            header.getStyleClass().add("table-header");
            header.setMinWidth(100);
            tabela.add(header, i, 0);
        }

        int linha = 1;
        for (FilaInfo info : fila) {
            Label[] colunas = {
                    new Label(String.valueOf(info.posicao)),
                    new Label(info.vertice),
                    new Label(String.valueOf(info.grau)),
            };

            for (int i = 0; i < colunas.length; i++) {
                colunas[i].getStyleClass().add("table-cell");
                colunas[i].setMinWidth(100);
                if (info.posicao == 1) {
                    colunas[i].setStyle("-fx-background-color: #fff3cd; -fx-font-weight: bold;");
                }
                tabela.add(colunas[i], i, linha);
            }
            linha++;
        }

        Label explicacao = new Label(
                "A fila é ordenada por grau decrescente. O vértice com maior grau é processado primeiro, " +
                        "seguido pelos demais em ordem decrescente de conexões."
        );
        explicacao.getStyleClass().add("log-info");
        explicacao.setWrapText(true);

        card.getChildren().addAll(titulo, tabela, explicacao);
        resultsVBox.getChildren().add(card);
    }

    private void criarCardTabelaColoracao() {
        VBox card = new VBox();
        card.getStyleClass().add("result-card");

        Label titulo = new Label("Tabela de Coloração Final");
        titulo.getStyleClass().add("card-title");

        GridPane tabela = new GridPane();
        tabela.getStyleClass().add("analysis-table");

        String[] cabecalhos = {"Vértice", "Cor Atribuída", "Status"};
        for (int i = 0; i < cabecalhos.length; i++) {
            Label header = new Label(cabecalhos[i]);
            header.getStyleClass().add("table-header");
            header.setMinWidth(120);
            tabela.add(header, i, 0);
        }

        int[][] matrizCor = coloracao.getMatrizColoracao();
        Map<String, Integer> coresMap = new HashMap<>();

        for (int i = 0; i < rotulos.length; i++) {
            for (int j = 0; j < matrizCor[i].length; j++) {
                if (matrizCor[i][j] == 1) {
                    coresMap.put(rotulos[i], j + 1);
                    break;
                }
            }
        }

        int linha = 1;
        for (String vertice : rotulos) {
            Integer cor = coresMap.get(vertice);
            String corStr = cor != null ? "Cor " + cor : "Não colorido";

            Label[] colunas = {
                    new Label(vertice),
                    new Label(corStr),
                    new Label("✓ Colorido")
            };

            for (int i = 0; i < colunas.length; i++) {
                colunas[i].getStyleClass().add("table-cell");
                colunas[i].setMinWidth(120);

                // Cores diferentes para cada cor atribuída
                if (cor != null) {
                    String[] cores = {
                            "#ffcccc", "#ccffcc", "#ccccff", "#ffffcc",
                            "#ffccff", "#ccffff", "#ffd9b3", "#d9b3ff"
                    };
                    String corFundo = cores[(cor - 1) % cores.length];
                    colunas[i].setStyle("-fx-background-color: " + corFundo + "; -fx-font-weight: bold;");
                }

                tabela.add(colunas[i], i, linha);
            }
            linha++;
        }

        card.getChildren().addAll(titulo, tabela);
        resultsVBox.getChildren().add(card);
    }

    private void criarCardMatrizColoracao() {
        VBox card = new VBox();
        card.getStyleClass().add("result-card");

        Label titulo = new Label("Matriz de Coloração");
        titulo.getStyleClass().add("card-title");

        int[][] matrizCor = coloracao.getMatrizColoracao();
        GridPane matriz = new GridPane();
        matriz.getStyleClass().add("analysis-table");
        matriz.setHgap(2);
        matriz.setVgap(2);

        // Cabeçalho vazio
        Label empty = new Label("");
        empty.getStyleClass().add("table-header");
        empty.setMinWidth(80);
        matriz.add(empty, 0, 0);

        // Cabeçalhos de coluna (cores)
        for (int i = 0; i < matrizCor[0].length; i++) {
            Label header = new Label("Firewall " + (i + 1));
            header.getStyleClass().add("table-header");
            header.setMinWidth(80);
            header.setAlignment(Pos.CENTER);
            matriz.add(header, i + 1, 0);
        }

        // Linhas da matriz
        for (int i = 0; i < rotulos.length; i++) {
            Label rowHeader = new Label(rotulos[i]);
            rowHeader.getStyleClass().add("table-header");
            rowHeader.setMinWidth(80);
            rowHeader.setAlignment(Pos.CENTER);
            matriz.add(rowHeader, 0, i + 1);

            for (int j = 0; j < matrizCor[i].length; j++) {
                String valor = matrizCor[i][j] == 1 ? "✓" : (matrizCor[i][j] == -1 ? "✗" : "");
                Label cell = new Label(valor);
                cell.getStyleClass().add("table-cell");
                cell.setMinWidth(80);
                cell.setAlignment(Pos.CENTER);

                if (matrizCor[i][j] == 1) {
                    cell.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold; -fx-font-size: 16px;");
                } else if (matrizCor[i][j] == -1) {
                    cell.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-size: 16px;");
                }

                matriz.add(cell, j + 1, i + 1);
            }
        }

        Label legenda = new Label(
                "Legenda: ✓ = Cor atribuída ao vértice | ✗ = Cor bloqueada (vértice adjacente) | (vazio) = Não utilizado"
        );
        legenda.getStyleClass().add("log-info");
        legenda.setWrapText(true);

        card.getChildren().addAll(titulo, matriz, legenda);
        resultsVBox.getChildren().add(card);
    }

    @FXML
    protected void limparTabela() {
        resultsVBox.getChildren().clear();
        fileContentArea.clear();
        fileInfoLabel.setText("");
        matrizAdjacencia = null;
        rotulos = null;
        coloracao = null;
        statusLabel.setText("Limpo - Pronto para novo arquivo");
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().add("status-success");
    }

    private void criarCardInfo(String titulo, String conteudo) {
        VBox card = new VBox();
        card.getStyleClass().add("result-card");

        Label tituloLabel = new Label(titulo);
        tituloLabel.getStyleClass().add("card-title");

        Label conteudoLabel = new Label(conteudo);
        conteudoLabel.getStyleClass().add("log-info");
        conteudoLabel.setWrapText(true);

        card.getChildren().addAll(tituloLabel, conteudoLabel);
        resultsVBox.getChildren().add(card);
    }

    private void criarCardErro(String mensagem) {
        VBox card = new VBox();
        card.getStyleClass().add("result-card");
        card.setStyle("-fx-border-color: #dc3545; -fx-background-color: #f8d7da;");

        Label tituloLabel = new Label("Erro");
        tituloLabel.getStyleClass().add("card-title");
        tituloLabel.setStyle("-fx-text-fill: #721c24;");

        Label mensagemLabel = new Label(mensagem);
        mensagemLabel.setStyle("-fx-text-fill: #721c24;");
        mensagemLabel.setWrapText(true);

        card.getChildren().addAll(tituloLabel, mensagemLabel);
        resultsVBox.getChildren().add(card);
    }

    private static class FilaInfo {
        int posicao;
        String vertice;
        int grau;

        FilaInfo(int posicao, String vertice, int grau) {
            this.posicao = posicao;
            this.vertice = vertice;
            this.grau = grau;
        }
    }
}