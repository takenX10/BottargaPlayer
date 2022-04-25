package BottargaPlayer.PlayerNegamax;

import BottargaPlayer.Utils.Cell.EvalStatus;


/**
 * In questa versione di UpdateEvalMatrix l'aggiornamento avviene in maniera incrementale.
 * Quindi gli elementi nella matrice incrementano o decrementano di 1 ogni volta che viene aggiunta
 * una mossa nella corrispettiva riga. Nella versione finale invece che incrementare di uno, incrementiamo
 * in potenze di 10. Abbiamo dovuto ridefinire solo la update_matrix e la invert_matrix
 */
public class UpdateEvalMatrix extends BottargaPlayer.Utils.Matrix.UpdateEvalMatrix {

    public UpdateEvalMatrix(int M, int N, int K, boolean first_player, int[][][] M_Matrix, int[][][] N_Matrix,
            int[][][] K1_Matrix, int[][][] K2_Matrix) {
        super(M, N, K, first_player, M_Matrix, N_Matrix, K1_Matrix, K2_Matrix);
    }

    @Override
    protected int update_matrix(int x, int y, int[][][] matrix, int partial_sum){
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

    @Override
    protected int invert_matrix(int x, int y, boolean mymove, int[][][] matrix, int partial_sum){
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
        return partial_sum;
    }

}