package bottargaPlayer;

import mnkgame.MNKCell;

public class customMNKCell {
    public MNKCell cell;
    public boolean used;
    public float eval;

    customMNKCell(MNKCell cell, boolean used){
        this.cell = cell;
        this.used = used;
    }

    public void setEval(float eval){
        this.eval = eval;
    }

    public float getEval(){
        return this.eval;
    }
}
