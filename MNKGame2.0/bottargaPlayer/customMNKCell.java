package bottargaPlayer;

import mnkgame.MNKCell;

public class customMNKCell {
    public MNKCell cell;
    public boolean used;
    public float eval;

    customMNKCell(MNKCell cell, boolean used, float eval){
        this.cell = cell;
        this.used = used;
        this.eval = eval;
    }

    public void setEval(float eval){
        this.eval = eval;
    }

    public float getEval(){
        return this.eval;
    }
}
