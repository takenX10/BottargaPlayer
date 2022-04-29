package BottargaPlayer.Utils.Cell;

/**
 * Assegno ad ogni possibile situazione nella table uno score: più alto è migliore è lo stato in cui mi trovo.
 * CANT_WIN e DRAW hanno lo stesso valore perche' questo aiuta nei confronti in CustomScore
 */
public enum EvalStatus {
    LOSE(-2),
    CANT_WIN(-1),
    DRAW(0),
    NOT_DEFINED(0),  
    CANT_LOSE(1),
    WIN(2);
    
    public final Integer value;

    /**
     * @param value score della table che sto valutando.
     */
    EvalStatus(Integer value){
        this.value = value;
    }
}
