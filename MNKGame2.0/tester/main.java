package tester;
import mnkgame.*;
import BottargaPlayer.Utils.Matrix.*;

//Questo main contiene degli esempi utili a capire praticamente come usare le classi EvalMatrix ed UpdateEvalMatrix.
public class main {
    public static void main(String[] args) {
        /*
        CustomScore a = new CustomScore((double)10, EvalStatus.DRAW);
        CustomScore b = new CustomScore((double)3, EvalStatus.NOT_DEFINED);

        System.out.println(a.compare(b));
        System.out.println(b.compare(a));*/
        // int M = 5;
        // int N = 4;
        // int K = 4;
        // // MNKCell[] FC = new MNKCell[12];
        // // MNKCell[] MC = new MNKCell[8];
        // MNKCell[] FC = new MNKCell[14];
        // MNKCell[] MC = new MNKCell[6];
        // MC[0] = new MNKCell(0, 0, MNKCellState.P1);
        // MC[1] = new MNKCell(1, 1, MNKCellState.P2);
        // MC[2] = new MNKCell(4, 0, MNKCellState.P1);
        // MC[3] = new MNKCell(2, 2, MNKCellState.P2);
        // MC[4] = new MNKCell(4, 3, MNKCellState.P1);
        // MC[5] = new MNKCell(1, 2, MNKCellState.P2);
        // // MC[6] = new MNKCell(2, 3, MNKCellState.P1);
        // // MC[7] = new MNKCell(3, 2, MNKCellState.P2);
        

        // FC[0] = new MNKCell(0, 3, MNKCellState.FREE);
        // FC[1] = new MNKCell(4, 2, MNKCellState.FREE);
        // FC[2] = new MNKCell(3, 1, MNKCellState.FREE);
        // FC[3] = new MNKCell(3, 0, MNKCellState.FREE);
        // FC[4] = new MNKCell(3, 3, MNKCellState.FREE);
        // FC[5] = new MNKCell(4, 1, MNKCellState.FREE);
        // FC[6] = new MNKCell(1, 0, MNKCellState.FREE);
        // FC[7] = new MNKCell(0, 2, MNKCellState.FREE);
        // FC[8] = new MNKCell(2, 1, MNKCellState.FREE);

        // FC[9] = new MNKCell(3, 2, MNKCellState.FREE);

        // FC[10] = new MNKCell(0, 1, MNKCellState.FREE);
        // FC[11] = new MNKCell(1, 3, MNKCellState.FREE);
        // FC[12] = new MNKCell(2, 0, MNKCellState.FREE);

        // FC[13] = new MNKCell(2, 3, MNKCellState.FREE);

        // bottargaPlayer4.negamaxPlayer p1 = new bottargaPlayer4.negamaxPlayer();
        // bottargaPlayer1.negamaxPlayer p2 = new bottargaPlayer1.negamaxPlayer();
        // p1.initPlayer(M,N,K,true,100000);
        // p2.initPlayer(M,N,K,false,10000);
        // System.out.println(p1.selectCell(FC, MC));
        
        /*
        int M = 4;
        int N = 4;
        int K = 4;
        MNKCell[] FC = new MNKCell[10];
        MNKCell[] MC = new MNKCell[8];
        MC[0] = new MNKCell(0, 0, MNKCellState.P1);
        MC[1] = new MNKCell(1, 1, MNKCellState.P2);
        MC[2] = new MNKCell(1, 2, MNKCellState.P1);
        MC[3] = new MNKCell(0, 3, MNKCellState.P2);
        MC[4] = new MNKCell(2, 3, MNKCellState.P1);
        MC[5] = new MNKCell(3, 2, MNKCellState.P2);
        MC[6] = new MNKCell(0, 2, MNKCellState.P1);
        MC[7] = new MNKCell(0, 1, MNKCellState.P2);
        //MC[8] = new MNKCell(3, 1, MNKCellState.P1);


        EvalMatrix my_eval_matrix = new EvalMatrix(M, N, K);
        UpdateEvalMatrix update = new UpdateEvalMatrix(M, N, K, true, my_eval_matrix.getM_Matrix(), my_eval_matrix.getN_Matrix(), my_eval_matrix.getK1_Matrix(), my_eval_matrix.getK2_Matrix());

        update.multiple_update_matrix(MC);
        System.out.println(update.eval);
        */

        
        int M = 3;
        int N = 3;
        int K = 3;
        EvalMatrix my_eval_matrix = new EvalMatrix(M, N, K);
        UpdateEvalMatrix update = new UpdateEvalMatrix(M, N, K, false, my_eval_matrix.getM_Matrix(), my_eval_matrix.getN_Matrix(), my_eval_matrix.getK1_Matrix(), my_eval_matrix.getK2_Matrix());
        // MNKCell[] FC = new MNKCell[5];
        // for(int i = 0; i < M; i++){
        //     for(int j = 0; j < N; j++){
        //         FC[i*N + j] = new MNKCell(i,j,MNKCellState.FREE);
        //     }
        // }
        
        MNKCell a = new MNKCell(0,0, MNKCellState.FREE);
        MNKCell b = new MNKCell(0,1, MNKCellState.FREE);
        MNKCell c = new MNKCell(0,2, MNKCellState.FREE);
        MNKCell d = new MNKCell(1,0, MNKCellState.FREE);
        MNKCell e = new MNKCell(1,1, MNKCellState.FREE);
        MNKCell f = new MNKCell(1,2, MNKCellState.FREE);
        MNKCell g = new MNKCell(2,0, MNKCellState.FREE);
        MNKCell h = new MNKCell(2,1, MNKCellState.FREE);
        MNKCell i = new MNKCell(2,2, MNKCellState.FREE);
        // MNKCell j = new MNKCell(3,0, MNKCellState.FREE);
        // MNKCell k = new MNKCell(3,1, MNKCellState.FREE);
        // MNKCell l = new MNKCell(3,2, MNKCellState.FREE);

        MNKCell[] FC = {a,b,c,d,e,f,g,h,i};//,j,k,l};

        MNKCell[] MC = {};
        update.multiple_update_matrix(MC);
        update.single_update_matrix(a, MNKCellState.P1);
        System.out.println("insert: "+a);
        System.out.println(update.eval);
        update.single_update_matrix(e, MNKCellState.P1);
        System.out.println("insert: "+e);
        System.out.println(update.eval);
        update.single_update_matrix(h, MNKCellState.P1);
        System.out.println("insert: "+h);
        System.out.println(update.eval);
        update.single_update_matrix(f, MNKCellState.P1);
        System.out.println("insert: "+f);
        System.out.println(update.eval);
        update.single_update_matrix(b, MNKCellState.P2);
        System.out.println("insert: "+b);
        System.out.println(update.eval);
        update.single_update_matrix(d, MNKCellState.P2);
        System.out.println("insert: "+d);
        System.out.println(update.eval);
        update.single_update_matrix(i, MNKCellState.P2);
        System.out.println("insert: "+i);
        System.out.println(update.eval);
        update.single_update_matrix(c, MNKCellState.P2);
        System.out.println("insert: "+c);
        System.out.println(update.eval);
        update.single_update_matrix(g, MNKCellState.P2);
        System.out.println("insert: "+g);
        System.out.println(update.eval);
        update.single_invert_matrix(g, MNKCellState.P2);
        System.out.println("remove: "+g);
        System.out.println(update.eval);
        update.single_invert_matrix(c, MNKCellState.P2);
        System.out.println("remove: "+c);
        System.out.println(update.eval);
        update.single_invert_matrix(i, MNKCellState.P2);
        System.out.println("remove: "+i);
        System.out.println(update.eval);
        update.single_invert_matrix(d, MNKCellState.P2);
        System.out.println("remove: "+d);
        System.out.println(update.eval);
        update.single_invert_matrix(b, MNKCellState.P2);
        System.out.println("remove: "+b);
        System.out.println(update.eval);
        update.single_invert_matrix(f, MNKCellState.P1);
        System.out.println("remove: "+f);
        System.out.println(update.eval);
        update.single_invert_matrix(h, MNKCellState.P1);
        System.out.println("remove: "+h);
        System.out.println(update.eval);
        update.single_invert_matrix(e, MNKCellState.P1);
        System.out.println("remove: "+e);
        System.out.println(update.eval);
        update.single_invert_matrix(a, MNKCellState.P1);
        System.out.println("remove: "+a);
        System.out.println(update.eval);
        System.out.println("ciao");
        //player pg = new player();
        //pg.initPlayer(M,N,K,true,10);
        //MNKCell move = pg.selectCell(FC,MC);
        //System.out.println(move);
        
    }
}