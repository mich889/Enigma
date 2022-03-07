package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = fromAlpha.indexOf(c), ei = fromAlpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    @Test
    public void checkPermute() {
        Alphabet a = new Alphabet();
        String cycle = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
        Permutation p = new Permutation(cycle, a);
        assertEquals(p.permute('A'), 'E');
        assertEquals(p.permute('U'), 'A');
        assertEquals(p.permute('S'), 'S');
        assertEquals(p.permute('I'), 'V');
        assertEquals(p.permute(0), 4);
        assertEquals(p.permute(20), 0);
        assertEquals(p.permute(18), 18);
        assertEquals(p.permute(8), 21);
    }

    @Test
    public void checkInverse() {
        Alphabet a = new Alphabet();
        String cycle = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
        Permutation p = new Permutation(cycle, a);
        assertEquals(p.invert('A'), 'U');
        assertEquals(p.invert('U'), 'R');
        assertEquals(p.invert('S'), 'S');
        assertEquals(p.invert(0), 20);
        assertEquals(p.invert(20),  17);
        assertEquals(p.invert(18), 18);
    }

    @Test
    public void checkDerangement() {
        Alphabet a = new Alphabet();
        String cycle = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
        Permutation p = new Permutation(cycle, a);
        assertFalse(p.derangement());

        String c = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZS)";
        Permutation p2 = new Permutation(c, a);
        assertTrue(p2.derangement());

        String ca = "(ELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
        Permutation p3 = new Permutation(ca, a);
        assertFalse(p3.derangement());
    }

    @Test
    public void test1() {
        Alphabet a = new Alphabet("abcdefghij");
        Permutation p = new Permutation("(ade)(fc)(hig)", a);
        assertEquals(p.permute('a'), 'd');
        assertEquals(p.invert('a'), 'e');
        assertEquals(p.permute(3), 4);
        assertEquals(p.invert(0), 4);
    }

}
