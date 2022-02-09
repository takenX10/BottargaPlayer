package bottargaPlayer3;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;

import java.util.BitSet;
import java.util.Random;
// TODO: implementa scambio di elementi all'interno della tabella
// table size = 64mb circa, 2^23b
public class transpositionTable {

    private int M;
    private int N;
    private int size;
    private long current_hash;
    private Long[][] zobrist;
    private TableValues[] table;

    public TableValues current_value;

    public transpositionTable(int M, int N, int size){
        this.M = M;
        this.N = N;
        this.size = size;
        this.zobrist = generateZobristTable(this.M * this.N);
        this.table = new TableValues[this.size];
        this.current_hash = 0x0L;
        this.current_value = null;
    }

    // aggiunge un valore all'interno della tabella
    // non aggiorna current_hash e current_value
    public void updatepos(MNKCell pos, MNKCellState stato, double eval, int depth, int maxdepth, MNKCell next, BitSet type){
        TableValues newvalue = new TableValues();
        newvalue.hash = this.current_hash;
        newvalue.eval = eval;
        newvalue.depth = depth;
        newvalue.next = next;
        newvalue.type = type;
        newvalue.maxDepth = maxdepth;
        this.table[calculateIndex(this.current_hash)] = newvalue;
    }

    // aggiorna l'hash riportandolo indietro
    public void invertpos(MNKCell pos, MNKCellState stato){
        if(pos != null){
            this.current_hash = this.current_hash ^ (stato == MNKCellState.P1 ? zobrist[0][(this.N*pos.i) + pos.j] : zobrist[1][(this.N*pos.i) + pos.j]);
            this.current_value = this.table[calculateIndex(this.current_hash)];
        }
    }

    // controlla se nella tabella è salvato il valore cercato
    // e aggiorna current_hash e current_value
    public boolean exist(MNKCell cell, MNKCellState stato){
        if(cell != null){
            this.current_hash = this.current_hash ^ calculatepos(cell, stato);
            this.current_value = this.table[calculateIndex(this.current_hash)];
            if(this.current_value != null){
                if(this.current_value.hash == this.current_hash){
                    return true;
                }
            }
        }
        return false;
    }

    private int calculateIndex(Long hash){
        return (int) (hash % this.size);
    }

    // considerando che i -> M -> colonne (asse y) e j -> N -> righe (asse x)
    private Long calculatepos(MNKCell C, MNKCellState state){
        int pos = ((this.N*C.i) + C.j);
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
