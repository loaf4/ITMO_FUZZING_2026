package org.itmo.fuzzing.lect9;


import java.util.List;
import java.util.Random;

public class DictMutator extends FuzzMutator {
    /** Mutate strings using keywords from a dictionary */

    private List<String> dictionary;
    private Random random;

    public DictMutator(List<String> dictionary) {
        /** Constructor. `dictionary` is the list of keywords to use. */
        super();
        this.dictionary = dictionary;
        this.random = new Random();
        this.mutators.add(this::insertFromDictionary);
    }

    public String insertFromDictionary(String s) {
        /** Returns s with a keyword from the dictionary inserted */
        int pos = random.nextInt(s.length() + 1); // random position in the string
        String randomKeyword = dictionary.get(random.nextInt(dictionary.size())); // choose a random keyword
        return s.substring(0, pos) + randomKeyword + s.substring(pos);
    }
}