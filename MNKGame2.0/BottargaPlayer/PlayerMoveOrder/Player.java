package BottargaPlayer.PlayerMoveOrder;

import mnkgame.MNKPlayer;
import mnkgame.MNKCell;

// Classe negamax 
public class Player extends BottargaPlayer.Utils.Player.Player {
    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        Alphabeta runner = new Alphabeta(this.M, this.N, this.K, this.first, MC, FC, timeout, true, true);
        return runner.iterativeNegamax();
    }

    @Override
    public String playerName() {
        return "BottargaPlayer-MoveOrder";
    }
    
}
