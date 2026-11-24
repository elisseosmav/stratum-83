package org.stratum83.research;

import java.util.ArrayList;
import java.util.List;
import org.stratum83.labyrinth.Maze;
import org.stratum83.labyrinth.MazeGenerator;
import org.stratum83.labyrinth.MazeSolver;
import org.stratum83.labyrinth.PathResult;

/** Compares shortest-path algorithms on the same set of generated mazes. */
public class MazeBenchmarkRunner {

    public MazeBenchmarkResult run(long firstSeed, int mazeCount) {
        if (mazeCount < 1 || mazeCount > 100) {
            throw new IllegalArgumentException("Maze count must be between 1 and 100");
        }

        MazeGenerator generator = new MazeGenerator();
        MazeSolver solver = new MazeSolver();
        MazeSolver.Algorithm[] algorithms = MazeSolver.Algorithm.values();
        long[] totalSteps = new long[algorithms.length];
        long[] totalExplored = new long[algorithms.length];

        for (int index = 0; index < mazeCount; index++) {
            Maze maze = generator.generate(11, 15, firstSeed + index);
            int shortestSteps = -1;

            for (int i = 0; i < algorithms.length; i++) {
                PathResult path = solver.solve(maze, algorithms[i]);
                if (!path.found()) {
                    throw new IllegalStateException("Generated maze has no route to the goal");
                }
                if (shortestSteps >= 0 && path.steps() != shortestSteps) {
                    throw new IllegalStateException("Solvers disagree about shortest path");
                }
                shortestSteps = path.steps();
                totalSteps[i] += path.steps();
                totalExplored[i] += path.exploredNodes();
            }
        }

        List<MazeBenchmarkResult.AlgorithmResult> results = new ArrayList<>();
        for (int i = 0; i < algorithms.length; i++) {
            results.add(new MazeBenchmarkResult.AlgorithmResult(
                    algorithms[i],
                    (double) totalSteps[i] / mazeCount,
                    (double) totalExplored[i] / mazeCount));
        }
        return new MazeBenchmarkResult(firstSeed, mazeCount, results);
    }
}
