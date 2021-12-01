package bottargaPlayer;

import java.util.BitSet;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;

/*
    note:
    - ho creato la struttura dati customMNKCell per tenere traccia delle FC già ispezionate ai livelli superiori
    - L'alphabeta è di tipo failsoft
    - TODO: aspiration windows
    - fixare tagli esagerati

 */

public class alphabeta{
    // alphabeta, negamax e transposition table.

    // costanti
    private final long finishms = 9800;
    private final int ttableSize = (int)Math.pow(2,20);
    private final BitSet EXACT = new BitSet(2);
    private final BitSet UPPER = new BitSet(2);
    private final BitSet LOWER = new BitSet(2);
    private final MNKCellState me;
    private final MNKCellState enemy;

    // valori per benchmark
    private int totalNodesReached;
    private int TranspositionTableMatches;
    private int totalCuts;

    // valori per condizioni finali
    private MNKCell bestCell;
    private float bestValue;
    private long finish;
    private final int currentDepth;
    private boolean last_depth;
    private boolean theoreticalLoss;
    private int currentMaxDepth; // per inserirla nella transposition table
    private final int k; // per pareggio

    // strutture
    private final UpdateEvalMatrix current_matrix;
    private final customMNKCell[] FC;
    private final transpositionTable ttable;

    public alphabeta(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC){
        // azzera valori benchmark
        this.totalNodesReached = 0;
        this.totalCuts = 0;
        this.TranspositionTableMatches = 0;

        // imposto condizioni finali
        this.currentDepth = MC.length;
        this.theoreticalLoss = false;
        this.k = K;

        // imposta costanti
        this.EXACT.set(1);
        this.UPPER.set(2);
        if(first){
            this.me = MNKCellState.P1;
            this.enemy = MNKCellState.P2;
        }else{
            this.me = MNKCellState.P2;
            this.enemy = MNKCellState.P1;
        }

        // inizializza transposition table
        this.ttable = new transpositionTable(M,N,ttableSize);

        // inizializza matrice per eval
        EvalMatrix defaultMatrix = new EvalMatrix(M,N,K);
        this.current_matrix = new UpdateEvalMatrix(M,N,K,first, defaultMatrix.M_Matrix(), defaultMatrix.N_Matrix(), defaultMatrix.K1_Matrix(), defaultMatrix.K2_Matrix());
        this.current_matrix.multiple_update_matrix(MC);

        // inizializza array delle celle vuote
        this.FC = new customMNKCell[FC.length];
        for(int i = 0; i < FC.length; i++){
            this.FC[i] = new customMNKCell(FC[i],false, this.current_matrix.lose_value, false);
        }
    }

