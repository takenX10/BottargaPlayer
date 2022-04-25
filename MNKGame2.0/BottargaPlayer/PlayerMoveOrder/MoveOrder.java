package BottargaPlayer.PlayerMoveOrder;

import BottargaPlayer.Utils.Matrix.UpdateEvalMatrix;
import BottargaPlayer.Utils.Cell.CustomScore;
import BottargaPlayer.Utils.Cell.CustomMNKCell;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;

public class MoveOrder {
    public CustomMNKCell[] cells;
    private int currentCell;
    private MNKCellState state;

    public MoveOrder(CustomMNKCell[] FC, MNKCell toRemove, UpdateEvalMatrix currentMatrix, MNKCellState state, int depth){
        this.state = state;
        this.currentCell = 0;
        if(toRemove == null){
            this.cells = new CustomMNKCell[FC.length];
            int j = 0;
            for (int i = 0; i < FC.length; i++){
                cells[j] = new CustomMNKCell(FC[i].cell, false, null);
                j++;
            }    
        }else{
            this.cells = new CustomMNKCell[FC.length - 1];
            
            // Create the array of cells
            int j = 0;
            for (int i = 0; i < FC.length; i++){
                if(FC[i].cell != toRemove){
                    cells[j] = new CustomMNKCell(FC[i].cell,false, null);
                    j++;
                }
            }
        }

        // Sort the array
        if(depth < 1){
            // Get the eval of every cell
            getEval(currentMatrix);
            sort();
        }
    }

    private void getEval(UpdateEvalMatrix currentMatrix){
        for(int i = 0; i < this.cells.length; i++){
            currentMatrix.single_update_matrix(cells[i].cell, state);
            this.cells[i].eval = new CustomScore(currentMatrix.eval.score, currentMatrix.eval.status);
            currentMatrix.single_invert_matrix(cells[i].cell, state);
        }
    }

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
    
    private void quickSort(int begin, int end) {
        if (end <= begin) return;
        int pivot = partition(begin, end);
        quickSort(begin, pivot-1);
        quickSort(pivot+1, end);
    }

    private void sort(){
        quickSort(0, (this.cells.length - 1) );
        return;
    }

    public CustomMNKCell getCell(){
        if(currentCell >= cells.length){
            return null;
        }
        currentCell++;
        return cells[currentCell - 1];
    }
}
