package bottargaPlayer2;


public class CustomScore{
    public double score;
    public EvalStatus status;
    public CustomScore(double score, EvalStatus status){
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
        if(b.status.value > this.status.value){
            return false;
        }else if(b.status.value < this.status.value){
            return true;
        }else{
            if(b.status == this.status){ // caso normale 
                switch(b.status){
                    case DRAW:
                        if(this.score >= b.score){
                            return true;
                        }else{
                            return false;
                        }
                    case LOSE:
                        if(this.score >= b.score){
                            return true;
                        }else{
                            return false;
                        }
                    case NOT_DEFINED:
                        if(this.score >= b.score){
                            return true;
                        }else{
                            return false;
                        }
                    case CANT_LOSE:    
                        if(this.score >= b.score){
                            return true;
                        }else{
                            return false;
                        }
                    case CANT_WIN:
                        if(this.score >= b.score){
                            return true;
                        }else{
                            return false;
                        }
                    case WIN:
                        if(this.score <= b.score){
                            return true;
                        }else{
                            return false;
                        }
                    
                }
            }else{ // Siamo in un caso di Draw/ not_defined o viceversa    
                /*
                    L'idea generale e' questa
                    not_defined<0 -> Draw > not_defined (Il punteggio tende verso l'avversario, quindi e' meglio pareggiare)
                    not_defined==0 -> Draw > not_defined (Il punteggio e' bilanciato, quindi meglio pareggiare per sicurezza)
                    not_defined>0 -> Draw < not_defined (Il punteggio tende verso il player quindi meglio provare a giocare che pareggiare)

                    Al momento c'e' un bug per cui non capisce bene se lo score si riferisce a se stesso oppure no, quindi per ora vince sempre il draw
                */
                if(this.status == EvalStatus.DRAW){
                    return true;
                    /*
                    if(b.score > 0){
                        return false;
                    }else{
                        return true;
                    }*/
                }else{
                    return false;
                    /*
                    if(this.score > 0){
                        return true;
                    }else{
                        return false;
                    }*/
                }

            }
        }
        return true;
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

    // return true if this is better than b (better for the main player, not the enemy)
    public Boolean maximize(CustomScore b){
        if(b.status.value > this.status.value){
            return false;
        }else if(b.status.value < this.status.value){
            return true;
        }else{
            if(b.status == this.status){ // caso normale 
                switch(b.status){
                    case DRAW:
                        if(this.score >= b.score){
                            return true;
                        }else{
                            return false;
                        }
                    case LOSE:
                        if(this.score >= b.score){
                            return true;
                        }else{
                            return false;
                        }
                    case NOT_DEFINED:
                        if(this.score >= b.score){
                            return true;
                        }else{
                            return false;
                        }
                    case CANT_LOSE:    
                        if(this.score >= b.score){
                            return true;
                        }else{
                            return false;
                        }
                    case CANT_WIN:
                        if(this.score >= b.score){
                            return true;
                        }else{
                            return false;
                        }
                    case WIN:
                        if(this.score <= b.score){
                            return true;
                        }else{
                            return false;
                        }
                    
                }
            }else{ // Siamo in un caso di Draw/ not_defined o viceversa    
                
                if(this.status == EvalStatus.DRAW){
                    return true;
                }else{
                    return false;
                }

            }
        }
        return true;
    }

    // Return true if this is better or equal than b (for the enemy)
    // Game states are always referring the main player
    // So a lose for the player is a win for the enemy
    public static CustomScore minimize(CustomScore a, CustomScore b){
        boolean retval = false;
        // false -> return b
        // true -> return a;
        if(b.status.value > a.status.value){
            retval = true;
        }else if(b.status.value < a.status.value){
            retval = false;
        }else{
            if(b.status == a.status){ // caso normale 
                switch(b.status){
                    case DRAW:
                        if(a.score >= b.score){
                            retval = true;
                        }else{
                            retval = false;
                        }
                    case LOSE:
                        if(a.score <= b.score){
                            retval = true;
                        }else{
                            retval = false;
                        }
                    case NOT_DEFINED:
                        if(a.score <= b.score){
                            retval = true;
                        }else{
                            retval = false;
                        }
                    case CANT_LOSE:    
                        if(a.score <= b.score){
                            retval = true;
                        }else{
                            retval = false;
                        }
                    case CANT_WIN:
                        if(a.score <= b.score){
                            retval = true;
                        }else{
                            retval = false;
                        }
                    case WIN:
                        if(a.score >= b.score){
                            retval = true;
                        }else{
                            retval = false;
                        }
                    
                }
            }else{ // Siamo in un caso di Draw/ not_defined o viceversa    
                if(a.status == EvalStatus.DRAW){
                    retval = true;
                }else{
                    retval = false;
                }
            }
        }
        if(retval){
            return (new CustomScore(a.score, a.status));
        }else{
            return (new CustomScore(b.score, b.status));
        }
    }


    public static CustomScore maximize(CustomScore a, CustomScore b){
        boolean retval = false;
        // false -> return b
        // true -> return a;
        if(b.status.value > a.status.value){
            retval = false;
        }else if(b.status.value < a.status.value){
            retval = true;
        }else{
            if(b.status == a.status){ // caso normale 
                switch(b.status){
                    case DRAW:
                        if(a.score >= b.score){
                            retval = true;
                        }else{
                            retval = false;
                        }
                    case LOSE:
                        if(a.score >= b.score){
                            retval = true;
                        }else{
                            retval = false;
                        }
                    case NOT_DEFINED:
                        if(a.score >= b.score){
                            retval = true;
                        }else{
                            retval = false;
                        }
                    case CANT_LOSE:    
                        if(a.score >= b.score){
                            retval = true;
                        }else{
                            retval = false;
                        }
                    case CANT_WIN:
                        if(a.score >= b.score){
                            retval = true;
                        }else{
                            retval = false;
                        }
                    case WIN:
                        if(a.score <= b.score){
                            retval = true;
                        }else{
                            retval = false;
                        }
                    
                }
            }else{ // Siamo in un caso di Draw/ not_defined o viceversa    
                if(a.status == EvalStatus.DRAW){
                    retval = true;
                }else{
                    retval = false;
                }
            }
        }
        if(retval){
            return (new CustomScore(a.score, a.status));
        }else{
            return (new CustomScore(b.score, b.status));
        }
    }
    // Utile per i pareggi
    // Assume che l'avversario prediliga sempre pareggiare piuttosto che l'incognita
    // Si aspetta che b sia in caso Il nuovo valore, e this e' il valore migliore(peggiore).
    // Restituisce true se sono da scambiare
    public Boolean minimizeCompare(CustomScore b){
        if(this.status == EvalStatus.DRAW){
            if(b.status != EvalStatus.NOT_DEFINED){
                if(!b.compare(this)){
                    return true;
                }
            }
        }else if(this.status == EvalStatus.NOT_DEFINED){
            if(b.status == EvalStatus.DRAW){
                return true;
            }
        }
        return false;
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