package BottargaPlayer.Utils.Matrix;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import java.lang.Math;
import BottargaPlayer.Utils.Cell.*;

/** 
 * La classe nasce per aggiornare le 4 matrici dell'eval.
 * Contiene dei metodi per l'aggiornamento sia dopo una singola mossa che dopo una serie di mosse.
*/
public class UpdateEvalMatrix {
    // Parametro utile a capire se sono il primo a giocare.
    protected boolean first_player;

    // Parametro che mi dice se la mossa che considero è mia o dell'avversario.
    /* Mi è utile saperlo per interpretare il valore in MNKCellState in cui ad una cella si assegna P1 o P2 in base a chi l'ha scelta. */
    protected boolean my_move;
    
    // Coordinate in cui aggiungo i simboli. (Da 0 a M-1 oppure N-1)
    protected int x;
    protected int y;

    // Valori del tris M, N, K
    protected final int k;
    protected final int m;

    // Le 4 matrici dell'eval e 4 rispettive variabili che mi permettono di lavorare su di esse.
    public int[][][] M_Matrix;
    public int[][][] N_Matrix;
    public int[][][] K1_Matrix;
    public int[][][] K2_Matrix;
    protected int partial_sum_M_Matrix;
    protected int partial_sum_N_Matrix;
    protected int partial_sum_K1_Matrix;
    protected int partial_sum_K2_Matrix;

    // Variabili che utilizzo per conteggiare le situazioni di pareggio.
    protected int conta_patta_p1;
    protected int conta_patta_p2;

    // Numero di celle totali nelle varie matrici.
    protected final int ncelle;

    // Eval attuale
    public CustomScore eval;

    // Numero di simboli attualmente presenti in una board.
    public int symbolsInside;

    /* Variabili di lavoro */

    // Dimensione colonne matrice M_Matrix.
    public int limit_y;

    //Dimensione righe matrice N_Matrix
    public int limit_x;

    // Numero di colonne della matrice K2_Matrix
    public int K1_Matrix_Y;
    // Idealmente la riga della matrice generale in cui comincia la nostra matrice K1 (Se viste sovrapposte)
    public int K1_Location_X;

    // Numero righe della matrice K2_Matrix 
    public int K2_Matrix_X;

    // Numero colonne matrice K2_Matrix
    public int K2_Matrix_Y;

    /**
     * 
     * @param M Righe.
     * @param N Colonne.
     * @param K Simboli da mettere in fila.
     * @param first_player Gioco da primo giocatore?
     * @param M_Matrix Matrice dell'eval per le righe.
     * @param N_Matrix Matrice dell'eval per le colonne.
     * @param K1_Matrix Matrice dell'eval per la diagonale dal basso verso l'alto.
     * @param K2_Matrix Matrice dell'eval per la dianale dall'alto verso il basso.
     */
    public UpdateEvalMatrix(int M, int N, int K, boolean first_player, int[][][] M_Matrix, int[][][] N_Matrix, int[][][] K1_Matrix, int[][][] K2_Matrix){
        this.symbolsInside = 0;
        this.conta_patta_p1 = 0;
        this.conta_patta_p1 = 0;
        this.ncelle = M_Matrix.length * M_Matrix[0].length + N_Matrix.length * N_Matrix[0].length + K1_Matrix.length * K1_Matrix[0].length +K2_Matrix.length * K2_Matrix[0].length;
        this.m = M - 1;
        this.k = K;
        this.first_player = first_player;
        this.M_Matrix = M_Matrix;
        this.N_Matrix = N_Matrix;
        this.K1_Matrix = K1_Matrix;
        this.K2_Matrix = K2_Matrix;
        this.partial_sum_M_Matrix = 0;
        this.partial_sum_N_Matrix = 0;
        this.partial_sum_K1_Matrix = 0;
        this.partial_sum_K2_Matrix = 0;

        limit_x = N_Matrix.length - 1;
        limit_y = M_Matrix[0].length - 1;
        K1_Matrix_Y = K1_Matrix[0].length - 1;
        K1_Location_X = m - K1_Matrix.length + 1;
        K2_Matrix_X = K2_Matrix.length - 1;
        K2_Matrix_Y = K2_Matrix[0].length - 1;

        this.eval = new CustomScore(0,EvalStatus.NOT_DEFINED);
    }
    
