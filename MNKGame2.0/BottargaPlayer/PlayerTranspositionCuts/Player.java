package BottargaPlayer.PlayerTranspositionCuts;

import mnkgame.MNKPlayer;
import mnkgame.MNKCell;

// Classe negamax 
public class Player implements MNKPlayer{
    private int M;
    private int N;
    private int K;
    private boolean first;
    private int timeout;
    @Override
    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.timeout = timeout_in_secs;
        this.first = first;
    }

    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        Alphabeta runner = new Alphabeta(this.M, this.N, this.K, this.first, MC, FC, timeout);

        return runner.iterative_negamax();
    }

    @Override
    public String playerName() {
        return "BottargaPlayer-TranspositionWithCuts";
    }
    
}
