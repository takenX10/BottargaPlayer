package BottargaPlayer.Utils.Cell;

import mnkgame.MNKCell;

// Un implementazione custom delle MNKCell, in modo da
// contenere eval e cella in un unico oggetto.
// la variabile used serve ad indicare se la cella e' gia stata
// utilizzata oppure no
public class CustomMNKCell {
    /**
     * valore della cella
     */
    public MNKCell cell;

    /**
     * true se la cella e' attualmente utilizzata, false altrimenti
     */
    public boolean used;

    /**
     * eval della cella
     */
    public CustomScore eval;

    /**
     * 
     * @param cell valore della cella
     * @param used true se la cella e' attualmente utilizzata, false altrimenti
     * @param eval eval della cella
     */
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