    // restituisce il valore migliore allo scadere del tempo, o la soluzione se la trova
    public MNKCell iterative_negamax(){
        this.finish = System.currentTimeMillis() + finishms;
        for(currentMaxDepth = 1;;currentMaxDepth++){
            // DEBUG TODO REMOVE
            //System.out.println("livello "+currentMaxDepth);

            this.last_depth = true; // viene cambiato in false se avviene un taglio per profondità massima

            // return value di negamax inutile
            negamax(currentMaxDepth,1, null,this.enemy, this.current_matrix.lose_value, this.current_matrix.win_value);
            // DEBUG TODO REMOVE IMPORTANT
            //System.out.println("Maxvalue: "+this.bestValue+" "+this.bestCell);

            maxValue();

            // pareggio no perchè se ad esempio alcuni nodi pareggiano e altri
            // non sono stati ancora esplorati non ha senso finire senza esplorarli
            if(this.last_depth || this.bestValue == this.current_matrix.win_value || this.bestValue == this.current_matrix.lose_value ||System.currentTimeMillis() > finish){
                break;
            }
            if(theoreticalLoss){
                break;
            }
        }

        // DEBUG TODO REMOVE
        long time = System.currentTimeMillis();
        System.out.println("\nVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
        System.out.println("Mossa "+this.currentDepth);
        if(theoreticalLoss || this.bestValue == this.current_matrix.lose_value){
            System.out.println("TECNICAMENTE GIA PERSO O PAREGGIATO!!!");
        }
        System.out.println("Total alphabeta cuts: "+this.totalCuts);
        System.out.println("Transposition Table match: "+this.TranspositionTableMatches);
        System.out.println("Nodi totali raggiunti: "+this.totalNodesReached);
        System.out.println("Eval: "+this.bestValue);
        System.out.println("Time elapsed: "+(time - finish + finishms) + (time > finish ? "ms (timed out)":"ms"));
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");

        maxValue();
        return this.bestCell;
    }

    private float negamax(int depth, int sign, MNKCell node, MNKCellState stato, float alpha, float beta) {
        // DEBUG TODO REMOVE
        //System.out.println("Cella corrente: "+node+"\nstato: "+stato+"\ndepth: "+depth+"\n");

        float alphaOrig = alpha;

        // timer e valori per benchmark
        if(this.totalNodesReached % 10000 == 0){
            if(System.currentTimeMillis() > this.finish){
                return 0;
            }
        }
        this.totalNodesReached++;

        // controllo della transposition table
        // nota: la funzione exist aggiorna l'hash all'interno di ttable, a prescindere che poi
        // l'elemento sia presente o no, quindi è necessario ritornare indietro con la funzione
        // ttable.invertpos(node, stato) prima di fare la return (stessa cosa dell'update della matrice)
        if(this.ttable.exist(node,stato)){
            if(this.ttable.current_value.depth <= depth){
                this.TranspositionTableMatches++;
                if(this.ttable.current_value.type == this.EXACT){
                    // quando entra nell'if sottostante il valore è stato trovato nell'
                    // iterazione attuale, altrimenti in precedenti
                    if(this.ttable.current_value.maxDepth == this.currentMaxDepth){
                        float eval = this.ttable.current_value.eval;
                        this.ttable.invertpos(node,stato);
                        return eval;
                    }else{
                        // TODO usa questa casistica per aspiration window.
                    }

                }else if(this.ttable.current_value.type == UPPER){
                    beta = Math.min(beta, this.ttable.current_value.eval);
                }else{
                    alpha = Math.max(alpha, this.ttable.current_value.eval);
                }
                if(alpha >= beta){
                    this.totalCuts++;
                    float eval = this.ttable.current_value.eval;
                    this.ttable.invertpos(node,stato);
                    return eval;
                }
            }
        }

        // aggiorno la matrice allo stato corrente (va poi invertita prima di fare return)
        this.current_matrix.single_update_matrix(node, stato);
        // nodo foglia
        if(this.current_matrix.eval == this.current_matrix.win_value || this.current_matrix.lose_value == this.current_matrix.eval || this.k + 1 == this.current_matrix.eval || - this.k - 1 == this.current_matrix.eval){
            float tmpeval = this.current_matrix.eval;
            this.ttable.invertpos(node,stato);
            this.current_matrix.single_invert_matrix(node,stato);
            return sign * tmpeval;
        }else if(depth == 0){ // foglia per profondità, possibile andare più giu all'iterazione successiva.
            this.last_depth = false;
            float tmpeval = this.current_matrix.eval;
            this.current_matrix.single_invert_matrix(node,stato);
            return sign * tmpeval;
        }

        float maxscore = this.current_matrix.lose_value;
        float tmpscore;
        int i;
        for(i = 0; i < this.FC.length; i++){
            if(!this.FC[i].used){
                this.FC[i].used = true;
                tmpscore =-negamax(depth - 1, -sign, this.FC[i].cell, (stato == this.me ? this.enemy : this.me), -beta, -alpha);
                this.FC[i].used = false;
                if( node == null ){ // per il primo giro
                    if(tmpscore != this.current_matrix.lose_value){
                        this.theoreticalLoss = false;
                        this.FC[i].setEval(tmpscore);
                    }else{
                        this.FC[i].lost = true;
                    }
                }
                maxscore = Math.max(maxscore, tmpscore);
                alpha = Math.max(alpha, maxscore);
                if(alpha >= beta){
                    totalCuts++;
                    break;
                }
            }
        }

        // inserimento valore finale nella transposition table
        BitSet currentType;
        if(maxscore <= alphaOrig){
            currentType = this.UPPER;
        }else if(maxscore >= beta){
            currentType = this.LOWER;
        }else{
            currentType = this.EXACT;
        }
        // TODO: Update next value
        this.ttable.updatepos(node, stato, maxscore, depth, currentMaxDepth, null, currentType);

        this.ttable.invertpos(node,stato);
        this.current_matrix.single_invert_matrix(node, stato);
        return maxscore;
    }

    // restituisce la cella massima tra quelle libere
    private void maxValue(){
        customMNKCell max = this.FC[0];
        // questa variabile serve a decidere la mossa che perde più tardi
        // nel caso che perdano tutte
        boolean islost;
        if(!max.lost){
            islost = false;
            max.lostVal++;
        }else{
            islost = true;
        }
        // DEBUG TODO REMOVE
        //System.out.println(this.FC[0].cell.toString()+ " "+this.FC[0].getEval()+ " "+ this.FC[0].lost + " " +this.FC[0].lostVal);

        for(int i = 1; i < this.FC.length; i++){
            if(this.FC[i].getEval() == - this.k - 1){
                this.FC[i].eval = -this.FC[i].eval;
            }
            if(this.FC[i].getEval() > max.getEval()){
                max = this.FC[i];
            }
            if(!this.FC[i].lost){
                this.FC[i].lostVal++;
                islost = false;
            }

            // DEBUG TODO REMOVE
            //System.out.println(this.FC[i].cell.toString()+ " "+this.FC[i].getEval()+ " "+ this.FC[i].lost+ " " +this.FC[i].lostVal);

        }

        if(islost){
            // scegliamo la mossa che perde più tardi
            max = this.FC[0];
            for(int i = 1; i < this.FC.length; i++){
                // DEBUG TODO REMOVE
                if(this.FC[i].lostVal > max.lostVal){
                    max = this.FC[i];
                }
            }
        }

        //System.out.println("");
        this.bestCell = max.cell;
        this.bestValue = max.eval;
    }


// negamax e alphabeta
    /*
    // negamax e alphabeta
    private final long finishms = 98000;

    private MNKCell bestCell;
    private float bestValue;
    private long finish;
    private int totalNodesReached = 0;
    private int currentDepth;
    private int totalCuts;
    private int k; // per pareggio

    private UpdateEvalMatrix current_matrix;
    private customMNKCell[] FC;
    private MNKCellState me;
    private MNKCellState enemy;
    private boolean theoreticalLoss;

    public alphabeta(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC){
        // imposto valori di default
        this.currentDepth = MC.length;
        this.theoreticalLoss = false;
        this.k = K;
        this.totalCuts = 0;

        EvalMatrix defaultMatrix = new EvalMatrix(M,N,K);
        this.current_matrix = new UpdateEvalMatrix(M,N,K,first, defaultMatrix.M_Matrix(), defaultMatrix.N_Matrix(), defaultMatrix.K1_Matrix(), defaultMatrix.K2_Matrix());
        this.current_matrix.multiple_update_matrix(MC);

        if(first){
            this.me = MNKCellState.P1;
            this.enemy = MNKCellState.P2;
        }else{
            this.me = MNKCellState.P2;
            this.enemy = MNKCellState.P1;
        }

        this.FC = new customMNKCell[FC.length];
        for(int i = 0; i < FC.length; i++){
            this.FC[i] = new customMNKCell(FC[i],false, this.current_matrix.lose_value);
        }
    }

    // restituisce il valore migliore allo scadere del tempo, o la soluzione se la trova
    public MNKCell iterative_negamax(){
        this.finish = System.currentTimeMillis() + finishms;
        int i;
        for(i = 1;;i++){
            // DEBUG PRINT TODO
            //System.out.println("livello "+i);
            float useless = negamax(i,1, null,this.enemy, this.current_matrix.lose_value, this.current_matrix.win_value);
            maxValue();
            // DEBUG PRINT TODO
            //System.out.println("Maxvalue: "+this.bestValue+" "+this.bestCell);
            if(this.bestValue == this.current_matrix.win_value || this.bestValue == this.current_matrix.lose_value || k+1 == this.bestValue || -k-1 == this.bestValue || System.currentTimeMillis() > finish){
                break;
            }
            if(theoreticalLoss){
                break;
            }
        }

        // DEBUG PRINT TODO
        long time = System.currentTimeMillis();
        System.out.println("\nVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
        System.out.println("Mossa "+this.currentDepth);
        if(theoreticalLoss || this.bestValue == this.current_matrix.lose_value){
            System.out.println("TECNICAMENTE GIA PERSO!!!");
        }
        System.out.println("Total alphabeta cuts: "+this.totalCuts);
        System.out.println("Nodi totali raggiunti: "+this.totalNodesReached);
        System.out.println("Eval: "+this.bestValue);
        System.out.println("Time elapsed: "+(time - finish + finishms) + (time > finish ? "ms (timed out)":"ms"));
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");


        return this.bestCell;
    }

    private float negamax(int depth, int sign, MNKCell node, MNKCellState stato, float alpha, float beta) {
        // DEBUG TODO REMOVE
        //System.out.println("Cella corrente: "+node+"\nstato: "+stato+"\ndepth: "+depth+"\n");

        // timer e valori per benchmark
        if(this.totalNodesReached % 10000 == 0){
            if(System.currentTimeMillis() > this.finish){
                return 0;
            }
        }
        this.totalNodesReached++;

        // nodo foglia
        this.current_matrix.single_update_matrix(node, stato);
        if(depth == 0 || this.current_matrix.eval == this.current_matrix.win_value || this.current_matrix.lose_value == this.current_matrix.eval || this.k + 1 == this.current_matrix.eval || - this.k - 1 == this.current_matrix.eval){
            float tmpeval = this.current_matrix.eval;
            this.current_matrix.single_invert_matrix(node,stato);
            return sign * tmpeval;
        }
        float maxscore = this.current_matrix.lose_value;
        float tmpscore;
        int i;
        for(i = 0; i < this.FC.length; i++){
            if(!this.FC[i].used){
                this.FC[i].used = true;
                tmpscore =-negamax(depth - 1, -sign, this.FC[i].cell, (stato == this.me ? this.enemy : this.me), -beta, -alpha);
                this.FC[i].used = false;
                if( node == null && tmpscore != this.current_matrix.lose_value){ // per il primo giro
                    this.theoreticalLoss = false;
                    this.FC[i].setEval(tmpscore);
                }
                maxscore = Math.max(maxscore, tmpscore);
                alpha = Math.max(alpha, maxscore);
                if(alpha >= beta){
                    totalCuts++;
                    break;
                }
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

*/

// solo negamax
    /* solo negamax
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
    private boolean theoreticalLoss;

    public alphabeta(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC){
        // imposto valori di default
        this.currentDepth = MC.length;
        this.theoreticalLoss = false;
        this.k = K;

        EvalMatrix defaultMatrix = new EvalMatrix(M,N,K);
        this.current_matrix = new UpdateEvalMatrix(M,N,K,first, defaultMatrix.M_Matrix(), defaultMatrix.N_Matrix(), defaultMatrix.K1_Matrix(), defaultMatrix.K2_Matrix());
        this.current_matrix.multiple_update_matrix(MC);

        if(first){
            this.me = MNKCellState.P1;
            this.enemy = MNKCellState.P2;
        }else{
            this.me = MNKCellState.P2;
            this.enemy = MNKCellState.P1;
        }

        this.FC = new customMNKCell[FC.length];
        for(int i = 0; i < FC.length; i++){
            this.FC[i] = new customMNKCell(FC[i],false, this.current_matrix.lose_value);
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
            // DEBUG PRINT TODO
            //System.out.println("Maxvalue: "+this.bestValue+" "+this.bestCell);
            if(this.bestValue == this.current_matrix.win_value || this.bestValue == this.current_matrix.lose_value || k+1 == this.bestValue || System.currentTimeMillis() > finish){
                break;
            }
            if(theoreticalLoss){
                break;
            }
        }

        // DEBUG PRINT TODO
        long time = System.currentTimeMillis();
        System.out.println("\nVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
        System.out.println("Mossa "+this.currentDepth);
        if(theoreticalLoss || this.bestValue == this.current_matrix.lose_value){
            System.out.println("TECNICAMENTE GIA PERSO!!!");
        }
        System.out.println("Nodi totali raggiunti: "+this.totalNodesReached);
        System.out.println("Eval: "+this.bestValue);
        System.out.println("Time elapsed: "+(time - finish + finishms) + (time > finish ? "ms (timed out)":"ms"));
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");


        return this.bestCell;
    }

    private float negamax(int depth, int sign, MNKCell node, MNKCellState stato) {
        if(node == null){ // prima iterazione, siamo sicuramente nel caso massimizzante
            this.theoreticalLoss = true;
            for(int i = 0; i < this.FC.length; i++){
                if(!this.FC[i].used){
                    this.FC[i].used = true;
                    float tmpeval = - negamax(depth - 1, -sign, this.FC[i].cell, this.me);
                    if(tmpeval != this.current_matrix.lose_value){
                        this.theoreticalLoss = false;
                        this.FC[i].setEval(tmpeval);
                    }
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
*/


}
