package tester;
import bottargaPlayer.*;
import mnkgame.*;

//Questo main contiene degli esempi utili a capire praticamente come usare le classi EvalMatrix ed UpdateEvalMatrix.
public class main {
    public static UpdateEvalMatrix mossa(int m, int n, MNKCellState stato, UpdateEvalMatrix my_eval_matrix_updated, EvalMatrix my_eval_matrix){
        MNKCell move1 = new MNKCell(m, n, stato);
        my_eval_matrix_updated.single_update_matrix(move1);
        System.out.println("\n\nEval:\t"+my_eval_matrix_updated.eval);
        return my_eval_matrix_updated;
    }
    public static void main(String[] args) {
        int M = 3;
        int N = 3;
        int K = 3;
        EvalMatrix my_eval_matrix = new EvalMatrix(M, N, K);
        UpdateEvalMatrix my_eval_matrix_updated = new UpdateEvalMatrix(M, N, K, true, my_eval_matrix.getM_Matrix(), my_eval_matrix.getN_Matrix(), my_eval_matrix.getK1_Matrix(), my_eval_matrix.getK2_Matrix());
        MNKCell[] FC = new MNKCell[9];
        for(int i = 0; i < M; i++){
            for(int j = 0; j < N; j++){
                FC[i*N + j] = new MNKCell(i,j,MNKCellState.FREE);
            }
        }
        alphabeta test = new alphabeta(M,N,K,true,3,null,FC);
        MNKCell move = test.start();
        System.out.println(move);
    }
}