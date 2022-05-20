package BottargaPlayer.PlayerMoveOrder;

import BottargaPlayer.Utils.Cell.*;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;

public class Alphabeta extends BottargaPlayer.Utils.Player.Alphabeta{

    public Alphabeta(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC, Integer timeout, boolean debugPrint, boolean debugLevels) {
        super(M, N, K, first, MC, FC, timeout, debugPrint, debugLevels);
    }

    /**
     * Il loop e' custom perche' a differenza dell'alphabeta le celle da visitare vengono 
     * decise tramite l'algoritmo scritto in MoveOrder, evitando cosi di scorrere tutte le
     * celle ogni volta. C'e' da dire che anche se cosi vengono attraversate solo le celle libere, per ottenerle
     * sono stati svolti molti passaggi che computazionalmente rendono il costo finale uguale, se non
     * addirittura peggiore di scorrerre direttamente tutte le celle e controllare se sono libere oppure no.
     */
    @Override
    protected CustomScore loop(int depth, int sign, MNKCellState stato, CustomMNKCell[] currentFreeCells, CustomScore alpha, CustomScore beta, MNKCell node){
        CustomScore maxscore = minusInf;
        CustomScore tmpscore;
        int i;
        int draw = 0;
        MoveOrder myOrder = new MoveOrder(currentFreeCells, node, currentMatrix, stato, currentMaxDepth - depth);
        
        CustomMNKCell currentCell;
        while((currentCell = myOrder.getCell()) != null){
            tmpscore = negamax(depth - 1, -sign, currentCell.cell, myOrder.cells, (stato == this.me ? this.enemy : this.me), beta.invert(), alpha.invert()).invert();
            if( node == null ){ // per il primo giro
                if(!this.endNegamax){
                    i = 0;
                    while(currentCell.cell != this.FC[i].cell) i++;
                    this.FC[i] = new CustomMNKCell(currentCell.cell, false, tmpscore);
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
        return maxscore;
    }

    @Override
    public MNKCell iterativeNegamax(){
        this.finish = System.currentTimeMillis() + finishms;
        
        // viene settato a true se scade il tempo
        this.endNegamax = false;
        MoveOrder myOrder = new MoveOrder(this.FC, null, this.currentMatrix, this.enemy, 0);
        this.FC = myOrder.cells;
        for(currentMaxDepth = 1;;currentMaxDepth++){
            this.lastDepth = true; // viene cambiato in false se avviene un taglio per profondità massima
            if(debugLevels){
                System.out.println("Livello "+currentMaxDepth);
            }
            // return value di negamax inutile
            negamax(currentMaxDepth, 1, null, this.FC, this.enemy, this.minusInf, this.inf);
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
}