package BottargaPlayer.PlayerTransposition;

import BottargaPlayer.Utils.TranspositionTable.AlphabetaTransposition;
import mnkgame.MNKCell;


/**
 * Rispetto a Alphabeta sono state aggiunte le transposition table
 * ma non e' stato aggiunto il move ordering
 */
public class Player extends BottargaPlayer.Utils.Player.Player{

    // Ridefinita per utilizzare la classe Alphabeta corretta
    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        AlphabetaTransposition runner = new AlphabetaTransposition(this.M, this.N, this.K, this.first, MC, FC, timeout, false, false);
        return runner.iterativeNegamax();
    }

    @Override
    public String playerName() {
        return "BottargaPlayer-Transposition";
    }
    
}
