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
public class alphabeta{
    private UpdateEvalMatrix current_matrix;;
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
    private customMNKCell[] FC;

    private boolean cutbeforeend;

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
        this.best = null;
        this.bestval = this.current_matrix.lose_value;
        long start = System.currentTimeMillis();
        this.timerend = start + 5000;

        for(Integer i = 1;;i++){
            MNKCell tmp;
            this.cutbeforeend = false;
            this.maxdepth = i;
            singlestart();
            // TODO REMOVE THIS DEBUG
            System.out.println("Livello "+i.toString()+": ");
            System.out.println("Best cell -> "+this.best+" Best val -> "+this.bestval+"\n");
            System.out.println("Time: "+ (System.currentTimeMillis() - start));
            //
            if(System.currentTimeMillis() > this.timerend){
                System.out.println("not enough time\n");
                return this.best;
            }else if(this.best == null){ // se persino con maxdepth == 1 abbiamo sconfitta assicurata restituisce la prima cella vuota
                this.best = this.FC[0].cell;
                return this.best;
            } else if (bestval == current_matrix.win_value || !this.cutbeforeend) {
                System.out.println("Time: "+ (System.currentTimeMillis() - start));
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
            tot = recursive(1, this.FC[i].cell, this.current_matrix, this.me, this.current_matrix.lose_value, this.current_matrix.win_value);
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

    // initialize with recursive(0,null,this.current_matrix)
    private float recursive(int depth, MNKCell node, UpdateEvalMatrix matrix, MNKCellState stato, float alpha, float beta) {
        matrix.single_update_matrix(node, stato);
        // TODO: REMOVE THIS, DEBUG ONLY
        //System.out.println(node.toString() + "\ndepth: "+depth+"\nstato: "+stato.toString()+"\neval: "+matrix.eval+"\nalpha: "+alpha+"\tbeta: "+beta);
        ///////////////////
        if(matrix.eval == 1000 || matrix.eval == -1000 || matrix.eval == this.k+1){ // TODO: fixa questo per vittoria o sconfitta
            float eval = matrix.eval;
            matrix.single_invert_matrix(node, stato);
            return eval;
        }else if(depth == this.maxdepth){
            this.cutbeforeend = true;
            float eval = matrix.eval;
            matrix.single_invert_matrix(node, stato);
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
            return val;
        }
    }
}
