package com.example.teoriadosgrafosfx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NoColoracao {
    private final StringProperty no;
    private final StringProperty cor;

    public NoColoracao(String no, String cor) {
        this.no = new SimpleStringProperty(no);
        this.cor = new SimpleStringProperty(cor);
    }

    public String getNo() { return no.get(); }
    public StringProperty noProperty() { return no; }

    public String getCor() { return cor.get(); }
    public StringProperty corProperty() { return cor; }

    public void setCor(String cor) { this.cor.set(cor); }
}
