package com.example.teoriadosgrafosfx;

import java.util.*;

public class Coloracao {

    private NoMA raiz;
    private int[][] matrizColoracao;
    private int ordem;

    public Coloracao(NoMA raiz, int ordem) {
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

    public int[][] getMatrizColoracao() {
        return matrizColoracao;
    }

    public void constroiListaDaMatriz(int[][] matriz, int ordem) {
        NoMA[] nos = new NoMA[ordem];
        for (int i = 0; i < ordem; i++) {
            nos[i] = new NoMA("Firewall " + (i + 1));
            if (i == 0)
                raiz = nos[i];
            else
                nos[i - 1].setProx(nos[i]);
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
            if (atual.getLigacoes().size() > maior.getLigacoes().size())
                maior = atual;
            atual = atual.getProx();
        }
        return maior;
    }

    public int pegaNumeroString(String rotulo) {
        String numeroStr = rotulo.replaceAll("[^0-9]", "");
        return Integer.parseInt(numeroStr);
    }

    public void colorirGrafo() {
        PriorityQueue<NoMA> fila = new PriorityQueue<>(
                (a, b) -> Integer.compare(b.getLigacoes().size(), a.getLigacoes().size())
        );
        List<Integer> visitados = new ArrayList<>();
        NoMA inicial = pegaNoMaior();
        while (inicial != null) {
            int numInicial = pegaNumeroString(inicial.getRotulo());
            if (!visitados.contains(numInicial)) {
                visitados.add(numInicial);
                fila.add(inicial);
            }
            List<NoMA> ligacoes = inicial.getLigacoes();
            for (NoMA ligacao : ligacoes) {
                int numLigacao = pegaNumeroString(ligacao.getRotulo());
                if (!visitados.contains(numLigacao)) {
                    visitados.add(numLigacao);
                    fila.add(ligacao);
                }
            }

            inicial = inicial.getProx();
        }
        matrizColoracao = new int[ordem][ordem];
        Queue<NoMA> filaColoracao = new ArrayDeque<>(fila);
        int x, y;
        while (!filaColoracao.isEmpty()) {
            NoMA atual = filaColoracao.poll();
            x = pegaNumeroString(atual.getRotulo()) - 1;
            y = 0;
            while(y < ordem && matrizColoracao[x][y] != 0)
                y++;
            if(y < ordem) {
                matrizColoracao[x][y] = 1;
                for(NoMA ligacao : atual.getLigacoes()) {
                    int xVizinho = pegaNumeroString(ligacao.getRotulo()) - 1;
                    matrizColoracao[xVizinho][y] = -1;
                }
            }
        }
    }


    public int contaCores() {
        int qtdCores = 0;
        for (int j = 0; j < ordem; j++) {
            boolean encontrou = false;
            for (int i = 0; i < ordem; i++) {
                if (matrizColoracao[i][j] == 1) {
                    encontrou = true;
                }
            }
            if (encontrou) {
                qtdCores++;
            }
        }
        return qtdCores;
    }

}
