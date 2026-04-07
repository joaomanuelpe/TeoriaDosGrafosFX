package com.example.teoriadosgrafosfx;

import java.util.*;

public class ListaMAComLogs {
    private NoMA raiz;
    private ArrayList<NoMA> articulacoes;
    private ArrayList<LogEntry> logs;
    private ArrayList<EntradaTabela> dadosTabela;

    public ListaMAComLogs() {
        this.raiz = null;
        this.articulacoes = new ArrayList<>();
        this.logs = new ArrayList<>();
        this.dadosTabela = new ArrayList<>();
    }

    public NoMA getRaiz() { return raiz; }
    public List<NoMA> getArticulacoes() { return articulacoes; }
    public List<LogEntry> getLogs() { return logs; }
    public List<EntradaTabela> getDadosTabela() { return dadosTabela; }

    public void constroiListaDaMatriz(int[][] matriz, int ordem) {
        NoMA[] nos = new NoMA[ordem];
        for (int i = 0; i < ordem; i++) {
            nos[i] = new NoMA("Firewall " + (i + 1));
            if (i == 0) raiz = nos[i];
            else nos[i-1].setProx(nos[i]);
        }
        for (int i = 0; i < ordem; i++) {
            for (int j = 0; j < ordem; j++) {
                if (matriz[i][j] == 1 && i != j) {
                    nos[i].addLigacao(nos[j]);
                }
            }
        }

        logs.add(new LogEntry(LogEntry.TipoLog.INFO,
                "Lista de adjacência construída com " + ordem + " nós"));
    }
    public void detectaArticulacoes() {
        articulacoes.clear();
        logs.clear();
        dadosTabela.clear();
        logs.add(new LogEntry(LogEntry.TipoLog.INFO,
                "Iniciando detecção de pontos de articulação..."));
        //debugGrafo();
        NoMA atual = raiz;
        while (atual != null) {
            atual.setPrenum(0); //0 = não visitado
            atual.setMenor(0);
            atual = atual.getProx();
        }

        int[] tempo = {1};
        articulationPointDFS(raiz, null, tempo);

        logs.add(new LogEntry(LogEntry.TipoLog.INFO,
                "Detecção concluída. " + articulacoes.size() + " pontos de articulação encontrados."));
        //verificacaoManual();
        criarTabelaAnalise();
    }

    private void articulationPointDFS(NoMA u, NoMA parent, int[] tempo) {
        int filhos = 0;
        //marcar como visitado
        u.setPrenum(tempo[0]);
        u.setMenor(tempo[0]);
        tempo[0]++;

        logs.add(new LogEntry(LogEntry.TipoLog.INFO,
                String.format("Visitando %s: prenum=%d, menor=%d",
                        u.getRotulo(), u.getPrenum(), u.getMenor())));

        //percorrer todos os vizinhos
        for (NoMA v : u.getLigacoes()) {
            if (v.getPrenum() == 0) { //se 'v' nao tiver sido visitado
                filhos++;
                logs.add(new LogEntry(LogEntry.TipoLog.INFO,
                        String.format("Explorando aresta %s -> %s", u.getRotulo(), v.getRotulo())));

                articulationPointDFS(v, u, tempo);
                u.setMenor(Math.min(u.getMenor(), v.getMenor())); //att do valor menor de 'v'

                logs.add(new LogEntry(LogEntry.TipoLog.INFO,
                        String.format("Voltando de %s para %s: menor[%s] = menor entre (%d, %d) = %d",
                                v.getRotulo(), u.getRotulo(), u.getRotulo(),
                                u.getPrenum(), v.getMenor(), u.getMenor())));
                if (parent == null && filhos > 1) {//caso 1: u é raiz e tem mais de um filho
                    if (!articulacoes.contains(u)) {
                        articulacoes.add(u);
                        logs.add(new LogEntry(LogEntry.TipoLog.ARTICULACAO,
                                String.format("RAIZ %s é ponto de articulação (tem %d filhos)",
                                        u.getRotulo(), filhos), u.getRotulo(), true));
                    }
                }
                if (parent != null && v.getMenor() >= u.getPrenum()) { //caso 2: u não é raiz e menor[v] >= prenum[u]
                    if (!articulacoes.contains(u)) {
                        articulacoes.add(u);
                        logs.add(new LogEntry(LogEntry.TipoLog.ARTICULACAO,
                                String.format("%s é ponto de articulação: menor[%s]=%d >= prenum[%s]=%d",
                                        u.getRotulo(), v.getRotulo(), v.getMenor(), u.getRotulo(), u.getPrenum()),
                                u.getRotulo(), true));
                    }
                }
            }
            else if (v != parent) { //se v foi visitado e não é o pai de u
                u.setMenor(Math.min(u.getMenor(), v.getPrenum()));
                logs.add(new LogEntry(LogEntry.TipoLog.INFO,
                        String.format("Back edge %s -> %s: menor[%s] = menor entre (%d, %d) = %d",
                                u.getRotulo(), v.getRotulo(), u.getRotulo(),
                                u.getMenor() + v.getPrenum() - Math.min(u.getMenor(), v.getPrenum()),
                                v.getPrenum(), u.getMenor())));
            }
        }
    }

