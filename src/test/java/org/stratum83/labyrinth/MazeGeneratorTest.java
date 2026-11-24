package org.stratum83.labyrinth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MazeGeneratorTest {

    private final MazeGenerator generator = new MazeGenerator();

    @Test
    void sameSeedCreatesIdenticalMaze() {
        Maze first = generator.generate(11, 15, 1983);
        Maze second = generator.generate(11, 15, 1983);
        for (int row = 0; row < first.rows(); row++) {
            for (int col = 0; col < first.columns(); col++) {
                Position cell = new Position(row, col);
                assertEquals(first.isOpen(cell), second.isOpen(cell));
            }
        }
    }

    @Test
    void generatedMazeHasReachableGoal() {
        for (long seed = 0; seed < 30; seed++) {
            Maze maze = generator.generate(11, 15, seed);
            PathResult result = new MazeSolver().solve(maze, MazeSolver.Algorithm.BFS);
            assertTrue(result.found(), "Seed " + seed + " should have a route");
            assertEquals(maze.start(), result.path().getFirst());
            assertEquals(maze.goal(), result.path().getLast());
        }
    }

    @Test
    void rejectsEvenOrTooSmallDimensions() {
        assertThrows(IllegalArgumentException.class, () -> generator.generate(10, 15, 1));
        assertThrows(IllegalArgumentException.class, () -> generator.generate(11, 4, 1));
    }
}
