package BottargaPlayer.PlayerTranspositionCuts;

import java.util.BitSet;

import BottargaPlayer.Utils.Cell.CustomMNKCell;
import BottargaPlayer.Utils.Cell.CustomScore;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;

public class Alphabeta extends BottargaPlayer.Utils.TranspositionTable.AlphabetaTransposition{
    public Alphabeta(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC, Integer timeout, boolean debugPrint, boolean debugLevels) {
        super(M, N, K, first, MC, FC, timeout, debugPrint, debugLevels);
    }


    @Override
    protected CustomScore checkTable(MNKCell node, MNKCellState stato, int depth, CustomScore alpha, CustomScore beta){
        if(this.ttable.exist(node,stato)){
            if(this.ttable.current_value.depth == depth){
                if(this.ttable.current_value.type == this.EXACT){
                    // quando entra nell'if sottostante il valore è stato trovato nell'
                    // iterazione attuale, altrimenti in precedenti
                    if(this.ttable.current_value.maxDepth == this.currentMaxDepth){
                        this.transpositionCuts++;
                        CustomScore eval = new CustomScore(this.ttable.current_value.eval.score, this.ttable.current_value.eval.status);
                        this.ttable.invertpos(node,stato);
                        return eval;
                    }else{
                        // TODO usa questa casistica per aspiration window.
                    }

                }else if(this.ttable.current_value.type == UPPER){
                    beta = (CustomScore.maximize(beta, this.ttable.current_value.eval) == beta ? this.ttable.current_value.eval : beta); // minimize
                }else if(this.ttable.current_value.type == LOWER){
                    alpha = CustomScore.maximize(alpha, this.ttable.current_value.eval);
                }
                if(alpha.compare(beta)){
                    this.transpositionCuts++;
                    CustomScore eval = new CustomScore(this.ttable.current_value.eval.score, this.ttable.current_value.eval.status);
                    this.ttable.invertpos(node,stato);
                    return eval;
                }
            }
        }
        return null;
    }

}