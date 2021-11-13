# BottargaBOT
Giocatore per progetto di Algoritmi e strutture dati UniBO 2020/21
## Cose da fare
- [] Eval -> patta
- [] Alpha-Beta intelligente
- [] Precalcolo dati
- [] Pattern Matching

### Altre info

- Lo scan deve occupare poca memoria, quindi quando torni indietro aggiorni la matrice e distruggi quella vecchia

### Eval

Costruiamo 4 matrici (una per ogni direzione in cui è possibile vincere), in modo che in ogni spazio della matrice sia presente una possibile "linea di vittoria".
- teniamo da parte la somma dei valori di ogni matrice (in modo da rendere più immediato il calcolo in seguito)
- Se la somma di tutte le somme delle matrici è un valore negativo particolare ( - n dove n è il numero di celle di tutte le matrici) allora **non puoi vincere**, se entrambi i giocatori non possono vincere è patta
> sarà da definire come strutturare la matrice delle diagonali

### Alpha-Beta intelligente

- L'alpha beta elimina in range, non con strettamente inferiore e superiore.
- Per ogni passaggio fai eval di tutte le mosse direttamente inferiori e le metti in coda in ordine decrescente
- Dopo il numero di livelli prestabilito continueremo a scendere in maniera dinamica, quindi se l'eval sembra promettente scendiamo, altrimenti rimaniamo fermi. Il controllo avviene ad ogni passaggio di discesa, e ad ogni passaggio il controllo si fa più stringente, per questo la discesa è dinamica e non sappiamo precisamente quanto vogliamo andare in fondo in partenza.
- Iterative deepening
- aspiration window

### Precalcolo dati

- Il salvataggio del precalocolo dei dati deve avvenire in modo che vengano salvate le mosse sbagliate e non quelle corrette, in modo da risparmiare spazio.