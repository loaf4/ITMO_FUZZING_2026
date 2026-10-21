package org.itmo.fuzzing.lect9;

import org.itmo.fuzzing.lect6.grammar.BetterGrammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLGrammar {

    public static final String START_SYMBOL = "<start>";

    public static final LinkedHashMap<String, List<String>> HTML_GRAMMAR = new LinkedHashMap<>() {{
        put("<start>", Arrays.asList("<html-tree>"));

        put("<html-tree>", Arrays.asList(
                "<text>",
                "<html-open-tag><html-tree><html-close-tag>",
                "<html-openclose-tag>",
                "<html-tree><html-tree>"
        ));

        put("<html-open-tag>", Arrays.asList(
                "<<id>>",
                "<<id> <html-attribute>>"
        ));

        put("<html-openclose-tag>", Arrays.asList(
                "<<id>/>",
                "<<id> <html-attribute>/>"
        ));

        put("<html-close-tag>", Arrays.asList(
                "</<id>>"
        ));

        put("<html-attribute>", Arrays.asList(
                "<id>=<id>",
                "<html-attribute> <html-attribute>"
        ));

        put("<id>", Arrays.asList(
                "<letter>",
                "<id><letter>"
        ));

        put("<text>", Arrays.asList(
                "<text><letter_space>",
                "<letter_space>"
        ));

        put("<letter>", Arrays.asList(
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "\"", "'", "."
        ));

        put("<letter_space>", Arrays.asList(
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "\"", "'", " ", "\t"
        ));
    }};


    public static final Pattern RE_NONTERMINAL = Pattern.compile("(<[^<> ]*>)");

    public static List<String> NonTerminals(Object expansion) {
        String expansionStr;
        if (expansion instanceof String[]) {
            expansionStr = ((String[]) expansion)[0];
        } else {
            expansionStr = expansion.toString();
        }

        List<String> result = new ArrayList<>();
        Matcher matcher = RE_NONTERMINAL.matcher(expansionStr);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    public static boolean isNonTerminal(String s) {
        return RE_NONTERMINAL.matcher(s).matches();
    }

    public static BetterGrammar getBetterGrammar() {
        return new BetterGrammar(HTML_GRAMMAR);
    }
}
