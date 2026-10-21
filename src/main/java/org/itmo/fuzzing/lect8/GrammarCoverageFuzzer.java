package org.itmo.fuzzing.lect8;

import org.itmo.fuzzing.lect6.grammar.BetterGrammar;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrammarCoverageFuzzer extends SimpleGrammarCoverageFuzzer {
    public GrammarCoverageFuzzer(BetterGrammar grammar, String startSymbol, int minNonterminals, int maxNonterminals, boolean disp, boolean log) {
        super(grammar, startSymbol, minNonterminals, maxNonterminals, disp, log);
    }

    public GrammarCoverageFuzzer(BetterGrammar grammar) {
        super(grammar);
    }

    public GrammarCoverageFuzzer(BetterGrammar grammar, String startSymbol) {
        super(grammar, startSymbol);
    }

    public Set<String> newChildCoverage(String symbol, List<DerivationTreeNode> children, double maxDepth) {
        // Return new coverage that would be obtained by expanding (symbol, children)
        Set<String> newCov = _newChildCoverage(children, maxDepth);
        newCov.add(expansionKey(symbol, children));
        newCov.removeAll(expansionCoverage());  // -= is equivalent to removeAll in Java
        return newCov;
    }

    public Set<String> newChildCoverage(String symbol, List<DerivationTreeNode> children) {
        return newChildCoverage(symbol, children, Double.POSITIVE_INFINITY);
    }

    private Set<String> _newChildCoverage(List<DerivationTreeNode> children, double maxDepth) {
        Set<String> newCov = new HashSet<>();
        for (DerivationTreeNode child : children) {
            String childSymbol = child.getValue();  // Assuming getSymbol() returns the symbol of the child
            if (grammar.rules.containsKey(childSymbol)) {
                newCov.addAll(maxExpansionCoverage(childSymbol, maxDepth));
            }
        }
        return newCov;
    }

    public List<Set<String>> newCoverages(DerivationTreeNode node, List<List<DerivationTreeNode>> childrenAlternatives) {
        String symbol = node.getValue();

        for (int maxDepth = 0; maxDepth < grammar.rules.size(); maxDepth++) {
            List<Set<String>> newCoverages = new ArrayList<>();
            for (List<DerivationTreeNode> children : childrenAlternatives) {
                newCoverages.add(newChildCoverage(symbol, children, maxDepth));
            }

            int maxNewCoverage = newCoverages.stream()
                    .mapToInt(Set::size)
                    .max()
                    .orElse(0);

            if (maxNewCoverage > 0) {
                // Uncovered node found
                return newCoverages;
            }
        }

        // All covered
        return null;
    }

    @Override
    public int chooseNodeExpansionFromList(DerivationTreeNode node, List<List<DerivationTreeNode>> childrenAlternatives) {
        String symbol = node.getValue();
        List<Set<String>> newCoveragesOpt = newCoverages(node, childrenAlternatives);

        if (newCoveragesOpt == null) {
            // All expansions covered - use superclass method
            return chooseCoveredNodeExpansion(node, childrenAlternatives);
        }

        int maxNewCoverage = newCoveragesOpt.stream()
                .mapToInt(Set::size)
                .max()
                .orElse(0);

        List<List<DerivationTreeNode>> childrenWithMaxNewCoverage = new ArrayList<>();
        List<Integer> indexMap = new ArrayList<>();

        for (int i = 0; i < childrenAlternatives.size(); i++) {
            if (newCoveragesOpt.get(i).size() == maxNewCoverage) {
                childrenWithMaxNewCoverage.add(childrenAlternatives.get(i));
                indexMap.add(i);
            }
        }

        // Select a random expansion among the uncovered nodes with maximum coverage
        int newChildrenIndex = chooseUncoveredNodeExpansion(node, childrenWithMaxNewCoverage);
        List<DerivationTreeNode> newChildren = childrenWithMaxNewCoverage.get(newChildrenIndex);

        // Save the expansion as covered
        String key = expansionKey(symbol, newChildren);
//        System.out.println("Now covered: " + key);
        coveredExpansions.add(key);

        return indexMap.get(newChildrenIndex);
    }
}