    private void criarTabelaAnalise() {
        Map<NoMA, NoMA> pais = new HashMap<>();

        NoMA atual = raiz;
        while (atual != null) {
            atual.setPrenum(0);
            atual = atual.getProx();
        }
        int[] tempo = {1};
        construirRelacoesPais(raiz, null, pais, tempo);
        //agora criar a tabela
        atual = raiz;
        while (atual != null) {
            String analise = articulacoes.contains(atual) ? "Sim" : "Não";
            NoMA pai = pais.get(atual);
            Integer prenumPai = (pai != null) ? pai.getPrenum() : null;

            dadosTabela.add(new EntradaTabela(
                    atual.getRotulo(),
                    atual.getPrenum(),
                    prenumPai,
                    atual.getMenor(),
                    analise
            ));
            atual = atual.getProx();
        }

        //ordenando por prenum
        //dadosTabela.sort((a, b) -> Integer.compare(a.getPrenum(), b.getPrenum()));
    }

    private void construirRelacoesPais(NoMA u, NoMA parent, Map<NoMA, NoMA> pais, int[] tempo) {
        u.setPrenum(tempo[0]);
        u.setMenor(tempo[0]);
        tempo[0]++;

        pais.put(u, parent);

        for (NoMA v : u.getLigacoes()) {
            if (v.getPrenum() == 0) { //nao foi visitado
                construirRelacoesPais(v, u, pais, tempo);
                u.setMenor(Math.min(u.getMenor(), v.getMenor()));
            } else if (v != parent) {
                u.setMenor(Math.min(u.getMenor(), v.getPrenum()));
            }
        }
    }

    //controi a rep. da lista de adj.
    public List<String> construirListaAdjacencia() {
        List<String> resultado = new ArrayList<>();
        NoMA atual = raiz;

        while (atual != null) {
            StringBuilder conexoes = new StringBuilder();
            for (NoMA conn : atual.getLigacoes()) {
                conexoes.append(conn.getRotulo()).append(" ");
            }
            String conexoesStr = conexoes.toString().trim();
            if (conexoesStr.isEmpty()) {
                conexoesStr = "(sem conexões)";
            }
            resultado.add(atual.getRotulo() + " -> " + conexoesStr);
            atual = atual.getProx();
        }

        return resultado;
    }

    //metodo para debugar o grafo
    public void debugGrafo() {
        logs.add(new LogEntry(LogEntry.TipoLog.INFO, "=== DEBUG DO GRAFO ==="));
        NoMA atual = raiz;
        while (atual != null) {
            StringBuilder conexoes = new StringBuilder();
            for (NoMA vizinho : atual.getLigacoes()) {
                conexoes.append(vizinho.getRotulo()).append(" ");
            }
            logs.add(new LogEntry(LogEntry.TipoLog.INFO,
                    atual.getRotulo() + " conectado a: [" + conexoes.toString().trim() + "]"));
            atual = atual.getProx();
        }
        logs.add(new LogEntry(LogEntry.TipoLog.INFO, "=== FIM DEBUG ==="));
    }

    public void verificacaoManual() {
        logs.add(new LogEntry(LogEntry.TipoLog.INFO, "=== VERIFICAÇÃO MANUAL ==="));

        NoMA atual = raiz;
        while (atual != null) {
            int componentesOriginais = contarComponentes(null);
            int componentesSemNo = contarComponentes(atual);

            if (componentesSemNo > componentesOriginais) {
                logs.add(new LogEntry(LogEntry.TipoLog.INFO,
                        String.format("VERIFICAÇÃO: %s É ponto de articulação (%d -> %d componentes)",
                                atual.getRotulo(), componentesOriginais, componentesSemNo)));
            } else {
                logs.add(new LogEntry(LogEntry.TipoLog.INFO,
                        String.format("VERIFICAÇÃO: %s NÃO é ponto de articulação (%d componentes)",
                                atual.getRotulo(), componentesOriginais)));
            }

            atual = atual.getProx();
        }

        logs.add(new LogEntry(LogEntry.TipoLog.INFO, "=== FIM VERIFICAÇÃO ==="));
    }

    private int contarComponentes(NoMA excluir) {
        Set<NoMA> visitados = new HashSet<>();
        int componentes = 0;

        NoMA atual = raiz;
        while (atual != null) {
            if (atual != excluir && !visitados.contains(atual)) {
                componentes++;
                dfsConexao(atual, visitados, excluir);
            }
            atual = atual.getProx();
        }

        return componentes;
    }

    private void dfsConexao(NoMA no, Set<NoMA> visitados, NoMA excluir) {
        visitados.add(no);

        for (NoMA vizinho : no.getLigacoes()) {
            if (vizinho != excluir && !visitados.contains(vizinho)) {
                dfsConexao(vizinho, visitados, excluir);
            }
        }
    }
}