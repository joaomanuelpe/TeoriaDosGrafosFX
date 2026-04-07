package com.example.teoriadosgrafosfx;

import java.util.ArrayList;

public class NoMA {
    private String rotulo;
    private ArrayList<NoMA> ligacoes;
    private int[] array; // [0] = prenum, [1] = menor
    private NoMA prox;

    public NoMA(String rotulo) {
        this.rotulo = rotulo;
        this.ligacoes = new ArrayList<>();
        this.array = new int[2];
        this.prox = null;
    }

    public String getRotulo() {
        return rotulo;
    }

    public void setRotulo(String rotulo) {
        this.rotulo = rotulo;
    }

    public ArrayList<NoMA> getLigacoes() {
        return ligacoes;
    }

    public void addLigacao(NoMA no) {
        ligacoes.add(no);
    }

    public int getPrenum() {
        return array[0];
    }

    public void setPrenum(int valor) {
        this.array[0] = valor;
    }

    public int getMenor() {
        return array[1];
    }

    public void setMenor(int valor) {
        this.array[1] = valor;
    }

    public NoMA getProx() {
        return prox;
    }

    public void setProx(NoMA prox) {
        this.prox = prox;
    }
}
