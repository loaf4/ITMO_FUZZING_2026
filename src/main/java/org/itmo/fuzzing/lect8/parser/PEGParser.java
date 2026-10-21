package org.itmo.fuzzing.lect8.parser;

import org.itmo.fuzzing.lect2.instrumentation.Pair;
import org.itmo.fuzzing.lect6.grammar.BetterGrammar;
import org.itmo.fuzzing.lect6.grammar.Expansion;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;

import java.util.*;

public class PEGParser extends Parser{

    public PEGParser(BetterGrammar grammar, String startSymbol, boolean log, boolean coalesce, Set<String> tokens) {
        super(grammar, startSymbol, log, coalesce, tokens);
    }

    public PEGParser(BetterGrammar grammar) {
        super(grammar);
    }

    @Override
    public Pair<Integer, List<DerivationTreeNode>> parsePrefix(String text) throws Exception {
        // Call the unify_key method with the starting symbol and the text, starting at index 0
        Pair<Integer, DerivationTreeNode> result = unifyKey(startSymbol, text, 0);
        int cursor = result.getKey();  // Get the cursor from the result
        DerivationTreeNode tree = result.getValue();  // Get the parse tree from the result

        // Return the cursor and a list containing the parse tree
        return new Pair<>(cursor, Collections.singletonList(tree));
    }

    public Pair<Integer, DerivationTreeNode> unifyKey(String key, String text, int at) {
        if (log) {
            System.out.println("unify_key: " + key + " with " + text.substring(at));
        }

        // Check if the key is not in the grammar
        if (!grammar.rules.containsKey(key)) {
            // Check if the text starts with the key
            if (text.startsWith(key, at)) {
                return new Pair<>(at + key.length(), new DerivationTreeNode(key, new ArrayList<>()));
            } else {
                return new Pair<>(at, null);
            }
        }

        // Iterate through the rules associated with the key
        for (var rule : grammar.rules.get(key)) {
            Pair<Integer, List<DerivationTreeNode>> result = unifyRule(Collections.singletonList(rule.toString()), text, at);
            if (result.getValue() != null) {
                return new Pair<>(result.getKey(), new DerivationTreeNode(key, result.getValue().getFirst().getChildren()));
            }
        }
        return new Pair<>(0, null);
    }

    public Pair<Integer, List<DerivationTreeNode>> unifyRule(List<String> rule, String text, int at) {
        if (log) {
            System.out.println("unify_rule: " + rule + " with " + text.substring(at));
        }

        List<DerivationTreeNode> results = new ArrayList<>();
        for (String token : rule) {
            Pair<Integer, DerivationTreeNode> result = unifyKey(token, text, at);
            at = result.getKey(); // Update the position

            if (result.getValue() == null) {
                return new Pair<>(at, null); // Return if there was no match
            }
            results.add(result.getValue()); // Add the matched result to the list
        }
        return new Pair<>(at, results); // Return the updated position and results
    }
}
