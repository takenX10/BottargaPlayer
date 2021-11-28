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
        /*MNKCell[] FC = new MNKCell[5];
        for(int i = 0; i < M; i++){
            for(int j = 0; j < N; j++){
                FC[i*N + j] = new MNKCell(i,j,MNKCellState.FREE);
            }
        }*/
        MNKCell a = new MNKCell(0,0, MNKCellState.FREE);
        MNKCell b = new MNKCell(0,1, MNKCellState.FREE);
        MNKCell c = new MNKCell(0,2, MNKCellState.FREE);
        MNKCell d = new MNKCell(1,0, MNKCellState.FREE);
        MNKCell e = new MNKCell(1,1, MNKCellState.FREE);
        MNKCell f = new MNKCell(1,2, MNKCellState.FREE);
        MNKCell g = new MNKCell(2,0, MNKCellState.FREE);
        MNKCell h = new MNKCell(2,1, MNKCellState.FREE);
        MNKCell i = new MNKCell(2,2, MNKCellState.FREE);

        MNKCell[] FC = {a,b,c,d,e,f,g,h,i};

        MNKCell[] MC = {};
        alphabeta test = new alphabeta(M,N,K,true,10,MC,FC);
        MNKCell move = test.start();
        System.out.println(move);
    }
}