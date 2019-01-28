import java.util.ArrayList;
import java.util.List;

public class typeTest {

    public static void main() {
        List a = new ArrayList(3);

        a.add(1);
        a.add("xxx");

        set(1.1);

        System.out.println("=======================");
        System.out.println(a.get(0));
        System.out.println(a.get(1));
        System.out.println("=======================");
    }

    public static void set(Object o) {

    }
}
