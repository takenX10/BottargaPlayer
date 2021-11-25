package bottargaPlayer;

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
    public int[][] M_Matrix;  //Matrice delle righe
    private int partial_sum_M_Matrix;
    public int[][] N_Matrix;  //Matrice delle colonne
    private int partial_sum_N_Matrix;
    public int[][] K1_Matrix; // Matrice diagonali che vanno dal basso verso l'alto. (Lettura da sinistra a destra)
    private int partial_sum_K1_Matrix;
    public int[][] K2_Matrix; // Matrice diagonali che vanno dall'alto verso il basso. (Lettura da sinistra a destra)
    private int partial_sum_K2_Matrix;

    //todo se so che in una serie di mosse una mi ha portato alla vittoria smetto di aggiornare tutte le matrici analizzando le altre mosse? Si
    public float eval; //Valore finale che assume l'eval (-2 hai perso, -3 hai vinto, otherwise punteggio che indica quanto siamo messi bene)
    float win_value = 1000;
    float lose_value = -1000;
    //NB: Le coordinate X ed Y sottostanti partono da 0 (come prima cella) ed arrivano ad M-1 e N-1.
    public UpdateEvalMatrix(int M, int N, int K, boolean first_player, int[][] M_Matrix, int[][] N_Matrix, int[][] K1_Matrix, int[][] K2_Matrix){
        if (x > M-1 || y > N-1 ) {
            System.out.println("\n !!! ERRORE\n Le coordinate del nuovo simbolo non rientrano nella matrice di gioco! ");
            this.M_Matrix = null;   //Impostando questi valori a null tutti gli eventuali metodi chiamati successivamente saranno praticamente fermati evitando calcoli inutili
            this.N_Matrix = null;
            this.K1_Matrix = null;
            this.K2_Matrix = null;
        }
        this.m = M - 1;
        this.k = K;
        this.first_player = first_player;
        this.M_Matrix = M_Matrix;
        this.partial_sum_M_Matrix = 0;
        this.N_Matrix = N_Matrix;
        this.partial_sum_N_Matrix = 0;
        this.K1_Matrix = K1_Matrix;
        this.partial_sum_K1_Matrix = 0;
        this.K2_Matrix = K2_Matrix;
        this.partial_sum_K2_Matrix = 0;
    }

    /*
    * Scelta implementativa: per modularità e chiarezza del codice farò un metodo per ogni matrice da aggiornare.
    * Dopodichè creerò il metodo che richiama tutti i suddetti permettendo l'aggiornamento generale.
    */

    //Aggiornamento valori della matrice M_Matrix ( Matrice delle righe )
    private void update_M_Matrix(){
        if (M_Matrix != null){
            //todo Le successive due righe sono qui per leggibilità, per una maggiore efficienza spostarle nel metodo di init
            int limit_x = M_Matrix.length - 1; //Dimensione righe matrice M_Matrix
            int limit_y = M_Matrix[0].length - 1; //Dimensione colonne matrice M_Matrix

            boolean is_in; //Mi dice se il nuovo simbolo rientra direttamente nella matrice M_Matrix

            if ( x <= limit_x && y <= limit_y ) is_in = true;
            else is_in = false;

            //Aggiorno il valore presente nelle stesse coordinate, se non sono fuori matrice
            if ( is_in ){
                if (my_move) {
                    ///// EDITATO DA ALE
                    if(M_Matrix[x][y] == k+1) {
                        // non fare niente, il simbolo non cambia la situazione
                    }else if(M_Matrix[x][y] >=0){
                        partial_sum_M_Matrix++;
                        M_Matrix[x][y]++; // solo simboli tuoi
                    }else{ // entrambi i simboli -> pari
                        partial_sum_M_Matrix -= M_Matrix[x][y];
                        M_Matrix[x][y] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                    }
                    if (M_Matrix[x][y] == k){ //Hai vinto
                        eval = win_value;
                    }
                } else {
                    if(M_Matrix[x][y] <=0){
                        partial_sum_M_Matrix--;
                        M_Matrix[x][y]--; // solo simboli dell'avversario
                    }else{ // entrambi i simboli -> pari
                        partial_sum_M_Matrix -= M_Matrix[x][y];
                        M_Matrix[x][y] = k+1; // K + 1 perchè è un valore tecnicamente irraggiungibile
                    }
                    if(M_Matrix[x][y] == -k){
                        eval = lose_value;
                    }
                    //////////
                }
            }

            //Aggiorno tutti i valori alla sinistra
            for ( int i = 1; i < k && (y-i >= 0); i++ ){ //y-i non deve portarmi fuori dal range matrice.
                if( (y-i <= limit_y)){
                    if (my_move) {
                        ///// EDITATO DA ALE
                        if(M_Matrix[x][y - i] == k+1) {
                            // non fare niente, il simbolo non cambia la situazione
                        }else if(M_Matrix[x][y - i] >=0){
                            partial_sum_M_Matrix++;
                            M_Matrix[x][y - i]++; // solo simboli tuoi
                        }else{ // entrambi i simboli -> pari
                            partial_sum_M_Matrix -= M_Matrix[x][y - i];
                            M_Matrix[x][y - i] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                        }
                        if (M_Matrix[x][y - i] == k){ //Hai vinto
                            eval = win_value;
                        }
                    } else {
                        if(M_Matrix[x][y - i] <=0){
                            partial_sum_M_Matrix--;
                            M_Matrix[x][y - i]--; // solo simboli dell'avversario
                        }else{ // entrambi i simboli -> pari
                            partial_sum_M_Matrix -= M_Matrix[x][y - i];
                            M_Matrix[x][y - i] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                        }
                        if(M_Matrix[x][y - i] == -k){
                            eval = lose_value;
                        }
                        //////////
                    }
                }
            }
        }
    }

    //Aggiornamento valori matrice N_Matrix ( Matrice delle colonne )
    private void update_N_Matrix(){
        if (N_Matrix != null){
            //todo Le successive due righe sono qui per leggibilità, per una maggiore efficienza spostarle nel metodo di init
            int limit_x = N_Matrix.length - 1; //Dimensione righe matrice N_Matrix
            int limit_y = N_Matrix[0].length - 1; //Dimensione colonne matrice N_Matrix

            boolean is_in; //Mi dice se il nuovo simbolo rientra direttamete nella matrice N_Matrix o se si trova 'al di fuori'

            if ( x <= limit_x && y <= limit_y ) is_in = true;
            else is_in = false;

            //Aggiorno il valore presente nelle stesse coordinate, se non sono fuori matrice
            if ( is_in ) {
                if (my_move) {
                    ///// EDITATO DA ALE
                    if(N_Matrix[x][y] == k+1) {
                        // non fare niente, il simbolo non cambia la situazione
                    }else if(N_Matrix[x][y] >=0){
                        partial_sum_N_Matrix++;
                        N_Matrix[x][y]++; // solo simboli tuoi
                    }else{ // entrambi i simboli -> pari
                        partial_sum_N_Matrix -= N_Matrix[x][y];
                        N_Matrix[x][y] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                    }
                    if (N_Matrix[x][y] == k){ //Hai vinto
                        eval = win_value;
                    }
                } else {
                    if(N_Matrix[x][y] <=0){
                        partial_sum_N_Matrix--;
                        N_Matrix[x][y]--; // solo simboli dell'avversario
                    }else{ // entrambi i simboli -> pari
                        partial_sum_N_Matrix -= N_Matrix[x][y];
                        N_Matrix[x][y] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                    }
                    if(N_Matrix[x][y] == -k){
                        eval = lose_value;
                    }
                    //////////
                }
            }

            //Aggiorno tutti i valori in alto
            for ( int i = 1; i < k && ( x-i >= 0 ); i++ ){
                if ( (x-i <= limit_x )) {
                    if (my_move) {
                        ///// EDITATO DA ALE
                        if(N_Matrix[x - i][y] == k+1) {
                            // non fare niente, il simbolo non cambia la situazione
                        }else if(N_Matrix[x - i][y] >=0){
                            partial_sum_N_Matrix++;
                            N_Matrix[x - i][y]++; // solo simboli tuoi
                        }else{ // entrambi i simboli -> pari
                            partial_sum_N_Matrix -= N_Matrix[x - i][y];
                            N_Matrix[x - i][y] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                        }
                        if (N_Matrix[x - i][y] == k){ //Hai vinto
                            eval = win_value;
                        }
                    } else {
                        if(N_Matrix[x - i][y] <=0){
                            partial_sum_N_Matrix--;
                            N_Matrix[x - i][y]--; // solo simboli dell'avversario
                        }else{ // entrambi i simboli -> pari
                            partial_sum_N_Matrix -= N_Matrix[x - i][y];
                            N_Matrix[x - i][y] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                        }
                        if(N_Matrix[x - i][y] == -k){
                            eval = lose_value;
                        }
                        //////////
                    }
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

            boolean is_in; //Mi dice se il nuovo simbolo rientra direttamente nella matrice K1_Matrix

            if ( x >= K1_Location_X && y <= K1_Matrix_Y ) is_in = true;
            else is_in = false;

            if ( is_in ) {
                if (my_move) {
                    ///// EDITATO DA ALE
                    if(K1_Matrix[x][y] == k+1) {
                        // non fare niente, il simbolo non cambia la situazione
                    }else if(K1_Matrix[x][y] >=0){
                        partial_sum_K1_Matrix++;
                        K1_Matrix[x][y]++; // solo simboli tuoi
                    }else{ // entrambi i simboli -> pari
                        partial_sum_K1_Matrix -= K1_Matrix[x][y];
                        K1_Matrix[x][y] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                    }
                    if (K1_Matrix[x][y] == k){ //Hai vinto
                        eval = win_value;
                    }
                } else {
                    if(K1_Matrix[x][y] <=0){
                        partial_sum_K1_Matrix--;
                        K1_Matrix[x][y]--; // solo simboli dell'avversario
                    }else{ // entrambi i simboli -> pari
                        partial_sum_K1_Matrix -= K1_Matrix[x][y];
                        K1_Matrix[x][y] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                    }
                    if(K1_Matrix[x][y] == -k){
                        eval = lose_value;
                    }
                    //////////
                }
            }

            //Aggiorno tutti i valori nella diagonale muovendomi in direzione ' in basso a sinstra '
            for (int i = 1; i < k && x+i <= m && y-i >= 0; i++){
                if ( (x+i) >= K1_Location_X && y-i <= K1_Matrix_Y){
                    if (my_move) {
                        ///// EDITATO DA ALE
                        if(K1_Matrix[x + i - K1_Location_X][y-i] == k+1) {
                            // non fare niente, il simbolo non cambia la situazione
                        }else if(K1_Matrix[x + i - K1_Location_X][y-i] >=0){
                            partial_sum_K1_Matrix++;
                            K1_Matrix[x + i - K1_Location_X][y-i]++; // solo simboli tuoi
                        }else{ // entrambi i simboli -> pari
                            partial_sum_K1_Matrix -= K1_Matrix[x + i - K1_Location_X][y-i];
                            K1_Matrix[x + i - K1_Location_X][y-i] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                        }
                        if (K1_Matrix[x + i - K1_Location_X][y-i] == k){ //Hai vinto
                            eval = win_value;
                        }
                    } else {
                        if(K1_Matrix[x + i - K1_Location_X][y-i] <=0){
                            partial_sum_K1_Matrix--;
                            K1_Matrix[x + i - K1_Location_X][y-i]--; // solo simboli dell'avversario
                        }else{ // entrambi i simboli -> pari
                            partial_sum_K1_Matrix -= K1_Matrix[x + i - K1_Location_X][y-i];
                            K1_Matrix[x + i - K1_Location_X][y-i] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                        }
                        if(K1_Matrix[x + i - K1_Location_X][y-i] == -k){
                            eval = lose_value;
                        }
                        //////////
                    }
                }
            }
        }
    }

    private void update_K2_Matrix(){
        if (K2_Matrix != null){
            //todo Le successive due righe sono qui per leggibilità, per una maggiore efficienza verranno spostate nel metodo di init
            int K2_Matrix_X = K2_Matrix.length - 1; //Numero righe matrice K2_Matrix contate partendo da zero
            int K2_Matrix_Y = K2_Matrix[0].length - 1; //Numero colonne matrice K2_Matrix contato partendo da zero

            boolean is_in; //Mi dice se il nuovo simbolo rientra direttamente nella matrice K1_Matrix
            if (x <= K2_Matrix_X && y <= K2_Matrix_Y) is_in = true;
            else is_in = false;

            if (is_in) {
                if (my_move) {
                    ///// EDITATO DA ALE
                    if(K2_Matrix[x][y] == k+1) {
                        // non fare niente, il simbolo non cambia la situazione
                    }else if(K2_Matrix[x][y] >=0){
                        partial_sum_K2_Matrix++;
                        K2_Matrix[x][y]++; // solo simboli tuoi
                    }else{ // entrambi i simboli -> pari
                        partial_sum_K2_Matrix -= K2_Matrix[x][y];
                        K2_Matrix[x][y] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                    }
                    if (K2_Matrix[x][y] == k){ //Hai vinto
                        eval = win_value;
                    }
                } else {
                    if(K2_Matrix[x][y] <=0){
                        partial_sum_K2_Matrix--;
                        K2_Matrix[x][y]--; // solo simboli dell'avversario
                    }else{ // entrambi i simboli -> pari
                        partial_sum_K2_Matrix -= K2_Matrix[x][y];
                        K2_Matrix[x][y] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                    }
                    if(K2_Matrix[x][y] == -k){
                        eval = lose_value;
                    }
                    //////////
                }
            }

            //Aggiorno tutti i valori nella diagonale muovendomi in direzione ' in alto a sinistra '
            for (int i = 1; i < k && (x-i) >= 0 && (y-i) >= 0; i++){
                if (x-i <= K2_Matrix_X && y-1 <= K2_Matrix_Y){
                    if (my_move) {
                        ///// EDITATO DA ALE
                        if(K2_Matrix[x-i][y-i] == k+1) {
                            // non fare niente, il simbolo non cambia la situazione
                        }else if(K2_Matrix[x-i][y-i] >=0){
                            partial_sum_K2_Matrix++;
                            K2_Matrix[x-i][y-i]++; // solo simboli tuoi
                        }else{ // entrambi i simboli -> pari
                            partial_sum_K2_Matrix -= K2_Matrix[x-i][y-i];
                            K2_Matrix[x-i][y-i] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                        }
                        if (K2_Matrix[x-i][y-i] == k){ //Hai vinto
                            eval = win_value;
                        }
                    } else {
                        if(K2_Matrix[x-i][y-i] <=0){
                            partial_sum_K2_Matrix--;
                            K2_Matrix[x-i][y-i]--; // solo simboli dell'avversario
                        }else{ // entrambi i simboli -> pari
                            partial_sum_K2_Matrix -= K2_Matrix[x-i][y-i];
                            K2_Matrix[x-i][y-i] = k+1; // K + 1 perchè è un valore virtualmente irraggiungibile
                        }
                        if(K2_Matrix[x-i][y-i] == -k){
                            eval = lose_value;
                        }
                        //////////
                    }
                }
            }
        }
    }

    private void calcolate_eval_value(int [][] M, int [][] N, int [][] K1, int [][] K2){
        if(eval != win_value && eval != lose_value) {
            eval = (float) (partial_sum_M_Matrix + partial_sum_N_Matrix + partial_sum_K1_Matrix + partial_sum_K2_Matrix) / 4;
        }
    }

    public void single_update_matrix(MNKCell cell){ //Metodo per aggiornare tutte le 4 matrici dell'eval considerando una singola mossa
        this.x = cell.i;
        this.y = cell.j;
        if (( cell.state == MNKCellState.P1 && first_player) || (cell.state == MNKCellState.P2 && !first_player) ) this.my_move = true;
        else this.my_move = false;
        update_M_Matrix();
        update_N_Matrix();
        update_K1_Matrix();
        update_K2_Matrix();
        if (eval != win_value) calcolate_eval_value(M_Matrix, N_Matrix, K1_Matrix, K2_Matrix);
    }

    public void single_update_matrix_state(MNKCell cell, MNKCellState state){ //Metodo per aggiornare tutte le 4 matrici dell'eval considerando una singola mossa
        this.x = cell.i;
        this.y = cell.j;
        if (( state == MNKCellState.P1 && first_player) || (state == MNKCellState.P2 && !first_player) ) this.my_move = true;
        else this.my_move = false;
        update_M_Matrix();
        update_N_Matrix();
        update_K1_Matrix();
        update_K2_Matrix();
        if (eval != win_value) calcolate_eval_value(M_Matrix, N_Matrix, K1_Matrix, K2_Matrix);
    }

    //Metodo per aggiornare le 4 matrici dell'eval considerando una serie di mosse
    public void multiple_update_matrix(MNKCell[] move_list){
        if(move_list != null){
            for (int i = 0; i < move_list.length; i++){
                single_update_matrix(move_list[i]);
            }
            calcolate_eval_value(M_Matrix, N_Matrix, K1_Matrix, K2_Matrix);
        }
    }
}