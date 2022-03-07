package enigma;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/** Class that represents a complete enigma machine.
 *  @author Michelle
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotorsH = new HashMap<String, Rotor>();
        movingRotors = new Rotor[pawls];
        Iterator<Rotor> a = allRotors.iterator();
        for (int i = 0; i < allRotors.size(); i++) {
            Rotor b = a.next();
            _allRotorsH.put(b.name(), b);
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return rotorsInUse[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        rotorsInUse = new Rotor[rotors.length];
        int counter = 0;
        if (!(_allRotorsH.get(rotors[0]).reflecting())) {
            throw new EnigmaException("Reflector is not first");
        }
        for (int i = 0; i < rotors.length; i++) {
            if (!(_allRotorsH.containsKey(rotors[i]))) {
                throw new EnigmaException("Rotor not in config file");
            }
            rotorsInUse[i] = _allRotorsH.get(rotors[i]);
            if (rotorsInUse[i].rotates()) {
                if (counter == _pawls) {
                    throw new EnigmaException("wrong number of moving rotors");
                }
                movingRotors[counter] = _allRotorsH.get(rotors[i]);
                counter += 1;
            }
        }
        if (counter != _pawls) {
            throw new EnigmaException("Wrong number of moving rotors");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        int c = 0;
        for (int i = 1; i < rotorsInUse.length; i++) {
            rotorsInUse[i].set(setting.charAt(c));
            c += 1;
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        if (_plugboard != null) {
            return _plugboard;
        }
        return new Permutation("", alphabet());
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        boolean [] b = new boolean[movingRotors.length];
        for (int i = movingRotors.length - 1; i > 0; i--) {
            if (movingRotors[i].atNotch()) {
                b[i - 1] = true;
                b[i] = true;
            }
        }
        b[movingRotors.length - 1] = true;
        for (int i = 0; i < b.length; i++) {
            if (b[i]) {
                movingRotors[i].advance();
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int d = c;
        for (int i = rotorsInUse.length - 1; i >= 0; i--) {
            d = rotorsInUse[i].convertForward(d);
        }
        for (int i = 1; i < rotorsInUse.length; i++) {
            d = rotorsInUse[i].convertBackward(d);
        }
        return d;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String[] words = msg.split(" ");
        String[] codedWord = new String [words.length];
        for (int j = 0; j < words.length; j++) {
            String r = "";
            for (int i = 0; i < words[j].length(); i++) {
                int a = alphabet().toInt(words[j].charAt(i));
                r += alphabet().toChar(convert(a));
            }
            codedWord[j] = r;
        }
        String f = "";
        for (int i = 0; i < codedWord.length; i++) {
            if (i == codedWord.length - 1) {
                f += codedWord[i];
            } else {
                f += codedWord[i] + " ";
            }
        }
        return f;
    }


    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Number of rotors. */
    private final int _numRotors;
    /** Number of pawls. */
    private final int _pawls;
    /** All rotors in machine. */
    private HashMap<String, Rotor> _allRotorsH;
    /** plugboard permutation. */
    private Permutation _plugboard;
    /** Array of rotors in use. */
    private Rotor[] rotorsInUse;
    /** Array of moving rotors. */
    private Rotor[] movingRotors;
}
