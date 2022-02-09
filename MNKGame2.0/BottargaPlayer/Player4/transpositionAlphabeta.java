package BottargaPlayer.Player4;

import java.util.BitSet;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;

public class transpositionAlphabeta {
   // Debug print
   private Boolean debugPrint = true;
   private boolean debugLevels = true;

   // costanti
   private final long finishms;
   private final int TABLE_SIZE = (int)Math.pow(2,23);
   private final BitSet EXACT = new BitSet(2);
   private final BitSet UPPER = new BitSet(2);
   private final BitSet LOWER = new BitSet(2);
   private final MNKCellState me;
   private final MNKCellState enemy;
   private final int m;
   private final int n;
   private final CustomScore minusInf;
   private final CustomScore inf;


   // valori per benchmark
   private int totalNodesReached;
   private int totalCuts;
   private int transpositionExact;
   private int transpositionCuts;

   // valori per condizioni finali
   private boolean endNegamax;
   private MNKCell bestCell;
   private CustomScore bestValue;
   private long finish;
   private final int currentDepth;
   private boolean last_depth;
   private int currentMaxDepth; // per inserirla nella transposition table

   // strutture
   private final UpdateEvalMatrix current_matrix;
   private final customMNKCell[] FC;
   private final transpositionTable ttable;


    public transpositionAlphabeta(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC, Integer timeout){
        // azzera valori benchmark
        this.totalNodesReached = 0;
        this.totalCuts = 0;

        // imposto condizioni finali
        this.currentDepth = MC.length;
        this.finishms = timeout * 1000 - 200;

        // Inserisco valori costanti
        this.m = M;
        this.n = N;
        this.minusInf = new CustomScore(-1, EvalStatus.LOSE);
        this.inf = new CustomScore(this.m * this.n + 1, EvalStatus.WIN);

        // Inizializzo la transposition table
        this.ttable = new transpositionTable(this.m, this.n, TABLE_SIZE);
        
        if(first){
            this.me = MNKCellState.P1;
            this.enemy = MNKCellState.P2;
        }else{
            this.me = MNKCellState.P2;
            this.enemy = MNKCellState.P1;
        }

        // inizializza matrice per eval
        EvalMatrix defaultMatrix = new EvalMatrix(M,N,K);
        this.current_matrix = new UpdateEvalMatrix(M,N,K,first, defaultMatrix.M_Matrix(), defaultMatrix.N_Matrix(), defaultMatrix.K1_Matrix(), defaultMatrix.K2_Matrix());
        this.current_matrix.multiple_update_matrix(MC);

        // inizializza array delle celle vuote
        this.FC = new customMNKCell[FC.length];
        for(int i = 0; i < FC.length; i++){
            this.FC[i] = new customMNKCell(FC[i], false, this.minusInf, false);
        }
    }


