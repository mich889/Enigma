package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Michelle
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;
        _charslist = chars.toCharArray();
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (_charslist[i] == (_charslist[j]) && i != j) {
                    throw new EnigmaException("Repeated alphabet");
                }
            }
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _chars.contains("" + ch);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _charslist[index];
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        for (int i = 0; i < _charslist.length; i++) {
            if (_charslist[i] == ch) {
                return i;
            }
        }
        return -1;
    }
    /** charecters. **/
    private String _chars;
    /** charecter list. **/
    private char[] _charslist;
}
