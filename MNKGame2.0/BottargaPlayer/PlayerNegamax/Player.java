package BottargaPlayer.PlayerNegamax;

import mnkgame.MNKCell;

public class Player extends BottargaPlayer.Utils.Player.Player {
    
    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        Alphabeta runner = new Alphabeta(this.M, this.N, this.K, this.first, MC, FC, timeout, false, false);
        return runner.iterativeNegamax();
    }

    @Override
    public String playerName() {
        return "BottargaPlayer-Negamax";
    }
}