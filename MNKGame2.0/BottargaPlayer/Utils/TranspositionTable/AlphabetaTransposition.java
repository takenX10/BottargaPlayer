package BottargaPlayer.Utils.TranspositionTable;

import java.util.BitSet;

import BottargaPlayer.Utils.Cell.EvalStatus;
import BottargaPlayer.Utils.Cell.CustomScore;
import BottargaPlayer.Utils.Cell.CustomMNKCell;


import mnkgame.MNKCell;
import mnkgame.MNKCellState;

public class AlphabetaTransposition extends BottargaPlayer.Utils.Player.Alphabeta {

    protected final int TABLE_SIZE = (int)Math.pow(2,23);
    protected final BitSet EXACT = new BitSet(2);
    protected final BitSet UPPER = new BitSet(2);
    protected final BitSet LOWER = new BitSet(2);

    protected int transpositionCuts;
    protected final TranspositionTable ttable;

    public AlphabetaTransposition(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC, Integer timeout, boolean debugPrint, boolean debugLevels) {
        super(M, N, K, first, MC, FC, timeout, debugPrint, debugLevels);
        this.transpositionCuts = 0;
        this.ttable = new TranspositionTable(this.m, this.n, TABLE_SIZE);
    }

    @Override
    protected void logPrint(long time){
        System.out.println("\nVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
        System.out.println("Profondita raggiunta: "+currentMaxDepth);
        System.out.println("Mossa "+this.currentDepth);
        System.out.println("Nodi esplorati: "+this.totalNodesReached);
        if(this.bestCell.eval.status == EvalStatus.LOSE){
            System.out.println("TECNICAMENTE GIA PERSO!!!");
        }
        System.out.println("Total alphabeta cuts: "+this.totalCuts);
        System.out.println("Total Transposition Table Cuts: "+this.transpositionCuts);
        System.out.println("Eval: "+this.bestCell.eval);
        System.out.println("Time elapsed: "+(time - finish + this.finishms) + (time > finish ? "ms (timed out)":"ms"));
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
    }
    
    protected CustomScore checkTable(MNKCell node, MNKCellState stato, int depth, CustomScore alpha, CustomScore beta){
        // Controllo se la posizione e' presente nella transposition table
        if(this.ttable.exist(node,stato)){
            if(this.ttable.current_value.depth == depth && this.ttable.current_value.type == this.EXACT && this.ttable.current_value.maxDepth == this.currentMaxDepth){
                this.transpositionCuts++;
                CustomScore eval = new CustomScore(this.ttable.current_value.eval.score, this.ttable.current_value.eval.status);
                this.ttable.invertpos(node,stato);
                return eval;
            }
        }
        return null;
    }

    @Override
    protected CustomScore negamax(int depth, int sign, MNKCell node, CustomMNKCell[] fc, MNKCellState stato, CustomScore alpha, CustomScore beta) {   
        CustomScore alphaOriginal = new CustomScore(alpha.score, alpha.status);
        // timer e valori per benchmark
        if(checkTime()){
            return this.minusInf;
        }

        CustomScore eval = checkTable(node, stato, depth, alpha, beta);
        if(eval != null){
            return eval;
        }

        // aggiorno la matrice allo stato corrente (va poi invertita prima di fare return)
        this.currentMatrix.single_update_matrix(node, stato);
        CustomScore score = checkLeaf(node, sign, depth, stato);
        if(score != null){
            return score;
        }

        score = loop(depth, sign, stato, alpha, beta, node);

        // Inserisco nuova entry nella transposition table
        BitSet currentType;
        if(alphaOriginal.compare(score)){
            currentType = this.UPPER;
        }else if(score.compare(beta)){
            currentType = this.LOWER;
        }else{
            currentType = this.EXACT;
        }
        this.ttable.updatepos(node, stato, score, depth, currentMaxDepth, null, currentType);
        this.ttable.invertpos(node,stato);


        this.currentMatrix.single_invert_matrix(node, stato);
        
        return score;
    }
}