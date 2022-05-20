package BottargaPlayer.PlayerBetterNegamax;

import mnkgame.MNKCell;

/**
 * Rispetto a PlayerNegamax l'eval incrementa i suoi valori nella matrice
 * in potenze di 10.
 */
public class Player extends BottargaPlayer.Utils.Player.Player {
    
    // Ridefinita per utilizzare la classe Alphabeta corretta
    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        Alphabeta runner = new Alphabeta(this.M, this.N, this.K, this.first, MC, FC, timeout, true, false);
        return runner.iterativeNegamax();
    }

    @Override
    public String playerName() {
        return "BottargaPlayer-BetterNegamax";
    }
    
}
