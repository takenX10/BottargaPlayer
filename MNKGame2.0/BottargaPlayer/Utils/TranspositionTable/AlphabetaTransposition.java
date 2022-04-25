package BottargaPlayer.Utils.TranspositionTable;

import java.util.BitSet;

import BottargaPlayer.Utils.Cell.EvalStatus;
import BottargaPlayer.Utils.Cell.CustomScore;
import BottargaPlayer.Utils.Cell.CustomMNKCell;


import mnkgame.MNKCell;
import mnkgame.MNKCellState;

public class AlphabetaTransposition extends BottargaPlayer.Utils.Player.Alphabeta {

    /* Dimensione della transposition table 2^23 */
    protected final int TABLE_SIZE = (int)Math.pow(2,23);
    /**
     * Costanti per le entry della transposition table
     */
    protected final BitSet EXACT = new BitSet(2);
    protected final BitSet UPPER = new BitSet(2);
    protected final BitSet LOWER = new BitSet(2);

    /* Accumulatore dei tagli fatti con la transopsition table per debugging */
    protected int transpositionCuts;

    /* Transposition table */
    protected final TranspositionTable ttable;

    /**
     * 
     * @param M valore M della tabella
     * @param N valore N della tabella
     * @param K valore K della tabella
     * @param first true se il player e' primo, false altrimenti
     * @param MC L'array di celle marcate/piene
     * @param FC L'array di celle libere
     * @param timeout Il tempo del timeout
     * @param debugPrint true per vedere le print di debug iniziali, false altrimenti
     * @param debugLevels true per vedere le print di debug per ogni livello dell'iterative deepening, false altrimenti
     */
    public AlphabetaTransposition(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC, Integer timeout, boolean debugPrint, boolean debugLevels) {
        super(M, N, K, first, MC, FC, timeout, debugPrint, debugLevels);
        // Azzero accumulatore
        this.transpositionCuts = 0;

        // Inizializzo la transposition table
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
    
    /**
     * 
     * @param node  nodo corrente
     * @param stato lo stato della cella corrente
     * @param depth profondita corrente
     * @param alpha valore attuale di alpha
     * @param beta  valore attuale di beta
     * @return  null se la board attuale non e' presente nella transposition table o non 
     *          e' presente ma non ha salvato il valore esatto, l'eval salvato altrimenti
     */
    protected CustomScore checkTable(MNKCell node, MNKCellState stato, int depth, CustomScore alpha, CustomScore beta){
        // Controllo se la posizione e' presente nella transposition table
        if(this.ttable.exist(node,stato)){
            if(this.ttable.currentValue.depth == depth && this.ttable.currentValue.type == this.EXACT && this.ttable.currentValue.maxDepth == this.currentMaxDepth){
                this.transpositionCuts++;
                CustomScore eval = new CustomScore(this.ttable.currentValue.eval.score, this.ttable.currentValue.eval.status);
                this.ttable.invertpos(node,stato);
                return eval;
            }
        }
        return null;
    }

    /**
     * 
     * @param depth profondita corrente
     * @param sign  segno, utilizzato per capire se invertire o meno l'eval
     * @param node  nodo corrente
     * @param fc    array delle celle libere, utile per il move ordering
     * @param stato lo stato della cella corrente
     * @param alpha valore di alpha corrente
     * @param beta  valore di beta corrente
     * @return  L'eval migliore che il negamax riesce a tirare fuori (il return value finale e' inutile).
     */
    @Override
    protected CustomScore negamax(int depth, int sign, MNKCell node, CustomMNKCell[] currentFreeCells, MNKCellState stato, CustomScore alpha, CustomScore beta) {   
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

        score = loop(depth, sign, stato, null, alpha, beta, node);

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