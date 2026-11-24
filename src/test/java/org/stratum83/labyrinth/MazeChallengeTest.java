package org.stratum83.labyrinth;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MazeChallengeTest {

    @Test
    void optimalHumanRouteScoresOneHundredPercent() {
        Maze maze = Maze.fromAscii("#####", "#S.G#", "#####");
        MazeChallenge challenge = new MazeChallenge(maze);

        assertEquals(0, challenge.moves());
        assertEquals(2, challenge.optimalMoves());
        assertFalse(challenge.finished());

        assertTrue(challenge.moveTo(new Position(1, 2)));
        assertTrue(challenge.moveTo(new Position(1, 3)));

        assertTrue(challenge.finished());
        assertEquals(2, challenge.moves());
        assertEquals(0, challenge.extraMoves());
        assertEquals(100.0, challenge.efficiencyPercent(), 0.001);
    }

    @Test
    void backtrackingCountsAsExtraMoves() {
        Maze maze = Maze.fromAscii("#######", "#S...G#", "#.....#", "#######");
        MazeChallenge challenge = new MazeChallenge(maze);

        assertTrue(challenge.moveTo(new Position(1, 2)));
        assertTrue(challenge.moveTo(new Position(1, 1)));
        assertTrue(challenge.moveTo(new Position(1, 2)));
        assertTrue(challenge.moveTo(new Position(1, 3)));
        assertTrue(challenge.moveTo(new Position(1, 4)));
        assertTrue(challenge.moveTo(new Position(1, 5)));

        assertTrue(challenge.finished());
        assertEquals(6, challenge.moves());
        assertEquals(4, challenge.optimalMoves());
        assertEquals(2, challenge.extraMoves());
        assertEquals(66.666, challenge.efficiencyPercent(), 0.01);
        assertEquals(7, challenge.playerRoute().size());
    }

    @Test
    void invalidMovesAndMovesAfterFinishDoNotChangeTheScore() {
        MazeChallenge challenge = new MazeChallenge(
                Maze.fromAscii("#####", "#S.G#", "#####"));

        assertFalse(challenge.moveTo(null));
        assertFalse(challenge.moveTo(new Position(0, 1)));
        assertFalse(challenge.moveTo(new Position(1, 3)));
        assertEquals(0, challenge.moves());

        assertTrue(challenge.moveTo(new Position(1, 2)));
        assertTrue(challenge.moveTo(new Position(1, 3)));
        assertFalse(challenge.moveTo(new Position(1, 2)));
        assertEquals(2, challenge.moves());
    }

    @Test
    void routeSnapshotsCannotBeModified() {
        MazeChallenge challenge = new MazeChallenge(
                Maze.fromAscii("#####", "#S.G#", "#####"));
        List<Position> snapshot = challenge.playerRoute();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.clear());

        challenge.moveTo(new Position(1, 2));
        assertEquals(1, snapshot.size());
        assertEquals(2, challenge.playerRoute().size());
        assertEquals(3, challenge.optimalRoute().size());
    }

    @Test
    void newAttemptCanReplayExactlyTheSameMaze() {
        Maze maze = new MazeGenerator().generate(11, 15, 1992L);
        MazeChallenge first = new MazeChallenge(maze);
        first.moveTo(first.optimalRoute().get(1));

        MazeChallenge retry = new MazeChallenge(maze);
        assertEquals(0, retry.moves());
        assertEquals(first.optimalMoves(), retry.optimalMoves());
        assertEquals(maze.start(), retry.playerPosition());
    }

    @Test
    void mazeWithoutRouteIsRejected() {
        Maze maze = Maze.fromAscii("#####", "#S#G#", "#####");
        assertThrows(IllegalArgumentException.class, () -> new MazeChallenge(maze));
    }

    @Test
    void startingOnGoalCountsAsPerfectWithoutMoving() {
        MazeChallenge challenge = new MazeChallenge(Maze.fromAscii("###", "#B#", "###"));
        assertTrue(challenge.finished());
        assertEquals(0, challenge.optimalMoves());
        assertEquals(0, challenge.extraMoves());
        assertEquals(100.0, challenge.efficiencyPercent(), 0.001);
    }
}