    // restituisce il valore migliore allo scadere del tempo, o la soluzione se la trova
    public MNKCell iterative_negamax(){
        this.finish = System.currentTimeMillis() + finishms;
        this.endNegamax = false;
        for(currentMaxDepth = 1;;currentMaxDepth++){
            this.last_depth = true; // viene cambiato in false se avviene un taglio per profondità massima
            // return value di negamax inutile
            if(debugLevels){
                System.out.println("Livello "+currentMaxDepth);
            }
            negamax(currentMaxDepth, 1, null,this.enemy, this.minusInf, this.inf);
            maxValue();
            
            // pareggio no perchè se ad esempio alcuni nodi pareggiano e altri
            // non sono stati ancora esplorati non ha senso finire senza esplorarli
            if(this.last_depth || this.endNegamax || this.bestValue.status == EvalStatus.WIN || this.bestValue.status == EvalStatus.LOSE){
                break;
            }
        }

        // DEBUG TODO REMOVE
        long time = System.currentTimeMillis();
        if(debugPrint){
            System.out.println("\nVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
            System.out.println("Profondita raggiunta: "+currentMaxDepth);
            System.out.println("Mossa "+this.currentDepth);
            System.out.println("Nodi esplorati: "+this.totalNodesReached);
            if(this.bestValue.status == EvalStatus.LOSE){
                System.out.println("TECNICAMENTE GIA PERSO!!!");
            }
            System.out.println("Total alphabeta cuts: "+this.totalCuts);
            System.out.println("Total TTable exact match: "+ this.transpositionExact);
            System.out.println("Total TTable cuts: "+ this.transpositionCuts);
            System.out.println("Eval: "+this.bestValue);
            System.out.println("Time elapsed: "+(time - finish + finishms) + (time > finish ? "ms (timed out)":"ms"));
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
        }
        return this.bestCell;
    }

    private CustomScore negamax(int depth, int sign, MNKCell node, MNKCellState stato, CustomScore alpha, CustomScore beta) {   
        CustomScore alphaOriginal = new CustomScore(alpha.score, alpha.status);
        MNKCell transpositionCell = null;

        // timer e valori per benchmark
        if(this.totalNodesReached % 10000 == 0 || this.endNegamax){
            if(System.currentTimeMillis() > this.finish){
                this.endNegamax = true;
                return minusInf;
            }
        }

        this.totalNodesReached++;
        if(this.ttable.exist(node,stato)){
            transpositionCell = this.ttable.current_value.next;
            if(this.ttable.current_value.depth == depth){
                if(this.ttable.current_value.type == this.EXACT){
                    // quando entra nell'if sottostante il valore è stato trovato nell'
                    // iterazione attuale, altrimenti in precedenti
                    if(this.ttable.current_value.maxDepth == this.currentMaxDepth){
                        this.transpositionExact++;
                        CustomScore eval = new CustomScore(this.ttable.current_value.eval.score, this.ttable.current_value.eval.status);
                        this.ttable.invertpos(node,stato);
                        return eval;
                    }else{
                        // TODO usa questa casistica per aspiration window.
                    }

                }else if(this.ttable.current_value.type == UPPER){
                    CustomScore test = CustomScore.maximize(beta, this.ttable.current_value.eval);
                    if(test == beta){
                        //beta = this.ttable.current_value.eval;
                    }
                }else if(this.ttable.current_value.type == LOWER){
                    //alpha = CustomScore.maximize(alpha, this.ttable.current_value.eval);
                }
                if(alpha.compare(beta)){
                    this.transpositionCuts++;
                    CustomScore eval = new CustomScore(this.ttable.current_value.eval.score, this.ttable.current_value.eval.status);
                    this.ttable.invertpos(node,stato);
                    return eval;
                }
            }
        }

        // aggiorno la matrice allo stato corrente (va poi invertita prima di fare return)
        this.current_matrix.single_update_matrix(node, stato);

        // nodo foglia
        if(node != null && this.current_matrix.eval.isFinal()){
            CustomScore tmpeval = new CustomScore(this.current_matrix.eval.score, this.current_matrix.eval.status);
            this.ttable.invertpos(node,stato);
            this.current_matrix.single_invert_matrix(node,stato);
            return (sign > 0 ? tmpeval : tmpeval.invert());
        }else if(depth == 0){ // foglia per profondità, possibile andare più giu all'iterazione successiva.
            this.last_depth = false;
            CustomScore tmpeval = new CustomScore(this.current_matrix.eval.score, this.current_matrix.eval.status);
            this.ttable.invertpos(node,stato);
            this.current_matrix.single_invert_matrix(node,stato);
            return (sign > 0 ? tmpeval : tmpeval.invert());
        }

        CustomScore maxscore = minusInf;
        CustomScore tmpscore;
        int i;
        int draw = 0;
        MNKCell maxCell = null;
        boolean breakVal = false;
        if(transpositionCell != null){ // primo giro fuori per renderlo piu efficiente
            int j = 0;
            while(this.FC[j].cell != transpositionCell){
                j++;
            }
            tmpscore = negamax(depth - 1, -sign, this.FC[j].cell, (stato == this.me ? this.enemy : this.me), beta.invert(), alpha.invert()).invert();
            this.FC[j].used = false;
            if( node == null ){ // per il primo giro
                if(!this.endNegamax){
                    // DEBUG TODO REMOVE
                    if(this.FC[j].eval.isFinal() && this.FC[j].eval != minusInf && this.FC[j].eval != inf && this.FC[j].eval.status != tmpscore.status){
                        System.out.println("ERRORE!");
                    }
                    this.FC[j].eval = new CustomScore(tmpscore.score, tmpscore.status);
                }
            }
            if(tmpscore.status == EvalStatus.NOT_DEFINED){
                draw = -1;
            }
            maxscore = CustomScore.maximize(maxscore, tmpscore);
            if(maxscore == tmpscore){
                maxCell = this.FC[j].cell;
            }
            if(maxscore.status != EvalStatus.DRAW){
                alpha = CustomScore.maximize(alpha, maxscore);
            }
            // alpha >= beta
            if(alpha.compare(beta)){
                draw = -1;
                totalCuts++;
                breakVal = true;
            }
        }
        if(!breakVal){
            for(i = 0; i < this.FC.length; i++){
                if(!this.FC[i].used && this.FC[i].cell != transpositionCell){
                    this.FC[i].used = true;
                    tmpscore = negamax(depth - 1, -sign, this.FC[i].cell, (stato == this.me ? this.enemy : this.me), beta.invert(), alpha.invert()).invert();
                    this.FC[i].used = false;
                    if( node == null ){ // per il primo giro
                        if(!this.endNegamax){
                            // DEBUG TODO REMOVE
                            if(this.FC[i].eval.isFinal() && this.FC[i].eval != minusInf && this.FC[i].eval != inf && this.FC[i].eval.status != tmpscore.status){
                                System.out.println("ERRORE!");
                            }
                            this.FC[i].eval = new CustomScore(tmpscore.score, tmpscore.status);
                        }
                    }
                    if(tmpscore.status == EvalStatus.NOT_DEFINED){
                        draw = -1;
                    }
                    maxscore = CustomScore.maximize(maxscore, tmpscore);
                    if(maxscore == tmpscore){
                        maxCell = this.FC[i].cell;
                    }
                    if(maxscore.status != EvalStatus.DRAW){
                        alpha = CustomScore.maximize(alpha, maxscore);
                    }
                    // alpha >= beta
                    if(alpha.compare(beta)){
                        draw = -1;
                        totalCuts++;
                        break;
                    }
                }
            }
        }
        if(draw == -1 && maxscore.status == EvalStatus.DRAW){// E' un pareggio sicuro
            maxscore = new CustomScore(0, EvalStatus.NOT_DEFINED);// notDefinedDraw;
        }

        BitSet currentType;
        if(alphaOriginal.compare(maxscore)){
            currentType = this.LOWER;
        }else if(maxscore.compare(beta)){
            currentType = this.UPPER;
        }else{
            currentType = this.EXACT;
        }
        // TODO: Update next value
        this.ttable.updatepos(node, stato, maxscore, depth, currentMaxDepth, maxCell, currentType);

        this.ttable.invertpos(node,stato);
        this.current_matrix.single_invert_matrix(node, stato);
        return maxscore;
    }

    // restituisce la cella massima tra quelle libere
    private void maxValue(){
        customMNKCell max = this.FC[0];
        if(debugLevels){
            System.out.println(this.FC[0]);
        }

        for(int i = 1; i < this.FC.length; i++){
            if(debugLevels){
                System.out.println(this.FC[i]);
            }
            if(this.FC[i].eval.compare(max.eval)){
                max = this.FC[i];
            }
        }
        if(debugLevels){
            System.out.println("Max: "+max);
        }
        this.bestCell = max.cell;
        this.bestValue = new CustomScore(max.eval.score, max.eval.status);
    }
}