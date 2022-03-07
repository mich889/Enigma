package enigma;

import java.util.HashMap;
import java.util.Map;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Michelle
 */

class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPH, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alph) {
        _alphabet = alph;

        for (int i = 0; i < cycles.length(); i++) {
            char c = cycles.charAt(i);
            if (!(alphabet().contains(c))) {
                if ((!(c == '(' || c == ')' || Character.isWhitespace(c)))) {
                    throw new EnigmaException("Not included in Alphabet");
                }
            }
        }

        int counter = 0;
        alphabetCode = new HashMap<Character, Integer>();
        cycleCode = new HashMap<Character, Character>();
        rCycleCode = new HashMap<Character, Character>();

        for (int i = 0; i < alphabet().size(); i++) {
            alphabetCode.put(alphabet().toChar(i), i);
        }

        for (int i = 0; i < cycles.length() - 1; i++) {
            char currChar = cycles.charAt(i);
            if (cycles.charAt(i + 1) == ')') {
                char newChar = cycles.charAt(cycles.indexOf('(', counter) + 1);
                cycleCode.put(currChar, newChar);
                counter = i;
            } else if (alphabetCode.containsKey(currChar)) {
                char tempChar = cycles.charAt(cycles.indexOf(currChar) + 1);
                cycleCode.put(currChar, tempChar);
            }
        }

        String rCycle = "";
        char ch;
        for (int i = 0; i < cycles.length(); i++) {
            ch = cycles.charAt(i);
            rCycle = ch + rCycle;
        }
        counter = 0;
        for (int i = 0; i < rCycle.length() - 1; i++) {
            char currChar = rCycle.charAt(i);
            if (rCycle.charAt(i + 1) == '(') {
                char newChar = rCycle.charAt(rCycle.indexOf(')', counter) + 1);
                rCycleCode.put(currChar, newChar);
                counter = i;
            } else if (alphabetCode.containsKey(currChar)) {
                char tempChar = rCycle.charAt(rCycle.indexOf(currChar) + 1);
                rCycleCode.put(currChar, tempChar);
            }
        }

        for (int i = 0; i < alphabet().size(); i++) {
            char a = alphabet().toChar(i);
            if (!(cycleCode.containsKey(a))) {
                cycleCode.put(a, a);
                rCycleCode.put(a, a);
            }
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return alphabet().size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char a = alphabet().toChar(p % alphabet().size());
        return alphabetCode.get(permute(a));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char a = alphabet().toChar(c % alphabet().size());
        return alphabetCode.get(invert(a));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return cycleCode.get(p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return rCycleCode.get(c);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (Map.Entry<Character, Character> entry : cycleCode.entrySet()) {
            if (entry.getKey() == entry.getValue()) {
                return false;
            }
        }
        return true;
    }


    /** @return ALPHABETCODE: the code of alphabet to int. **/
    HashMap<Character, Integer> getAlphabetCode() {
        return alphabetCode;
    }
    /** @return CYCLECODE: the permutation cycles. **/
    HashMap<Character, Character> getCycleCode() {
        return cycleCode;
    }
    /** @return RCYCLECODE: the permutation of reversed cycles. **/
    HashMap<Character, Character> getRCycleCode() {
        return rCycleCode;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** alphabet code. **/
    private HashMap<Character, Integer> alphabetCode;
    /** cycle code. **/
    private HashMap<Character, Character> cycleCode;
    /** reverse cycle code. **/
    private HashMap<Character, Character> rCycleCode;

}
