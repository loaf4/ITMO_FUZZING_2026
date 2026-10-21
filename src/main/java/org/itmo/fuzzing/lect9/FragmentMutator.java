package org.itmo.fuzzing.lect9;

import org.antlr.v4.runtime.Parser;
import org.itmo.fuzzing.lect6.grammar.BetterGrammar;
import org.itmo.fuzzing.lect6.tree.DerivationTree;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.itmo.fuzzing.lect6.tree.GrammarUtils.isNonTerminal;

public class FragmentMutator extends FuzzMutator {
    /**
     * Mutate inputs with input fragments from a pool
     */

    public Parser parser;
    public Set<String> tokens;
    public Map<String, List<DerivationTreeNode>> fragments;
    public List<SeedWithStructure> seenSeeds = new ArrayList<>();
    public int toSwap = 0;
    public int toDelete = 0;
    /** Функция разбора строки в дерево вывода (по умолчанию — HTML, {@link FuzzParser#parse}). */
    public final Function<String, DerivationTreeNode> parse;

    /** HTML-вариант: разбор сидов через {@link FuzzParser#parse}. */
    public FragmentMutator(Parser parser, Set<String> tokens) {
        this(parser, tokens, FuzzParser::parse);
    }

    /**
     * @param parser ANTLR-парсер целевой грамматики (из него берутся имена правил для пула фрагментов)
     * @param tokens символы, фрагменты которых не собираются
     * @param parse  функция разбора строки в дерево вывода для этой грамматики
     *               (аналог {@link FuzzParser#parse}, например для JSON)
     */
    public FragmentMutator(Parser parser, Set<String> tokens, Function<String, DerivationTreeNode> parse) {
        super(); // Call to parent constructor if needed
        mutators.clear();
        this.parser = parser;
        this.tokens = tokens;
        this.parse = parse;
        this.fragments = new HashMap<>();

        for (var ruleName : parser.getRuleNames()) {
            fragments.put("<" + ruleName.toLowerCase() + ">", new ArrayList<>());
        }
    }

    public void addFragment(DerivationTreeNode fragment) {
        /** Recursively adds fragments to the fragment pool */
        String symbol = fragment.getValue(); // Assuming a method to get symbol
        List<DerivationTreeNode> children = fragment.getChildren(); // Assuming a method to get children
        if (!isExcluded(symbol)) {
            fragments.get(symbol).add(fragment);
            if (children != null) {
                for (DerivationTreeNode subfragment : children) {
                    addFragment(subfragment);
                }
            }
        }
    }

    public boolean isExcluded(String symbol) {
        /** Returns true if a fragment starting with a specific symbol
         and all its descendants can be excluded */
        if (!fragments.containsKey(symbol)) return true;
        if (tokens.contains(symbol)) return true;
        if (!isNonTerminal(symbol)) return true;
        return false;
    }

    public void addToFragmentPool(SeedWithStructure seed) {
        /** Adds all fragments of a seed to the fragment pool */
        try {
            var parsed = execTask(seed.getData());
            seed.setStructure(parsed); // Assuming a next method to get the result
            addFragment(seed.getStructure());
            seed.setHasStructure(true);
        } catch (Exception e) { // Assuming these exceptions are defined
            seed.setHasStructure(false);
        }
    }

    private DerivationTreeNode execTask(String data) throws ExecutionException, InterruptedException, TimeoutException {
        PrintStream originalOut = System.err;
        PrintStream nullOut = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
            }
        });
        System.setErr(nullOut);
        // daemon-поток и finally: иначе при ошибке разбора/таймауте executor не закрывается,
        // потоки накапливаются, JVM не завершается, а System.err остаётся заглушенным
        ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "fragment-parse");
            t.setDaemon(true);
            return t;
        });
        try {
            Future<DerivationTreeNode> future = executor.submit(() -> parse.apply(data));
            return future.get(200, TimeUnit.MILLISECONDS);
        } finally {
            System.setErr(originalOut);
            executor.shutdownNow();
        }
    }

    public SeedWithStructure mutate(SeedWithStructure seed) {
        if (!seenSeeds.contains(seed)) {
            seenSeeds.add(seed);
            addToFragmentPool(seed);
        }
        if (random.nextBoolean()) {
            return swapFragment(seed);
        } else {
            return deleteFragment(seed);
        }
    }

    private int countNodes(DerivationTreeNode fragment) {
        var symbol = fragment.getValue();
        var children = fragment.getChildren();
        if (isExcluded(symbol)) {
            return 0;
        }
        assert children != null;
        return 1 + children.stream().mapToInt(this::countNodes).sum();
    }

    public DerivationTreeNode recursiveSwap(DerivationTreeNode fragment) {
        // Recursively finds the fragment to swap
        String symbol = fragment.getValue();
        var children = fragment.getChildren();

        if (isExcluded(symbol)) {
            return new DerivationTreeNode(symbol, children);
        }

        this.toSwap--;
        if (this.toSwap == 0) {
            var fragmentList = fragments.get(symbol);
            return fragmentList.get(random.nextInt(fragmentList.size()));
        }

        assert children != null : "Children should not be null";
        var newChildren = children.stream()
                .map(this::recursiveSwap)
                .collect(Collectors.toList());
        return new DerivationTreeNode(symbol, newChildren);
    }

    public SeedWithStructure swapFragment(SeedWithStructure seed) {
        // Substitutes a random fragment with another with the same symbol
        if (seed.hasStructure()) {
            int nNodes = countNodes(seed.getStructure());
            this.toSwap = random.nextInt(nNodes - 1) + 2;
            var newStructure = recursiveSwap(seed.getStructure());

            SeedWithStructure newSeed = new SeedWithStructure(newStructure.allTerminals());
            newSeed.setHasStructure(true);
            newSeed.setStructure(newStructure);
            return newSeed;
        }
        return seed;
    }

    public DerivationTreeNode recursiveDelete(DerivationTreeNode fragment) {
        // Recursively finds the fragment to delete
        var symbol = fragment.getValue();
        var children = fragment.getChildren();

        if (isExcluded(symbol)) {
            return new DerivationTreeNode(symbol, children);
        }

        this.toDelete--;
        if (this.toDelete == 0) {
            return new DerivationTreeNode(symbol, List.of());
        }

        assert children != null : "Children should not be null";
        var newChildren = children.stream()
                .map(this::recursiveDelete)
                .collect(Collectors.toList());
        return new DerivationTreeNode(symbol, newChildren);
    }

    public SeedWithStructure deleteFragment(SeedWithStructure seed) {
        // Delete a random fragment
        if (seed.hasStructure()) {
            int nNodes = countNodes(seed.getStructure());
            this.toDelete = random.nextInt(nNodes - 1) + 2;
            DerivationTreeNode newStructure = recursiveDelete(seed.getStructure());

            SeedWithStructure newSeed = new SeedWithStructure(newStructure.allTerminals());
            newSeed.setHasStructure(true);
            newSeed.setStructure(newStructure);

            // Do not return an empty newSeed
            if (newSeed.getData().isEmpty()) {
                return seed;
            } else {
                return newSeed;
            }
        }
        return seed;
    }
}