package tester;
import bottargaPlayer.*;
import mnkgame.*;

//Questo main contiene degli esempi utili a capire praticamente come usare le classi EvalMatrix ed UpdateEvalMatrix.
public class main {
    public static void main(String[] args) {
        int M = 7;
        int N = 6;
        int K = 5;

        EvalMatrix my_eval_matrix = new EvalMatrix(M, N, K); //Creo e inizializzo le 4 matrici dell'eval in base alla configurazione della partita ( M N K )

        //Invio le 4 matrici dell'eval e i dettagli di gioco alla classe che mi permetterà di modificarle in base a nuove mosse
        //N.B. Il quarto parametro 'first_player' mi chiede se in questa partita siamo noi a iniziare; se si impostare a true.
        UpdateEvalMatrix my_eval_matrix_updated = new UpdateEvalMatrix(M, N, K, true, my_eval_matrix.M_Matrix, my_eval_matrix.N_Matrix, my_eval_matrix.K1_Matrix, my_eval_matrix.K2_Matrix );

        //Esempio di aggiornamento matrici dell'eval con una singola mossa
        MNKCell move1 = new MNKCell(0, 0, MNKCellState.P1); //Inserisco le coordiante della mossa e chi la fa ( P1 è il primo player che gioca )

        my_eval_matrix_updated.single_update_matrix(move1); //Metodo che mi aggiorna le matrici dell'eval in base alla singola mossa

        //aggiorno tutte e 4 le matrici con i nuovi valori. //Devo farlo uno ad uno dato che è possibile ritornare più valori in un metodo. //Eventualmente si può risolvere con una struttura
        my_eval_matrix.M_Matrix = my_eval_matrix_updated.M_Matrix;
        my_eval_matrix.N_Matrix = my_eval_matrix_updated.N_Matrix;
        my_eval_matrix.K1_Matrix = my_eval_matrix_updated.K1_Matrix;
        my_eval_matrix.K2_Matrix = my_eval_matrix_updated.K2_Matrix;

        //metodi per la stampa delle matrici risulanti
        my_eval_matrix.printM_Matrix();
        my_eval_matrix.printN_Matrix();
        my_eval_matrix.printK1_Matrix();
        my_eval_matrix.printK2_Matrix();


        //esempio di aggiornamento matrici dell'eval con un vettore di più mosse

        //creo le diverse mosse da mettere nel vettore


        MNKCell move2 = new MNKCell(1, 0, MNKCellState.P2);
        MNKCell move3 = new MNKCell(2, 0, MNKCellState.P1);
        MNKCell move4 = new MNKCell(2, 1, MNKCellState.P2);

        //le inserisco nel vettore
        MNKCell[] lista_mosse = {move1, move2, move3, move4};
        my_eval_matrix_updated.multiple_update_matrix(lista_mosse);

        //aggiorno tutte e 4 le matrici con i nuovi valori.
        my_eval_matrix.M_Matrix = my_eval_matrix_updated.M_Matrix;
        my_eval_matrix.N_Matrix = my_eval_matrix_updated.N_Matrix;
        my_eval_matrix.K1_Matrix = my_eval_matrix_updated.K1_Matrix;
        my_eval_matrix.K2_Matrix = my_eval_matrix_updated.K2_Matrix;

        //metodi per la stampa delle matrici risulanti
        my_eval_matrix.printM_Matrix();
        my_eval_matrix.printN_Matrix();
        my_eval_matrix.printK1_Matrix();
        my_eval_matrix.printK2_Matrix();

    }
}