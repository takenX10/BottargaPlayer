package BottargaPlayer.PlayerMoveOrder;

import BottargaPlayer.Utils.Cell.*;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;

public class Alphabeta extends BottargaPlayer.Utils.Player.Alphabeta{

    public Alphabeta(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC, Integer timeout, boolean debugPrint, boolean debugLevels) {
        super(M, N, K, first, MC, FC, timeout, debugPrint, debugLevels);
    }

    @Override
    protected CustomScore loop(int depth, int sign, MNKCellState stato, CustomScore alpha, CustomScore beta, MNKCell node){
        CustomScore maxscore = minusInf;
        CustomScore tmpscore;
        int i;
        int draw = 0;
        MoveOrder myOrder = new MoveOrder(FC, node, currentMatrix, stato, currentMaxDepth - depth);
        CustomMNKCell currentCell;
        while((currentCell = myOrder.getCell()) != null){
            tmpscore = negamax(depth - 1, -sign, currentCell.cell, myOrder.cells, (stato == this.me ? this.enemy : this.me), beta.invert(), alpha.invert()).invert();
            if( node == null ){ // per il primo giro
                if(!this.endNegamax){
                    i=0;
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
}