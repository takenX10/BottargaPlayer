package bottargaPlayer;

import java.util.Random;
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
public class alphabeta {

    private UpdateEvalMatrix current_matrix;
    private float alpha;
    private float beta;
    private int maxdepth;
    private MNKCell best;
    private MNKCellState me;
    private MNKCellState enemy;
    private Long[][] zobrist;
    private int M;
    private int N;
    private customMNKCell[] FC;

    public alphabeta(int M, int N, int K, boolean first, int maxdepth, MNKCell[] MC, MNKCell[] FC){
        this.maxdepth = maxdepth;
        EvalMatrix m = new EvalMatrix(M,N,K);
        this.current_matrix = new UpdateEvalMatrix(M,N,K,first, m.M_Matrix(), m.N_Matrix(), m.K1_Matrix(), m.K2_Matrix());
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

    public MNKCell start_iterative(){
        MNKCell migliore;
        for(Integer i = 1;;i++){
            System.out.println("Livello "+i.toString());
            this.maxdepth = i;
            migliore = start();
            System.out.println(migliore.toString());
        }
    }

    public MNKCell start(){
        this.alpha = -1500;
        this.beta = 1500;
        float max = -1;
        float tot;
        for(int i = 0; i < FC.length; i++){
            this.FC[i].used = true;
            tot = recursive(1, this.FC[i].cell, this.current_matrix, this.me);
            this.FC[i].used = false;
            if(tot > max){
                max = tot;
                this.best = this.FC[i].cell;
            }
        }
        System.out.println(max);
        return best;
    }

    // initialize with recursive(0,null,this.current_matrix)
    private float recursive(int depth, MNKCell node, UpdateEvalMatrix sussymatrix, MNKCellState stato) {
        UpdateEvalMatrix matrix = sussymatrix;
        matrix.single_update_matrix_state(node, stato);
        System.out.println(node.toString() + "\ndepth:"+depth+"\neval:"+matrix.eval);
        if(matrix.eval == 1000 || matrix.eval == -1000 || depth == this.maxdepth){ // TODO: fixa questo per vittoria o sconfitta
            return (matrix.eval);
        }
        // massimizzante
        if((depth % 2 == 0 && this.me == MNKCellState.P1) || (depth%2 != 0 && this.me == MNKCellState.P2)){
            float val = -100;   // TODO: migliora questo
            for(int i = 0; i < FC.length; i++){
                if(this.FC[i].used == false){ // se la cella è effettivamente libera
                    this.FC[i].used = true;
                    val = Math.max(val, recursive(depth+1, this.FC[i].cell, matrix, this.me));
                    this.FC[i].used = false;
                    this.alpha = Math.max(this.alpha, val);
                    if(this.beta <= this.alpha){
                        break; // beta cut
                    }
                }
            }
            return val;
        }else{ // minimizzante
            float val = 100;     // TODO: migliora questo
            for(int i = 0; i < FC.length; i++){
                if(this.FC[i].used == false){ // se la cella è effettivamente libera
                     // TODO: migliora questo
                    this.FC[i].used = true;
                    val = Math.min(val, recursive(depth+1, this.FC[i].cell, matrix, this.enemy));
                    this.FC[i].used = false;
                    this.beta = Math.min(this.beta, val);
                    if(this.beta <= this.alpha){
                        break; // alpha cut
                    }
                }
            }
            return val;
        }
    }
}
