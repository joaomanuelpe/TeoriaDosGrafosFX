    package com.example.teoriadosgrafosfx;

    class No {
        String destino;
        int peso;
        No prox;

        public No(String destino, int peso) {
            this.destino = destino;
            this.peso = peso;
            this.prox = null;
        }
    }

    class Vertice {
        String nome;
        No arestas;
        Vertice prox;

        public Vertice(String nome) {
            this.nome = nome;
            this.arestas = null;
            this.prox = null;
        }
    }

    public class Lista {
        Vertice raiz = null;

        // Adiciona um vértice se não existir
        public Vertice adicionaVertice(String nome) {
            Vertice v = raiz;
            boolean encontrado = false;
            Vertice aux = raiz;

            while (aux != null && !encontrado) {
                if (aux.nome.equals(nome)) {
                    v = aux;
                    encontrado = true;
                }
                aux = aux.prox;
            }

            if (!encontrado) {
                Vertice novo = new Vertice(nome);
                novo.prox = raiz;
                raiz = novo;
                v = novo;
            }

            return v;
        }

        // Constroi lista a partir de linha "A B,15"
        public void constroiLista(String linha) {
            if (linha.trim().length() > 0) {
                String[] partes = linha.split("[ ,]");
                if (partes.length == 3) { // verifica se tem 3 partes
                    String origem = partes[0];
                    String destino = partes[1];
                    int peso = Integer.parseInt(partes[2]);

                    Vertice vOrigem = adicionaVertice(origem);

                    No novaAresta = new No(destino, peso);
                    if (vOrigem.arestas == null) {
                        vOrigem.arestas = novaAresta;
                    } else {
                        No aux = vOrigem.arestas;
                        while (aux.prox != null) {
                            aux = aux.prox;
                        }
                        aux.prox = novaAresta;
                    }
                }
            }
        }


        public boolean verificaOrientacao() {
            Vertice v1 = raiz;

            while (v1 != null) {
                No a1 = v1.arestas;
                while (a1 != null) {
                    boolean encontrouInversa = false;
                    Vertice v2 = raiz;
                    while (v2 != null) {
                        if (v2.nome.equals(a1.destino)) {
                            No a2 = v2.arestas;
                            while (a2 != null) {
                                if (a2.destino.equals(v1.nome)) {
                                    encontrouInversa = true;
                                }
                                a2 = a2.prox;
                            }
                        }
                        v2 = v2.prox;
                    }
                    if (!encontrouInversa) {
                        return true;
                    }
                    a1 = a1.prox;
                }
                v1 = v1.prox;
            }
            return false;
        }




        public boolean verificaSimples() {
            boolean simples = true;
            Vertice v = raiz;

            while (v != null) {
                No a = v.arestas;
                while (a != null) {
                    // verifica laço
                    if (a.destino.equals(v.nome)) {
                        simples = false;
                    }

                    // verifica múltiplas arestas
                    No b = a.prox;
                    while (b != null) {
                        if (b.destino.equals(a.destino)) {
                            simples = false;
                        }
                        b = b.prox;
                    }
                    a = a.prox;
                }
                v = v.prox;
            }

            return simples;
        }

        public boolean verificaMultigrafo() {
            Vertice v = raiz;
            boolean flag = false;

            while (v != null && !flag) {
                No raizAux = v.arestas;
                while (raizAux != null && !flag) {
                    No prox = raizAux.prox;
                    while (prox != null && !flag) {
                        if (prox.destino.equals(raizAux.destino)) {
                            // Encontrou duas arestas iguais (mesma origem e mesmo destino)
                            flag = true;
                        }
                        prox = prox.prox;
                    }
                    raizAux = raizAux.prox;
                }
                v = v.prox;
            }

            return flag; // não encontrou múltiplas arestas
        }


        public boolean possuiLaco() {
            boolean laco = false;
            Vertice v = raiz;

            while (v != null) {
                No a = v.arestas;
                while (a != null) {
                    if (a.destino.equals(v.nome)) {
                        laco = true;
                    }
                    a = a.prox;
                }
                v = v.prox;
            }

            return laco;
        }


        public boolean verificaRegular() {
            boolean regular = true;
            int grauRef = -1;
            Vertice v = raiz;

            while (v != null) {
                int grau = 0;
                No a = v.arestas;
                while (a != null) {
                    grau++;
                    a = a.prox;
                }
                if (grauRef == -1) {
                    grauRef = grau;
                } else if (grau != grauRef) {
                    regular = false;
                }
                v = v.prox;
            }

            return regular;
        }

        public boolean verificaCompleto() {
            boolean completo = true;

            // conta número de vértices
            int n = 0;
            Vertice vCount = raiz;
            while (vCount != null) {
                n++;
                vCount = vCount.prox;
            }

            Vertice v = raiz;
            while (v != null) {
                int grau = 0;
                No a = v.arestas;
                while (a != null) {
                    grau++;
                    a = a.prox;
                }
                if (grau != n - 1) { // para grafo simples, cada vértice tem n-1 vizinhos
                    completo = false;
                }
                v = v.prox;
            }

            return completo;
        }

        public int grauRegular() {
            if (!verificaRegular()) return -1;

            Vertice v = raiz;
            int grau = 0;
            if (v != null) {
                No a = v.arestas;
                while (a != null) {
                    grau++;
                    a = a.prox;
                }
            }
            return grau;
        }


        public int ordemCompleto() {
            if (!verificaCompleto()) return -1;

            int n = 0;
            Vertice v = raiz;
            while (v != null) {
                n++;
                v = v.prox;
            }
            return n;
        }


    }
