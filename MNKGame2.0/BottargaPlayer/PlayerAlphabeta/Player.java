package BottargaPlayer.PlayerAlphabeta;

import mnkgame.MNKCell;

/**
 * Rispetto a PlayerBetterNegamax abbiamo aggiunto i tagli dati da alpha e beta
 */
public class Player extends BottargaPlayer.Utils.Player.Player {

    @Override
    public String playerName() {
        return "BottargaPlayer-Alphabeta";
    }
    
}
