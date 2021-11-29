package bottargaPlayer;

import mnkgame.MNKCell;
import mnkgame.MNKPlayer;

//Questo main contiene degli esempi utili a capire praticamente come usare le classi EvalMatrix ed UpdateEvalMatrix.
public class player implements MNKPlayer {
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
        // TODO: elimina parametro maxdepth
        alphabeta runner = new alphabeta(this.M, this.N, this.K, this.first, 10, MC, FC);
        return runner.start_iterative();
    }

    @Override
    public String playerName() {
        return "bottargaPlayer";
    }
}