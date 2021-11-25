package bottargaPlayer;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;

import java.util.Random;

public class transpositionTable {
    private int M;
    private int N;
    private int size;
    private Long[][] zobrist;
    private int[] table;
    public transpositionTable(int M, int N, int size){
        this.M = M;
        this.N = N;
        this.size = size;
        this.zobrist = generateZobristTable(this.M * this.N);
        this.table = new int[this.size];
    }

    private int calculateIndex(Long hash){
        return (int) (hash % this.size);
    }

    // considerando che i -> M -> colonne (asse y) e j -> N -> righe (asse x)
    public Long calculatepos(MNKCell C){
        int pos = ((this.N*C.i) + C.j);
        if(C.state == MNKCellState.P1){
            return zobrist[0][pos];
        }else{
            return zobrist[1][pos];
        }
    }

    public Long generateHash(MNKCell[] MC){
        Long xor = 0x0000000000000000L;
        for(MNKCell cella: MC){
            xor = xor ^ calculatepos(cella);
        }
        return xor;
    }

    public Long xorPosition(Long oldHash, MNKCell newcell){
        return oldHash ^ calculatepos(newcell);
    }

    private Long[][] generateZobristTable(int size){
        Long[][] table = new Long[2][size];
        Random obj = new Random(); // per generare i Long random
        for(int i = 0; i<2; i++){
            for(int j = 0; j< size; j++){
                table[i][j] = obj.nextLong();
            }
        }
        return table;
    }
}
