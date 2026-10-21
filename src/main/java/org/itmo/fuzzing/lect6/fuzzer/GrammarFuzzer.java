package org.itmo.fuzzing.lect6.fuzzer;

import org.itmo.fuzzing.lect6.GrammarUtils;
import org.itmo.fuzzing.lect6.grammar.BetterGrammar;
import org.itmo.fuzzing.lect6.grammar.Expansion;
import org.itmo.fuzzing.lect6.tree.DerivationTree;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;
import org.itmo.fuzzing.lect6.util.Triple;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static org.itmo.fuzzing.lect6.tree.GrammarUtils.RE_NONTERMINAL;
import static org.itmo.fuzzing.lect6.tree.GrammarUtils.isNonTerminal;

public class GrammarFuzzer {

    // Fields for the GrammarFuzzer
    protected final BetterGrammar grammar;
    protected final String startSymbol;
    protected final int minNonterminals;
    protected final int maxNonterminals;
    protected final boolean disp;
    protected final boolean log;
    protected final Random random = new Random();
    public Function<DerivationTreeNode, DerivationTreeNode> expandNodeMethod = this::expandNodeMinCost;

    public GrammarFuzzer(BetterGrammar grammar, String startSymbol, int minNonterminals, int maxNonterminals, boolean disp, boolean log) {
        this.grammar = grammar;
        this.startSymbol = startSymbol;
        this.minNonterminals = minNonterminals;
        this.maxNonterminals = maxNonterminals;
        this.disp = disp;
        this.log = log;
    }

    public GrammarFuzzer(BetterGrammar grammar) {
        this.grammar = grammar;
        this.startSymbol = "<start>";
        this.minNonterminals = 3;
        this.maxNonterminals = 10;
        this.disp = false;
        this.log = false;
    }

    public GrammarFuzzer(BetterGrammar grammar, String startSymbol) {
        this.grammar = grammar;
        this.startSymbol = startSymbol;
        this.minNonterminals = 3;
        this.maxNonterminals = 10;
        this.disp = false;
        this.log = false;
    }

    public DerivationTree initTree() {
        var root = new DerivationTreeNode(startSymbol, null);
        return new DerivationTree(root);
    }

    //    //It will be overloaded later
    public int chooseNodeExpansionFromList(DerivationTreeNode node, List<List<DerivationTreeNode>> childrenAlternatives) {
        return random.nextInt(childrenAlternatives.size());
    }

    public int chooseNodeExpansion(DerivationTreeNode node, List<DerivationTreeNode> childrenAlternatives) {
        return random.nextInt(childrenAlternatives.size());
    }


    // Method for expanding an expansion into children nodes
    public List<DerivationTreeNode> expansionToChildren(Expansion expansion) {
        // Convert the expansion to a string representation
        String exp = expansion.toString();
        assert exp != null;

        // Special case: epsilon expansion (empty string)
        if (exp.isEmpty()) {
            List<DerivationTreeNode> epsilonChild = new ArrayList<>();
            epsilonChild.add(new DerivationTreeNode("", new ArrayList<>()));
            return epsilonChild;
        }

        var strings = new ArrayList<String>();
        Matcher matcher = RE_NONTERMINAL.matcher(exp);

        int lastEnd = 0;

        // Iterate over all matches
        while (matcher.find()) {
            // Add the part before the match (non-terminal)
            if (lastEnd < matcher.start()) {
                strings.add(exp.substring(lastEnd, matcher.start()));
            }
            // Add the matched non-terminal
            strings.add(matcher.group());
            // Update the last end position
            lastEnd = matcher.end();
        }

        // Add the remaining part of the string after the last match
        if (lastEnd < exp.length()) {
            strings.add(exp.substring(lastEnd));
        }

        List<DerivationTreeNode> children = new ArrayList<>();
        for (String s : strings) {
            if (!s.isEmpty()) {
                // If it's a nonterminal, the child will have null children; otherwise, it's a terminal
                if (isNonTerminal(s)) {
                    children.add(new DerivationTreeNode(s, null));
                } else {
                    children.add(new DerivationTreeNode(s, new ArrayList<>()));
                }
            }
        }

        return children;
    }

    public DerivationTreeNode expandNodeRandomly(DerivationTreeNode node) {
        assert node.getChildren() == null;

       if (log) {
           System.out.println("Expanding " + node.allTerminals() + " randomly");
       }

        List<Expansion> expansions = grammar.rules.get(node.getValue());
        List<List<DerivationTreeNode>> childrenAlternatives = new ArrayList<>();
        for (Expansion expansion : expansions) {
            childrenAlternatives.add(expansionToChildren(expansion));
        }

        int index = chooseNodeExpansionFromList(node, childrenAlternatives);
        List<DerivationTreeNode> chosenChildren = childrenAlternatives.get(index);
        chosenChildren = processChosenChildren(chosenChildren, expansions.get(index));

        return new DerivationTreeNode(node.getValue(), chosenChildren);
    }

//    public DerivationTreeNode expandNode(DerivationTreeNode node) {
//        return expandNodeRandomly(node);
//    }

//    public DerivationTreeNode expandNode(DerivationTreeNode node) {
//        return expandNodeMinCost(node).getRoot();
//    }

    public DerivationTreeNode expandNode(DerivationTreeNode node) {
        return expandNodeMethod.apply(node);
    }

    public List<DerivationTreeNode> processChosenChildren(List<DerivationTreeNode> chosenChildren, Expansion expansion) {
        return chosenChildren;
    }


