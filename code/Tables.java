
public class Tables {
    protected int init_value = 0; //Valore con il quale inizializzo le matrici.
    protected int M; //Righe
    protected int N; //Colonne
    protected int K; //Simboli da mettere in fila per vincere.
    protected int[][] M_Matrix;
    protected int[][] N_Matrix;
    protected int[][] K1_Matrix; // Matrice diagonali che vanno dal basso verso l'alto. (Lettura da sinistra a destra)
    protected int[][] K2_Matrix; // Matrice diagonali che vanno dall'alto verso il basso. (Lettura da sinistra a destra)
    int value1; //(M - K + 1) = 3 //TODO Dare un nome migliore a questa variabile
    int value2; //(N - K + 1) = 2 //TODO Dare un nome migliore a questa variabile

    public Tables (int m, int n, int k){
        this.M = m;
        this.N = n;
        this.K = k;
        this.value1 = (M - K + 1);
        this.value2= (N - K + 1);

        //Creazione matrici
        M_Matrix = new int[M][value2];
        N_Matrix = new int[value1][N];
        K1_Matrix = new int[value1][value2];
        K2_Matrix = new int[value1][value2]; //Si, le due matrici hanno lo stesso numero di righe e colonne.
    }

    public void initMatrix(){ //Inizializza tutte le matrici con un valore di default: 0
        //Inizializzo matrice righe
        for (int i = 0; i < M; i ++){
            for (int j = 0; j < value2; j ++){
                M_Matrix[i][j] = init_value;
            }
        }
        //Inizializzo matrice colonne
        for (int i = 0; i < value1; i ++){
            for (int j = 0; j < N; j ++){
                N_Matrix[i][j] = init_value;
            }
        }
        //Inizializzo le due matrici delle diagonali
        for (int i = 0; i < value1; i ++){
            for (int j = 0; j < value2; j ++){
                K1_Matrix[i][j] = init_value;
                K2_Matrix[i][j] = init_value;
            }
        }
    }

    public int[][] M_Matrix(){
        return M_Matrix;
    }

    public int[][] N_Matrix(){
        return N_Matrix;
    }

    public int[][] K1_Matrix(){
        return K1_Matrix;
    }

    public int[][] K2_Matrix(){
        return K2_Matrix;
    }


    // Metodi per la stampa delle matrici
    // #ForDebugPurpose

    public void printM_Matrix(){
        System.out.println("\nMatrice delle righe (M_Matrix) ");
        for (int i = 0; i < M; i ++){
            for (int j = 0; j < value2; j ++){
                System.out.print(M_Matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printN_Matrix(){
        System.out.println("\nMatrice delle colonne (N_Matrix) ");
        for (int i = 0; i < value1; i ++){
            for (int j = 0; j < N; j ++){
                System.out.print(N_Matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printK1_Matrix(){
        System.out.println("\nMatrice delle diagonali dal basso verso l'alto ( K1 )  ");
        for (int i = 0; i < value1; i ++){
            System.out.println();
            for (int j = 0; j < value2; j ++){
                System.out.print(K1_Matrix[i][j] + " ");
            }
        }
    }

    public void printK2_Matrix(){
        System.out.println("\nMatrice delle diagonali dall'alto verso il basso ( K2 ) ");
        for (int i = 0; i < value1; i ++){
            System.out.println();
            for (int j = 0; j < value2; j ++){
                System.out.print(K1_Matrix[i][j] + " ");
            }
        }
    }

}