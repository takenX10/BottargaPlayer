package BottargaPlayer.PlayerNegamax;
/*
* Questa classe permette la creazione e l'inizializzazione delle 4 matrici dell'eval.
* Questa classe è dotata di metodi per la stampa di ognuna delle 4 matrici dell'eval #fordebugpurpose.
*/



public class EvalMatrix {
    protected int init_value = 0; //Valore con il quale inizializzo le matrici.
    protected int M; //Righe
    protected int N; //Colonne
    protected int K; //Simboli da mettere in fila per vincere.
    protected int[][][] M_Matrix; //Matrice delle righe
    protected int[][][] N_Matrix; //Matrice delle colonne
    protected int[][][] K1_Matrix; // Matrice diagonali che vanno dal basso verso l'alto. (Lettura da sinistra a destra)
    protected int[][][] K2_Matrix; // Matrice diagonali che vanno dall'alto verso il basso. (Lettura da sinistra a destra)
    private int value1; //(M - K + 1)
    private int value2; //(N - K + 1)

    public EvalMatrix(int m, int n, int k){ //Le dimensioni devono essere reali. Ex: 3 3 3 creerà il classico tris 3righe/colonne/simboli
        if (m < 1 || n < 1 || k < 2){ //input di gioco non accettabili.
            this.value1 = this.value2 = 0; //Così facendo tutte le matrici verranno settate a null grazie a dei controlli sottostanti.
        }else{
            this.M = m;
            this.N = n;
            this.K = k;
            this.value1 = (M - K + 1);
            this.value2 = (N - K + 1);
        }


        //Creazione matrici

        if (value2 > 0) M_Matrix = new int[M][value2][2];
        else M_Matrix = null;

        if (value1 > 0) N_Matrix = new int[value1][N][2];
        else N_Matrix = null;

        if (value1 > 0 && value2 > 0){
            K1_Matrix = new int[value1][value2][2];
            K2_Matrix = new int[value1][value2][2]; //Si, le due matrici hanno lo stesso numero di righe e colonne.
        }else K1_Matrix = K2_Matrix = null;
   }

    //Inizializza tutte le matrici con un valore di default 'init_value'
    public void initMatrix(){
        //Inizializzo matrice righe
        if(M_Matrix != null){
            for (int i = 0; i < M; i ++){
                for (int j = 0; j < value2; j ++){
                    M_Matrix[i][j][0] = init_value;
                    M_Matrix[i][j][1] = init_value;

                }
            }
        }

        //Inizializzo matrice colonne
        if (N_Matrix != null){
            for (int i = 0; i < value1; i ++){
                for (int j = 0; j < N; j ++){
                    N_Matrix[i][j][0] = init_value;
                    N_Matrix[i][j][1] = init_value;

                }
            }
        }

        //Inizializzo le due matrici delle diagonali
        if (K1_Matrix != null){
            for (int i = 0; i < value1; i ++){
                for (int j = 0; j < value2; j ++){
                    K1_Matrix[i][j][0] = init_value;
                    K1_Matrix[i][j][1] = init_value;
                    K2_Matrix[i][j][0] = init_value;
                    K2_Matrix[i][j][1] = init_value;

                }
            }
        }
    }

    public int[][][] M_Matrix(){
        return M_Matrix;
    }

    public int[][][] N_Matrix(){
        return N_Matrix;
    }

    public int[][][] K1_Matrix(){
        return K1_Matrix;
    }

    public int[][][] K2_Matrix(){
        return K2_Matrix;
    }


    // Metodi per la stampa delle matrici
    // #fordebugpurpose
    public void printM_Matrix(){
        System.out.println("\nMatrice delle righe (M_Matrix) ");
        if (M_Matrix != null){
            for (int i = 0; i < M; i ++){
                for (int j = 0; j < value2; j ++){
                    System.out.print(M_Matrix[i][j] + " ");
                }
                System.out.println();
            }
        }else System.out.println("La matrice M per questa configurazione M N K non è disponibile");
    }

    public void printN_Matrix(){
        System.out.println("\nMatrice delle colonne (N_Matrix) ");
        if (N_Matrix != null){
            for (int i = 0; i < value1; i ++){
                for (int j = 0; j < N; j ++){
                    System.out.print(N_Matrix[i][j] + " ");
                }
                System.out.println();
            }
        }else System.out.println("La matrice N per questa configurazione M N K non è disponibile");
    }

    public void printK1_Matrix(){
        System.out.println("\nMatrice delle diagonali dal basso verso l'alto ( K1 )  ");
        if(K1_Matrix != null){
            for (int i = 0; i < value1; i ++){
                System.out.println();
                for (int j = 0; j < value2; j ++){
                    System.out.print(K1_Matrix[i][j] + " ");
                }
            }
        }else System.out.println("La matrice delle diagonali K1 per questi M N K non esiste.");
    }

    public void printK2_Matrix(){
        System.out.println("\nMatrice delle diagonali dall'alto verso il basso ( K2 ) ");
        if (K2_Matrix != null){
            for (int i = 0; i < value1; i ++){
                System.out.println();
                for (int j = 0; j < value2; j ++){
                    System.out.print(K2_Matrix[i][j] + " ");
                }
            }
        }else System.out.println("La matrice delle diagonali K2 per questi M N K non esiste.");
    }

    //Setter e Getter per le 4 matrici dell'eval.
    public void setM_Matrix(int[][][] M_Matrix){
        this.M_Matrix = M_Matrix;
    }

    public void setN_Matrix (int[][][] N_Matrix){
        this.N_Matrix = N_Matrix;
    }

    public void setK1_Matrix (int[][][] K1_Matrix){
        this.K1_Matrix = K1_Matrix;
    }

    public void setK2_Matrix (int[][][] K2_Matrix){
        this.K2_Matrix = K2_Matrix;
    }

    public int[][][] getM_Matrix(){
        return this.M_Matrix;
    }

    public int[][][] getN_Matrix(){
        return this.N_Matrix;
    }

    public int[][][] getK1_Matrix(){
        return this.K1_Matrix;
    }

    public int[][][] getK2_Matrix(){
        return  this.K2_Matrix;
    }
}