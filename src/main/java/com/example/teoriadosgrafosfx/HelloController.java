package com.example.teoriadosgrafosfx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private VBox resultsVBox;

    @FXML
    private TextArea fileContentArea;

    @FXML
    private Button carregarBtn;

    @FXML
    private Label fileInfoLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label versionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurações iniciais
        fileContentArea.setPromptText("Carregue um arquivo de matriz de adjacência...\n\nFormato esperado:\nMA\n0 1 2 3\n0 1 0 0\n1 0 1 0\n0 1 0 1\n0 0 1 0");
        statusLabel.setText("Pronto");
        statusLabel.getStyleClass().add("status-success");

        // Aplicar estilos CSS
        resultsVBox.getStylesheets().add(getClass().getResource("/com/example/teoriadosgrafosfx/style.css").toExternalForm());
    }
    @FXML
    protected void carregarArquivo() {
        resultsVBox.getChildren().clear();
        statusLabel.setText("Carregando...");
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().add("status-processing");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Arquivo de Grafo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos de Texto", "*.txt"));
        File arquivo = fileChooser.showOpenDialog(new Stage());

        if (arquivo == null) {
            statusLabel.setText("Cancelado");
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().add("status-error");
            criarCardErro("Nenhum arquivo selecionado.");
        } else {
            try {
                List<String> linhas = Files.readAllLines(arquivo.toPath());
                fileContentArea.setText(String.join("\n", linhas));
                fileInfoLabel.setText("Arquivo: " + arquivo.getName() + " (" + linhas.size() + " linhas)");

                if (linhas.isEmpty()) {
                    throw new IllegalArgumentException("Arquivo vazio.");
                }

                // Remove linhas vazias
                linhas.removeIf(linha -> linha.trim().isEmpty());

                String tipoArquivo = detectarTipoArquivo(linhas);

                statusLabel.setText("Processado com sucesso");
                statusLabel.getStyleClass().clear();
                statusLabel.getStyleClass().add("status-success");

                if (tipoArquivo.contains("Matriz de Adjacência")) {
                    processarMatrizAdjacenciaComLogs(linhas);
                } else {
                    criarCardInfo("Tipo de Arquivo", "Detectado: " + tipoArquivo + "\n\nApenas Matriz de Adjacência é suportada para análise de pontos de articulação.");
                }

            } catch (Exception e) {
                statusLabel.setText("Erro no processamento");
                statusLabel.getStyleClass().clear();
                statusLabel.getStyleClass().add("status-error");
                criarCardErro("Erro ao processar arquivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void processarMatrizAdjacenciaComLogs(List<String> linhas) {
        try {
            // Parse da matriz
            int inicioAnalise = linhas.get(0).trim().equals("MA") ? 1 : 0;
            String[] rotulos = linhas.get(inicioAnalise).trim().split("\\s+");
            int ordem = rotulos.length;

            int[][] matriz = new int[ordem][ordem];
            for (int i = 0; i < ordem; i++) {
                String[] valores = linhas.get(inicioAnalise + 1 + i).trim().split("\\s+");
                for (int j = 0; j < ordem; j++) {
                    matriz[i][j] = Integer.parseInt(valores[j]);
                }
            }

            // Usar a nova classe com logs
            ListaMAComLogs lista = new ListaMAComLogs();
            lista.constroiListaDaMatriz(matriz, ordem);
            lista.detectaArticulacoes();

            // Criar card de informações gerais
            criarCardInfo("Informações do Grafo",
                    "Tipo: Matriz de Adjacência\n" +
                            "Ordem: " + ordem + " vértices\n" +
                            "Pontos de articulação encontrados: " + lista.getArticulacoes().size());

            // Exibir lista de adjacência em card
            List<String> adjacencias = lista.construirListaAdjacencia();
            criarCardListaAdjacencia(adjacencias);

            // Exibir tabela de análise
            criarCardTabelaAnalise(lista.getDadosTabela());

            // Exibir logs detalhados
            criarCardLogs(lista.getLogs());

            // Exibir pontos de articulação
            criarCardPontosArticulacao(lista.getArticulacoes());

        } catch (Exception e) {
            criarCardErro("Erro ao processar Matriz de Adjacência: " + e.getMessage());
            e.printStackTrace();
        }
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

    private void criarCardListaAdjacencia(List<String> adjacencias) {
        VBox card = new VBox();
        card.getStyleClass().add("result-card");

        Label titulo = new Label("Lista de Adjacência");
        titulo.getStyleClass().add("card-title");

        VBox listaContainer = new VBox();
        listaContainer.getStyleClass().add("adjacency-list");

        for (String adj : adjacencias) {
            Label item = new Label(adj);
            item.getStyleClass().add("adjacency-item");
            listaContainer.getChildren().add(item);
        }

        card.getChildren().addAll(titulo, listaContainer);
        resultsVBox.getChildren().add(card);
    }

    private void criarCardTabelaAnalise(List<EntradaTabela> dadosTabela) {
        if (dadosTabela.isEmpty()) return;

        VBox card = new VBox();
        card.getStyleClass().add("result-card");

        Label titulo = new Label("Tabela de Análise DFS");
        titulo.getStyleClass().add("card-title");

        // Criar grid para a tabela
        GridPane tabela = new GridPane();
        tabela.getStyleClass().add("analysis-table");

        // Cabeçalhos
        String[] cabecalhos = {"Vértice", "Prenum", "Menor", "Análise"};
        for (int i = 0; i < cabecalhos.length; i++) {
            Label header = new Label(cabecalhos[i]);
            header.getStyleClass().add("table-header");
            header.setMinWidth(80);
            tabela.add(header, i, 0);
        }

        // Dados da tabela
        int linha = 1;
        for (EntradaTabela entrada : dadosTabela) {
            Label[] colunas = {
                    new Label(entrada.getNo()),
                    new Label(String.valueOf(entrada.getPrenum())),
                    //new Label(entrada.getPrenumMenos() != null ? entrada.getPrenumMenos().toString() : "-"),
                    new Label(String.valueOf(entrada.getMenor())),
                    new Label(entrada.getAnalise())
            };

            for (int i = 0; i < colunas.length; i++) {
                colunas[i].getStyleClass().add("table-cell");
                colunas[i].setMinWidth(80);

                // Destacar em verde se for ponto de articulação
                if (entrada.getAnalise().equals("Sim")) {
                    colunas[i].getStyleClass().add("table-cell-articulation");
                    // Estilo adicional inline para garantir a cor verde
                    colunas[i].setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                }

                tabela.add(colunas[i], i, linha);
            }
            linha++;
        }

        card.getChildren().addAll(titulo, tabela);
        resultsVBox.getChildren().add(card);
    }

    private void criarCardLogs(List<LogEntry> logs) {
        VBox card = new VBox();
        card.getStyleClass().add("result-card");

        Label titulo = new Label("Logs Detalhados de Detecção");
        titulo.getStyleClass().add("card-title");

        VBox logContainer = new VBox();
        logContainer.getStyleClass().add("log-container");

        for (LogEntry log : logs) {
            Label label = new Label();

            switch (log.getTipo()) {
                case ARTICULACAO:
                    label.setText("✓ " + log.getMensagem());
                    label.getStyleClass().add("log-articulation");
                    break;
                case DESCARTA:
                    label.setText(log.getMensagem());
                    label.getStyleClass().add("log-descarta");
                    break;
                case TESTE:
                    label.setText("  " + log.getMensagem());
                    if (log.getMensagem().contains("verdadeiro")) {
                        label.getStyleClass().add("log-test-true");
                    } else {
                        label.getStyleClass().add("log-test-false");
                    }
                    break;
                default:
                    label.setText(log.getMensagem());
                    label.getStyleClass().add("log-info");
            }

            label.setWrapText(true);
            logContainer.getChildren().add(label);
        }

        card.getChildren().addAll(titulo, logContainer);
        resultsVBox.getChildren().add(card);
    }

    private void criarCardPontosArticulacao(List<NoMA> articulacoes) {
        VBox card = new VBox();
        card.getStyleClass().add("result-card");

        Label titulo = new Label("Pontos de Articulação Encontrados");
        titulo.getStyleClass().add("card-title");

        if (articulacoes.isEmpty()) {
            Label nenhumLabel = new Label("Nenhum ponto de articulação encontrado neste grafo.");
            nenhumLabel.getStyleClass().add("log-info");
            card.getChildren().addAll(titulo, nenhumLabel);
        } else {
            VBox pontosContainer = new VBox();
            pontosContainer.getStyleClass().add("articulation-points");

            for (NoMA articulacao : articulacoes) {
                Label ponto = new Label("🔴 " + articulacao.getRotulo());
                ponto.getStyleClass().add("articulation-point");
                pontosContainer.getChildren().add(ponto);
            }

            Label explicacao = new Label("Os pontos de articulação são vértices cuja remoção aumenta o número de componentes conexas do grafo.");
            explicacao.getStyleClass().add("log-info");
            explicacao.setWrapText(true);

            card.getChildren().addAll(titulo, pontosContainer, explicacao);
        }

        resultsVBox.getChildren().add(card);
    }


    private String detectarTipoArquivo(List<String> linhas) {
        int inicioAnalise = 0;
        if (linhas.get(0).trim().matches("^(MA|MI|LA)$")) {
            inicioAnalise = 1;
        }

        boolean contemVirgula = false;
        for (int i = inicioAnalise; i < linhas.size() && !contemVirgula; i++) {
            String linha = linhas.get(i).trim();
            if (linha.contains(",")) {
                contemVirgula = true;
            }
        }

        if (!contemVirgula) {
            boolean ehMatriz = true;
            for (int i = inicioAnalise + 1; i < linhas.size() && ehMatriz; i++) {
                String linha = linhas.get(i).trim();
                String[] valores = linha.split("\\s+");
                for (String valor : valores) {
                    if (!valor.matches("^-?[0-9]+$")) {
                        ehMatriz = false;
                    }
                }
            }

            if (ehMatriz) {
                String[] rotulos = linhas.get(inicioAnalise).trim().split("\\s+");
                int numVertices = rotulos.length;
                int numLinhasMatriz = linhas.size() - inicioAnalise - 1;

                if (numLinhasMatriz == numVertices) {
                    return "Matriz de Adjacência (MA)";
                }
            }
        }

        return "Formato não reconhecido";
    }
}