    /**
     * Metodo per aggiornare le 4 matrici dell'eval considerando una serie di mosse.
     * @param move_list Vettore contenente le coordinate di ogni cella e chi dei due Player ha eseguito quella mossa.
     */
    public void multiple_update_matrix(MNKCell[] move_list){
        if(move_list != null){
            for (int i = 0; i < move_list.length; i++){
                single_update_matrix(move_list[i], move_list[i].state);
            }
        }
    }

    /**
     * Metodo per aggiornare tutte le 4 matrici dell'eval considerando una singola mossa.
     * @param cell Cella in cui è stata aggiunto un simbolo.
     * @param state A quale dei due giocatori toccava nel momento in cui è stato inserito il nuovo simbolo.
     */
    public void single_update_matrix(MNKCell cell, MNKCellState state){
            if(cell != null){
                this.symbolsInside++;
                CustomScore startingScore = new CustomScore(this.eval.score, this.eval.status);
                this.x = cell.i;
                this.y = cell.j;
                if (( state == MNKCellState.P1 && first_player) || (state == MNKCellState.P2 && !first_player) ) this.my_move = true;
                else this.my_move = false;
                update_M_Matrix();
                update_N_Matrix();
                update_K1_Matrix();
                update_K2_Matrix();
                calcolate_eval_value(M_Matrix, N_Matrix, K1_Matrix, K2_Matrix, startingScore);
            }
    }

/**
 * Metodo utile ad annullare un aggiornamento fatto delle 4 matrici dell'eval.
 * @param cell Cella in cui è stato aggiunto il simbolo da rimuovere.
 * @param state Giocatore a cui toccava nel momento in cui è stato inserito il simbolo da rimuovere.
 */
    public void single_invert_matrix(MNKCell cell, MNKCellState state){
        if(cell != null){
            this.symbolsInside--;
            CustomScore startingScore = new CustomScore(this.eval.score, this.eval.status);
            this.x = cell.i;
            this.y = cell.j;
            if (( state == MNKCellState.P1 && first_player) || (state == MNKCellState.P2 && !first_player) ) this.my_move = true;
            else this.my_move = false;
            invert_M_Matrix();
            invert_N_Matrix();
            invert_K1_Matrix();
            invert_K2_Matrix();
            calcolate_eval_value(M_Matrix, N_Matrix, K1_Matrix, K2_Matrix, startingScore);
        }
    }

    /**
     * Questo metodo permette di calcolare lo stato (score) dell'eval.
     * @param M Matrice dell'eval per le righe.
     * @param N Matrice dell'eval per le colonne.
     * @param K1 Matrice dell'eval per le diagonali dal basso verso l'alto.
     * @param K2 Matrice dell'eval per le diagonali dall'alto verso il basso.
     * @param startingScore Score dell'eval nel moomento della chiamata a questo metodo.
     */
    protected void calcolate_eval_value(int[][][] M, int[][][] N, int[][][] K1, int[][][] K2, CustomScore startingScore){
        if(eval.status != EvalStatus.WIN && eval.status != EvalStatus.LOSE) {
            if(conta_patta_p1 == ncelle && conta_patta_p2 == ncelle){
                this.eval.status = EvalStatus.DRAW;
                if(startingScore.status != EvalStatus.DRAW){
                    this.eval.score = (double)this.symbolsInside;
                }
                return;
            }else if(conta_patta_p1 == ncelle){
                this.eval.status = EvalStatus.CANT_WIN;
            }else if(conta_patta_p2 == ncelle){
                this.eval.status = EvalStatus.CANT_LOSE;
            }else{  
                this.eval.status = EvalStatus.NOT_DEFINED;
            }
            this.eval.score = (double)(partial_sum_M_Matrix + partial_sum_N_Matrix + partial_sum_K1_Matrix + partial_sum_K2_Matrix);
        }else{
            if(startingScore.status != EvalStatus.WIN && startingScore.status != EvalStatus.LOSE){
                this.eval.score = (double)this.symbolsInside;
            }
        }
    }

