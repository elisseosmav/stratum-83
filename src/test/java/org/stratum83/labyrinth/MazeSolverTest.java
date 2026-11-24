package org.stratum83.labyrinth;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MazeSolverTest {

    private final MazeSolver solver = new MazeSolver();

    @Test
    void allAlgorithmsFindShortestPath() {
        Maze maze = Maze.fromAscii(
                "#####",
                "#S..#",
                "###.#",
                "#G..#",
                "#####");
        for (MazeSolver.Algorithm algorithm : MazeSolver.Algorithm.values()) {
            PathResult result = solver.solve(maze, algorithm);
            assertEquals(6, result.steps(), algorithm.name());
            assertEquals(maze.start(), result.path().getFirst());
            assertEquals(maze.goal(), result.path().getLast());
            assertTrue(result.exploredNodes() >= result.path().size());
        }
    }

    @Test
    void allAlgorithmsAgreeOnGeneratedMazes() {
        MazeGenerator generator = new MazeGenerator();
        for (long seed = 0; seed < 25; seed++) {
            Maze maze = generator.generate(11, 15, seed);
            int expected = solver.solve(maze, MazeSolver.Algorithm.BFS).steps();
            assertEquals(expected, solver.solve(maze, MazeSolver.Algorithm.DIJKSTRA).steps());
            assertEquals(expected, solver.solve(maze, MazeSolver.Algorithm.A_STAR).steps());
        }
    }

    @Test
    void unreachableGoalProducesEmptyPath() {
        Maze maze = Maze.fromAscii("S#G");
        for (MazeSolver.Algorithm algorithm : MazeSolver.Algorithm.values()) {
            PathResult result = solver.solve(maze, algorithm);
            assertFalse(result.found());
            assertEquals(-1, result.steps());
            assertEquals(List.of(), result.path());
        }
    }

    @Test
    void startCanAlreadyBeTheGoal() {
        Maze maze = Maze.fromAscii("B");
        for (MazeSolver.Algorithm algorithm : MazeSolver.Algorithm.values()) {
            PathResult result = solver.solve(maze, algorithm);
            assertEquals(0, result.steps());
            assertEquals(List.of(maze.start()), result.path());
            assertEquals(1, result.exploredNodes());
        }
    }

    @Test
    void mazeRejectsInvalidInputAndCopiesWalls() {
        assertThrows(IllegalArgumentException.class, () -> Maze.fromAscii("S..", ".G"));
        assertThrows(IllegalArgumentException.class, () -> Maze.fromAscii("S.G?"));
        boolean[][] input = {{false, false}};
        Maze maze = new Maze(input, new Position(0, 0), new Position(0, 1));
        input[0][1] = true;
        assertTrue(maze.isOpen(new Position(0, 1)));
    }
}
