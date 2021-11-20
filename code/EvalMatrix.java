//La classe serve ad aggiornare le 4 matrici dell'eval
//La classe riceve in input: le 4 matrici, le coordinate X,Y del punto in cui, nella matrice di partenza,
//è stata aggiunta una X ( che da ora in poi chiamerò genericamente 'simbolo' )

/*
* Per aggiornare la matrice delle righe ( modifica fatta in x, y )
* Aggiorno il valore M_Matrix[x][y]
* Mi muovo prima a sinistra ( di max k - 1 ) e poi a destra ( di max k - 1 )
* Aggiorno i valori finchè non sforo il range della matrice.
* Quando lo sforo, se sono a sinsitra mi sposto a fare la stessa cosa a destra, se sono
* già a destra ho finito.
*/


public class EvalMatrix {
    private int x; // Coordinata x del punto in cui viene aggiunto il nuovo simbolo
    private int y; // Coordinata y del punto in cui viene aggiunto il nuovo simbolo
    private final int k; // tris ( M, N, K )
    private final int m;
    //private final int n;
    //4 matrici dell'eval.
    public int[][] M_Matrix;
    public int[][] N_Matrix;
    public int[][] K1_Matrix; // Matrice diagonali che vanno dal basso verso l'alto. (Lettura da sinistra a destra)
    public int[][] K2_Matrix; // Matrice diagonali che vanno dall'alto verso il basso. (Lettura da sinistra a destra)

    //NB: Le coordinate X ed Y sottostanti partono da 0 (come prima cella) ed arrivano ad M-1 e N-1.
    public EvalMatrix(int M, int N, int K, int[][] M_Matrix, int[][] N_Matrix, int[][] K1_Matrix, int[][] K2_Matrix){
        if (x > M-1 || y > N-1 ) {
            System.out.println("\n !!! ERRORE\n Le coordinate del nuovo simbolo non rientrano nella matrice di gioco! ");
            //TODO Impostare return qui ed un booleano che ferma l'esecuzione di tutti gli eventuali metodi, sarebbe lavoro inutile.
        }
        this.m = M - 1;
        //this.n = N - 1;
        this.k = K;
        this.M_Matrix = M_Matrix;
        this.N_Matrix = N_Matrix;
        this.K1_Matrix = K1_Matrix;
        this.K2_Matrix = K2_Matrix;
    }

    /*
    * Scelta implementativa: per modularità e chiarezza del codice farò un metodo per ogni matrice da aggironare.
    * Dopodichè creerò il metodo che richiama tutti i suddetti permettendo l'aggiornamento generale.
    */

    //Aggiornamento valori della matrice M_Matrix ( Matrice delle righe )
    private void update_M_Matrix(){
        if (M_Matrix != null){
            int limit_x = M_Matrix.length - 1; //Dimensione righe matrice M_Matrix
            int limit_y = M_Matrix[0].length - 1; //Dimensione colonne matrice M_Matrix
            boolean is_in; //Mi dice se il nuovo simbolo rientra direttamente nella matrice M_Matrix

            if ( x <= limit_x && y <= limit_y ) is_in = true;
            else is_in = false;

            //Aggiorno il valore presente nelle stesse coordinate, se non sono fuori matrice
            //Lo aggiorno solo se non è negativo; se lo fosse quella righa non potrebbe mai essere vincente dunque non avrebbe senso
            if ( is_in && M_Matrix[x][y] >= 0 ) M_Matrix[x][y] ++;

            //Aggiorno tutti i valori alla sinistra
            for ( int i = 1; i < k && (y-i >= 0); i++ ){ //y-i non deve portarmi fuori dal range matrice.
                if( (y-i <= limit_y) && M_Matrix[x][y-i] >= 0 ) M_Matrix[x][y-i]++;
            }
        }
    }

