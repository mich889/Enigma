package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.NoSuchElementException;



import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Michelle
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                    new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                        + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
     *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        while (_input.hasNext("\\*")) {
            String a = _input.nextLine();
            if (a.equals("")) {
                _output.println();
                continue;
            }
            setUp(m, a);
            while (!(_input.hasNext("\\*")) && _input.hasNext()) {
                String msg = _input.nextLine();
                String newMsg = m.convert(msg);
                printMessageLine(newMsg);
                _output.println();
            }
        }
        if (_input.hasNext()) {
            throw new EnigmaException("wrong number of arguments");
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            if (_config.hasNext("\\d")) {
                throw new EnigmaException("no alphabet set");
            }
            _alphabet = new Alphabet(_config.next());
            if (!(_config.hasNext("\\d"))) {
                throw new EnigmaException("no rotor input set");
            }
            int numRotors = Integer.parseInt(_config.next());
            int numPawls = Integer.parseInt(_config.next());
            Collection<Rotor> allRotors = new ArrayList<Rotor>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String temp = _config.next();
            String rotorType = String.valueOf(temp.charAt(0));
            String notches = temp.substring(1);
            String cycles = "";
            String pattern = "(\\([^\\(\\)\\*]*\\))+";
            while (_config.hasNext(pattern)) {
                cycles += _config.next(pattern);
            }
            if (rotorType.equals("M")) {
                Permutation p = new Permutation(cycles, _alphabet);
                return new MovingRotor(name, p, notches);
            } else if (rotorType.equals("N")) {
                return new FixedRotor(name, new Permutation(cycles, _alphabet));
            } else if (rotorType.equals("R")) {
                return new Reflector(name, new Permutation(cycles, _alphabet));
            } else {
                throw new EnigmaException("bad rotor description");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        if (settings.charAt(0) != '*') {
            throw new EnigmaException("wrong input formatting");
        }
        Scanner s = new Scanner(settings);
        if (!(s.next().equals("*"))) {
            throw new EnigmaException("wrong input formatting");
        }
        String[] inputRotors = new String[M.numRotors()];
        for (int i = 0; i < inputRotors.length; i++) {
            if (s.hasNext()) {
                inputRotors[i] = s.next();
            } else {
                throw new EnigmaException("wrong number of rotors in file");
            }
        }
        M.insertRotors(inputRotors);
        String rotorSettings = s.next();
        for (int i = 1; i < M.numRotors(); i++) {
            M.getRotor(i).set(rotorSettings.charAt(i - 1));
        }
        if (s.hasNext("[^\\(\\)]*")) {
            ringSettings = s.next();
            for (int i = 1; i < M.numRotors(); i++) {
                int charInt = _alphabet.toInt(ringSettings.charAt(i - 1));
                M.getRotor(i).setRing(charInt);
            }
        }
        String plugboard = "";
        while (s.hasNext()) {
            plugboard += s.next();
        }
        M.setPlugboard(new Permutation(plugboard, _alphabet));
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String newMsg = msg.replaceAll("\\s", "");
        for (int i = 0; i < newMsg.length(); i += 5) {
            if (newMsg.length() <= i + 5) {
                _output.print(newMsg.substring(i));
            } else {
                int min = Math.min(newMsg.length(), i + 5);
                String n = newMsg.substring(i, min);
                _output.print(n + " ");
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** String of ring settings. */
    private String ringSettings;
}
