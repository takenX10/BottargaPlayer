package BottargaPlayer.PlayerSecondLayerMoveOrder;

import BottargaPlayer.Utils.Matrix.UpdateEvalMatrix;
import BottargaPlayer.Utils.Cell.CustomScore;
import BottargaPlayer.Utils.Cell.CustomMNKCell;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;
/**
 * Il sorting delle celle avviene tramite quicksort
 */
public class MoveOrder {
    /**
     * Array delle celle libere da ordinare
     */
    public CustomMNKCell[] cells; 

    /**
     * Cella corrente da restituire
     */
    private int currentCell;

    /**
     * Stato corrente dell'albero (P1 o P2), serve per chiamare l'update della matrice
     */
    private MNKCellState state;

    /**
     * Profondita massima in cui vogliamo effettuare il sorting
     * la radice ha profondita 0 e va a crescere scendendo nell'albero
     */
    private final int maxSortingDepth = 1;

    /**
     * 
     * @param FC            Lista delle celle attualmente libere, piu la cella da rimuovere
     * @param toRemove      Cella da rimuovere
     * @param currentMatrix La classe che gestisce la matrice dell'eval
     * @param state         Lo stato del chiamante della classe
     * @param depth         La profondita attuale (assumendo che 0 e' la radice e va a crescere andando avanti nell'albero)
     */
    public MoveOrder(CustomMNKCell[] FC, MNKCell toRemove, UpdateEvalMatrix currentMatrix, MNKCellState state, int depth){
        this.state = state;
        this.currentCell = 0;
        if(toRemove == null){
            // inizializza le celle
            this.cells = new CustomMNKCell[FC.length];

            // Crea l'array ma non toglie l'elemento toRemove dato che e' null
            for (int i = 0; i < FC.length; i++){
                cells[i] = new CustomMNKCell(FC[i].cell, false, null);
            }    
        }else{
            // inizializza le celle
            this.cells = new CustomMNKCell[FC.length - 1];
            
            // Crea l'array e toglie l'elemento toRemove
            int j = 0;
            for (int i = 0; i < FC.length; i++){
                if(FC[i].cell != toRemove){
                    cells[j] = new CustomMNKCell(FC[i].cell,false, null);
                    j++;
                }
            }
        }

        // Ordina l'array solo se rispetta la profondita desiderata
        if(depth <= maxSortingDepth){
            // Setta l'eval per ogni cella
            getEval(currentMatrix);
            // Ordina l'array
            sort();
        }
    }

    /**
     * Assegna un eval ad ogni cella.
     * @param currentMatrix La classe che gestisce la matrice dell'eval
     */
    private void getEval(UpdateEvalMatrix currentMatrix){
        for(int i = 0; i < this.cells.length; i++){
            currentMatrix.single_update_matrix(cells[i].cell, state);
            this.cells[i].eval = new CustomScore(currentMatrix.eval.score, currentMatrix.eval.status);
            currentMatrix.single_invert_matrix(cells[i].cell, state);
        }
    }

    /**
     * Effettua il sorting delle celle tramite quicksort
     */
    private void sort(){
        quickSort(0, (this.cells.length - 1) );
        return;
    }
    
    /**
     * 
     * @return La cella di valore maggiore non ancora visitata
     */
    public CustomMNKCell getCell(){
        if(currentCell >= cells.length){
            return null;
        }
        currentCell++;
        return cells[currentCell - 1];
    }

    /************* Quicksort custom *************/
    private int partition(int begin, int end) {
        int pivot = end;
        int counter = begin;
        for (int i = begin; i < end; i++) {
            if (this.cells[pivot].eval.compare(this.cells[i].eval)) {
                CustomMNKCell temp = this.cells[counter];
                this.cells[counter] = this.cells[i];
                this.cells[i] = temp;
                counter++;
            }
        }
        CustomMNKCell temp = this.cells[pivot];
        this.cells[pivot] = this.cells[counter];
        this.cells[counter] = temp;
    
        return counter;
    }
    /**
     * 
     * @param begin da inizializzare a 0
     * @param end   da inizializzare a (this.cells.length-1)
     */
    private void quickSort(int begin, int end) {
        if (end <= begin) return;
        int pivot = partition(begin, end);
        quickSort(begin, pivot-1);
        quickSort(pivot+1, end);
    }

    /**********************************************/
}
