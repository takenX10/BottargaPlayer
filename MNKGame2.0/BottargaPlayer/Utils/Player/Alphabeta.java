package BottargaPlayer.Utils.Player;

import BottargaPlayer.PlayerNegamax.UpdateEvalMatrix;
import BottargaPlayer.Utils.Cell.*;
import BottargaPlayer.Utils.Matrix.EvalMatrix;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;


/**
 * Costrutto dell'alphabeta, la base e' quella di PlayerAlphabeta.
 * Per venire chiamato da fuori basta inizializzarlo, poi chiamare iterativeNegamax e la funzione restituisce
 * La migliore cella trovata.
 */
public class Alphabeta {
    // Debug print
    protected final boolean debugPrint;
    protected final boolean debugLevels;

    // costanti
    protected final long finishms;
    protected final MNKCellState me;
    protected final MNKCellState enemy;
    protected final int m;
    protected final int n;
    protected final CustomScore minusInf;
    protected final CustomScore inf;

    // valori per benchmark
    protected int totalNodesReached;
    protected int totalCuts;

    // valori per condizioni finali
    protected boolean endNegamax;
    protected CustomMNKCell bestCell;
    protected long finish;
    protected final int currentDepth;
    protected boolean lastDepth;
    protected int currentMaxDepth; // per inserirla nella transposition table

    // strutture
    protected final UpdateEvalMatrix currentMatrix;
    protected CustomMNKCell[] FC;

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
    public Alphabeta(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC, Integer timeout, boolean debugPrint, boolean debugLevels){
        this.debugLevels = debugLevels;
        this.debugPrint = debugPrint;
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
        this.currentMatrix = new UpdateEvalMatrix(M,N,K,first, defaultMatrix.M_Matrix(), defaultMatrix.N_Matrix(), defaultMatrix.K1_Matrix(), defaultMatrix.K2_Matrix());
        this.currentMatrix.multiple_update_matrix(MC);

        // inizializza array delle celle vuote
        this.FC = new CustomMNKCell[FC.length];
        for(int i = 0; i < FC.length; i++){
            this.FC[i] = new CustomMNKCell(FC[i], false, this.minusInf);
        }
    }

