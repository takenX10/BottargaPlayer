package BottargaPlayer.PlayerNegamax;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
//La classe serve ad aggiornare le 4 matrici dell'eval
//La classe riceve in input: le 4 matrici, le coordinate X,Y del punto in cui, nella matrice di partenza,
//è stata aggiunta una X ( che da ora in poi chiamerò genericamente 'simbolo' )

/*
* Questa classe nasce per aggiornare le 4 matrici dell'eval.
* La classe contiene dei metodi per l'aggiornamento delle 4 matrici dopo una singola mossa
* La classe contiene un metodo per l'aggiornamento delle 4 matrici dopo una serie di mosse, questa serie di mosse viene presa in input
* come un vettore MNKCell[] contenente le coordinate della cella e chi ha eseguito quella mossa (Player1 o Player2)
*
*
*/


public class UpdateEvalMatrix {
    private boolean first_player; //Parametro che mi dice se sono il primo a giocare. If True io sarò considerato P1, else P2
    /* Mi è utile saperlo per interpretare il valore in MNKCellState in cui ad una cella si assegna P1 o P2 in base a chi l'ha scelta. */
    private int x; // Coordinata x del punto in cui viene aggiunto il nuovo simbolo, si parte da zero
    private int y; // Coordinata y del punto in cui viene aggiunto il nuovo simbolo, si parte da zero
    private boolean my_move; //La mossa che considero è mia o dell'avversario? True = mia, false otherwise
    private final int k; // valori del tris ( M, N, K ), in quanto tali partono da 1, non da zero. ( M = 5, abbiamo 5 righe in quanto conto da 1 a 5 )
    private final int m; //

    //4 matrici dell'eval.
    public int[][][] M_Matrix;  //Matrice delle righe
    public int[][][] N_Matrix;  //Matrice delle colonne
    public int[][][] K1_Matrix; // Matrice diagonali che vanno dal basso verso l'alto. (Lettura da sinistra a destra)
    public int[][][] K2_Matrix; // Matrice diagonali che vanno dall'alto verso il basso. (Lettura da sinistra a destra)
    private int partial_sum_M_Matrix;
    private int partial_sum_N_Matrix;
    private int partial_sum_K1_Matrix;
    private int partial_sum_K2_Matrix;
    private int conta_patta_p1;
    private int conta_patta_p2;
    private final int ncelle;     // numero delle celle totali nelle varie matrici
    //todo se so che in una serie di mosse una mi ha portato alla vittoria smetto di aggiornare tutte le matrici analizzando le altre mosse? Si
    public CustomScore eval;
    public int symbolsInside;

