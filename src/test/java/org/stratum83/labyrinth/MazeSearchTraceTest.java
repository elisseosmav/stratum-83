package org.stratum83.labyrinth;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MazeSearchTraceTest {

    private final MazeSolver solver = new MazeSolver();

    @Test
    void traceAndGameplayUseExactlyTheSameSearchForEveryAlgorithm() {
        Maze maze = new MazeGenerator().generate(11, 15, 1983L);
        for (MazeSolver.Algorithm algorithm : MazeSolver.Algorithm.values()) {
            MazeSearchTrace trace = solver.trace(maze, algorithm);
            PathResult ordinary = solver.solve(maze, algorithm);
            assertEquals(ordinary, trace.result());
            assertEquals(ordinary.exploredNodes(), trace.steps().size());
            assertTrue(ordinary.found());
            assertEquals(maze.start(), trace.steps().get(0).current());
            assertEquals(maze.goal(), trace.steps().get(trace.steps().size() - 1).current());
            for (int i = 0; i < trace.steps().size(); i++) {
                MazeSearchTrace.Step step = trace.steps().get(i);
                assertEquals(i + 1, step.explored().size());
                assertEquals(step.current(), step.explored().get(i));
                assertFalse(step.frontier().contains(step.current()));
                assertTrue(step.costSoFar() >= 0);
                assertTrue(step.explanation().contains("Expanded row"));
            }
            assertThrows(UnsupportedOperationException.class, () -> trace.steps().clear());
            assertThrows(UnsupportedOperationException.class,
                    () -> trace.steps().get(0).frontier().clear());
        }
    }

    @Test
    void tracesAreReproducibleForTheSameSeed() {
        Maze maze = new MazeGenerator().generate(11, 15, 1992L);
        for (MazeSolver.Algorithm algorithm : MazeSolver.Algorithm.values()) {
            assertEquals(solver.trace(maze, algorithm), solver.trace(maze, algorithm));
        }
    }

    @Test
    void allAlgorithmsAgreeOnOptimalDistanceAcrossManyMazes() {
        MazeGenerator generator = new MazeGenerator();
        for (long seed = 1983; seed < 2013; seed++) {
            Maze maze = generator.generate(11, 15, seed);
            int length = solver.solve(maze, MazeSolver.Algorithm.BFS).steps();
            assertEquals(length, solver.trace(maze, MazeSolver.Algorithm.DIJKSTRA).result().steps());
            assertEquals(length, solver.trace(maze, MazeSolver.Algorithm.A_STAR).result().steps());
        }
    }

    @Test
    void aStarHeuristicIsManhattanDistance() {
        Maze maze = Maze.fromAscii(
                "#####",
                "#S..#",
                "###.#",
                "#..G#",
                "#####");
        MazeSearchTrace trace = solver.trace(maze, MazeSolver.Algorithm.A_STAR);
        MazeSearchTrace.Step first = trace.steps().get(0);
        assertEquals(4, first.heuristic());
        assertEquals(first.costSoFar() + first.heuristic(), first.priority());
        assertEquals(0, solver.trace(maze, MazeSolver.Algorithm.DIJKSTRA)
                .steps().get(0).heuristic());
    }

    @Test
    void unreachableMazeReportsNoRouteAndAccurateVisitedCount() {
        Maze maze = Maze.fromAscii("#####", "#S#G#", "#####");
        for (MazeSolver.Algorithm algorithm : MazeSolver.Algorithm.values()) {
            MazeSearchTrace trace = solver.trace(maze, algorithm);
            assertFalse(trace.result().found());
            assertEquals(trace.result().exploredNodes(), trace.steps().size());
            assertEquals(1, trace.steps().size());
        }
    }

    @Test
    void snapshotsStayUnchangedAfterSubsequentOperations() {
        Maze maze = new MazeGenerator().generate(11, 15, 1983);
        MazeSearchTrace trace = solver.trace(maze, MazeSolver.Algorithm.BFS);
        List<Position> firstSnapshot = trace.steps().get(0).explored();
        solver.solve(maze, MazeSolver.Algorithm.A_STAR);
        assertEquals(List.of(maze.start()), firstSnapshot);
    }
}
