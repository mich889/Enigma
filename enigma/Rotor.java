package enigma;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Michelle
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _setting = alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int pNew = _permutation.wrap(p + _setting - ring);
        char a = alphabet().toChar(pNew);
        char b = _permutation.getCycleCode().get(a);
        int temp = _permutation.getAlphabetCode().get(b) - _setting + ring;
        return _permutation.wrap(temp);
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int eNew = _permutation.wrap(e + _setting - ring);
        char a = alphabet().toChar(eNew);
        char b = _permutation.getRCycleCode().get(a);
        int temp = _permutation.getAlphabetCode().get(b) - _setting + ring;
        return _permutation.wrap(temp);
    }

    /** Returns the positions of the notches, as a string giving the letters
     *  on the ring at which they occur. */
    String notches() {
        return _notches;
    }

    void setNotch(String a) {
        _notches = a;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            if (_setting == alphabet().toInt(_notches.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    void setRing(int i) {
        ring = i;
    }


    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;
    /** setting of rotor. */
    private int _setting;
    /** notch of rotor. */
    private String _notches;
    /** ring setting in terms of integers. */
    private int ring;

}
