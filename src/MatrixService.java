import java.math.BigInteger;
import java.util.ArrayList;

public class MatrixService {
    public static Matrix mult(Matrix m1, Matrix m2) {
        int row1 = m1.getRow();
        int col1 = m1.getCol();
        int row2 = m2.getRow();
        int col2 = m2.getCol();
        if (col1 != row2) {
            System.out.println("Неправильная размерность матриц для произведения");
            return null;
        }
        ArrayList<ArrayList<BigInteger>> res = new ArrayList<>();
        for (int i = 0; i < row1; ++i) {
            ArrayList<BigInteger> row = new ArrayList<>();
            for (int j = 0; j < col2; ++j) {
                row.add(multIJ(m1, m2, i, j));
            }
            res.add(row);
        }
        return new Matrix(res, m1.getP());
    }

    private static BigInteger multIJ(Matrix m1, Matrix m2, int row, int col) {
        BigInteger res = BigInteger.ZERO;
        for (int k = 0; k < m1.get(0).size(); ++k) {
            res = res.add(m1.get(row).get(k).multiply(m2.get(k).get(col))).mod(m1.getP());
        }
        res = res.mod(m1.getP());
        return res;
    }

    public static Matrix transpose(Matrix m) {
        // System.out.println(m);
        ArrayList<ArrayList<BigInteger>> res = new ArrayList<>();
        for (int i = 0; i < m.getCol(); ++i) {
            ArrayList<BigInteger> row = new ArrayList<>();
            for (int j = 0; j < m.getRow(); ++j) {
                // System.out.println("j, i = " + j + ", " + i);
                row.add(m.get(j).get(i));
            }
            res.add(row);
        }
        return new Matrix(res, m.getP());
    }

    public static BigInteger determinant(Matrix m) {
        BigInteger p = m.getP();
        int col = m.getCol();

        //System.out.printf("Вызов det: %d, %d", p, col);

        BigInteger det = BigInteger.ZERO;
        if (col == 1) {
            det = m.get(0).get(0);
        } else if (col == 2) {
            //System.out.println("test " + p);
            det = m.get(0).get(0).multiply(m.get(1).get(1))
                    .subtract(m.get(1).get(0).multiply(m.get(0).get(1))).mod(p);
        } else {
            BigInteger k = BigInteger.ONE;
            for (int i = 0; i < col; ++i) {
                int col_1 = col - 1;
                Matrix tmp = getMatr(m, col, 0, i);
//                System.out.println("\nтемп матр:" + tmp);
                det = det.add(k.multiply(m.get(0).get(i)).multiply(determinant(tmp)));
//                System.out.println("i = " + i + ": " + det);
//                det = det.mod(p);
                k = k.negate();
                //det = det.add(m.get(0).get(i).multiply(BigInteger.ONE.pow(i)).multiply(determinant(tmp))).mod(p);
            }
        }
        return det.mod(p);
    }

    public static Matrix swapRows(Matrix m, int row0, int row1) {
        if (row0 < 0 || row0 >= m.get(0).size() || row1 < 0 || row1 >= m.get(0).size())
            throw new IndexOutOfBoundsException("Выход за гарницы массива");
        ArrayList<BigInteger> tmp = m.get(row0);
        m.set(row0, m.get(row1));
        m.set(row1, tmp);
        return m;
    }

    public static Matrix multiplyRow(Matrix m, int row, BigInteger factor) {
        if (row < 0 || row >= m.get(0).size()) {
            throw new IndexOutOfBoundsException("Выход за гарницы массива");
        }
        for (int j = 0, cols = m.getCol(); j < cols; j++) {
            m.set(row, j, m.get(row).get(j).multiply(factor).mod(m.getP()));
        }
        return m;
    }

    // destRow += srcRow * factor
    public static Matrix addRows(Matrix m, int srcRow, int destRow, BigInteger factor) {
        if (srcRow < 0 || srcRow >= m.get(0).size() || destRow < 0 || destRow >= m.get(0).size()) {
            throw new IndexOutOfBoundsException("Выход за гарницы массива");
        }
        for (int j = 0, cols = m.getCol(); j < cols; j++) {
            m.set(destRow, j, m.get(destRow).get(j).add(m.get(srcRow).get(j).multiply(factor)).mod(m.getP()));
        }
        return m;
    }

    private static Matrix getMatr(Matrix m, int col, int indRow, int indCol) {
        ArrayList<ArrayList<BigInteger>> res = new ArrayList<>();
        int col_1 = col - 1;
        for(int i = 0; i < col_1; ++i){
            ArrayList<BigInteger> row = new ArrayList<>();
            for(int j = 0; j < col_1; ++j){
                row.add(BigInteger.ZERO);
            }
            res.add(row);
        }
        for(int i = 0, ki = 0; i < col; ++i){
            if (i != indRow){
                for(int j = 0, kj = 0; j < col; ++j){
                    if (j != indCol){
                        res.get(ki).set(kj, m.get(i).get(j));
                        ++kj;
                    }
                }
                ++ki;
            }
        }
        return new Matrix(res, m.getP());
    }

    public static Matrix inverse(Matrix m){
        int col = m.getCol();
        BigInteger p = m.getP();
        BigInteger det = determinant(m);
        if (det.equals(BigInteger.ZERO)){
            System.out.println("Матрица вырожденная и обратной не имеет.");
            return null;
        }
        ArrayList<ArrayList<BigInteger>> res = new ArrayList<>();
        for(int i = 0; i < col; ++i){
            ArrayList<BigInteger> row = new ArrayList<>();
            for(int j = 0; j < col; ++j){
                Matrix tmp = getMatr(m, col, i, j);
                BigInteger a = determinant(tmp);
                if ((i + j + 2) % 2 != 0){
                    a = a.negate();
                }
                a = a.mod(p);
                row.add(a.multiply(det.modInverse(p)).mod(p));
            }
            res.add(row);
        }
        return transpose(new Matrix(res, p));
    }
}
