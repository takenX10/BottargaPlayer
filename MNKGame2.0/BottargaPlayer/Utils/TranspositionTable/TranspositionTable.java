package BottargaPlayer.Utils.TranspositionTable;

import BottargaPlayer.Utils.Cell.CustomScore;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;

import java.util.BitSet;
import java.util.Random;
// table size = 64mb circa, 2^23b

/**
 * La dimensione della tabella e' di 2^23 entry, circa 64mb
 * Oltre a controllare la mossa passata, fa in automatico un controllo delle seguenti simmetrie:
 * 1 = current
 * 2 = 180deg rotation
 * 3 = mirror current
 * 4 = mirror current + 180deg rotation
 * 5 = 90deg rotation right
 * 6 = 90deg rotation left
 * 7 = mirror current + 90deg rotation right
 * 8 = mirror current + 90deg rotation left
 * 
 * E' presente una variabile currentHash pubblica di dimensione 8, ogni indice corrisponde all'hash della simmetria
 * elencata sopra. E' presente anche una variabile currentValue pubblica, dove viene salvato il valore 
 * della mossa corrispondente all'hash corrente in caso sia presente nella tabella.
 * 
 * Come algoritmo di hashing utilizziamo zobrist, e come politica di scambio in caso di due elementi
 * nella stessa posizione della tabella, inseriamo sempre il nuovo elemento.
 */
public class TranspositionTable {

    /**
     * Valore di M
     */
    private int M;

    /**
     * Valore di N
     */
    private int N;

    /**
     * Dimensione della Transposition table
     */
    private final int size;
    /**
     * Tabella zobrist
     */
    private final Long[][] zobrist;
    /**
     * La tabella con le entry
     */
    private TableValues[] table;
    /**
     * Il valore corrente salvato nella tabella
     */
    public TableValues currentValue;
    
    /**
     * vale 4 se la tabella e' rettangolare (quindi abbiamo solo 4 simmetrie), 8 altrimenti
     */
    private final int maxValues;

    /**
     * 
     * L'hash ha dimensione 8 perche' le possibili simmetrie sono 8.
     */
    private long[] currentHash = new long[8];

    /**
     * 
     * @param M la dimensione M della board
     * @param N la dimensione N della board
     * @param size la dimensione della transposition table
     */
    public TranspositionTable(int M, int N, int size){
        this.M = M;
        this.N = N;
        this.size = size;
        this.zobrist = generateZobristTable(this.M * this.N);
        this.table = new TableValues[this.size];
        this.currentValue = null;
        this.maxValues = (N == M ? 8 : 4);
        for(int i=0; i<maxValues; i++){
            this.currentHash[i] = 0x0L;
        }
    }

    /**
     * Aggiunge un valore all'interno della tabella
     * Non aggiorna currentHash e currentValue
     * @param pos       Il valore della cella da aggiungere
     * @param stato     Lo stato della cella da aggiungere (se e' di P1 o di P2)
     * @param eval      L'eval
     * @param depth     La profondita a cui e' situata questa mossa
     * @param maxdepth  La profondita massima della ricerca al momento del ritrovamento di questo eval (nell'iterative deepening)
     * @param next      La mossa migliore dopo questa
     * @param type      Il tipo di mossa aggiunta (EXACT, LOWER o UPPER)
     */
    public void updatepos(MNKCell pos, MNKCellState stato, CustomScore eval, int depth, int maxdepth, MNKCell next, BitSet type){
        // per ogni simmetria aggiorniamo la tabella
        for(int i = 0; i<maxValues; i++){
            TableValues newvalue = new TableValues();
            newvalue.hash = this.currentHash[i];
            newvalue.eval = eval;
            newvalue.depth = depth;
            newvalue.next = next;
            newvalue.type = type;
            newvalue.maxDepth = maxdepth;
            this.table[calculateIndex(this.currentHash[i])] = newvalue;
        }
    }

