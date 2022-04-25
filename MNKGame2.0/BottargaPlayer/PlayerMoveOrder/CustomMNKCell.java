package BottargaPlayer.PlayerMoveOrder;

import mnkgame.MNKCell;

public class CustomMNKCell {
    public MNKCell cell;
    public CustomScore eval;

    CustomMNKCell(MNKCell cell, CustomScore eval){
        this.cell = cell;
        this.eval = eval;
    }

    @Override
    public String toString() {
        return "Cell: "+this.cell+" "+this.eval;
    }
}
