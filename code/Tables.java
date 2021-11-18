
public class Tables {
    protected int init_value = 0; //Valore con il quale inizializzo le matrici.
    protected int M; //Righe
    protected int N; //Colonne
    protected int K; //Simboli da mettere in fila per vincere.
    protected int[][] M_Matrix;
    protected int[][] N_Matrix;
    protected int[][] K1_Matrix; // Matrice diagonali che vanno dal basso verso l'alto. (Lettura da sinistra a destra)
    protected int[][] K2_Matrix; // Matrice diagonali che vanno dall'alto verso il basso. (Lettura da sinistra a destra)
    int diagonalMatrixM; //Righe matrice delle diagonali
    int diagonalMatrixN; //Colonne matrice delle diagonali

    public Tables (int m, int n, int k){
        this.M = m;
        this.N = n;
        this.K = k;
        this.diagonalMatrixM = (M - K + 1);
        this.diagonalMatrixN= (N - K + 1);

        //Creazione matrici
        M_Matrix = new int[M][1];
        N_Matrix = new int[1][N];
        K1_Matrix = new int[diagonalMatrixM][diagonalMatrixN];
        K2_Matrix = new int[diagonalMatrixM][diagonalMatrixN]; //Si, le due matrici hanno lo stesso numeri di righe e colonne.
    }

    public void initMatrix(){ //Inizializza tutte le matrici con un valore di default: 0
        //Inizializzo matrice righe
        for (int i = 0; i < M; i ++){
            M_Matrix[i][0] = init_value;
        }
        //Inizializzo matrice colonne
        for (int i = 0; i < N; i ++){
            N_Matrix[0][i] = init_value;
        }
        //Inizializzo le due matrici delle diagonali
        for (int i = 0; i < diagonalMatrixM; i ++){
            for (int j = 0; j < diagonalMatrixN; j ++){
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
        for (int i = 0; i < M; i++){
            System.out.print(M_Matrix[i][0] + " ");
            System.out.println(); //Essendo una matrice colonna la printo come tale aggiungendo un a capo dopo ogni cella.
        }
    }

    public void printN_Matrix(){
        System.out.println("\nMatrice delle colonne (N_Matrix) ");
        for (int i = 0; i < N; i++){
            System.out.print(N_Matrix[0][i] + " ");
        }
    }

    public void printK1_Matrix(){
        System.out.println("\nMatrice delle diagonali dal basso verso l'alto ( K1 )  ");
        for (int i = 0; i < diagonalMatrixM; i ++){
            System.out.println();
            for (int j = 0; j < diagonalMatrixN; j ++){
                System.out.print(K1_Matrix[i][j] + " ");
            }
        }
    }

    public void printK2_Matrix(){
        System.out.println("\nMatrice delle diagonali dall'alto verso il basso ( K2 ) ");
        for (int i = 0; i < diagonalMatrixM; i ++){
            System.out.println();
            for (int j = 0; j < diagonalMatrixN; j ++){
                System.out.print(K1_Matrix[i][j] + " ");
            }
        }
    }

}