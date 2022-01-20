package bottargaPlayer2;

import java.util.BitSet;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;

public class negamaxAlphabetaTransposition {
    // Debug print
    private Boolean debugPrint = false;
    private boolean debugLevels = false;

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

    public negamaxAlphabetaTransposition(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC, Integer timeout){
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
    public MNKCell iterative_minimax(){
        this.finish = System.currentTimeMillis() + finishms;
        this.endNegamax = false;
        for(currentMaxDepth = this.currentDepth + 1;currentMaxDepth <= this.TABLE_SIZE;currentMaxDepth++){

            if(debugLevels){
                System.out.println("Livello "+currentMaxDepth);
            }
            this.last_depth = true; // viene cambiato in false se avviene un taglio per profondità massima
            // return value di negamax inutile
            minimaxAlphaBeta(this.currentDepth, null, this.enemy, this.minusInf, this.inf);
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
            System.out.println("Profondita esplorata: da "+this.currentDepth+" a "+this.currentMaxDepth);
            System.out.println("Mossa "+this.currentDepth);
            System.out.println("Nodi esplorati: "+this.totalNodesReached);
            if(this.bestValue.status == EvalStatus.LOSE){
                System.out.println("TECNICAMENTE GIA PERSO!!!");
            }
            System.out.println("Total alphabeta cuts: "+this.totalCuts);
            System.out.println("Total cuts form TTable: "+this.transpositionCuts);
            System.out.println("Total match form TTable: "+this.transpositionExact);
            System.out.println("Eval: "+this.bestValue);
            System.out.println("Time elapsed: "+(time - finish + finishms) + (time > finish ? "ms (timed out)":"ms"));
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
        }
        
        return this.bestCell;
    }

    // Depth: The current depth of the total gametree (startin)
    // node: The node we just added (null for the first one)
    // stato: The player who added the last node
    // alpha: ..
    // beta: ..
    private CustomScore minimaxAlphaBeta(int depth, MNKCell node, MNKCellState stato, CustomScore alpha, CustomScore beta) { 

        // timer e valori per benchmark
        if(this.totalNodesReached % 10000 == 0 || this.endNegamax){
            if(System.currentTimeMillis() > this.finish){
                this.endNegamax = true;
                return minusInf;
            }
        }

        this.totalNodesReached++;

        // aggiorno la matrice allo stato corrente (va poi invertita prima di fare return)
        this.current_matrix.single_update_matrix(node, stato);

        // nodo foglia
        if(node != null && this.current_matrix.eval.isFinal()){
            CustomScore tmpeval = new CustomScore(this.current_matrix.eval.score, this.current_matrix.eval.status);
            this.current_matrix.single_invert_matrix(node,stato);
            return tmpeval;
        }else if(depth == this.currentMaxDepth){ // foglia per profondità, possibile andare più giu all'iterazione successiva.
            this.last_depth = false;
            CustomScore tmpeval = new CustomScore(this.current_matrix.eval.score, this.current_matrix.eval.status);
            this.current_matrix.single_invert_matrix(node,stato);
            return tmpeval;
        }
        CustomScore bestscore;
        CustomScore tmpscore;
        if(maximizing(stato)){
            bestscore = minusInf;
            int i;
            for(i = 0; i < this.FC.length; i++){
                if(!this.FC[i].used){
                    this.FC[i].used = true;
                    tmpscore = minimaxAlphaBeta(depth+1, this.FC[i].cell, (stato == this.me ? this.enemy : this.me), alpha, beta);
                    this.FC[i].used = false;
                    if( node == null ){ // per il primo giro
                        if(!this.endNegamax){
                            // DEBUG TODO REMOVE
                            if(this.FC[i].eval.isFinal() && this.FC[i].eval != minusInf && this.FC[i].eval != inf && this.FC[i].eval.status != tmpscore.status){
                                System.out.println("ERRORE!");
                            }
                            if(this.FC[i].eval.status == EvalStatus.CANT_WIN && tmpscore.status.value > this.FC[i].eval.status.value && tmpscore.status != EvalStatus.DRAW){
                                System.out.println("ERRORE!");
                            }
                            this.FC[i].eval = new CustomScore(tmpscore.score, tmpscore.status);
                        }
                    }
                    bestscore = CustomScore.maximize(bestscore, tmpscore);
                    alpha = CustomScore.maximize(alpha, bestscore);
                    if(!bestscore.compare(beta)){
                        this.totalCuts++;
                        break;
                    }
                }
            }

        }else{ // minimizing
            bestscore = inf;
            int i;
            for(i = 0; i < this.FC.length; i++){
                if(!this.FC[i].used){
                    this.FC[i].used = true;
                    tmpscore = minimaxAlphaBeta(depth+1, this.FC[i].cell, (stato == this.me ? this.enemy : this.me), alpha, beta);
                    this.FC[i].used = false;
                    if( node == null ){ // per il primo giro
                        if(!this.endNegamax){
                            this.FC[i].eval = new CustomScore(tmpscore.score, tmpscore.status);
                        }
                    }
                    bestscore = CustomScore.minimize(bestscore, tmpscore);
                    beta = CustomScore.minimize(beta, bestscore);
                    if(!alpha.compare(bestscore)){
                        this.totalCuts++;
                        break;
                    }
                }
            }
        }
        
        this.current_matrix.single_invert_matrix(node, stato);
        return bestscore;
    }

    private boolean maximizing(MNKCellState stato){
        if(stato == this.enemy){
            return true;
        }else{
            return false;
        }
    }

    // restituisce la cella massima tra quelle libere
    private void maxValue(){
        customMNKCell max = this.FC[0];
        if(this.debugLevels){
            System.out.println(this.FC[0]);
        }
        for(int i = 1; i < this.FC.length; i++){
            if(this.debugLevels){
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