package BottargaPlayer.PlayerBetterNegamax;
import BottargaPlayer.Utils.Cell.*;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;

public class Alphabeta extends BottargaPlayer.Utils.Player.Alphabeta {
    public Alphabeta(int M, int N, int K, boolean first, MNKCell[] MC, MNKCell[] FC, Integer timeout, boolean debugPrint, boolean debugLevels) {
        super(M, N, K, first, MC, FC, timeout, debugPrint, debugLevels);
    }

    @Override
    protected CustomScore loop(int depth, int sign, MNKCellState stato, CustomScore alpha, CustomScore beta, MNKCell node){
        CustomScore maxscore = minusInf;
        CustomScore tmpscore;
        int i;
        int draw = 0;
        for(i = 0; i < this.FC.length; i++){
            if(!this.FC[i].used){
                this.FC[i].used = true;
                tmpscore = negamax(depth - 1, -sign, this.FC[i].cell, null, (stato == this.me ? this.enemy : this.me), null, null).invert();
                this.FC[i].used = false;
                if( node == null ){ // per il primo giro
                    if(!this.endNegamax){
                        this.FC[i].eval = new CustomScore(tmpscore.score, tmpscore.status);
                    }
                }
                if(!tmpscore.isFinal()){
                    draw = -1;
                }
                maxscore = CustomScore.maximize(maxscore, tmpscore);
            }
        }
        if(draw == -1 && maxscore.status == EvalStatus.DRAW){// E' un pareggio sicuro
            maxscore = new CustomScore(0, EvalStatus.NOT_DEFINED);// notDefinedDraw;
        }
        return maxscore;
    }
}