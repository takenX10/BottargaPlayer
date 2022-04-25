package BottargaPlayer.Utils.Cell;

import mnkgame.MNKCell;

public class CustomMNKCell {
    public MNKCell cell;
    public boolean used;
    public CustomScore eval;

    public CustomMNKCell(MNKCell cell, boolean used, CustomScore eval){
        this.cell = cell;
        this.used = used;
        this.eval = eval;
    }

    @Override
    public String toString() {
        return "Cell: "+this.cell+" "+this.eval;
    }
}
