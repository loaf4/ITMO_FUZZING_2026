package org.itmo.fuzzing.lect8;

import org.itmo.fuzzing.lect6.ExprGrammar;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;

import java.util.List;

public class GrammarCoverageFuzzerTest {

    public static void main(String[] args) {
        var grammar = ExprGrammar.getBetterGrammar();
//        var fuzzer = new GrammarCoverageFuzzer(grammar, "<digit>");
//        var tmp = fuzzer.fuzz();
//        fuzzer.expansionCoverage().forEach(System.out::println);
//        System.out.println("---------");
//        var node1 = new DerivationTreeNode("0", List.of());
//        var newChildCov1 = fuzzer.newChildCoverage("<digit>", List.of(node1));
//        newChildCov1.forEach(System.out::println);
//        System.out.println("----------");
//        var node2 = new DerivationTreeNode(tmp, List.of());
//        var newChildCov2 = fuzzer.newChildCoverage("<digit>", List.of(node2));
//        if (newChildCov2.isEmpty()) {
//            System.out.println("{}");
//        } else {
//            newChildCov2.forEach(System.out::println);
//        }
//
        var fuzzer2 = new GrammarCoverageFuzzer(grammar);
        System.out.println(fuzzer2.fuzz());
        var missingCoverage = fuzzer2.missingExpansionCoverage();
        if (missingCoverage.isEmpty()) {
            System.out.println("[]");
        } else {
            missingCoverage.forEach(System.out::println);
        }

    }
}
