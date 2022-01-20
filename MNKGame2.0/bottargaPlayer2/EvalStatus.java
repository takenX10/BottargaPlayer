package bottargaPlayer2;

public enum EvalStatus {
    LOSE(-2),
    CANT_WIN(-1),
    NOT_DEFINED(0),  
    DRAW(0),
    CANT_LOSE(2),
    WIN(3);
    
    public final Integer value;

    EvalStatus(Integer value){
        this.value = value;
    }
}
