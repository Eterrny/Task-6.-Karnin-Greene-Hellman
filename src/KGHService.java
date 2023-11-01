import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class KGHService {
    private final BigInteger p;
    private final int n, t; // n - участники, t - минимум для раскрытия секрета
    private ArrayList<Matrix> V;
    private Matrix U, trU;
    private BigInteger secret;
    private ArrayList<BigInteger> shares = new ArrayList<>(); // доли секрета

    public KGHService(BigInteger p, int n, int t) {
        this.p = p;
        this.n = n;
        this.t = t;
        System.out.println(this);
        this.scheme();
    }

    @Override
    public String toString() {
        return String.format("""
                        Схема Карнина-Грини-Хеллмана. Параметры:
                           Количество участников n = %d
                           Минимальное число участников для раскрытия секрета t = %d
                           Вычисления проводятся в поле GF(%d)
                           """,
                n, t, p);
    }

    private void scheme() {
        generateVectors();
        setShares();
        System.out.println("U = " + U);
        for (int i = 0; i < V.size(); ++i) {
            System.out.println("V" + i + " = " + V.get(i));
        }
        this.secret = MatrixService.mult(U, MatrixService.transpose(V.get(0))).get(0).get(0);
        System.out.println("\nСекрет M = UV0 = " + secret);
        System.out.println("\nДоли секрета: ");
        for (int i = 0; i < shares.size(); ++i) {
            System.out.println("UV" + (i + 1) + " = " + shares.get(i));
        }
        ArrayList<Integer> equastions = new ArrayList<>();
        for (int i = 1; i <= n; ++i) {
            equastions.add(i);
        }
        while (equastions.size() != t) {
            equastions.remove(new Random().nextInt(equastions.size()));
        }
        System.out.println("\nДля проверки выбраны следующие t участников: " + equastions);
        System.out.println("Они составили систему линейных уравнений. Матрица системы:");
        SLE system = new SLE(V, shares, equastions, p);
        System.out.println("Решение системы: " + system.getSolution());
        checkSolution(system.getSolution());
    }

    private void checkSolution(ArrayList<BigInteger> solution) {
        for (int i = 0; i < solution.size(); ++i) {
            if (!solution.get(i).equals(U.get(0).get(i))) {
                System.out.println("Найденное решение не является правильным. Секрет не может быть раскрыт.");
                return;
            }
        }
        BigInteger calculatedSecret = MatrixService.mult(getMatrix(solution), MatrixService.transpose(V.get(0))).get(0).get(0);
        System.out.println("Участники вычислили значение секрета: " + calculatedSecret);
        if (calculatedSecret.equals(secret)) {
            System.out.println("Найденное решение является правильным. Секрет успешно раскрыт.");
        } else {
            System.out.println("Ошибка в подсчете значения секрета.");
        }
    }

    private void generateVectors() {
        this.U = generateVector(t);
        this.trU = MatrixService.transpose(this.U);
        do {
            V = new ArrayList<>();
            for (int i = 0; i <= n; ++i) {
                V.add(generateVector(t));
            }
        } while (!checkLinearDependency(combination()));
    }

    private void setShares() {
        for (int i = 1; i < V.size(); ++i) {
            shares.add(MatrixService.mult(U, MatrixService.transpose(V.get(i))).get(0).get(0));
        }
    }

    private ArrayList<Matrix> combination() {
        ArrayList<Matrix> subsets = new ArrayList<>();
        int[] s = new int[t];
        if (t <= V.size()) {
            for (int i = 0; (s[i] = i) < t - 1; ++i) ;
            subsets.add(getSubset(s));
            for (; ; ) {
                int i;
                for (i = t - 1; i >= 0 && s[i] == V.size() - t + i; --i) ;
                if (i < 0) {
                    break;
                }
                s[i]++;
                for (++i; i < t; ++i) {
                    s[i] = s[i - 1] + 1;
                }
                subsets.add(getSubset(s));
            }
        }
        return subsets;
    }

    private Matrix getSubset(int[] subset) {
        ArrayList<ArrayList<BigInteger>> vi = new ArrayList<>();
        for (int i = 0; i < subset.length; ++i) {
            vi.add(V.get(subset[i]).get(0));
        }
        return new Matrix(vi, p);
    }

    private boolean checkLinearDependency(ArrayList<Matrix> combination) {
        for (Matrix each : combination) {
            if (MatrixService.determinant(each).equals(BigInteger.ZERO)) {
                return false;
            }
        }
        return true;
    }

    private Matrix generateVector(int t) {
        SecureRandom rnd = new SecureRandom();
        ArrayList<BigInteger> res = new ArrayList<>();
        for (int i = 0; i < t; ++i) {
            res.add(new BigInteger(p.bitLength(), rnd).mod(p));
        }
        return getMatrix(res);
    }

    private Matrix getMatrix(ArrayList<BigInteger> vector) {
        ArrayList<ArrayList<BigInteger>> matrix = new ArrayList<>();
        matrix.add(vector);
        return new Matrix(matrix, p);
    }
}
