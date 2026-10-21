package org.itmo.fuzzing.lect9;

import org.antlr.v4.runtime.tree.ParseTree;
import org.itmo.fuzzing.lect2.FunctionRunner;
import org.itmo.fuzzing.lect3.AdvancedMutationFuzzer;
import org.itmo.fuzzing.lect3.PowerSchedule;
import org.itmo.fuzzing.lect3.Seed;
import org.itmo.fuzzing.util.SetUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class GreyBoxGrammarFuzzer extends AdvancedMutationFuzzer {

    /** protected: наследники из других пакетов (lab2) переопределяют run() и пополняют пул фрагментов. */
    protected FragmentMutator fragmentMutator;
    Random random = new Random();

    public GreyBoxGrammarFuzzer(
            List<String> seeds,
            FragmentMutator fragmentMutator,
            FuzzMutator mutator,
            PowerSchedule schedule,
            int minMutations,
            int maxMutations
    ) {
        super(seeds, mutator, schedule, minMutations, maxMutations);
        this.fragmentMutator = fragmentMutator;
    }

    @Override
    public void reset() {
        super.reset();
        coveragesSeen = new HashSet<>();
        population = new ArrayList<>();
    }

    @Override
    public String createCandidate() {
        if (population.isEmpty()) {
            throw new IllegalStateException(
                    "Популяция пуста: ни один сид не дал нового покрытия с разобранной структурой. "
                            + "Запускайте через runWithAgent (./gradlew runWithAgent -PmainClass=...), "
                            + "проверьте, что пакет цели есть в includes агента покрытия (CoverageAgent), "
                            + "и что начальные сиды разбираются парсером FragmentMutator.");
        }
        var seed = (SeedWithStructure) schedule.choose(population);
        var trials = (new Random()).nextInt(5);
        for (int i = 0; i < trials; i++) {
            seed = fragmentMutator.mutate((SeedWithStructure) seed);
        }

        var candidate = seed.getData();
        if (trials == 0 || !seed.hasStructure() || random.nextInt(2) == 1) {
            int dumbTrials = Math.min(seed.getData().length(), 1 << random.nextInt(5) + 1);
            for (int i = 0; i < dumbTrials; i++) {
                candidate = this.mutator.mutate(candidate);
            }
        }

        return candidate;
    }

    @Override
    public Object run(FunctionRunner runner, String input) {
        FunctionRunner.Tuple<Object, String> resultOutcome = runner.run(input);
        var result = resultOutcome.first;
        if (!SetUtils.diff(runner.coverage, coveragesSeen).isEmpty()) {
            System.out.println("NEW COVERAGE");
            var newSeed = new SeedWithStructure(input);
            fragmentMutator.addToFragmentPool(newSeed);
            if (newSeed.hasStructure()) {
                population.add(newSeed);
            }
            coveragesSeen.addAll(runner.coverage);
            System.out.println("INPUT = " + input);
        }
        return result;
    }
}
