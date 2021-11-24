package bottargaPlayer;

import mnkgame.MNKCell;

/*
    note:
    - ho creato la struttura dati customMNKCell per tenere traccia delle FC già ispezionate ai livelli superiori
    - L'alphabeta è di tipo failsoft, quindi aggiorna l'alpha

 */
public class alphabeta {

    private UpdateEvalMatrix current_matrix;
    private float alpha;
    private float beta;
    private final int maxdepth;
    private MNKCell best;
    private customMNKCell[] FC;

    public alphabeta(int M, int N, int K, boolean first, int maxdepth, MNKCell[] MC, MNKCell[] FC){
        this.maxdepth = maxdepth;
        EvalMatrix m = new EvalMatrix(M,N,K);
        this.current_matrix = new UpdateEvalMatrix(M,N,K,first, m.M_Matrix(), m.N_Matrix(), m.K1_Matrix(), m.K2_Matrix());
        this.current_matrix.multiple_update_matrix(MC);
        this.FC = new customMNKCell[FC.length];
        for(int i = 0; i < FC.length; i++){
            this.FC[i].cell = FC[i];
            this.FC[i].used = false;
        }
    }

    public MNKCell start(){
        this.alpha = -100;
        this.beta = 100;
        float max = -1;
        float tot;
        for(customMNKCell child: this.FC){
            tot = recursive(1, child.cell, this.current_matrix);
            if(tot > max){
                max = tot;
                this.best = child.cell;
            }
        }
        return best;
    }

    // initialize with recursive(0,null,this.current_matrix)
    private float recursive(int depth, MNKCell node, UpdateEvalMatrix matrix) {
        matrix.single_update_matrix(node);
        if(matrix.eval <= -2 || depth == this.maxdepth){
            return (matrix.eval);
        }
        // massimizzante, perchè noi scegliamo sempre il nodo migliore, e l'eval inizia sempre
        // dalla nostra mossa
        if(depth % 2 == 0){
            float val = -100;   // TODO: migliora questo
            for(customMNKCell child: FC){
                if(child.used == false){ // se la cella è effettivamente libera
                    child.used = true;
                    val = Math.max(val, recursive(depth+1, child.cell, matrix));
                    child.used = false;
                    this.alpha = Math.max(this.alpha, val);
                    if(this.beta <= this.alpha){
                        break; // beta cut
                    }
                }
            }
            return val;
        }else{ // minimizzante
            float val = 100;     // TODO: migliora questo
            for(customMNKCell child: this.FC){
                if(child.used == false){ // se la cella è effettivamente libera
                     // TODO: migliora questo
                    val = Math.min(val, recursive(depth+1, child.cell, matrix));
                    child.used = false;
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
