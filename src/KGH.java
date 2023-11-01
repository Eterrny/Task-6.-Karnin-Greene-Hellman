import java.math.BigInteger;
import java.security.SignatureException;

public class KGH {
    public static void main(String[] args) throws SignatureException {
        if (args.length == 0) {
            System.out.println("Входные параметры отсутствуют");
            return;
        }
        if (args[0].equals("/help") || args[0].equals("h")) {
            System.out.println("""
                    Программе должны передаваться следующие параметры:
                    \t- простое число p (вычисления проводятся в конечном поле GF(p))
                    \t- количество участников n
                    \t- минимальное число участников для раскрытия секрета t""");
            return;
        }
        if (args.length < 3) {
            System.out.println("Передано некорректное число параметров.");
            return;
        }
        BigInteger p;
        int n, t;
        try {
            p = new BigInteger(args[0]);
            if (!p.isProbablePrime(50)){
                System.out.println("Передано составное число.");
                throw new IllegalArgumentException();
            }
            n = Integer.parseInt(args[1]);
            if (n < 2){
                System.out.println("Число n должно быть больше или равно 2.");
                throw new IllegalArgumentException();
            }
            t = Integer.parseInt(args[2]);
            if (t < 2 || t > n) {
                System.out.println("Число t должно быть больше или равно 2 и не более, чем n.");
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка в чтении входных параметров.");
            return;
        }
        KGHService service = new KGHService(p, n, t);
//        SecureRandom rnd = new SecureRandom();
//        KGHService service = new KGHService(BigInteger.probablePrime(4, rnd), 3, 10, 7);
    }
}
