package com.example.teoriadosgrafosfx;

public class LogEntry {
    public enum TipoLog {
        INFO, TESTE, ARTICULACAO, TABELA, DESCARTA
    }

    private TipoLog tipo;
    private String mensagem;
    private String no;
    private boolean ehArticulacao;

    public LogEntry(TipoLog tipo, String mensagem) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.no = "";
        this.ehArticulacao = false;
    }

    public LogEntry(TipoLog tipo, String mensagem, String no, boolean ehArticulacao) {
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.no = no;
        this.ehArticulacao = ehArticulacao;
    }

    // Getters
    public TipoLog getTipo() { return tipo; }
    public String getMensagem() { return mensagem; }
    public String getNo() { return no; }
    public boolean isEhArticulacao() { return ehArticulacao; }
}