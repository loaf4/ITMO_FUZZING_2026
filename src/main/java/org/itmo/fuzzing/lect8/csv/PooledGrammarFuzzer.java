package org.itmo.fuzzing.lect8.csv;

import org.itmo.fuzzing.lect6.fuzzer.GrammarFuzzer;
import org.itmo.fuzzing.lect6.grammar.BetterGrammar;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PooledGrammarFuzzer extends GrammarFuzzer {

    private Map<String, List<DerivationTreeNode>> nodeCache = new HashMap<>();

    public PooledGrammarFuzzer(BetterGrammar grammar, String startSymbol, int minNonterminals, int maxNonterminals, boolean disp, boolean log) {
        super(grammar, startSymbol, minNonterminals, maxNonterminals, disp, log);
    }

    public PooledGrammarFuzzer(BetterGrammar grammar) {
        super(grammar);
    }

    public PooledGrammarFuzzer(BetterGrammar grammar, String startSymbol) {
        super(grammar, startSymbol);
    }

    public void updateCache(String key, List<DerivationTreeNode> values) {
        nodeCache.put(key, values);
    }

    public DerivationTreeNode expandNodeRandomly(DerivationTreeNode node) {
        String symbol = node.getValue(); // Assuming you have a method to get the symbol
        List<DerivationTreeNode> children = node.getChildren(); // Assuming you have a method to get the children

        assert children == null;

        if (nodeCache.containsKey(symbol)) {
            if (new Random().nextInt(2) == 1) {
                return super.expandNodeRandomly(node);
            }
            return randomChoice(nodeCache.get(symbol)).deepCopy();
        }
        return super.expandNodeRandomly(node);
    }

    private <T> T randomChoice(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }
}
