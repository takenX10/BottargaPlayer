package BottargaPlayer.PlayerAlphabeta;

public enum EvalStatus {
    LOSE(-2),
    CANT_WIN(-1),
    DRAW(0),
    NOT_DEFINED(0),  
    CANT_LOSE(1),
    WIN(2);
    
    public final Integer value;

    EvalStatus(Integer value){
        this.value = value;
    }
}
