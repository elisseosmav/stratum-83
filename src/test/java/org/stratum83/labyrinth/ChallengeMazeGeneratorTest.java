package org.stratum83.labyrinth;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChallengeMazeGeneratorTest {

    private final ChallengeMazeGenerator generator = new ChallengeMazeGenerator();
    private final MazeSolver solver = new MazeSolver();

    @Test
    void seedProducesReproducibleConnectedLargeMaze() {
        Maze a = generator.generate(1983L);
        Maze b = generator.generate(1983L);
        assertEquals(23, a.rows());
        assertEquals(31, a.columns());
        assertEquals(a.start(), b.start());
        assertEquals(a.goal(), b.goal());
        for (int r = 0; r < a.rows(); r++) {
            for (int c = 0; c < a.columns(); c++) {
                Position p = new Position(r, c);
                assertEquals(a.isOpen(p), b.isOpen(p));
            }
        }
        PathResult solution = solver.solve(a, MazeSolver.Algorithm.A_STAR);
        assertTrue(solution.found());
        assertTrue(solution.steps() >= 20, "The exit should not be trivial");
        assertEquals(solver.solve(a, MazeSolver.Algorithm.BFS).steps(), solution.steps());
    }

    @Test
    void moreThanOneRouteIsPossibleDueToExtraPassages() {
        Maze maze = generator.generate(1983L);
        int open = 0;
        int directedEdges = 0;
        for (int r = 0; r < maze.rows(); r++) {
            for (int c = 0; c < maze.columns(); c++) {
                Position p = new Position(r, c);
                if (maze.isOpen(p)) {
                    open++;
                    directedEdges += maze.neighbors(p).size();
                }
            }
        }
        assertTrue(directedEdges / 2 > open - 1,
                "The connected maze should have loops and alternative routes");
    }

    @Test
    void severalSeedsRemainSolvableAndHaveDistantExits() {
        for (long seed = 1983L; seed < 2003L; seed++) {
            Maze maze = generator.generate(seed);
            PathResult computer = solver.solve(maze, MazeSolver.Algorithm.A_STAR);
            assertTrue(computer.found(), "Seed " + seed);
            assertTrue(computer.steps() >= 20, "Seed " + seed);
            assertEquals(computer.steps(),
                    solver.solve(maze, MazeSolver.Algorithm.BFS).steps());
        }
    }
}
