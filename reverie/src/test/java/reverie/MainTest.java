package reverie;

public class MainTest {

  public static void main(String[] args) {

    int mask = 00000000;

    int b = 2 | (1 << mask);

    int c = 3 | (1 << mask);

    int d = 4 | (1 << mask);

    int f = (b & d);

    boolean fl = b == (b & (1 << mask));

    System.out.println(f);
    System.out.println(fl);
  }
}
