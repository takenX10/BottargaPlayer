package BottargaPlayer.Utils.Player;

import mnkgame.MNKPlayer;
import mnkgame.MNKCell;

public class Player implements MNKPlayer{
    protected int M;
    protected int N;
    protected int K;
    protected boolean first;
    protected int timeout;
    @Override
    public void initPlayer(int M, int N, int K, boolean first, int timeotInSecs) {
        this.M = M;
        this.N = N;
        this.K = K;
        this.timeout = timeotInSecs;
        this.first = first;
    }

    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        Alphabeta runner = new Alphabeta(this.M, this.N, this.K, this.first, MC, FC, timeout, true, true);
        return runner.iterativeNegamax();
    }

    @Override
    public String playerName() {
        return "BottargaPlayer-ToOverride";
    }
    
}
