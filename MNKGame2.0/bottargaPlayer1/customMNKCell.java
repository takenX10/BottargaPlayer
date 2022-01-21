package bottargaPlayer1;

import mnkgame.MNKCell;

public class customMNKCell {
    public MNKCell cell;
    public boolean used;
    public CustomScore eval;

    customMNKCell(MNKCell cell, boolean used, CustomScore eval, boolean lost){
        this.cell = cell;
        this.used = used;
        this.eval = eval;
    }

    @Override
    public String toString() {
        return "Cell: "+this.cell+" "+this.eval;
    }
}
