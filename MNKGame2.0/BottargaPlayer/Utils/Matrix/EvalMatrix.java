package BottargaPlayer.Utils.Matrix;

/**
 * La classe permette la creazione e l'inizializzazione delle 4 matrici dell'eval.
 */
public class EvalMatrix {
    // Valore con cui inizializzo le celle delle matrici.
    protected int init_value = 0; 

    // Dimensioni della board dalla quale creo le matrici dell'eval.
    protected int M; 
    protected int N;
    protected int K;

    // Matrici dell'eval.
    protected int[][][] M_Matrix;
    protected int[][][] N_Matrix;
    protected int[][][] K1_Matrix;
    protected int[][][] K2_Matrix;

    // Valori utili nel rendere più efficiente la creazione delle matrici dell'eval.
    private int value1;
    private int value2;

    
    /**
     * Prendo in input le dimensioni della table e creo le matrici dell'eval.
     * @param m Righe della table da creare.
     * @param n Colonne della table da creare.
     * @param k Numero di simboli da mettere in fila per vincere nella table.
     */
    public EvalMatrix(int m, int n, int k){
        if (m < 1 || n < 1 || k < 2){ // Dimensioni della table non accettabili: errore.
            this.value1 = this.value2 = 0;
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
            K2_Matrix = new int[value1][value2][2];
        }else K1_Matrix = K2_Matrix = null;
   }

    //Inizializza tutte le matrici con un valore di default 'init_value'
    public void initMatrix(){

        //Inizializzazione matrice delle righe.
        if(M_Matrix != null){
            for (int i = 0; i < M; i ++){
                for (int j = 0; j < value2; j ++){
                    M_Matrix[i][j][0] = init_value;
                    M_Matrix[i][j][1] = init_value;

                }
            }
        }

        //Inizializzazione matrice delle colonne.
        if (N_Matrix != null){
            for (int i = 0; i < value1; i ++){
                for (int j = 0; j < N; j ++){
                    N_Matrix[i][j][0] = init_value;
                    N_Matrix[i][j][1] = init_value;

                }
            }
        }

        //Inizializzazione delle due matrici delle diagonali.
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