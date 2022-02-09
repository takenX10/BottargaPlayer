package BottargaPlayer.Player4;

import mnkgame.MNKCell;

import java.util.BitSet;

public class TableValues {
    public long hash;   // per verificare che il valore coincida con quello dell'hash
    public CustomScore eval;
    public BitSet type;    // 2 bit sono sufficienti: 10 -> exact | 00 -> lower | 01 -> upper
    public int depth;       // La profondita serve perchè se troviamo una corrispondenza ad una profondita inferiore dobbiamo usare quella
    public int maxDepth;
    // TODO inserisci questa
    public MNKCell next;    // migliore mossa sotto di questa, utile per move ordering.
}
