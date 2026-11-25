package org.stratum83.labyrinth;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MazeFogOfWarTest {

    @Test
    void onlyNearbyCellsAreInitiallyVisibleAndWallsBlockVision() {
        Maze maze = Maze.fromAscii(
                "#######",
                "#S#..G#",
                "#.....#",
                "#######");
        MazeFogOfWar fog = new MazeFogOfWar(maze, 4);
        assertTrue(fog.isVisible(maze.start()));
        assertTrue(fog.isVisible(new Position(1, 2)), "The wall itself is visible");
        assertFalse(fog.isVisible(new Position(1, 3)),
                "The corridor beyond the wall must remain hidden");
        assertFalse(fog.wasDiscovered(maze.goal()), "A distant goal must not be revealed");
    }

    @Test
    void exploredCellsRemainRememberedButNotBrightlyVisible() {
        Maze maze = Maze.fromAscii(
                "###########",
                "#S.......G#",
                "###########");
        MazeFogOfWar fog = new MazeFogOfWar(maze, 2);
        int initial = fog.discoveredCells();
        fog.revealFrom(new Position(1, 7));
        assertTrue(fog.wasDiscovered(maze.start()));
        assertFalse(fog.isVisible(maze.start()));
        assertTrue(fog.isVisible(new Position(1, 7)));
        assertTrue(fog.discoveredCells() > initial);
        assertTrue(fog.discoveredCells() <= fog.totalCells());
    }

    @Test
    void invalidLocationsOrRadiusAreRejected() {
        Maze maze = Maze.fromAscii("#####", "#S.G#", "#####");
        assertThrows(IllegalArgumentException.class, () -> new MazeFogOfWar(maze, 0));
        MazeFogOfWar fog = new MazeFogOfWar(maze, 2);
        assertThrows(IllegalArgumentException.class, () -> fog.revealFrom(new Position(0, 0)));
        assertFalse(fog.isVisible(null));
        assertFalse(fog.wasDiscovered(new Position(99, 99)));
    }

    @Test
    void generatedMazeHidesExitUntilExploration() {
        Maze maze = new ChallengeMazeGenerator().generate(1983L);
        MazeFogOfWar fog = new MazeFogOfWar(maze, 3);
        assertTrue(fog.isVisible(maze.start()));
        assertFalse(fog.wasDiscovered(maze.goal()));
    }
}
