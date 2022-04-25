package BottargaPlayer.PlayerMoveOrder;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;

public class Alphabeta {
    // Debug print
    private Boolean debugPrint = true;
    private Boolean debugLevels = true;

    // costanti
    private long finishms;
    private final MNKCellState me;
    private final MNKCellState enemy;
    private final int m;
    private final int n;
    private final CustomScore minusInf;
    private final CustomScore inf;

    // valori per benchmark
    private int totalNodesReached;
    private int totalCuts;

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
    private final CustomMNKCell[] FC;

    public Alphabeta(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC, Integer timeout){
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
        this.FC = new CustomMNKCell[FC.length];
        for(int i = 0; i < FC.length; i++){
            this.FC[i] = new CustomMNKCell(FC[i], this.minusInf);
        }
    }


    // restituisce il valore migliore allo scadere del tempo, o la soluzione se la trova
    public MNKCell iterative_negamax(){
        this.finish = System.currentTimeMillis() + finishms;
        this.endNegamax = false;
        for(currentMaxDepth = 1;;currentMaxDepth++){
            this.last_depth = true; // viene cambiato in false se avviene un taglio per profondità massima
            if(debugLevels){
                System.out.println("Livello "+currentMaxDepth);
            }
            // return value di negamax inutile
            negamax(currentMaxDepth, 1, null, this.FC, this.enemy, this.minusInf, this.inf);
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
            System.out.println("Eval: "+this.bestValue);
            System.out.println("Time elapsed: "+(time - finish + finishms) + (time > finish ? "ms (timed out)":"ms"));
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
        }
        return this.bestCell;
    }

    private CustomScore negamax(int depth, int sign, MNKCell node, CustomMNKCell[] FC, MNKCellState stato, CustomScore alpha, CustomScore beta) {   
        
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
            return (sign > 0 ? tmpeval : tmpeval.invert());
        }else if(depth == 0){ // foglia per profondità, possibile andare più giu all'iterazione successiva.
            this.last_depth = false;
            CustomScore tmpeval = new CustomScore(this.current_matrix.eval.score, this.current_matrix.eval.status);
            this.current_matrix.single_invert_matrix(node,stato);
            return (sign > 0 ? tmpeval : tmpeval.invert());
        }

        CustomScore maxscore = minusInf;
        CustomScore tmpscore;
        int i;
        int draw = 0;
        MoveOrder myOrder = new MoveOrder(FC, node, current_matrix, stato, currentMaxDepth - depth);
        CustomMNKCell currentCell;
        while((currentCell = myOrder.getCell()) != null){
            tmpscore = negamax(depth - 1, -sign, currentCell.cell, myOrder.cells, (stato == this.me ? this.enemy : this.me), beta.invert(), alpha.invert()).invert();
            if( node == null ){ // per il primo giro
                if(!this.endNegamax){
                    i=0;
                    while(currentCell.cell != this.FC[i].cell) i++;
                    this.FC[i] = new CustomMNKCell(currentCell.cell, tmpscore);
                }
            }
            if(tmpscore.status == EvalStatus.NOT_DEFINED || tmpscore.status == EvalStatus.CANT_WIN || tmpscore.status == EvalStatus.CANT_LOSE){
                draw = -1;
            }
            maxscore = CustomScore.maximize(maxscore, tmpscore);
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
        if(draw == -1 && maxscore.status == EvalStatus.DRAW){// E' un pareggio sicuro
            maxscore = new CustomScore(0, EvalStatus.NOT_DEFINED);// notDefinedDraw;
        }

        this.current_matrix.single_invert_matrix(node, stato);
        
        return maxscore;
    }

    // restituisce la cella massima tra quelle libere
    private void maxValue(){
        CustomMNKCell max = this.FC[0];
        if(debugLevels){
            System.out.println(this.FC[0]);
        }

        for(int i = 1; i < this.FC.length; i++){
            if(debugLevels){
                System.out.println(this.FC[i]);
            }
            if(!max.eval.compare(this.FC[i].eval)){
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