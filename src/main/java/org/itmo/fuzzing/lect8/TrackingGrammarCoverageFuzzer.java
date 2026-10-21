package org.itmo.fuzzing.lect8;

import org.itmo.fuzzing.lect6.fuzzer.GrammarFuzzer;
import org.itmo.fuzzing.lect6.grammar.BetterGrammar;
import org.itmo.fuzzing.lect6.grammar.Expansion;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.itmo.fuzzing.lect6.ExprGrammar.START_SYMBOL;
import static org.itmo.fuzzing.lect6.GrammarUtils.nonterminals;

public class TrackingGrammarCoverageFuzzer extends GrammarFuzzer {

    protected final Set<String> coveredExpansions = new HashSet<>();
    protected Set<String> symbolsSeen = new HashSet<>();

    public TrackingGrammarCoverageFuzzer(BetterGrammar grammar, String startSymbol, int minNonterminals, int maxNonterminals, boolean disp, boolean log) {
        super(grammar, startSymbol, minNonterminals, maxNonterminals, disp, log);
    }

    public TrackingGrammarCoverageFuzzer(BetterGrammar grammar) {
        super(grammar);
    }

    public TrackingGrammarCoverageFuzzer(BetterGrammar grammar, String startSymbol) {
        super(grammar, startSymbol);
    }

    public void resetCoverage() {
        coveredExpansions.clear();
    }

    public Set<String> expansionCoverage() {
        return coveredExpansions;
    }

    public String expansionKey(
            String value,
            Expansion expansion
    ) {
        return value + " -> " + expansion.toString();
    }

    public String expansionKey(
            String value,
            DerivationTreeNode node
    ) {
        return value + " -> " + node.allTerminals();
    }

    public String expansionKey(
            String value,
            List<DerivationTreeNode> node
    ) {
        return value + " -> " + node.stream().map(DerivationTreeNode::allTerminals).collect(Collectors.joining());
    }


    private Set<String> maxExpansionCoverage1(String symbol, double maxDepth) {
        if (maxDepth <= 0) {
            return new LinkedHashSet<>();
        }

        symbolsSeen.add(symbol);

        Set<String> expansions = new LinkedHashSet<>();
        List<Expansion> grammarExpansions = grammar.rules.get(symbol);
        for (Expansion expansion : grammarExpansions) {
            expansions.add(expansionKey(symbol, expansion));
            for (String nonterminal : nonterminals(expansion.toString())) {
                if (!symbolsSeen.contains(nonterminal)) {
                    expansions.addAll(maxExpansionCoverage1(nonterminal, maxDepth - 1));
                }
            }
        }

        return expansions;
    }

    public Set<String> maxExpansionCoverage(String symbol, Double maxDepth) {
        if (symbol == null) {
            symbol = startSymbol;
        }

        symbolsSeen = new HashSet<>();
        Set<String> cov = maxExpansionCoverage1(symbol, maxDepth);

        if (START_SYMBOL.equals(symbol)) {
            assert symbolsSeen.size() == grammar.rules.size();
        }

        return cov;
    }

    public Set<String> maxExpansionCoverage() {
        return maxExpansionCoverage(null, Double.POSITIVE_INFINITY);
    }

    public void addCoverage(String symbol, Expansion newChild) {
        var key = expansionKey(symbol, newChild);
        coveredExpansions.add(key);
    }

    public void addCoverage(String symbol, List<DerivationTreeNode> newChild) {
        var key = expansionKey(symbol, newChild);
//        System.out.println("Now covered: " + key);
        coveredExpansions.add(key);
    }

    @Override
    public int chooseNodeExpansionFromList(DerivationTreeNode node, List<List<DerivationTreeNode>> children_alternatives) {
        var index = super.chooseNodeExpansionFromList(node, children_alternatives);
        addCoverage(node.getValue(), children_alternatives.get(index));
        return index;
    }

    public Set<String> missingExpansionCoverage() {
        var maxCoverage = maxExpansionCoverage();
        maxCoverage.removeAll(expansionCoverage());
        return maxCoverage;
    }
}