    /**
     * Metodo per aggiornare singolarmente una matrice dell'eval.
     * @param x Coordinata x del nuovo simbolo aggiunto.
     * @param y Coordinata y del nuovo simbolo aggiunto.
     * @param matrix Matrice sulla quale eseguire l'operazione di aggiornamento.
     * @param partial_sum Valore parziale del valore dell'eval prima della chiamata al metodo.
     * @return
     */
    protected int update_matrix(int x, int y, int[][][] matrix, int partial_sum){
        if(this.my_move){
            double pow = Math.pow(10, matrix[x][y][0] - 1);
            matrix[x][y][0]++;
            if(matrix[x][y][0] == 1){
                conta_patta_p2++;
                partial_sum++;
            }else{
                partial_sum += pow * 10 - pow; 
            }
            if(matrix[x][y][0] == k){
                this.eval.status = EvalStatus.WIN;
            }else if(matrix[x][y][1] > 0){
                if(matrix[x][y][0] == 1) {
                    partial_sum += Math.pow(10, matrix[x][y][1] - 1) - 1;
                }else {
                    partial_sum -= pow * 10 - pow;
                }
            }
        }else{
            double pow = Math.pow(10, matrix[x][y][1] - 1);
            matrix[x][y][1]++;
            if(matrix[x][y][1] == 1){
                conta_patta_p1++;
                partial_sum--;
            }else{
                partial_sum -= pow * 10 - pow;
            }
            if(matrix[x][y][1] == k){
                this.eval.status = EvalStatus.LOSE;
            }else if(matrix[x][y][0] > 0){
                if(matrix[x][y][1] == 1) {
                    partial_sum -= Math.pow(10, matrix[x][y][0] - 1) - 1;
                }else {
                    partial_sum += pow * 10 - pow;
                }
            }
        }
        return partial_sum;
    }

    /**
     * Metodo per annullare un update di una matrice dell'eval eseguito.
     * @param x Coordinata x del simbolo da rimuovere.
     * @param y Coordinata y del simbolo da rimuovere.
     * @param mymove Parametro necessario per capire se è stata una mia mosssa quella di aggiunta del simbolo.
     * @param matrix Matrice sulla quale annullare l'update dell'eval eseguito.
     * @param partial_sum Valore parziale dell'eval nel momento della chiamata al metodo.
     * @return partial_sum
     */
    protected int invert_matrix(int x, int y, boolean mymove, int[][][] matrix, int partial_sum){
        if(mymove){
            double pow = Math.pow(10, matrix[x][y][0] - 2);
            if(matrix[x][y][0] == k){
                this.eval.status = EvalStatus.NOT_DEFINED; //Per forzarne l'aggiornamento
            }else if(matrix[x][y][1] > 0){
                if(matrix[x][y][0] == 1) {
                    partial_sum -= Math.pow(10, matrix[x][y][1] - 1) - 1;
                }else {
                    partial_sum += pow * 10 - pow; 
                }
            }
            if(matrix[x][y][0] == 1){
                conta_patta_p2--;
                partial_sum--;
            }else{
                partial_sum -= pow * 10 - pow;
            }
            matrix[x][y][0]--;
        }else{
            double pow = Math.pow(10, matrix[x][y][1] - 2);
            if(matrix[x][y][1] == k){
                this.eval.status = EvalStatus.NOT_DEFINED; // Per forzarne l'aggiornamento
            }else if(matrix[x][y][0] > 0){
                if(matrix[x][y][1] == 1) {
                    partial_sum += Math.pow(10, matrix[x][y][0] - 1) - 1;
                }else {
                    partial_sum -= pow * 10 - pow;
                }
            }
            if(matrix[x][y][1] == 1){
                conta_patta_p1--;
                partial_sum++;
            }else{
                partial_sum += pow * 10 - pow;
            }
            matrix[x][y][1]--;
        }
        if(matrix[x][y][0] < 0 || matrix[x][y][1] < 0){
            System.out.println("ERRORE");
        }
        return partial_sum;
    }

    /**
     * Aggiornamento valori della matrice M_Matrix ( Matrice delle righe )
     */
    public void update_M_Matrix(){
        if (M_Matrix != null){
            //Aggiorno tutti i valori alla sinistra
            for ( int i = 0; i < k && (y-i >= 0); i++ ){ //y-i non deve portarmi fuori dal range matrice.
                if( (y-i <= limit_y)){
                    partial_sum_M_Matrix = update_matrix(x, y-i, M_Matrix, partial_sum_M_Matrix);
                }
            }
        }
    }

