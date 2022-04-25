package BottargaPlayer.Utils.TranspositionTable;

import BottargaPlayer.Utils.Cell.CustomScore;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;

import java.util.BitSet;
import java.util.Random;
// table size = 64mb circa, 2^23b
public class TranspositionTable {

    private int M;
    private int N;
    private int size;
    private Long[][] zobrist;
    private TableValues[] table;
    public TableValues current_value;
    
    
    private int maxValues;
    // 1 = current
    // 2 = 180deg rotation
    // 3 = mirror current
    // 4 = mirror current + 180deg rotation
    // 5 = 90deg rotation right
    // 6 = 90deg rotation left
    // 7 = mirror current + 90deg rotation right
    // 8 = mirror current + 90deg rotation left
    private long[] current_hash = new long[8];

    public TranspositionTable(int M, int N, int size){
        this.M = M;
        this.N = N;
        this.size = size;
        this.zobrist = generateZobristTable(this.M * this.N);
        this.table = new TableValues[this.size];
        this.current_value = null;
        this.maxValues = N == M ? 8 : 4;
        for(int i=0; i<maxValues; i++){
            this.current_hash[i] = 0x0L;
        }
    }

    // aggiunge un valore all'interno della tabella
    // non aggiorna current_hash e current_value
    public void updatepos(MNKCell pos, MNKCellState stato, CustomScore eval, int depth, int maxdepth, MNKCell next, BitSet type){
        for(int i = 0; i<maxValues; i++){
            TableValues newvalue = new TableValues();
            newvalue.hash = this.current_hash[i];
            newvalue.eval = eval;
            newvalue.depth = depth;
            newvalue.next = next;
            newvalue.type = type;
            newvalue.maxDepth = maxdepth;
            this.table[calculateIndex(this.current_hash[i])] = newvalue;
        }
    }

    // aggiorna l'hash riportandolo indietro
    public void invertpos(MNKCell pos, MNKCellState stato){
        Integer[] cords;
        if(pos != null){
            for(int i = 0; i<maxValues; i++){
                cords = calculateRotation(pos, i);
                this.current_hash[i] = this.current_hash[i] ^ (stato == MNKCellState.P1 ? zobrist[0][(this.N*cords[0]) + cords[1]] : zobrist[1][(this.N*cords[0]) + cords[1]]);
                this.current_value = this.table[calculateIndex(this.current_hash[i])];
            }
        }
    }

    // controlla se nella tabella è salvato il valore cercato
    // e aggiorna current_hash e current_value
    public boolean exist(MNKCell cell, MNKCellState stato){
        if(cell != null){
            for(int i = 0; i<maxValues; i++){
                this.current_hash[i] = this.current_hash[i] ^ calculatepos(cell, stato, i);
            }
            this.current_value = this.table[calculateIndex(this.current_hash[0])];
            if(this.current_value != null){
                if(this.current_value.hash == this.current_hash[0]){
                    return true;
                }
            }
        }
        return false;
    }

    private int calculateIndex(Long hash){
        return (int) (hash % this.size);
    }

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

    // considerando che i -> M -> colonne (asse y) e j -> N -> righe (asse x)
    private Long calculatepos(MNKCell C, MNKCellState state, int rotation){
        Integer[] cords = calculateRotation(C, rotation);
        int pos = ((this.N * cords[0]) + cords[1]);
        if(state == MNKCellState.P1){
            return zobrist[0][pos];
        }else{
            return zobrist[1][pos];
        }
    }

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
