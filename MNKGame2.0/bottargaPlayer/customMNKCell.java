package bottargaPlayer;

import mnkgame.MNKCell;

public class customMNKCell {
    public MNKCell cell;
    public boolean used;
    public float eval;
    public boolean lost;
    public int lostVal;

    customMNKCell(MNKCell cell, boolean used, float eval, boolean lost){
        this.cell = cell;
        this.used = used;
        this.eval = eval;
        this.lost = lost;
        this.lostVal = 0;
    }

    public void setEval(float eval){
        this.eval = eval;
    }

    public float getEval(){
        return this.eval;
    }
}
