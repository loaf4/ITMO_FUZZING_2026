package org.itmo.fuzzing.lect8;

import org.itmo.fuzzing.lect6.ExprGrammar;

import static org.itmo.fuzzing.lect6.fuzzer.GrammarFuzzerTest.createSimpleDerivationTree;

public class TrackingGrammarCoverageFuzzerTest {

    public static void main(String[] args) {
        var grammar = ExprGrammar.getBetterGrammar();
        var fuzzer = new TrackingGrammarCoverageFuzzer(grammar);
//        var res = fuzzer.expansionKey("<start>", grammar.rules.get("<start>").get(0));
//        System.out.println(res);
//        var tree = createSimpleDerivationTree();
//        var res2 = fuzzer.expansionKey("<expr>", tree.getRoot().getChildren());
//        System.out.println(res2);
//        System.out.println("-----------");
//        fuzzer.maxExpansionCoverage(null, (double) Integer.MAX_VALUE).stream().sorted(String::compareToIgnoreCase).forEach(System.out::println);
//
//
        var digitFuzzer = new TrackingGrammarCoverageFuzzer(grammar, "<digit>");
        var digitFuzzerFuzz1 = digitFuzzer.fuzz();
        var digitFuzzerFuzz2 = digitFuzzer.fuzz();
        var digitFuzzerFuzz3 = digitFuzzer.fuzz();
        digitFuzzer.expansionCoverage().forEach(System.out::println);
        System.out.println("---------");
        digitFuzzer.maxExpansionCoverage().forEach(System.out::println);
        System.out.println("---------");
        digitFuzzer.missingExpansionCoverage().forEach(System.out::println);
    }
}
