package org.itmo.fuzzing.lect3;

import java.util.List;
import java.util.Map;

public class DirectedSchedule extends PowerSchedule {
    private final Map<String, Integer> distances;

    public DirectedSchedule(Map<String, Integer> distances) {
        super();
        this.distances = distances;
    }

    @Override
    public void assignEnergy(List<Seed> population) {
        double minDist = Double.MAX_VALUE;
        double maxDist = 0;

        // First pass: distance of each seed = average distance of its covered methods.
        for (Seed seed : population) {
            if (seed.getDistance() < 0) {
                seed.setDistance(seedDistance(seed));
            }
            double d = seed.getDistance();
            if (d < 0) {
                continue; // no covered method can reach the target
            }
            minDist = Math.min(minDist, d);
            maxDist = Math.max(maxDist, d);
        }

        // Second pass: closer to minDist -> more energy.
        double range = maxDist - minDist;
        for (Seed seed : population) {
            double d = seed.getDistance();
            if (d < 0 || range <= 0) {
                seed.setEnergy(1.0);
            } else if (d == minDist) {
                seed.setEnergy(range);
            } else {
                // Cap at range so a seed slightly worse than the best never outweighs the best.
                seed.setEnergy(Math.min(range, range / (d - minDist)));
            }
        }
    }

    private double seedDistance(Seed seed) {
        double sumDist = 0;
        int numDist = 0;
        for (String function : seed.coverage.stream().map(Location::getFunction).distinct().toList()) {
            Integer d = distances.get(function);
            if (d != null) {
                sumDist += d;
                numDist++;
            }
        }
        return numDist > 0 ? sumDist / numDist : -1;
    }
}
