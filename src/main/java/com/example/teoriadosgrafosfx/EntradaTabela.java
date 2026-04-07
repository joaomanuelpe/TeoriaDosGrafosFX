package com.example.teoriadosgrafosfx;

public class EntradaTabela {
    private String no;
    private int prenum;
    private Integer prenumMenos; // pode ser null para raiz
    private int menor;
    private String analise;

    public EntradaTabela(String no, int prenum, Integer prenumMenos, int menor, String analise) {
        this.no = no;
        this.prenum = prenum;
        this.prenumMenos = prenumMenos;
        this.menor = menor;
        this.analise = analise;
    }

    // Getters
    public String getNo() { return no; }
    public int getPrenum() { return prenum; }
    public Integer getPrenumMenos() { return prenumMenos; }
    public int getMenor() { return menor; }
    public String getAnalise() { return analise; }
}