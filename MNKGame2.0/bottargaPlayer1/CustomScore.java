package bottargaPlayer1;


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
                */
                if(this.status == EvalStatus.DRAW){
                    if(b.score > 0){
                        return false;
                    }else{
                        return true;
                    }
                }else{
                    if(this.score > 0){
                        return true;
                    }else{
                        return false;
                    }
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