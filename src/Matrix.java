import java.math.BigInteger;
import java.rmi.MarshalException;
import java.util.ArrayList;

class IncorrectMatrixException extends Exception {
    public IncorrectMatrixException(String s) {
        super(s);
    }
}

public class Matrix {
    private int row, col;
    private BigInteger p;
    private ArrayList<ArrayList<BigInteger>> m;

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public BigInteger getP() {
        return p;
    }

    public ArrayList<BigInteger> get(int i) {
        return m.get(i);
    }

    public void set(int i, ArrayList<BigInteger> row){
        m.set(i, row);
    }

    public void set(int i, int j, BigInteger n){
        m.get(i).set(j, n);
    }

    @Override
    public String toString() {
        String matrix = "";
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                matrix += m.get(i).get(j) + " ";
            }
            if (i != row-1) {
                matrix += "\n";
            }
        }
        return matrix;
    }

    public Matrix(ArrayList<ArrayList<BigInteger>> m, BigInteger p) {
        try {
            if (m == null || m.size() == 0 || m.get(0).size() == 0) {
                throw new IncorrectMatrixException("Передана некорректная матрица");
            }
            this.m = m;
            this.row = m.size();
            this.col = m.get(0).size();
            this.p = p;
        } catch (IncorrectMatrixException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
