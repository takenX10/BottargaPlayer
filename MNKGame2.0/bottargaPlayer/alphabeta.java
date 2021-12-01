package bottargaPlayer;

import java.util.BitSet;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;

/*
    note:
    - ho creato la struttura dati customMNKCell per tenere traccia delle FC già ispezionate ai livelli superiori
    - L'alphabeta è di tipo failsoft
    - TODO: aspiration windows
    - TODO: zebrist
    - TODO: Iterative deepening

 */
public class alphabeta{
    /*
    private final BitSet EXACT = new BitSet(2);
    private final BitSet UPPER = new BitSet(2);
    private BitSet LOWER = new BitSet(2);
    private UpdateEvalMatrix current_matrix;
    private int maxdepth;
    private MNKCell best; // current best move found
    private float bestval; // eval value of current best move
    private MNKCellState me;
    private MNKCellState enemy;
    private Long[][] zobrist;
    private long timerend;
    private int M;
    private int N;
    private int k;
    private int current_depth;
    private customMNKCell[] FC;
    private transpositionTable table;

    // benchmark
    private long nodes_touched;
    private long hits;
    private long nodecuts;

    private boolean cutbeforeend;

    public alphabeta(int M, int N, int K, boolean first, int current_depth, MNKCell[] MC, MNKCell[] FC){
        this.EXACT.set(1);
        this.UPPER.set(2);
        this.table = new transpositionTable(M,N,(int)Math.pow(2,20));
        this.current_depth = current_depth;
        EvalMatrix m = new EvalMatrix(M,N,K);
        this.current_matrix = new UpdateEvalMatrix(M,N,K,first, m.M_Matrix(), m.N_Matrix(), m.K1_Matrix(), m.K2_Matrix());
        this.current_matrix.multiple_update_matrix(MC);
        this.FC = new customMNKCell[FC.length];
        this.k = K;
        if(first){
            this.me = MNKCellState.P1;
            this.enemy = MNKCellState.P2;
        }else{
            this.me = MNKCellState.P2;
            this.enemy = MNKCellState.P1;
        }
        for(int i = 0; i < FC.length; i++){
            this.FC[i] = new customMNKCell(FC[i],false);
        }
    }

    public MNKCell start_iterative(){
        // benchmark
        this.nodes_touched = 0;
        this.nodecuts = 0;
        this.hits = 0;
        //System.out.println(current_depth);
        this.best = null;
        this.bestval = this.current_matrix.lose_value;
        long start = System.currentTimeMillis();
        this.timerend = start + 7500;

        for(Integer i = 1;;i++){
            MNKCell tmp;
            this.cutbeforeend = false;
            this.maxdepth = current_depth + i;
            singlestart();
            // TODO REMOVE THIS DEBUG
            //System.out.println("Livello "+i.toString()+": ");
            //System.out.println("Best cell -> "+this.best+" Best val -> "+this.bestval+"\n");
            //System.out.println("Time: "+ (System.currentTimeMillis() - start));
            //
            if(System.currentTimeMillis() > this.timerend){
                System.out.println("Best cell: "+this.best+" val: "+this.bestval+"\nnot enough time, time: "+(System.currentTimeMillis() - start)+ "\ndepth: "+this.maxdepth);
                System.out.println("hits: "+this.hits);
                System.out.println("nodes: "+this.nodes_touched);
                System.out.println("node cuts: "+this.nodecuts+"\n");
                return this.best;
            }else if(this.best == null){ // se persino con maxdepth == 1 abbiamo sconfitta assicurata restituisce la prima cella vuota
                this.best = this.FC[0].cell;
                return this.best;
            } else if (bestval == current_matrix.win_value || !this.cutbeforeend) {
                System.out.println("Best cell: "+this.best+" val: "+this.bestval+"\nTime: "+ (System.currentTimeMillis() - start));
                System.out.println("hits: "+this.hits);
                System.out.println("nodes: "+this.nodes_touched+"\n");
                System.out.println("node cuts: "+this.nodecuts+"\n");
                return this.best;
            }
        }
    }

    public void singlestart(){
        float max = this.current_matrix.lose_value;
        float tot;
        MNKCell currentbest = null;
        // primo ciclo sempre massimizzante perchè la prima mossa da scegliere è sempre quella del player
        for(int i = 0; i < FC.length; i++) {
            this.FC[i].used = true;
            tot = recursive(this.current_depth + 1, this.FC[i].cell, this.current_matrix, this.me, this.current_matrix.lose_value, this.current_matrix.win_value);
            // TODO: REMOVE THIS, DEBUG ONLY
            //System.out.println("cell: "+this.FC[i].cell.toString()+"\ntot: "+tot+"\n");
            /////////////
            this.FC[i].used = false;
            // TODO: implementare alpha beta anche a questo livello
            // e ottimizzare questa parte di codice (ripetizioni inutili in giro)
            if (tot > max) {
                max = tot;
                currentbest = this.FC[i].cell;
                if (this.bestval < max) {
                    this.bestval = max;
                    this.best = currentbest;
                }
            }
            // controllo per il tempo, forza il ritorno del massimo tra il livello prima e i valori calcolati fino a quel punto del livello nuovo
            if (System.currentTimeMillis() > this.timerend) {
                return;
            }
        }
        // necessario perchè se ad un livello inferiore si scopre che tutti i valori sono più bassi, vanno aggiornate lo stesso le mosse
        // seguendo il nuovo ordine (sicuramente più preciso di quello del livello prima)
        if(currentbest != null){
            this.bestval = max;
            this.best = currentbest;
        }
    }

*/
    /////
    private final long finishms = 9800;

