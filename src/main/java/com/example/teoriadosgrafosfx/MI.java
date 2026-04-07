package com.example.teoriadosgrafosfx;

public class MI {

    public static boolean verificaOrientacao(int[][] matriz, int numVertices, int numArestas) {
        boolean orientado = true;

        for (int c = 0; c < numArestas; c++) {
            int contPos = 0;
            int contNeg = 0;
            for (int l = 0; l < numVertices; l++) {
                if (matriz[l][c] > 0)
                    contPos++;
                if (matriz[l][c] < 0)
                    contNeg++;
            }
            if (contPos > 1 || contNeg > 1) {
                orientado = false;
            }
        }

        return orientado;
    }

    public static boolean verificaSimples(int[][] matriz, int numVertices, int numArestas) {
        return !verificaLaco(matriz, numVertices, numArestas) && !verificaMultigrafo(matriz, numVertices, numArestas);
    }

    public static boolean verificaLaco(int[][] matriz, int numVertices, int numArestas) {
        boolean flag = false;

        for (int c = 0; c < numArestas && !flag; c++) {
            int cont = 0;
            for (int l = 0; l < numVertices; l++) {
                if (matriz[l][c] != 0) {
                    if (cont == 0) cont += matriz[l][c];
                    else cont = 0;
                }
            }
            if (cont != 0) flag = true;
        }

        return flag;
    }

//    public static boolean verificaMultigrafo(int[][] matriz, int numVertices, int numArestas) {
//        for (int j = 0; j < numArestas; j++) {
//            for (int k = j + 1; k < numArestas; k++) {
//                int l = 0;
//                while (l < numVertices && matriz[l][j] == matriz[l][k]) {
//                    l++;
//                }
//                if (l == numVertices) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    } -> função detectando multigrafo sem peso dos números nas devidas coordenadas

    public static boolean verificaMultigrafo(int[][] matriz, int numVertices, int numArestas) {
        for (int j = 0; j < numArestas; j++) {
            for (int k = j + 1; k < numArestas; k++) {
                boolean colunasIguais = true;
                for (int l = 0; l < numVertices; l++) {
                    int valorJ = matriz[l][j];
                    int valorK = matriz[l][k];
                    if (!((valorJ > 0 && valorK > 0) || (valorJ < 0 && valorK < 0) || (valorJ == 0 && valorK == 0))) {
                        colunasIguais = false;
                    }
                }
                if (colunasIguais) {
                    return true;
                }
            }
        }
        return false;
    }



    public static boolean verificaRegular(int[][] matriz, int numVertices, int numArestas) {
        // Conta grau do primeiro vértice
        int grauRef = 0;
        for (int c = 0; c < numArestas; c++) {
            if (matriz[0][c] != 0) grauRef++;
        }

        // Compara com os outros
        for (int l = 1; l < numVertices; l++) {
            int grau = 0;
            for (int c = 0; c < numArestas; c++) {
                if (matriz[l][c] != 0) grau++;
            }
            if (grau != grauRef) {
                return false;
            }
        }
        return true;
    }


    public static boolean verificaCompleto(int[][] matriz, int numVertices, int numArestas) {
        int grauRef = 0;
        for (int c = 0; c < numArestas; c++) {
            grauRef += Math.abs(matriz[0][c]);
        }

        return verificaRegular(matriz, numVertices, numArestas) && grauRef == numVertices - 1;
    }

    public static boolean verificaRegularEmissao(int[][] matriz, int numVertices, int numArestas) {
        int ref = -1;
        for (int l = 0; l < numVertices; l++) {
            int emissao = 0;
            for (int c = 0; c < numArestas; c++) {
                if (matriz[l][c] > 0) emissao++; // qualquer valor positivo conta
            }
            if (ref == -1) ref = emissao;
            else if (emissao != ref) return false;
        }
        return true;
    }

    public static boolean verificaRegularRecepcao(int[][] matriz, int numVertices, int numArestas) {
        int ref = -1;
        for (int l = 0; l < numVertices; l++) {
            int recepcao = 0;
            for (int c = 0; c < numArestas; c++) {
                if (matriz[l][c] < 0) recepcao++; // qualquer valor negativo conta
            }
            if (ref == -1) ref = recepcao;
            else if (recepcao != ref) return false;
        }
        return true;
    }

    public static int grauEmissao(int[][] matriz, int numVertices, int numArestas) {
        int grau = 0;
        for (int c = 0; c < numArestas; c++) {
            if (matriz[0][c] > 0) grau++; // qualquer valor positivo
        }
        return grau;
    }

    public static int grauRecepcao(int[][] matriz, int numVertices, int numArestas) {
        int grau = 0;
        for (int c = 0; c < numArestas; c++) {
            if (matriz[0][c] < 0) grau++; // qualquer valor negativo
        }
        return grau;
    }



    public static int ordemCompleto(int[][] matriz, int numVertices, int numArestas) {
        if (verificaCompleto(matriz, numVertices, numArestas)) {
            return numVertices;
        }
        return -1;
    }



}
