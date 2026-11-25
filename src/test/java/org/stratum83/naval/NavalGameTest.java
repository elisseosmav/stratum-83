package org.stratum83.naval;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class NavalGameTest {
    private static NavalGame.Position at(String coordinate) {
        return NavalGame.Position.parse(coordinate);
    }

    @Test
    void coordinateParsingAndBounds() {
        assertEquals(new NavalGame.Position(9, 9), at("j10"));
        assertEquals("A1", at("A1").label());
        assertThrows(IllegalArgumentException.class, () -> at("K2"));
        assertThrows(IllegalArgumentException.class, () -> at("A0"));
        assertThrows(IllegalArgumentException.class, () -> at("C11"));
        assertThrows(IllegalArgumentException.class, () -> new NavalGame.Position(-1, 0));
    }

    @Test
    void startsInDeploymentWithHiddenComputerFleet() {
        NavalGame game = new NavalGame(new Random(7), NavalGame.Difficulty.NORMAL);
        assertEquals(NavalGame.Phase.DEPLOYMENT, game.phase());
        assertEquals(0, game.deployedShips());
        assertEquals(NavalGame.Vessel.CARRIER, game.nextVessel());
        assertEquals(5, game.enemyShipsRemaining());
        assertFalse(game.fire(at("D4")));
        for (int r = 0; r < 10; r++) for (int c = 0; c < 10; c++) {
            assertEquals(NavalGame.Mark.UNKNOWN,
                    game.enemyMark(new NavalGame.Position(r, c)));
        }
    }

    @Test
    void manualPlacementRejectsOverlapOrOverflow() {
        NavalGame game = new NavalGame(new Random(1), NavalGame.Difficulty.EASY);
        assertFalse(game.canDeploy(at("H1"), NavalGame.Orientation.HORIZONTAL));
        assertTrue(game.placeNext(at("A1"), NavalGame.Orientation.HORIZONTAL));
        assertEquals(NavalGame.Vessel.BATTLESHIP, game.nextVessel());
        assertFalse(game.placeNext(at("A1"), NavalGame.Orientation.VERTICAL));
        assertTrue(game.placeNext(at("A2"), NavalGame.Orientation.HORIZONTAL));
        assertTrue(game.placeNext(at("A3"), NavalGame.Orientation.HORIZONTAL));
        assertTrue(game.placeNext(at("A4"), NavalGame.Orientation.HORIZONTAL));
        assertTrue(game.placeNext(at("A5"), NavalGame.Orientation.HORIZONTAL));
        assertEquals(NavalGame.Phase.COMBAT, game.phase());
        assertEquals(5, game.ownShipsRemaining());
        assertTrue(game.playerShipAt(at("E1")));
        assertFalse(game.placeNext(at("A6"), NavalGame.Orientation.HORIZONTAL));
    }

    @Test
    void partialManualDeploymentCanFinishAutomatically() {
        NavalGame game = new NavalGame(new Random(83), NavalGame.Difficulty.HARD);
        assertTrue(game.placeNext(at("A1"), NavalGame.Orientation.HORIZONTAL));
        game.autoDeploy();
        assertEquals(5, game.deployedShips());
        assertEquals(NavalGame.Phase.COMBAT, game.phase());
        assertNull(game.nextVessel());
    }

    @Test
    void everyValidShotProducesExactlyOneComputerResponse() {
        NavalGame game = new NavalGame(new Random(83), NavalGame.Difficulty.EASY);
        game.autoDeploy();
        List<NavalGame.Position> all = new ArrayList<>();
        for (int row = 0; row < 10; row++) for (int col = 0; col < 10; col++) {
            all.add(new NavalGame.Position(row, col));
        }
        for (NavalGame.Position target : all) {
            if (game.phase() != NavalGame.Phase.COMBAT) break;
            int previous = game.playerShots().size();
            int response = game.computerShots().size();
            assertTrue(game.fire(target));
            assertEquals(previous + 1, game.playerShots().size());
            assertNotEquals(NavalGame.Mark.UNKNOWN, game.enemyMark(target));
            assertFalse(game.fire(target));
            assertTrue(game.computerShots().size() == response + 1
                    || game.result() == NavalGame.Result.PLAYER_WINS);
        }
        assertEquals(NavalGame.Phase.FINISHED, game.phase());
        assertNotEquals(NavalGame.Result.PLAYING, game.result());
        assertFalse(game.fire(at("A1")));
    }

    @Test
    void hardAiUsesOnlyKnowledgeAndRejectsPreviouslyFiredTiles() {
        NavalGame.Mark[][] known = emptyKnowledge();
        known[4][4] = NavalGame.Mark.HIT;
        known[4][3] = NavalGame.Mark.MISS;
        known[3][4] = NavalGame.Mark.SUNK;
        NavalAI.Decision hard = NavalAI.choose(known, List.of(5, 4, 3, 3, 2),
                NavalGame.Difficulty.HARD, new Random(12));
        assertEquals(NavalGame.Mark.UNKNOWN,
                known[hard.target().row()][hard.target().col()]);
        assertEquals(0, hard.heatmap()[4][3]);
        assertEquals(0, hard.heatmap()[3][4]);
        int[][] heat = hard.heatmap();
        heat[4][5] = 999999;
        assertNotEquals(999999, hard.heatmap()[4][5]);
    }

    @Test
    void normalAiHuntsNeighborsOfUnresolvedHits() {
        NavalGame.Mark[][] known = emptyKnowledge();
        known[5][5] = NavalGame.Mark.HIT;
        known[4][5] = NavalGame.Mark.MISS;
        known[5][4] = NavalGame.Mark.MISS;
        known[6][5] = NavalGame.Mark.MISS;
        NavalAI.Decision decision = NavalAI.choose(known, List.of(3, 2),
                NavalGame.Difficulty.NORMAL, new Random(1));
        assertEquals(at("G6"), decision.target());
    }

    @Test
    void aiReturnsOnlyUnknownTilesAcrossDifficultyLevels() {
        for (NavalGame.Difficulty difficulty : NavalGame.Difficulty.values()) {
            NavalGame.Mark[][] known = emptyKnowledge();
            known[0][0] = NavalGame.Mark.SUNK;
            known[1][1] = NavalGame.Mark.MISS;
            known[3][4] = NavalGame.Mark.HIT;
            for (int seed = 0; seed < 15; seed++) {
                NavalAI.Decision choice = NavalAI.choose(known, List.of(4, 3),
                        difficulty, new Random(seed));
                assertEquals(NavalGame.Mark.UNKNOWN,
                        known[choice.target().row()][choice.target().col()]);
            }
        }
    }

    private static NavalGame.Mark[][] emptyKnowledge() {
        NavalGame.Mark[][] marks = new NavalGame.Mark[10][10];
        for (NavalGame.Mark[] row : marks) java.util.Arrays.fill(row, NavalGame.Mark.UNKNOWN);
        return marks;
    }
}
