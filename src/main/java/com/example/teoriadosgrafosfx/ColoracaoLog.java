package com.example.teoriadosgrafosfx;

import java.util.*;

public class ColoracaoLog {
    private NoMA raiz;
    private int[][] matrizColoracao;
    private int ordem;
    private List<String> filaOrdem = new ArrayList<>();
    private Map<String, Integer> coresPorNo = new LinkedHashMap<>();

    public ColoracaoLog(NoMA raiz, int ordem) {
        this.raiz = raiz;
        this.ordem = ordem;
    }

    public NoMA getRaiz() {
        return raiz;
    }

    public void setRaiz(NoMA raiz) {
        this.raiz = raiz;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public void constroiListaDaMatriz(int[][] matriz, int ordem) {
        NoMA[] nos = new NoMA[ordem];
        for (int i = 0; i < ordem; i++) {
            nos[i] = new NoMA("Firewall " + (i + 1));
            if (i == 0) raiz = nos[i];
            else nos[i - 1].setProx(nos[i]);
        }
        for (int i = 0; i < ordem; i++) {
            for (int j = 0; j < ordem; j++) {
                if (matriz[i][j] == 1 && i != j) {
                    nos[i].addLigacao(nos[j]);
                }
            }
        }
    }

    public NoMA pegaNoMaior() {
        NoMA atual = raiz;
        NoMA maior = raiz;
        while (atual != null) {
            if (atual.getLigacoes().size() > maior.getLigacoes().size()) maior = atual;
            atual = atual.getProx();
        }
        return maior;
    }

    public int pegaNumeroString(String rotulo) {
        String numeroStr = rotulo.replaceAll("[^0-9]", "");
        return Integer.parseInt(numeroStr);
    }

    public void colorirGrafo() {
        filaOrdem.clear();
        coresPorNo.clear();
        PriorityQueue<NoMA> fila = new PriorityQueue<>((a, b) -> Integer.compare(b.getLigacoes().size(), a.getLigacoes().size()));
        List<NoMA> visitados = new java.util.ArrayList<>();
        NoMA atual = pegaNoMaior();
        matrizColoracao = new int[ordem][ordem];
        while (atual != null) {
            visitados.add(atual);
            fila.add(atual);
            List<NoMA> ligacoes = atual.getLigacoes();
            for (NoMA ligacao : ligacoes) {
                if (!visitados.contains(ligacao)) {
                    visitados.add(ligacao);
                    fila.add(ligacao);
                }
            }
            atual = atual.getProx();
        }
        int x = 0, y;
        while (!fila.isEmpty()) {
            atual = fila.poll();
            filaOrdem.add(atual.getRotulo());
            x = pegaNumeroString(atual.getRotulo()) - 1;
            y = 0;
            while (y < ordem && matrizColoracao[x][y] != -1) y++;
            matrizColoracao[x][y] = 1;
            coresPorNo.put(atual.getRotulo(), y + 1);
            List<NoMA> ligacoes = atual.getLigacoes();
            for (NoMA ligacao : ligacoes) {
                x = pegaNumeroString(ligacao.getRotulo()) - 1;
                matrizColoracao[x][y] = -1;
            }
        }
    }

    public int contaCores() {
        int qtdCores = 0;
        boolean flag = false;
        for (int i = 0; i < ordem; i++) {
            flag = false;
            for (int j = 0; j < ordem && !flag; j++) {
                if (matrizColoracao[i][j] == 1) {
                    qtdCores++;
                    flag = true;
                }
            }
        }
        return qtdCores;
    }

    public int[][] getMatrizColoracao() {
        return matrizColoracao;
    }

    public List<String> getFilaOrdem() {
        return filaOrdem;
    }

    public Map<String, Integer> getCoresPorNo() {
        return coresPorNo;
    }

    public int getNumeroDeCoresUsadas() {
        Set<Integer> cores = new HashSet<>(coresPorNo.values());
        return cores.size();
    }
}