package org.itmo.fuzzing.lect8.parser;

import org.itmo.fuzzing.lect2.instrumentation.Pair;
import org.itmo.fuzzing.lect6.grammar.BetterGrammar;
import org.itmo.fuzzing.lect6.tree.DerivationTree;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;

import java.util.*;

public abstract class Parser {
    protected final BetterGrammar grammar;
    protected String startSymbol;
    protected final boolean log;
    protected final boolean coalesceTokens;
    protected final Set<String> tokens;

    public Parser(BetterGrammar grammar, String startSymbol, boolean log, boolean coalesce, Set<String> tokens) {
        this.grammar = grammar;
        this.startSymbol = startSymbol != null ? startSymbol : "<start>";
        this.log = log;
        this.coalesceTokens = coalesce;
        this.tokens = tokens != null ? tokens : new HashSet<>();
    }

    public Parser(BetterGrammar grammar) {
        this.grammar = grammar;
        this.startSymbol = "<start>";
        this.log = true;
        this.coalesceTokens = true;
        this.tokens = new HashSet<>();
    }

    public BetterGrammar getGrammar() {
        return grammar;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public abstract Pair<Integer, List<DerivationTreeNode>> parsePrefix(String text) throws Exception;

    public List<DerivationTreeNode> parse(String text) throws Exception {
        Pair<Integer, List<DerivationTreeNode>> result = parsePrefix(text);
        int cursor = result.getKey();
        List<DerivationTreeNode> forest = result.getValue();

        if (cursor < text.length()) {
            throw new Exception("at " + text.substring(cursor));
        }

        List<DerivationTreeNode> prunedTrees = new ArrayList<>();
        for (DerivationTreeNode tree : forest) {
            prunedTrees.add(pruneTree(tree));
        }
        return prunedTrees;
    }

    public Iterable<DerivationTreeNode> parseOn(String text, String startSymbol) throws Exception {
        String oldStart = this.startSymbol;
        this.startSymbol = startSymbol;

        try {
            return parse(text);
        } finally {
            this.startSymbol = oldStart;
        }
    }

    public List<DerivationTreeNode> coalesce(List<DerivationTreeNode> children) {
        StringBuilder last = new StringBuilder();
        List<DerivationTreeNode> newList = new ArrayList<>();

        for (DerivationTreeNode child : children) {
            String cn = child.getValue();
            List<DerivationTreeNode> cc = child.getChildren();

            if (!grammar.rules.containsKey(cn)) {
                last.append(cn);
            } else {
                if (last.length() > 0) {
                    newList.add(new DerivationTreeNode(last.toString(), new ArrayList<>()));
                    last.setLength(0); // Clear the last StringBuilder
                }
                newList.add(new DerivationTreeNode(cn, cc));
            }
        }

        if (last.length() > 0) {
            newList.add(new DerivationTreeNode(last.toString(), new ArrayList<>()));
        }

        return newList;
    }

    public DerivationTreeNode pruneTree(DerivationTreeNode tree) {
        String name = tree.getValue();
        List<DerivationTreeNode> children = tree.getChildren();

        if (coalesceTokens) {
            children = coalesce(children);
        }

        if (tokens.contains(name)) {
            return new DerivationTreeNode(name, Collections.singletonList(new DerivationTreeNode(treeToString(tree), Collections.emptyList())));
        } else {
            List<DerivationTreeNode> prunedChildren = new ArrayList<>();
            for (DerivationTreeNode child : children) {
                prunedChildren.add(pruneTree(child));
            }
            return new DerivationTreeNode(name, prunedChildren);
        }
    }

    protected String treeToString(DerivationTreeNode tree) {
        return new DerivationTree(tree).treeToString();
    }
}


