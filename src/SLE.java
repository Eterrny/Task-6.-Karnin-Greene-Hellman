import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

public class SLE {
    private final int t;
    private final BigInteger p;
    private final ArrayList<BigInteger> solution;
    private Matrix system;

    public SLE(ArrayList<Matrix> V, ArrayList<BigInteger> shares, ArrayList<Integer> indexes, BigInteger p) {
        this.t = indexes.size();
        this.p = p;
        ArrayList<ArrayList<BigInteger>> matrix = new ArrayList<>();
        for (int i = 0; i < t; ++i) {
            int curIndex = indexes.get(i);
            ArrayList<BigInteger> row = new ArrayList<>();
            for (int j = 0; j < t; ++j) {
                row.add(V.get(curIndex).get(0).get(j));
            }
            row.add(shares.get(curIndex - 1));
            matrix.add(row);
        }
        this.system = new Matrix(matrix, p);
        System.out.println(this);
        this.gauss();
        this.solution = this.setSolution();
    }

    private void gauss() {
        int rows = system.getRow();
        int cols = system.getCol();
        int numPivots = 0;
        for (int j = 0; j < cols && numPivots < rows; ++j) {
            int pivotRow = numPivots;
            while (pivotRow < rows && system.get(pivotRow).get(j).equals(BigInteger.ZERO)) {
                ++pivotRow;
            }
            if (pivotRow == rows) {
                continue;
            }
            system = MatrixService.swapRows(system, numPivots, pivotRow);
            pivotRow = numPivots;
            ++numPivots;
            system = MatrixService.multiplyRow(system, pivotRow, system.get(pivotRow).get(j).modInverse(p));
            for(int i = pivotRow + 1; i < rows; ++i){
                system = MatrixService.addRows(system, pivotRow, i, system.get(i).get(j).negate().mod(p));
            }
        }
        for (int i = numPivots - 1; i >= 0; --i) {
            int pivotCol = 0;
            while (pivotCol < cols && system.get(i).get(pivotCol).equals(BigInteger.ZERO)) {
                ++pivotCol;
            }
            if (pivotCol == cols) {
                continue;
            }
            for (int j = i - 1; j >= 0; --j) {
                system = MatrixService.addRows(system, i, j, system.get(j).get(pivotCol).negate().mod(p));
            }
        }
    }

    @Override
    public String toString(){
        return system.toString();
    }

    private ArrayList<BigInteger> setSolution() {
        ArrayList<BigInteger> solution = new ArrayList<>();
        for(int i = 0; i < system.getRow(); ++i) {
            try {
                solution.add(system.get(i).get(t));
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Ошибка в вычислении решении СЛУ. Решение не может быть найдено.");
            }
        }
        return solution;
    }

    public ArrayList<BigInteger> getSolution() {
        return solution;
    }
}
