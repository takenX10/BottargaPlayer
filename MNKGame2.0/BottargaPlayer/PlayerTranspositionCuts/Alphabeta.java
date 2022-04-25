package BottargaPlayer.PlayerTranspositionCuts;
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
            if(this.ttable.currentValue.depth == depth){
                if(this.ttable.currentValue.type == this.EXACT){
                    // quando entra nell'if sottostante il valore è stato trovato nell'
                    // iterazione attuale, altrimenti in precedenti
                    if(this.ttable.currentValue.maxDepth == this.currentMaxDepth){
                        this.transpositionCuts++;
                        CustomScore eval = new CustomScore(this.ttable.currentValue.eval.score, this.ttable.currentValue.eval.status);
                        this.ttable.invertpos(node,stato);
                        return eval;
                    }
                }else if(this.ttable.currentValue.type == UPPER){
                    // questa in realta' e' una minimize
                    beta = (CustomScore.maximize(beta, this.ttable.currentValue.eval) == beta ? this.ttable.currentValue.eval : beta);
                }else if(this.ttable.currentValue.type == LOWER){
                    alpha = CustomScore.maximize(alpha, this.ttable.currentValue.eval);
                }
                if(alpha.compare(beta)){
                    this.transpositionCuts++;
                    CustomScore eval = new CustomScore(this.ttable.currentValue.eval.score, this.ttable.currentValue.eval.status);
                    this.ttable.invertpos(node,stato);
                    return eval;
                }
            }
        }
        return null;
    }

}