package org.itmo.fuzzing.lect8.csv;

import org.itmo.fuzzing.lect6.grammar.BetterGrammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvGrammar {

    public static final String START_SYMBOL = "<start>";

    public static final LinkedHashMap<String, List<String>> CSV_GRAMMAR = new LinkedHashMap<>() {{
        put("<start>", Arrays.asList("<csvline>"));

        put("<csvline>", Arrays.asList("<items>"));

        put("<items>", Arrays.asList("<item>,<items>", "<item>"));

        put("<item>", Arrays.asList("<letters>"));

        put("<letters>", Arrays.asList("<letter><letters>", "<letter>"));

        put("<letter>", Arrays.asList(
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                ".", ",", "!", "?", ":", ";", "-", "_", "(", ")", "[", "]", "{", "}", "'", "\"", "@", "#", "$", "%", "&", "*", "+"
        ));
    }};

    public static final Pattern RE_NONTERMINAL = Pattern.compile("(<[^<> ]*>)");

    public static BetterGrammar getBetterGrammar() {
        return new BetterGrammar(CSV_GRAMMAR);
    }
}
