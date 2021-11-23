package bottargaPlayer;

import mnkgame.MNKCell;

public class main implements mnkgame.MNKPlayer{
    private int M;
    private int N;
    private int K;
    private boolean first;
    private int timeout;
    @Override
    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        this.first = first;
        this.M = M;
        this.N = N;
        this.K = K;
        this.timeout = timeout_in_secs;
    }

    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
       return FC[0];
    }

    @Override
    public String playerName() {
        return "bottargaPlayer";
    }
}
