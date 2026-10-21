package org.itmo.fuzzing.lect8;

import org.itmo.fuzzing.lect6.ExprGrammar;

public class SimpleGrammarCoverageFuzzerTest {

    public static void main(String[] args) {
        var grammar = ExprGrammar.getBetterGrammar();
        var fuzzer = new SimpleGrammarCoverageFuzzer(grammar, "<digit>");
        for (int i = 0; i < 10; i++) {
            fuzzer.fuzz();
        }
        fuzzer.missingExpansionCoverage().forEach(System.out::println);
    }
}