    /**
     * Print di log utili per debugging, attivabili con debugPrint = true nell'inizializzazione
     * Sono un segmento utilizzato nell'iterative deepening
     * @param time il tempo trascorso
     */
    protected void logPrint(long time){
        System.out.println("\nVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
        System.out.println("Profondita raggiunta: "+currentMaxDepth);
        System.out.println("Mossa "+this.currentDepth);
        System.out.println("Nodi esplorati: "+this.totalNodesReached);
        if(this.bestCell.eval.status == EvalStatus.LOSE){
            System.out.println("TECNICAMENTE GIA PERSO!!!");
        }
        System.out.println("Total alphabeta cuts: "+this.totalCuts);
        System.out.println("Eval: "+this.bestCell.eval);
        System.out.println("Time elapsed: "+(time - finish + this.finishms) + (time > finish ? "ms (timed out)":"ms"));
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
    }

    /**
     * segmento utilizzato nel negamax
     * @return true sse e' scaduto il tempo, false altrimenti
     */
    protected boolean checkTime(){
        this.totalNodesReached++;
        if((this.totalNodesReached & 16383) == 0 || this.endNegamax){
            if(System.currentTimeMillis() > this.finish){
                this.endNegamax = true;
                return true;
            }
        }
        return false;
    }

    // TODO: Fixare sign / segno, che e' inutile

    /**
     * 
     * @param   node nodo corrente
     * @param   sign segno, utilizzato per capire se invertire o meno l'eval
     * @param   depth profondita corrente
     * @param   stato lo stato della cella corrente
     * @return  null se il nodo non e' foglia, quindi non siamo alla profondita massima ed il nodo ha figli
     *          L'eval da restituire altrimenti.
     */
    protected CustomScore checkLeaf(MNKCell node, int sign, int depth, MNKCellState stato){
        if(node != null && this.currentMatrix.eval.isFinal()){
            CustomScore tmpeval = new CustomScore(this.currentMatrix.eval.score, this.currentMatrix.eval.status);
            this.currentMatrix.single_invert_matrix(node,stato);
            return (sign > 0 ? tmpeval : tmpeval.invert());
        }else if(depth == 0){ // foglia per profondità, possibile andare più giu all'iterazione successiva.
            this.lastDepth = false;
            CustomScore tmpeval = new CustomScore(this.currentMatrix.eval.score, this.currentMatrix.eval.status);
            this.currentMatrix.single_invert_matrix(node,stato);
            return (sign > 0 ? tmpeval : tmpeval.invert());
        }
        return null;
    }

    /**
     * 
     * @param depth profondita corrente
     * @param sign segno, utilizzato per capire se invertire o meno l'eval
     * @param stato lo stato della cella corrente
     * @param alpha valore di alpha corrente
     * @param beta valore di beta corrente
     * @param node nodo corrente
     * @return lo score della cella corrente tramite ricerca negamax.
     */
    protected CustomScore loop(int depth, int sign, MNKCellState stato, CustomMNKCell[] currentFreeCells, CustomScore alpha, CustomScore beta, MNKCell node){
        CustomScore maxscore = minusInf;
        CustomScore tmpscore;
        int i;
        // se draw e' negativo sappiamo che e' avvenuto un taglio o almeno un elemento non e' finale
        // dunque se il valore che verra' restituito e' DRAW dobbiamo sostituirlo con (NOT_DEFINED, 0)
        // altrimenti possiamo restituire DRAW dato che questo sara' sicuramente un pareggio.
        int draw = 0;
        for(i = 0; i < this.FC.length; i++){
            if(!this.FC[i].used){
                this.FC[i].used = true;
                tmpscore = negamax(depth - 1, -sign, this.FC[i].cell, null, (stato == this.me ? this.enemy : this.me), beta.invert(), alpha.invert()).invert();
                this.FC[i].used = false;
                if( node == null ){ // salva i valori in FC nel primo giro per restituirli alla fine
                    if(!this.endNegamax){
                        this.FC[i].eval = new CustomScore(tmpscore.score, tmpscore.status);
                    }
                }
                if(!tmpscore.isFinal()){
                    draw = -1;
                }
                maxscore = CustomScore.maximize(maxscore, tmpscore);
                if(maxscore.status != EvalStatus.DRAW){
                    alpha = CustomScore.maximize(alpha, maxscore);
                }
                if(alpha.compare(beta)){
                    draw = -1;
                    this.totalCuts++;
                    break;
                }
            }
        }
        // Sappiamo che non e' sicuro che sia un pareggio, ma ha restituito draw
        // dunque lo trasformiamo in (NOT_DEFINED, 0)
        if(draw == -1 && maxscore.status == EvalStatus.DRAW){
            maxscore = new CustomScore(0, EvalStatus.NOT_DEFINED);
        }
        return maxscore;
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
    protected CustomScore negamax(int depth, int sign, MNKCell node, CustomMNKCell[] currentFreeCells, MNKCellState stato, CustomScore alpha, CustomScore beta) {   
        // timer e valori per benchmark
        if(checkTime()){
            return this.minusInf;
        }

        // aggiorno la matrice allo stato corrente (va poi invertita prima di fare return)
        this.currentMatrix.single_update_matrix(node, stato);
        CustomScore score = checkLeaf(node, sign, depth, stato);
        if(score != null){
            return score;
        }

        // eseguo il corpo principale del negamax (in cui avviene la ricorsione)
        score = loop(depth, sign, stato, currentFreeCells, alpha, beta, node);

        // riporto la matrice allo stato originale
        this.currentMatrix.single_invert_matrix(node, stato);
        
        return score;
    }
    /**
     * 
     * @return la cella migliore che ha trovato entro lo scadere del tempo
     */
    public MNKCell iterativeNegamax(){
        this.finish = System.currentTimeMillis() + finishms;
        
        // viene settato a true se scade il tempo
        this.endNegamax = false;
        for(currentMaxDepth = 1;;currentMaxDepth++){
            this.lastDepth = true; // viene cambiato in false se avviene un taglio per profondità massima
            if(debugLevels){
                System.out.println("Livello "+currentMaxDepth);
            }
            // return value di negamax inutile
            negamax(currentMaxDepth, 1, null, null, this.enemy, this.minusInf, this.inf);
            maxValue();
            
            if(debugLevels){
                System.out.println("Max: "+this.bestCell);
            }
            // pareggio no perchè se ad esempio alcuni nodi pareggiano e altri
            // non sono stati ancora esplorati non ha senso finire senza esplorarli
            if(this.lastDepth || this.endNegamax || this.bestCell.eval.status == EvalStatus.WIN || this.bestCell.eval.status == EvalStatus.LOSE){
                break;
            }
        }
        long time = System.currentTimeMillis();
        if(debugPrint){
            logPrint(time);
        }
        return this.bestCell.cell;
    }

    // 
    /**
     * setta in bestCell la cella migliore tra quelle libere.
     */
    protected void maxValue(){
        CustomMNKCell max = this.FC[0];
        if(debugLevels){
            System.out.println(this.FC[0]);
        }

        for(int i = 1; i < this.FC.length; i++){
            if(debugLevels){
                System.out.println(this.FC[i]);
            }
            // solo se e' maggiore avviene lo scambio, infatti in caso di parita potrebbe essere
            // una cella che e' stata tagliata e quindi ha solo un valore inesatto
            if(!max.eval.compare(this.FC[i].eval)){
                max = this.FC[i];
            }
        }
        this.bestCell = new CustomMNKCell(max.cell, false, new CustomScore(max.eval.score, max.eval.status));
    }
}
