package com.example.teoriadosgrafosfx;

public class MA {

    //função para verificar se é um grafo orientado ou não, precisamos comparar os valores
    //acima e abaixo da diagonal principal

    //multigrafo não existe

    //regular, verificar se cada vértice possui o mesmo número de arestas

    //completo cada par de vértices possui conexão com os demais, (n-1)

    //simples, não possui arestas múltiplas e não possui laços (diagonal principal precisa ser toda com valores 0)

    public static boolean verificaOrientacao(int[][] matriz, int ordem) {
        int i = 0, j = 1, vLinha, vColuna, k = 0;
        vLinha = matriz[i][j];
        vColuna = matriz[j][i];
        while(i < ordem && vLinha == vColuna) {
            j = i + 1;
            while(j < ordem - 1 && vLinha == vColuna) {
                j++;
                vLinha = matriz[i][j];
                vColuna = matriz[j][i];
            }
            i++;
        }

        if(k < ordem)
            return false;
        return true;
    }

    public static boolean verificaLaco(int[][] matriz, int ordem) {
        int i = 0;
        while(i < ordem && matriz[i][i] != 1) {
            i++;
        }

        if(i < ordem)
            return true;
        return false;
    }

    public static boolean verificaSimples(int[][] matriz, int ordem) {
        boolean laco = verificaLaco(matriz, ordem);
        return laco;
    }

    public static boolean verificaRegular(int[][] matriz, int ordem) {
        int i = 0, j = 0, acc = 0, frog;

        while(i < ordem) {
            acc += matriz[0][i];
            i++;
        }
        i = 0;
        frog = acc;
        while(i < ordem && acc == frog) {
            frog = 0;
            j = 0;
            while(j < ordem) {
                frog += matriz[i][j];
                j++;
            }
            i++;
        }

        if(i < ordem)
            return false;
        return true;
    }

    public static boolean verificCompleto(int[][] matriz, int ordem) {
        int i = 0, j = 0, acc = 0;

        while(i < ordem) {
            acc += matriz[0][i];
            i++;
        }
        if(verificaRegular(matriz, ordem) && acc == ordem - 1)
            return true;
        return false;
    }

    public static int grauRegular(int[][] matriz, int ordem) {
        if (!verificaRegular(matriz, ordem)) return -1;
        int grau = 0;
        for (int j = 0; j < ordem; j++) {
            grau += matriz[0][j];
        }
        return grau;
    }



    public static int ordemCompleto(int[][] matriz, int ordem) {
        if (verificCompleto(matriz, ordem)) {
            return ordem; // número de vértices
        }
        return -1;
    }


}
