package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

public class AlphabetTest {
    @Test
    public void testContains() {
        Alphabet a = new Alphabet("abcde");
        assertTrue(a.contains('a'));
        assertFalse(a.contains('('));
    }

    @Test
    public void toInt() {
        Alphabet a = new Alphabet("abcde");
        assertEquals(a.toInt('e'), 4);
    }
}