    /**
     * Aggiorna l'hash riportandolo indietro
     * @param pos   Il valore della cella da invertire
     * @param stato Lo stato della cella da invertire (se e' di P1 o di P2)
     */
    public void invertpos(MNKCell pos, MNKCellState stato){
        Integer[] cords;
        if(pos != null){
            for(int i = 0; i<maxValues; i++){
                cords = calculateRotation(pos, i);
                this.currentHash[i] = this.currentHash[i] ^ (stato == MNKCellState.P1 ? zobrist[0][(this.N*cords[0]) + cords[1]] : zobrist[1][(this.N*cords[0]) + cords[1]]);
                this.currentValue = this.table[calculateIndex(this.currentHash[i])];
            }
        }
    }

    /**
     * Controlla se nella tabella e' stato salvato il valore cercato
     * e aggiorna currentHash e currentValue
     * @param cell  Il valore della cella da controllare
     * @param stato Lo stato della cella da controllare (se e' di P1 o di P2)
     * @return true sse la mossa e' salvata nella tabella, false altrimenti
     */
    public boolean exist(MNKCell cell, MNKCellState stato){
        if(cell != null){
            for(int i = 0; i<maxValues; i++){
                this.currentHash[i] = this.currentHash[i] ^ calculatePos(cell, stato, i);
            }
            this.currentValue = this.table[calculateIndex(this.currentHash[0])];
            if(this.currentValue != null){
                if(this.currentValue.hash == this.currentHash[0]){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Calcola l'index nella tabella da un hash
     * @param hash  l'hash di cui si vuole trovare l'indice
     * @return  l'indice
     */
    private int calculateIndex(Long hash){
        return (int) (hash % this.size);
    }

    /**
     * Calcola la nuova cella per ogni rotazione
     * @param C         cella iniziale
     * @param rotation  rotazione di cui si vogliono calcolare le coordinate
     * @return una coppia di Integer con dentro le coordinate di C ruotate della rotazione richiesta
     */
    private Integer[] calculateRotation(MNKCell C, int rotation){
        int i = 0;
        int j = 0;
        switch(rotation){
            case 0:     // current
                i = C.i;
                j = C.j;
                break;
            case 1: // 180deg rotation
                i = this.M - C.i - 1;
                j = this.N - C.j - 1;
                break;
            case 2: // mirror current
                i = C.i;
                j = this.N - C.j - 1;
                break;
            case 3: // mirror + 180deg rotation
                i = this.M - C.i - 1;
                j = C.j;
                break;
            case 4: // 90deg rotation right
                i = C.j;
                j = this.N - C.i - 1;
                break;
            case 5: // 90deg rotation left
                i = this.M - C.j - 1;
                j = C.i;
                break;
            case 6: // mirror + 90deg rotation right
                i = this.N - C.j - 1;
                j = this.M - C.i - 1;
                break;
            case 7: // mirror + 90deg rotation left
                i = C.j;
                j = C.i;
                break;
        }
        Integer[] cords = new Integer[2];
        cords[0] = i;
        cords[1] = j;
        return cords;
    }

    /**
     * 
     * @param C         la cella di cui si vuole sapere l'hash
     * @param state     lo stato della cella passata (P1 o P2)
     * @param rotation  rotazione della cella richiesta
     * @return  L'hash di zobrist corretto rispetto alla cella inizale, lo stato e la rotazione passata
     */
    private Long calculatePos(MNKCell C, MNKCellState state, int rotation){
        Integer[] cords = calculateRotation(C, rotation);
        int pos = ((this.N * cords[0]) + cords[1]);
        if(state == MNKCellState.P1){
            return zobrist[0][pos];
        }else{
            return zobrist[1][pos];
        }
    }

    /**
     * Genera la tabella di zobrist della dimensione passata
     * @param size  dimensione della tabella di zobrist che si vuole generare
     * @return  la tabella di zobrist generata
     */
    private Long[][] generateZobristTable(int size){
        Long[][] table = new Long[2][size];
        Random obj = new Random(); // per generare i Long random
        for(int i = 0; i<2; i++){
            for(int j = 0; j< size; j++){
                do{
                    table[i][j] = obj.nextLong();
                }while(table[i][j] < 0);
            }
        }
        return table;
    }
}