    /** 
     * Aggiornamento valori matrice N_Matrix ( Matrice delle colonne )
     */
    protected void update_N_Matrix(){
        if (N_Matrix != null){
            //Aggiorno tutti i valori in alto
            for ( int i = 0; i < k && ( x-i >= 0 ); i++ ) {
                if ((x - i <= limit_x)) {
                    partial_sum_N_Matrix = update_matrix(x - i, y, N_Matrix, partial_sum_N_Matrix);
                }
            }
        }
    }

    /**
     *Aggiornamento valori matrice K1_Matrix ( Matrice delle diagonali dal basso verso l'alto )
     */
    protected void update_K1_Matrix(){
        if (K1_Matrix != null){
            //Aggiorno tutti i valori nella diagonale muovendomi in direzione ' in basso a sinstra '
            for (int i = 0; i < k && x+i <= m && y-i >= 0; i++){
                if ( (x+i) >= K1_Location_X && y-i <= K1_Matrix_Y){
                    partial_sum_K1_Matrix = update_matrix(x + i - K1_Location_X, y-i, K1_Matrix, partial_sum_K1_Matrix);
                }
            }
        }
    }
    /**
     *Aggiornamento valori matrice K2_Matrix ( Matrice delle diagonali dall'alto verso il basso )
     */
    protected void update_K2_Matrix(){
        if (K2_Matrix != null){
            //Aggiorno tutti i valori nella diagonale muovendomi in direzione ' in alto a sinistra '
            for (int i = 0; i < k && (x-i) >= 0 && (y-i) >= 0; i++){
                if (x-i <= K2_Matrix_X && y-i <= K2_Matrix_Y){
                    partial_sum_K2_Matrix = update_matrix(x-i, y-i, K2_Matrix, partial_sum_K2_Matrix);
                }
            }
        }
    }

    /**
     * Opposto di Update_M_Matrix: per annullare l'aggiornamento della matrice dell'eval.
     */
    protected void invert_M_Matrix(){
        if (M_Matrix != null){
            //Aggiorno tutti i valori alla sinistra
            for ( int i = 0; i < k && (y-i >= 0); i++ ){ //y-i non deve portarmi fuori dal range matrice.
                if( (y-i <= limit_y)){
                    partial_sum_M_Matrix = invert_matrix(x, y-i, my_move, M_Matrix, partial_sum_M_Matrix);
                }
            }
        }
    }

    /**
     * Opposto di Update_N_Matrix: per annullare l'aggiornamento della matrice dell'eval.
     */
    protected void invert_N_Matrix(){
        if (N_Matrix != null){
            //Aggiorno tutti i valori in alto
            for ( int i = 0; i < k && ( x-i >= 0 ); i++ ) {
                if ((x - i <= limit_x)) {
                    partial_sum_N_Matrix = invert_matrix(x - i, y, my_move, N_Matrix, partial_sum_N_Matrix);
                }
            }
        }
    }

    /**
     * Opposto di Update_K1_Matrix: per annullare l'aggiornamento della matrice dell'eval.
     */
    protected void invert_K1_Matrix(){
        if (K1_Matrix != null){
            //Aggiorno tutti i valori nella diagonale muovendomi in direzione ' in basso a sinstra '
            for (int i = 0; i < k && x+i <= m && y-i >= 0; i++){
                if ( (x+i) >= K1_Location_X && y-i <= K1_Matrix_Y){
                    partial_sum_K1_Matrix = invert_matrix(x + i - K1_Location_X, y-i, my_move, K1_Matrix, partial_sum_K1_Matrix);
                }
            }
        }
    }

    /**
     * Opposto di Update_K2_Matrix: per annullare l'aggiornamento della matrice dell'eval.
     */
    protected void invert_K2_Matrix(){
        if (K2_Matrix != null){
            //Aggiorno tutti i valori nella diagonale muovendomi in direzione ' in alto a sinistra '
            for (int i = 0; i < k && (x-i) >= 0 && (y-i) >= 0; i++){
                if (x-i <= K2_Matrix_X && y-i <= K2_Matrix_Y){
                    partial_sum_K2_Matrix = invert_matrix(x-i, y-i, my_move, K2_Matrix, partial_sum_K2_Matrix);
                }
            }
        }
    }

}