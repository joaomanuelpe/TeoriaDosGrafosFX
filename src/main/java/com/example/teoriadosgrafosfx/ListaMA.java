package com.example.teoriadosgrafosfx;

import java.util.*;

public class ListaMA {
    private NoMA raiz;
    private int contadorPrenum;
    private ArrayList<NoMA> articulacoes;

    public ListaMA() {
        this.raiz = null;
        this.contadorPrenum = 1;
        this.articulacoes = new ArrayList<>();
    }

    public NoMA getRaiz() {
        return raiz;
    }

    public List<NoMA> getArticulacoes() {
        return articulacoes;
    }

    public void constroiListaDaMatriz(int[][] matriz, int ordem) {
        NoMA[] nos = new NoMA[ordem];
        for (int i = 0; i < ordem; i++) {
            nos[i] = new NoMA("Firewall " + i);
            if (i == 0)
                raiz = nos[i];
            else
                nos[i-1].setProx(nos[i]);
        }
        for (int i = 0; i < ordem; i++) {
            for (int j = 0; j < ordem; j++) {
                if (matriz[i][j] == 1) {
                    nos[i].addLigacao(nos[j]);
                }
            }
        }
    }

    public void detectaArticulacoes() {
        contadorPrenum = 1;
        articulacoes.clear();
        dfs(raiz, null);

        System.out.println("Pontos de articulação encontrados:");
        for (NoMA no : articulacoes) {
            System.out.println(no.getRotulo());
        }
    }

    private void dfs(NoMA v, NoMA pai) {
        v.setPrenum(contadorPrenum);
        v.setMenor(contadorPrenum);
        contadorPrenum++;

        int filhos = 0;
        boolean ehArticulacao = false;

        for (NoMA vizinho : v.getLigacoes()) {
            if (vizinho.getPrenum() == 0) { // ainda não visitado
                filhos++;
                dfs(vizinho, v);
                v.setMenor(Math.min(v.getMenor(), vizinho.getMenor()));

                // condição para vértices internos
                if (pai != null && vizinho.getMenor() >= v.getPrenum()) {
                    ehArticulacao = true;
                }
            } else if (vizinho != pai) {
                // aresta de retorno (back edge)
                v.setMenor(Math.min(v.getMenor(), vizinho.getPrenum()));
            }
        }

        // debug: mostrar filhos de cada nó
        if (pai == null) {
            System.out.println("Raiz " + v.getRotulo() + " tem " + filhos + " filhos DFS.");
        } else {
            System.out.println(v.getRotulo() + " visitado com " + filhos + " filhos DFS.");
        }

        // condição para raiz
        if (pai == null && filhos > 1) {
            ehArticulacao = true;
        }

        if (ehArticulacao && !articulacoes.contains(v)) {
            articulacoes.add(v);
            System.out.println("Vértice " + v.getRotulo() + " é ponto de articulação.");
        }
    }

}
