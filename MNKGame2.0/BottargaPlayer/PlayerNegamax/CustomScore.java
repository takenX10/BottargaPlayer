package BottargaPlayer.PlayerNegamax;


public class CustomScore{
    public double score;
    public EvalStatus status;
    CustomScore(double score, EvalStatus status){
        this.score = score;
        this.status = status;
    }

    public Boolean isFinal(){
        if(this.status == EvalStatus.WIN || this.status == EvalStatus.LOSE || this.status == EvalStatus.DRAW){
            return true;
        }
        return false;
    }

    // Restituisce true se l'elemento che chiama la funzione e' meglio del parametro passato, false altrimenti
    // In caso di pareggio restituisce true (per evitare altri scambi)

    /* 
        Rappresentazione concettuale dello score:

        NOT DEFINED:    Score della partita standard
        CANT_WIN:       Score della partita standard
        CANT_LOSE:      Score della partita standard
        DRAW:           Distanza del pareggio, piu' e' alto maggiore e' la distanza   
        WIN:            Distanza della vittoria, piu' e' alto maggiore e' la distanza  
        LOSE:           Distanza della sconfitta, piu' e' alto maggiore e' la distanza

        Quindi vogliamo massimizzare tutti gli score a parita di stato eccetto win, che vogliamo minimizzare

    */
    public Boolean compare(CustomScore b){
        if(this.status.value > b.status.value){
            return true;
        }else if(b.status.value > this.status.value){
            return false;
        }else{
            if(b.status == this.status){
                if(this.status != EvalStatus.WIN){
                    if(this.score >= b.score){
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    if(this.score <= b.score){
                        return true;
                    }else{
                        return false;
                    }
                }
            }else{ // NOT DEFINED DRAW
                if(this.status == EvalStatus.DRAW){
                    return true;
                }else{
                    return false;
                }
            }
        }
    }

    public static CustomScore maximize(CustomScore a, CustomScore b){
        CustomScore retval = null;
        if(a.status.value > b.status.value){
            retval = a;
        }else if(a.status.value < b.status.value){
            retval = b;
        }else{
            if(a.status == b.status){
                switch(a.status){
                    case LOSE:
                        if(a.score >= b.score){
                            retval = a;
                        }else{
                            retval = b;
                        }
                        break;
                    case NOT_DEFINED:
                        if(a.score >= b.score){
                            retval = a;
                        }else{
                            retval = b;
                        }
                        break;
                    case CANT_LOSE:    
                        if(a.score >= b.score){
                            retval = a;
                        }else{
                            retval = b;
                        }
                        break;
                    case CANT_WIN:
                        if(a.score >= b.score){
                            retval = a;
                        }else{
                            retval = b;
                        }
                        break;
                    case WIN:
                        if(a.score <= b.score){
                            retval = a;
                        }else{
                            retval = b;
                        }
                        break;
                    case DRAW:
                        if(a.score >= b.score){
                            retval = a;
                        }else{
                            retval = b;
                        }
                        break;
                }
            }else{ // DRAW NOT_DEFINED
                if(a.status == EvalStatus.NOT_DEFINED){
                    if(a.score > 0){
                        retval = a;
                    }else{
                        retval = b;
                    }
                }else{
                    if(b.score > 0){
                        retval = b;
                    }else{
                        retval = a;
                    }
                }
            }
        }
        
        return new CustomScore(retval.score, retval.status);
    }

    public CustomScore invert(){
        CustomScore inverse = new CustomScore(this.score, this.status);
        switch(this.status){
            case WIN:
                inverse.status = EvalStatus.LOSE;
                break;
            case LOSE:
                inverse.status = EvalStatus.WIN;
                break;
            case DRAW:
                break;
            case NOT_DEFINED:
                inverse.score = -this.score;
                break;
            case CANT_WIN:
                inverse.status = EvalStatus.CANT_LOSE;
                inverse.score = -this.score;
                break;
            case CANT_LOSE:
                inverse.status = EvalStatus.CANT_WIN;
                inverse.score = -this.score;
                break;
        }

        return inverse;
    }

    public String toString(){
        String out = "Status: "+this.status;
        switch(this.status){
            case WIN:
                out += "\t\t";
                break;
            case LOSE:
                out += "\t\t";
                break;
            case NOT_DEFINED:
                out += "\t";
                break;
            case CANT_LOSE:
                out += "\t";
                break;
            case CANT_WIN:
                out+="\t";
                break;
            case DRAW:
                out += "\t\t";
        }
        out += "Value: "+this.score;
        return out;
    }
}