    public int possibleExpansions(DerivationTreeNode node) {
        List<DerivationTreeNode> children = node.getChildren();

        if (children == null) {
            return 1;
        }

        int sum = 0;
        for (DerivationTreeNode child : children) {
            sum += possibleExpansions(child);
        }

        return sum;
    }

    public boolean anyPossibleExpansions(DerivationTreeNode node) {
        List<DerivationTreeNode> children = node.getChildren();

        if (children == null) {
            return true;
        }

        for (DerivationTreeNode child : children) {
            if (anyPossibleExpansions(child)) {
                return true;
            }
        }

        return false;
    }

    public DerivationTreeNode expandTreeOnce(DerivationTreeNode node) {
        List<DerivationTreeNode> children = node.getChildren();

        if (children == null) {
            return expandNode(node);
        }

        List<DerivationTreeNode> expandableChildren = new ArrayList<>();
        List<Integer> indexMap = new ArrayList<>();

        for (int i = 0; i < children.size(); i++) {
            DerivationTreeNode child = children.get(i);
            if (anyPossibleExpansions(child)) {
                expandableChildren.add(child);
                indexMap.add(i);
            }
        }

        int childToBeExpanded = chooseNodeExpansion(node, expandableChildren);
        DerivationTreeNode expandedChild = expandTreeOnce(expandableChildren.get(childToBeExpanded));

        children.set(indexMap.get(childToBeExpanded), expandedChild);
        return node;
    }


    public int symbolCost(String symbol, Set<String> seen) {
        if (seen == null) {
            seen = new HashSet<>();
        }

        List<Expansion> expansions = grammar.rules.get(symbol);
        Set<String> finalSeen = seen;
        return expansions.stream()
                .mapToInt(e -> expansionCost(e, new HashSet<>(finalSeen) {{
                    add(symbol);
                }}))
                .min()
                .orElse(Short.MAX_VALUE);
    }

    public int expansionCost(Expansion expansion, Set<String> seen) {
        List<String> symbols = GrammarUtils.nonterminals(expansion.toString());

        if (symbols.isEmpty()) {
            return 1; // no symbol
        }

        if (symbols.stream().anyMatch(seen::contains)) {
            return Short.MAX_VALUE;
        }

        return symbols.stream()
                .mapToInt(s -> symbolCost(s, seen))
                .sum() + 1;
    }

    public DerivationTree expandNodeByCost(DerivationTreeNode node, Comparator<Integer> choose) {

        List<Expansion> expansions = grammar.rules.get(node.getValue());

        List<Triple<List<DerivationTreeNode>, Integer, Expansion>> childrenAlternativesWithCost = new ArrayList<>();
        for (Expansion expansion : expansions) {
            List<DerivationTreeNode> children = expansionToChildren(expansion);
            int cost = expansionCost(expansion, new HashSet<>() {{
                add(node.getValue());
            }});
            childrenAlternativesWithCost.add(new Triple<>(children, cost, expansion));
        }

        List<Integer> costs = childrenAlternativesWithCost.stream()
                .map(Triple::getSecond)
                .toList();
        int chosenCost = Collections.min(costs, choose);

        List<List<DerivationTreeNode>> childrenWithChosenCost = childrenAlternativesWithCost.stream()
                .filter(t -> t.getSecond() == chosenCost)
                .map(Triple::getFirst)
                .collect(Collectors.toList());

        List<Expansion> expansionWithChosenCost = childrenAlternativesWithCost.stream()
                .filter(t -> t.getSecond() == chosenCost)
                .map(Triple::getThird)
                .toList();

        int index = chooseNodeExpansionFromList(node, childrenWithChosenCost);

        List<DerivationTreeNode> chosenChildren = childrenWithChosenCost.get(index);
        Expansion chosenExpansion = expansionWithChosenCost.get(index);
        chosenChildren = processChosenChildren(chosenChildren, chosenExpansion);

        return new DerivationTree(new DerivationTreeNode(node.getValue(), chosenChildren));
    }

    public DerivationTreeNode expandNodeMinCost(DerivationTreeNode node) {
//        if (log) {
//            System.out.println("Expanding " + node.allTerminals() + " at min cost");
//        }
        return expandNodeByCost(node, Integer::compareTo).getRoot();
    }

    public DerivationTreeNode expandNodeMaxCost(DerivationTreeNode node) {
//        if (log) {
//        System.out.println("Expanding " + node.allTerminals() + " at max cost");
//        }
        return expandNodeByCost(node, (o1, o2) -> -o1.compareTo(o2)).getRoot();
    }


    public DerivationTree expandNodeWithStrategy(DerivationTreeNode node,
                                                 Function<DerivationTreeNode, DerivationTreeNode> expandNodeMethod,
                                                 Integer limit) {
        this.expandNodeMethod = expandNodeMethod;
        while ((limit == null || possibleExpansions(node) < limit) && anyPossibleExpansions(node)) {
            node = expandTreeOnce(node);
        }
        return new DerivationTree(node);
    }

    public DerivationTree expandTree(DerivationTree tree) {
        tree = expandNodeWithStrategy(tree.getRoot(), this::expandNodeMaxCost, minNonterminals);
        tree = expandNodeWithStrategy(tree.getRoot(), this::expandNodeRandomly, maxNonterminals);
        tree = expandNodeWithStrategy(tree.getRoot(), this::expandNodeMinCost, null);

        assert possibleExpansions(tree.getRoot()) == 0;

        return tree;
    }

    public String fuzz() {
        var tree = fuzzTree();
        return tree.allTerminals();
    }

    public DerivationTreeNode fuzzTree() {
        var tree = initTree();
        tree = expandTree(tree);
        return tree.getRoot();
    }

}
