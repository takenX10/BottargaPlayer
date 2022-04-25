package BottargaPlayer.Utils.Cell;


/**
 * Uno score customizzato, secondo EvalStatus.
 * Permette di controllare >= con compare, e di invertire un valore con invert.
 * Il sistema di punteggio funziona in questo modo:
 * ci sono 6 stati, ed ognuno contiene uno score che rappresenta:
 * - NOT DEFINED:    Score della partita standard
 * - CANT_WIN:       Score della partita standard
 * - CANT_LOSE:      Score della partita standard
 * - DRAW:           Distanza del pareggio, piu' e' alto maggiore e' la distanza   
 * - WIN:            Distanza della vittoria, piu' e' alto maggiore e' la distanza  
 * - LOSE:           Distanza della sconfitta, piu' e' alto maggiore e' la distanza
 * 
 * Gli stati considerati definitivi sono DRAW, WIN e LOSE.
 * Una volta che lo score viene dichiarato DRAW, significa non solo che e' possibile un pareggio, ma che
 * e' anche assicurato che la partita finisca in pareggio
 * 
 * Per decidere quale score e' maggiore, a parita di stato ci basta massimizzare il valore, eccetto
 * che per win, dove vogliamo vincere il prima possibile quindi minimizziamo.
 * 
 * Con stati diversi il confronto funziona seguendo quest'ordine:
 * - WIN
 * - CANT_LOSE
 * - DRAW e NOT_DEFINED
 * - CANT_WIN
 * - LOSE
 * 
 * DRAW e NOT_DEFINED sono insieme perche' vengono considerati allo stesso livello, e draw
 * viene considerato come un NOT_DEFINED con eval 0.
 */
public class CustomScore{
    public double score;
    public EvalStatus status;
    public CustomScore(double score, EvalStatus status){
        this.score = score;
        this.status = status;
    }

    /**
     * 
     * @return true sse l'elemento chiamante e' finale, quindi ha uno tra questi stati: DRAW, WIN o LOSE
     */
    public Boolean isFinal(){
        if(this.status == EvalStatus.WIN || this.status == EvalStatus.LOSE || this.status == EvalStatus.DRAW){
            return true;
        }
        return false;
    }

    /**
     * verifica che l'elemento che chiama la funzione sia maggiore o uguale dell'elemento passato
     * @param b parametro che viene confrontato
     * @return true sse l'elemento chiamante e' >= di b, false altrimenti.
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

    /**
     * 
     * @param a primo elemento
     * @param b secondo elemento
     * @return crea un nuovo elemento, uguale al maggiore tra i due.
     */
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

    /**
     * 
     * @return un nuovo valore, uguale all'inverso del valore del chiamante
     */
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