    //Aggiornamento valori matrice N_Matrix ( Matrice delle colonne )
    private void update_N_Matrix(){
        if (N_Matrix != null){
            int limit_x = N_Matrix.length - 1; //Dimensione righe matrice N_Matrix
            int limit_y = N_Matrix[0].length - 1; //Dimensione colonne matrice N_Matrix
            boolean is_in; //Mi dice se il nuovo simbolo rientra direttamete nella matrice N_Matrix o se si trova 'al di fuori'

            if ( x <= limit_x && y <= limit_y ) is_in = true;
            else is_in = false;

            //Aggiorno il valore presente nelle stesse coordinate, se non sono fuori matrice
            //Lo aggiorno solo se non è negativo; se lo fosse quella righa non potrebbe mai essere vincente dunque non avrebbe senso
            if ( is_in && N_Matrix[x][y] >= 0 ) N_Matrix[x][y] ++;

            //Aggiorno tutti i valori in alto
            for ( int i = 1; i < k && ( x-i >= 0 ); i++ ){
                if ( (x-i <= limit_x ) && N_Matrix[x-i][y] >= 0 ) N_Matrix[x-i][y]++;
            }
        }
    }

    //Aggiornamento valori matrice K1_Matrix ( Matrice delle diagonali dal basso verso l'alto )
    private void update_K1_Matrix(){
        if (K1_Matrix != null){
            int K1_Matrix_X = K1_Matrix.length - 1; // Numero righe matrice K1_Matrix contate da zero
            int K1_Matrix_Y = K1_Matrix[0].length - 1; // Numero colonne matrice K2_Matrix contate da zero
            boolean is_in; //Mi dice se il nuovo simbolo rientra direttamente nella matrice K1_Matrix
            int K1_Location_X = m - K1_Matrix.length + 1; //Riga della matrice in cui comincia la nostra matrice K1 (Parte arancio in foglio UNO )

            if ( x >= K1_Location_X && y <= K1_Matrix_Y ) is_in = true;
            else is_in = false;

            if ( is_in && K1_Matrix[x - K1_Location_X][y] >= 0 ) K1_Matrix[x - K1_Location_X][y] ++;

            //aggiorno tutti i valori nella diagonale muovendomi in direzione ' in basso a sinstra '
            //int x_K1_Matrix = k - 1 + K1_Matrix.length - 1; //Numero di righe totali (partendo da 0) della matrice di gioco ottenuto per formula inversa di ( M - K + 1 )

            for (int i = 1; i < k && x+i <= m && y-i >= 0; i++){
                if ( (x+i) >= K1_Location_X && y-i <= K1_Matrix_Y){
                    if (K1_Matrix[x+i - K1_Location_X][y-i] >= 0) K1_Matrix[x+i - K1_Location_X][y-i] ++;
                }
            }
        }
    }

    private void update_K2_Matrix(){
        if (K2_Matrix != null){
            int K2_Matrix_X = K2_Matrix.length - 1; //Numero righe matrice K2_Matrix contate partendo da zero
            int K2_Matrix_Y = K2_Matrix[0].length - 1; //Numero colonne matrice K2_Matrix contato partendo da zero

            boolean is_in; //Mi dice se il nuovo simbolo rientra direttamente nella matrice K1_Matrix
            if (x <= K2_Matrix_X && y <= K2_Matrix_Y) is_in = true;
            else is_in = false;

            if (is_in && K2_Matrix[x][y] >= 0) K2_Matrix[x][y]++;

            //Aggiorno tutti i valori nella diagonale muovendomi in direzione ' in alto a sinistra '
            for (int i = 1; i < k && (x-i) >= 0 && (y-i) >= 0; i++){
                if (x-i <= K2_Matrix_X && y-1 <= K2_Matrix_Y){
                    if (K2_Matrix[x-i][y-1] >= 0) K2_Matrix[x-i][y-i]++;
                }
            }
        }
    }

    public void update_Matrix(int x, int y){ //Metodo per aggiornare tutte le 4 matrici dell'eval
        this.x = x;
        this.y = y;
        update_M_Matrix();
        update_N_Matrix();
        update_K1_Matrix();
        update_K2_Matrix();
    }

}