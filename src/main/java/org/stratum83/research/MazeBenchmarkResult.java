package org.stratum83.research;

import java.util.List;
import org.stratum83.labyrinth.MazeSolver;

/** Average pathfinding results for one or more reproducible mazes. */
public record MazeBenchmarkResult(long firstSeed, int mazeCount, List<AlgorithmResult> algorithms) {

    public MazeBenchmarkResult {
        algorithms = List.copyOf(algorithms);
    }

    public record AlgorithmResult(MazeSolver.Algorithm algorithm,
                                  double averageSteps,
                                  double averageExplored) {
    }
}
