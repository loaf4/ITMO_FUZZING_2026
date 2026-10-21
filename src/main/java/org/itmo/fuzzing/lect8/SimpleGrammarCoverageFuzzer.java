package org.itmo.fuzzing.lect8;

import org.itmo.fuzzing.lect6.grammar.BetterGrammar;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;

import java.util.ArrayList;
import java.util.List;

public class SimpleGrammarCoverageFuzzer extends TrackingGrammarCoverageFuzzer{
    public SimpleGrammarCoverageFuzzer(BetterGrammar grammar, String startSymbol, int minNonterminals, int maxNonterminals, boolean disp, boolean log) {
        super(grammar, startSymbol, minNonterminals, maxNonterminals, disp, log);
    }

    public SimpleGrammarCoverageFuzzer(BetterGrammar grammar) {
        super(grammar);
    }

    public SimpleGrammarCoverageFuzzer(BetterGrammar grammar, String startSymbol) {
        super(grammar, startSymbol);
    }

    @Override
    public int chooseNodeExpansionFromList(DerivationTreeNode node, List<List<DerivationTreeNode>> childrenAlternatives) {
        // Prefer uncovered expansions
        String symbol = node.getValue();

        List<List<DerivationTreeNode>> uncoveredChildren = new ArrayList<>();
        List<Integer> indexMap = new ArrayList<>();

        for (int i = 0; i < childrenAlternatives.size(); i++) {
            List<DerivationTreeNode> alternative = childrenAlternatives.get(i);
            if (!coveredExpansions.contains(expansionKey(symbol, alternative))) {
                uncoveredChildren.add(alternative);
                indexMap.add(i);
            }
        }

        if (uncoveredChildren.isEmpty()) {
            // All expansions covered - use superclass method
            return chooseCoveredNodeExpansion(node, childrenAlternatives);
        }

        // Select from uncovered nodes
        int index = chooseUncoveredNodeExpansion(node, uncoveredChildren);
        return indexMap.get(index);
    }

    public int chooseUncoveredNodeExpansion(DerivationTreeNode node, List<List<DerivationTreeNode>> childrenAlternatives) {
        return super.chooseNodeExpansionFromList(node, childrenAlternatives);
    }

    public int chooseCoveredNodeExpansion(DerivationTreeNode node, List<List<DerivationTreeNode>> childrenAlternatives) {
        return super.chooseNodeExpansionFromList(node, childrenAlternatives);
    }

}
