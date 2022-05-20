package BottargaPlayer.PlayerSecondLayerMoveOrder;

import mnkgame.MNKCell;

/**
 * Rispetto a PlayerAlphabeta abbiamo modificato il loop del negamax in modo da
 * riordinare il primo strato del game tree
 */
public class Player extends BottargaPlayer.Utils.Player.Player {
    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        Alphabeta runner = new Alphabeta(this.M, this.N, this.K, this.first, MC, FC, timeout, true, false);
        return runner.iterativeNegamax();
    }

    @Override
    public String playerName() {
        return "BottargaPlayer-SecondLayerMoveOrder";
    }
    
}
