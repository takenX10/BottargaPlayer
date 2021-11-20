public class main {
    public static void main(String[] args) {
        int M = 10;
        int N = 2;
        int K = 2;
        Tables test = new Tables(M, N, K);
        EvalMatrix prova = new EvalMatrix(M, N, K, test.M_Matrix, test.N_Matrix, test.K1_Matrix, test.K2_Matrix );
        prova.update_Matrix(0, 0);
    }
}