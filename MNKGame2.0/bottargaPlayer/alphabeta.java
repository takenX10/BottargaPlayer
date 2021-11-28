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
    private int k;
    private customMNKCell[] FC;

    public alphabeta(int M, int N, int K, boolean first, int maxdepth, MNKCell[] MC, MNKCell[] FC){
        this.maxdepth = maxdepth;
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
        MNKCell migliore;
        for(Integer i = 1;;i++){
            System.out.println("Livello "+i.toString());
            this.maxdepth = i;
            migliore = start();
            System.out.println(migliore.toString());
        }
    }

    public MNKCell start(){
        alpha = -1500;
        this.beta = 1500;
        float max = this.current_matrix.lose_value;
        float tot;
        for(int i = 0; i < FC.length; i++){
            this.FC[i].used = true;
            tot = recursive(1, this.FC[i].cell, this.current_matrix, this.me, this.current_matrix.lose_value, this.current_matrix.win_value);
            System.out.println("cell: "+this.FC[i].cell.toString()+"\ntot: "+tot+"\n");
            this.FC[i].used = false;

            if(tot > max){
                max = tot;
                this.best = this.FC[i].cell;
            }else if(this.best == null){
                this.best = this.FC[i].cell;
            }
        }
        //System.out.println(max);
        return best;
    }

    // initialize with recursive(0,null,this.current_matrix)
    private float recursive(int depth, MNKCell node, UpdateEvalMatrix matrix, MNKCellState stato, float alpha, float beta) {
        matrix.single_update_matrix(node, stato);
        //System.out.println(node.toString() + "\ndepth: "+depth+"\nstato: "+stato.toString()+"\neval: "+matrix.eval+"\nalpha: "+alpha+"\tbeta: "+beta);

        if(matrix.eval == 1000 || matrix.eval == -1000 || matrix.eval == this.k+1 || depth == this.maxdepth){ // TODO: fixa questo per vittoria o sconfitta
            float eval = matrix.eval;
            matrix.single_invert_matrix(node, stato);
            //if(depth == 3)
            //System.out.println(node.toString() + "\ndepth: "+depth+"\nstato: "+stato.toString()+"\neval: "+eval+"\nalpha: "+alpha+"\tbeta: "+beta);
            return eval;
        }

        // massimizzante
        if(this.me != stato){
            float val = matrix.lose_value;   // TODO: migliora questo
            for(int i = 0; i < FC.length; i++){
                if(this.FC[i].used == false){ // se la cella è effettivamente libera
                    this.FC[i].used = true;
                    float eval = recursive(depth+1, this.FC[i].cell, matrix, this.me, alpha, beta);
                    val = Math.max(val, eval);
                    this.FC[i].used = false;
                    alpha = Math.max(alpha, eval);
                    if(beta <= alpha){
                        break; // beta cut
                    }
                }
            }
            matrix.single_invert_matrix(node, stato);
            //if(depth == 2){
            //    System.out.println(node.toString() + "\ndepth: "+depth+"\nstato: "+stato.toString()+"\neval: "+val+"\nalpha: "+alpha+"\tbeta: "+beta);
            return val;
        }else{ // minimizzante
            float val = matrix.win_value;     // TODO: migliora questo
            for(int i = 0; i < FC.length; i++){
                if(this.FC[i].used == false){ // se la cella è effettivamente libera
                     // TODO: migliora questo
                    this.FC[i].used = true;
                    float eval =  recursive(depth+1, this.FC[i].cell, matrix, this.enemy, alpha, beta);
                    val = Math.min(val, eval);
                    this.FC[i].used = false;
                    beta = Math.min(beta, eval);
                    if(beta <= alpha){
                        break; // alpha cut TODO EDIT
                    }
                }
            }
            matrix.single_invert_matrix(node, stato);
            //if(depth == 3){
                //System.out.println(node.toString() + "\ndepth: "+depth+"\nstato: "+stato.toString()+"\neval: "+val+"\nalpha: "+alpha+"\tbeta: "+beta);
            //}
            return val;
        }
    }
}
