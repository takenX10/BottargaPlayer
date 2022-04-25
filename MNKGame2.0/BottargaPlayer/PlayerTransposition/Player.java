package BottargaPlayer.PlayerTransposition;

import BottargaPlayer.Utils.TranspositionTable.AlphabetaTransposition;
import mnkgame.MNKCell;

// Classe negamax 
public class Player extends BottargaPlayer.Utils.Player.Player{

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