    private MNKCell bestCell;
    private float bestValue;
    private long finish;
    private int totalNodesReached = 0;
    private int currentDepth;
    private int k; // per pareggio

    private UpdateEvalMatrix current_matrix;
    private customMNKCell[] FC;
    private MNKCellState me;
    private MNKCellState enemy;

    public alphabeta(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC){
        // imposto valori di default
        this.currentDepth = MC.length;
        this.k = K;
        EvalMatrix defaultMatrix = new EvalMatrix(M,N,K);
        this.current_matrix = new UpdateEvalMatrix(M,N,K,first, defaultMatrix.M_Matrix(), defaultMatrix.N_Matrix(), defaultMatrix.K1_Matrix(), defaultMatrix.K2_Matrix());
        this.current_matrix.multiple_update_matrix(MC);
        this.FC = new customMNKCell[FC.length];
        if(first){
            this.me = MNKCellState.P1;
            this.enemy = MNKCellState.P2;
        }else{
            this.me = MNKCellState.P2;
            this.enemy = MNKCellState.P1;
        }
        for(int i = 0; i < FC.length; i++){
            this.FC[i] = new customMNKCell(FC[i],false);
        }
    }

    // restituisce il valore migliore allo scadere del tempo, o la soluzione se la trova
    public MNKCell iterative_negamax(){
        this.finish = System.currentTimeMillis() + finishms;
        int i;
        for(i = 1;;i++){
            // DEBUG PRINT TODO
            //System.out.println("livello "+i);
            float useless = negamax(i,1, null,this.enemy);
            maxValue();
            if(this.bestValue == this.current_matrix.win_value || this.bestValue == this.current_matrix.lose_value|| k+1 == this.bestValue || System.currentTimeMillis() > finish){
                break;
            }
        }
        // DEBUG PRINT TODO
        long time = System.currentTimeMillis();
        System.out.println("\nVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
        System.out.println("Mossa "+this.currentDepth);
        System.out.println("Nodi totali raggiunti: "+this.totalNodesReached);
        System.out.println("Eval: "+this.bestValue);
        System.out.println("Time elapsed: "+(time - finish + finishms) + (time > finish ? " (timed out)":""));
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
        return this.bestCell;
    }

    private float negamax(int depth, int sign, MNKCell node, MNKCellState stato) {
        if(node == null){ // prima iterazione, siamo sicuramente nel caso massimizzante
            for(int i = 0; i < this.FC.length; i++){
                if(!this.FC[i].used){
                    this.FC[i].used = true;
                    this.FC[i].eval = - negamax(depth - 1, -sign, this.FC[i].cell, this.me);
                    this.FC[i].used = false;
                }
            }
            return 0;
        }

        // timer e valori per benchmark
        if(this.totalNodesReached % 10000 == 0){
            if(System.currentTimeMillis() > this.finish){
                return 0;
            }
        }
        this.totalNodesReached++;

        // nodo foglia
        this.current_matrix.single_update_matrix(node, stato);
        if(depth == 0 || this.current_matrix.eval == this.current_matrix.win_value || this.current_matrix.lose_value == this.current_matrix.eval || this.k + 1 == this.current_matrix.eval){
            float tmpeval = this.current_matrix.eval;
            this.current_matrix.single_invert_matrix(node,stato);
            return sign * tmpeval;
        }
        float maxscore = this.current_matrix.lose_value;
        int i;
        for(i = 0; i < this.FC.length; i++){
            if(!this.FC[i].used){
                this.FC[i].used = true;
                maxscore = Math.max(maxscore, - negamax(depth - 1, -sign, this.FC[i].cell, stato == this.me ? this.enemy : this.me));
                this.FC[i].used = false;
            }
        }
        this.current_matrix.single_invert_matrix(node, stato);
        return maxscore;
    }

    // restituisce la cella massima tra quelle libere
    private void maxValue(){
        customMNKCell max = this.FC[0];
        for(int i = 1; i < this.FC.length; i++){
            if(this.FC[i].getEval() > max.getEval()){
                max = this.FC[i];
            }
        }
        this.bestCell = max.cell;
        this.bestValue = max.eval;
        // TODO DEBUG PRINT
        //System.out.println("Max eval value: "+max.eval);
    }

    // TODO rimuovimi -> debug
    public long factorial(int number) {
        long result = 1;

        for (int factor = 2; factor <= number; factor++) {
            result *= factor;
        }

        return result;
    }
    ///

}
