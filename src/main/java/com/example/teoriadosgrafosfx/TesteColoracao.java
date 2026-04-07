package com.example.teoriadosgrafosfx;

public class TesteColoracao {
    public static void main(String[] args) {
        // Matriz de adjacência baseada na imagem
        int[][] matriz = {
                {0, 1, 0, 0, 0, 1}, // A
                {1, 0, 1, 1, 0, 0}, // B
                {0, 1, 0, 1, 1, 0}, // C
                {0, 1, 1, 0, 1, 1}, // D
                {0, 0, 1, 1, 0, 1}, // E
                {1, 0, 0, 1, 1, 0}  // F
        };

        int ordem = 6;

        // Cria o grafo
        NoMA raiz = new NoMA("Firewall 1");
        Coloracao coloracao = new Coloracao(raiz, ordem);
        coloracao.constroiListaDaMatriz(matriz, ordem);

        coloracao.colorirGrafo();
        System.out.println("Matriz de Coloração:");
        int[][] resultado = coloracao.getMatrizColoracao();
        for (int i = 0; i < ordem; i++) {
            for (int j = 0; j < ordem; j++) {
                System.out.printf("%3d", resultado[i][j]);
            }
            System.out.println();
        }

        System.out.println("\nQuantidade de cores utilizadas: " + coloracao.contaCores());
    }
}
