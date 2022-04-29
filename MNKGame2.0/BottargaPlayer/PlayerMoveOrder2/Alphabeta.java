package BottargaPlayer.PlayerMoveOrder2;

import BottargaPlayer.Utils.Cell.*;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;

public class Alphabeta extends BottargaPlayer.Utils.Player.Alphabeta{

    public Alphabeta(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC, Integer timeout, boolean debugPrint, boolean debugLevels) {
        super(M, N, K, first, MC, FC, timeout, debugPrint, debugLevels);
    }

    @Override
    protected CustomScore negamax(int depth, int sign, MNKCell node, CustomMNKCell[] currentFreeCells, MNKCellState stato, CustomScore alpha, CustomScore beta) {   
        // timer e valori per benchmark
        if(checkTime()){
            return this.minusInf;
        }

        // aggiorno la matrice allo stato corrente (va poi invertita prima di fare return)
        this.currentMatrix.single_update_matrix(node, stato);
        CustomScore score = checkLeaf(node, sign, depth, stato);
        if(score != null){
            return score;
        }
        if(node == null){
            MoveOrder order = new MoveOrder(FC, null, currentMatrix, stato, 0);
            this.FC = order.cells;
        }
        // eseguo il corpo principale del negamax (in cui avviene la ricorsione)
        score = loop(depth, sign, stato, currentFreeCells, alpha, beta, node);

        // riporto la matrice allo stato originale
        this.currentMatrix.single_invert_matrix(node, stato);
        
        return score;
    }
}