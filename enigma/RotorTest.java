package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

public class RotorTest {
    @Test
    public void test1() {
        Alphabet a = new Alphabet("ABCDEF");
        Permutation p = new Permutation("(ACB)(DE)", a);
        Rotor r = new Rotor("a", p);
        assertEquals(r.convertForward(0), 2);
        assertEquals(r.convertBackward(0), 1);
    }
}
