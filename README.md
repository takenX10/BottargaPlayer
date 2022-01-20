# BottargaBOT
Giocatore per progetto di Algoritmi e strutture dati UniBO 2020/21
## Cose da fare
- [ ] Eval -> patta
- [ ] Alpha-Beta intelligente
- [ ] Precalcolo dati
- [ ] Pattern Matching
- [ ] Hashing con Zobrist (https://levelup.gitconnected.com/zobrist-hashing-305c6c3c54d0)

### Altre info

- Lo scan deve occupare poca memoria, quindi quando torni indietro aggiorni la matrice e distruggi quella vecchia

### Eval

Costruiamo 4 matrici (una per ogni direzione in cui è possibile vincere), in modo che in ogni spazio della matrice sia presente una possibile "linea di vittoria".
- teniamo da parte la somma dei valori di ogni matrice (in modo da rendere più immediato il calcolo in seguito)
- Se la somma di tutte le somme delle matrici è un valore negativo particolare ( - n dove n è il numero di celle di tutte le matrici) allora **non puoi vincere**, se entrambi i giocatori non possono vincere è patta
> sarà da definire come strutturare la matrice delle diagonali


Note sull'eval:

Ci sono 6 stati:
WIN ->          La board ha una vittoria assicurata
CANT_LOSE ->    Il player avversario non puo vincere
DRAW ->         Esiste un pareggio assicurato da questa posizione
NOT_DEFINED     Lo score finale non e' ancora stato trovato
CANT_WIN        Il player non puo vincere in nessun modo
LOSE            Esiste un modo per cui il player puo' perdere per forza


Quindi ogni eval ritornato avra' due valori: lo stato e lo score numerico


Per fare un confronto tra valori si confrontano gli stati come prima cosa
seguendo questi criteri:

WIN > CANT_LOSE > DRAW|NOT_DEFINED > CANT_WIN > LOSE

DRAW e NOT_DEFINED stanno sullo stesso livello.

Per confronti sullo stesso stato:

WIN ->  Mantiene come valore un numero che corrisponde alla profondita della vittoria
        Per trovare il migliore tra due win scelgo quello che mi fa vincere prima, ovvero
        quello con lo score minore

CANT_LOSE -> prende lo score normale, quindi si confrontano normalmente

CANT_WIN -> prende lo score normale, quindi si confrontano normalmente

DRAW -> stessa cosa di win, ma vince quello che fa pareggiare piu in fondo, quindi 
        quello con lo score maggiore

LOSE -> stessa cosa di draw

NOT_DEFINED -> prende lo score normale.

NOT_DEFINED e DRAW ->   Per confrontare questi due valori si controlla il valore di NOT_DEFINED
                        NOT_DEFINED > 0 (board che favorisce il player) -> NOT_DEFINED e' meglio di un pareggio
                        NOT_DEFINED <= 0 (board incerta o che favorisce l'avversario) -> NOT_DEFINED e' peggio di DRAW

Per invertire lo score basta chiamare il metodo  CustomScore.invert






### Alpha-Beta intelligente

- L'alpha beta elimina in range, non con strettamente inferiore e superiore.
- Per ogni passaggio fai eval di tutte le mosse direttamente inferiori e le metti in coda in ordine decrescente
- Dopo il numero di livelli prestabilito continueremo a scendere in maniera dinamica, quindi se l'eval sembra promettente scendiamo, altrimenti rimaniamo fermi. Il controllo avviene ad ogni passaggio di discesa, e ad ogni passaggio il controllo si fa più stringente, per questo la discesa è dinamica e non sappiamo precisamente quanto vogliamo andare in fondo in partenza.
- Iterative deepening
- aspiration window

### Precalcolo dati

- Il salvataggio del precalocolo dei dati deve avvenire in modo che vengano salvate le mosse sbagliate e non quelle corrette, in modo da risparmiare spazio.





## Note alphabeta di ale
- Descrivere bene il tipo di alphabeta
- Descrivere move ordering
- Descrivere iterative deepening
- Descrivere problema della resa anticipata
- Descrivere transposition table
- aspiration window
- tornare indietro con le mosse è più efficiente

- FARE BENCHMARK VARI
