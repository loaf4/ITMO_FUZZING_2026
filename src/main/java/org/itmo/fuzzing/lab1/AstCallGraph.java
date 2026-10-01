package org.itmo.fuzzing.lab1;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Построение call graph по AST исходного кода (альтернатива сбору рёбер в ASM-инструментации).
 *
 * <p>Учитываются только вызовы методов, объявленных в этом же файле: для {@link MazeGenerated}
 * все вызовы статические, внутри одного класса и имена методов уникальны, поэтому имени вызова
 * достаточно, чтобы однозначно определить вызываемый метод.</p>
 */
public final class AstCallGraph {

    public static final Path MAZE_SOURCE = Path.of("src/main/java/org/itmo/fuzzing/lab1/MazeGenerated.java");

    private AstCallGraph() {
    }

    /**
     * Строит граф вызовов: caller -> множество callee.
     */
    public static Map<String, Set<String>> build(Path source) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(source);

        Set<String> declared = new TreeSet<>();
        for (MethodDeclaration m : cu.findAll(MethodDeclaration.class)) {
            declared.add(m.getNameAsString());
        }

        Map<String, Set<String>> graph = new TreeMap<>();
        for (MethodDeclaration m : cu.findAll(MethodDeclaration.class)) {
            String caller = m.getNameAsString();
            Set<String> callees = graph.computeIfAbsent(caller, k -> new TreeSet<>());
            for (MethodCallExpr call : m.findAll(MethodCallExpr.class)) {
                String callee = call.getNameAsString();
                if (declared.contains(callee)) {
                    callees.add(callee);
                }
            }
        }
        return graph;
    }

    /**
     * Кратчайшие расстояния (в рёбрах) от каждого метода до {@code target}: BFS от цели по
     * обращённым рёбрам. Методы, из которых цель недостижима, в результат не попадают.
     */
    public static Map<String, Integer> distancesTo(Map<String, Set<String>> graph, String target) {
        Map<String, Set<String>> reversed = new HashMap<>();
        for (Map.Entry<String, Set<String>> e : graph.entrySet()) {
            for (String callee : e.getValue()) {
                reversed.computeIfAbsent(callee, k -> new TreeSet<>()).add(e.getKey());
            }
        }

        Map<String, Integer> dist = new HashMap<>();
        Deque<String> queue = new ArrayDeque<>();
        dist.put(target, 0);
        queue.add(target);
        while (!queue.isEmpty()) {
            String cur = queue.poll();
            for (String caller : reversed.getOrDefault(cur, Set.of())) {
                if (!dist.containsKey(caller)) {
                    dist.put(caller, dist.get(cur) + 1);
                    queue.add(caller);
                }
            }
        }
        return dist;
    }

    public static void main(String[] args) throws IOException {
        Map<String, Set<String>> graph = build(MAZE_SOURCE);
        graph.forEach((caller, callees) -> {
            if (!callees.isEmpty()) {
                System.out.println(caller + " -> " + callees);
            }
        });

        Map<String, Integer> dist = distancesTo(graph, MazeGenerated.targetTile());
        System.out.println();
        System.out.println("Distances to " + MazeGenerated.targetTile() + ":");
        new TreeMap<>(dist).forEach((m, d) -> System.out.println(m + " = " + d));
    }
}