    //NB: Le coordinate X ed Y sottostanti partono da 0 (come prima cella) ed arrivano ad M-1 e N-1.
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
        this.eval = new CustomScore(0,EvalStatus.NOT_DEFINED);
    }
    
    //Metodo per aggiornare le 4 matrici dell'eval considerando una serie di mosse
    public void multiple_update_matrix(MNKCell[] move_list){
        if(move_list != null){
            for (int i = 0; i < move_list.length; i++){
                single_update_matrix(move_list[i], move_list[i].state);
            }
        }
    }

    public void single_update_matrix(MNKCell cell, MNKCellState state){ //Metodo per aggiornare tutte le 4 matrici dell'eval considerando una singola mossa
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

    private void calcolate_eval_value(int[][][] M, int[][][] N, int[][][] K1, int[][][] K2, CustomScore startingScore){
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
            this.eval.score = (double)(partial_sum_M_Matrix + partial_sum_N_Matrix + partial_sum_K1_Matrix + partial_sum_K2_Matrix) / 4;
            int a = 0;
            a = a + 1;
        }else{
            if(startingScore.status != EvalStatus.WIN && startingScore.status != EvalStatus.LOSE){
                this.eval.score = (double)this.symbolsInside;
            }
        }
    }

    private int update_matrix(int x, int y, int[][][] matrix, int partial_sum){
        if(this.my_move){
            matrix[x][y][0]++;
            partial_sum++;
            if(matrix[x][y][0] == 1){
                conta_patta_p2++;
            }
            if(matrix[x][y][0] == k){
                this.eval.status = EvalStatus.WIN;
            }else if(matrix[x][y][1] > 0){
                if(matrix[x][y][0] == 1) {
                    partial_sum += matrix[x][y][1] - 1;
                }else {
                    partial_sum--;
                }
            }
        }else{
            matrix[x][y][1]++;
            partial_sum--;
            if(matrix[x][y][1] == 1){
                conta_patta_p1++;
            }
            if(matrix[x][y][1] == k){
                this.eval.status = EvalStatus.LOSE;
            }else if(matrix[x][y][0] > 0){
                if(matrix[x][y][1] == 1) {
                    partial_sum -= matrix[x][y][0] - 1;
                }else {
                    partial_sum++;
                }
            }
        }
        return partial_sum;
    }

    private int invert_matrix(int x, int y, boolean mymove, int[][][] matrix, int partial_sum){
        if(mymove){
            if(matrix[x][y][0] == k){
                this.eval.status = EvalStatus.NOT_DEFINED; // per dirgli di aggiornarlo
            }else if(matrix[x][y][1] > 0){
                if(matrix[x][y][0] == 1) {
                    partial_sum -= matrix[x][y][1] - 1;
                }else {
                    partial_sum++; // per annullare partial_sum--; dopo
                }
            }
            if(matrix[x][y][0] == 1){
                conta_patta_p2--;
            }
            matrix[x][y][0]--;
            partial_sum--;
        }else{
            if(matrix[x][y][1] == k){
                this.eval.status = EvalStatus.NOT_DEFINED; // per dirgli di aggiornarlo
            }else if(matrix[x][y][0] > 0){
                if(matrix[x][y][1] == 1) {
                    partial_sum += matrix[x][y][0] - 1;
                }else {
                    partial_sum--;
                }
            }
            if(matrix[x][y][1] == 1){
                conta_patta_p1--;
            }
            matrix[x][y][1]--;
            partial_sum++;
        }
        if(matrix[x][y][0] < 0 || matrix[x][y][1] < 0){
            System.out.println("ERRORE");
        }
        return partial_sum;
    }

    //Aggiornamento valori della matrice M_Matrix ( Matrice delle righe )
    private void update_M_Matrix(){
        if (M_Matrix != null){
            //todo Le successive due righe sono qui per leggibilità, per una maggiore efficienza spostarle nel metodo di init
            int limit_y = M_Matrix[0].length - 1; //Dimensione colonne matrice M_Matrix

            //Aggiorno tutti i valori alla sinistra
            for ( int i = 0; i < k && (y-i >= 0); i++ ){ //y-i non deve portarmi fuori dal range matrice.
                if( (y-i <= limit_y)){
                    partial_sum_M_Matrix = update_matrix(x, y-i, M_Matrix, partial_sum_M_Matrix);
                }
            }
        }
    }

    //Aggiornamento valori matrice N_Matrix ( Matrice delle colonne )
    private void update_N_Matrix(){
        if (N_Matrix != null){
            //todo Le successive due righe sono qui per leggibilità, per una maggiore efficienza spostarle nel metodo di init
            int limit_x = N_Matrix.length - 1; //Dimensione righe matrice N_Matrix
            //Aggiorno tutti i valori in alto
            for ( int i = 0; i < k && ( x-i >= 0 ); i++ ) {
                if ((x - i <= limit_x)) {
                    partial_sum_N_Matrix = update_matrix(x - i, y, N_Matrix, partial_sum_N_Matrix);
                }
            }
        }
    }

    //Aggiornamento valori matrice K1_Matrix ( Matrice delle diagonali dal basso verso l'alto )
    private void update_K1_Matrix(){
        if (K1_Matrix != null){
            //todo Le successive due righe sono qui per leggibilità, per una maggiore efficienza verranno spostate nel metodo di init
            int K1_Matrix_Y = K1_Matrix[0].length - 1; // Numero colonne matrice K2_Matrix contate da zero
            int K1_Location_X = m - K1_Matrix.length + 1; //Riga della matrice in cui comincia la nostra matrice K1 (Parte arancio in foglio UNO )

            //Aggiorno tutti i valori nella diagonale muovendomi in direzione ' in basso a sinstra '
            for (int i = 0; i < k && x+i <= m && y-i >= 0; i++){
                if ( (x+i) >= K1_Location_X && y-i <= K1_Matrix_Y){
                    partial_sum_K1_Matrix = update_matrix(x + i - K1_Location_X, y-i, K1_Matrix, partial_sum_K1_Matrix);
                }
            }
        }
    }

    private void update_K2_Matrix(){
        if (K2_Matrix != null){
            //todo Le successive due righe sono qui per leggibilità, per una maggiore efficienza verranno spostate nel metodo di init
            int K2_Matrix_X = K2_Matrix.length - 1; //Numero righe matrice K2_Matrix contate partendo da zero
            int K2_Matrix_Y = K2_Matrix[0].length - 1; //Numero colonne matrice K2_Matrix contato partendo da zero

            //Aggiorno tutti i valori nella diagonale muovendomi in direzione ' in alto a sinistra '
            for (int i = 0; i < k && (x-i) >= 0 && (y-i) >= 0; i++){
                if (x-i <= K2_Matrix_X && y-i <= K2_Matrix_Y){
                    partial_sum_K2_Matrix = update_matrix(x-i, y-i, K2_Matrix, partial_sum_K2_Matrix);
                }
            }
        }
    }

    private void invert_M_Matrix(){
        if (M_Matrix != null){
            //todo Le successive due righe sono qui per leggibilità, per una maggiore efficienza spostarle nel metodo di init
            int limit_y = M_Matrix[0].length - 1; //Dimensione colonne matrice M_Matrix

            //Aggiorno tutti i valori alla sinistra
            for ( int i = 0; i < k && (y-i >= 0); i++ ){ //y-i non deve portarmi fuori dal range matrice.
                if( (y-i <= limit_y)){
                    partial_sum_M_Matrix = invert_matrix(x, y-i, my_move, M_Matrix, partial_sum_M_Matrix);
                }
            }
        }
    }

    private void invert_N_Matrix(){
        if (N_Matrix != null){
            //todo Le successive due righe sono qui per leggibilità, per una maggiore efficienza spostarle nel metodo di init
            int limit_x = N_Matrix.length - 1; //Dimensione righe matrice N_Matrix
            //Aggiorno tutti i valori in alto
            for ( int i = 0; i < k && ( x-i >= 0 ); i++ ) {
                if ((x - i <= limit_x)) {
                    partial_sum_N_Matrix = invert_matrix(x - i, y, my_move, N_Matrix, partial_sum_N_Matrix);
                }
            }
        }
    }

    private void invert_K1_Matrix(){
        if (K1_Matrix != null){
            //todo Le successive due righe sono qui per leggibilità, per una maggiore efficienza verranno spostate nel metodo di init
            int K1_Matrix_Y = K1_Matrix[0].length - 1; // Numero colonne matrice K2_Matrix contate da zero
            int K1_Location_X = m - K1_Matrix.length + 1; //Riga della matrice in cui comincia la nostra matrice K1 (Parte arancio in foglio UNO )

            //Aggiorno tutti i valori nella diagonale muovendomi in direzione ' in basso a sinstra '
            for (int i = 0; i < k && x+i <= m && y-i >= 0; i++){
                if ( (x+i) >= K1_Location_X && y-i <= K1_Matrix_Y){
                    partial_sum_K1_Matrix = invert_matrix(x + i - K1_Location_X, y-i, my_move, K1_Matrix, partial_sum_K1_Matrix);
                }
            }
        }
    }

    private void invert_K2_Matrix(){
        if (K2_Matrix != null){
            //todo Le successive due righe sono qui per leggibilità, per una maggiore efficienza verranno spostate nel metodo di init
            int K2_Matrix_X = K2_Matrix.length - 1; //Numero righe matrice K2_Matrix contate partendo da zero
            int K2_Matrix_Y = K2_Matrix[0].length - 1; //Numero colonne matrice K2_Matrix contato partendo da zero

            //Aggiorno tutti i valori nella diagonale muovendomi in direzione ' in alto a sinistra '
            for (int i = 0; i < k && (x-i) >= 0 && (y-i) >= 0; i++){
                if (x-i <= K2_Matrix_X && y-i <= K2_Matrix_Y){
                    partial_sum_K2_Matrix = invert_matrix(x-i, y-i, my_move, K2_Matrix, partial_sum_K2_Matrix);
                }
            }
        }
    }

}