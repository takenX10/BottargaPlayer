package BottargaPlayer.PlayerTranspositionCuts;

import mnkgame.MNKPlayer;
import mnkgame.MNKCell;

/**
 * Rispetto a PlayerTransposition sono stati aggiunti i alpha-beta cuts per 
 * entry della tabella non di tipo EXACT (quindi LOWER e UPPER).
 */
public class Player extends BottargaPlayer.Utils.Player.Player{

    // Ridefinita per utilizzare la classe Alphabeta corretta
    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        Alphabeta runner = new Alphabeta(this.M, this.N, this.K, this.first, MC, FC, timeout, false, false);
        return runner.iterativeNegamax();
    }

    @Override
    public String playerName() {
        return "BottargaPlayer-TranspositionWithCuts";
    }
    
}
