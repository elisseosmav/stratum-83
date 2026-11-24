package org.stratum83.research;

import org.junit.jupiter.api.Test;
import org.stratum83.labyrinth.MazeSolver;
import static org.junit.jupiter.api.Assertions.*;

class MazeBenchmarkRunnerTest {

    private final MazeBenchmarkRunner runner = new MazeBenchmarkRunner();

    @Test
    void comparesAllThreeAlgorithmsOnOneMaze() {
        MazeBenchmarkResult result = runner.run(1983L, 1);

        assertEquals(1983L, result.firstSeed());
        assertEquals(1, result.mazeCount());
        assertEquals(3, result.algorithms().size());
        assertEquals(MazeSolver.Algorithm.BFS, result.algorithms().get(0).algorithm());
        assertEquals(MazeSolver.Algorithm.DIJKSTRA, result.algorithms().get(1).algorithm());
        assertEquals(MazeSolver.Algorithm.A_STAR, result.algorithms().get(2).algorithm());

        double steps = result.algorithms().get(0).averageSteps();
        assertTrue(steps > 0);
        for (MazeBenchmarkResult.AlgorithmResult algorithm : result.algorithms()) {
            assertEquals(steps, algorithm.averageSteps());
            assertTrue(algorithm.averageExplored() > 0);
        }
    }

    @Test
    void multipleMazesAreReproducible() {
        MazeBenchmarkResult first = runner.run(2026L, 12);
        MazeBenchmarkResult second = runner.run(2026L, 12);
        assertEquals(first, second);
    }

    @Test
    void rejectsOutOfRangeSampleCounts() {
        assertThrows(IllegalArgumentException.class, () -> runner.run(1983L, 0));
        assertThrows(IllegalArgumentException.class, () -> runner.run(1983L, 101));
    }

    @Test
    void resultIsNotExternallyMutable() {
        MazeBenchmarkResult result = runner.run(1983L, 1);
        assertThrows(UnsupportedOperationException.class, () -> result.algorithms().clear());
    }
}
