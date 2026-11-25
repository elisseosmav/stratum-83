package org.stratum83.crisis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class CrisisGameTest {
    private static CrisisGame.Position at(int row, int col) {
        return new CrisisGame.Position(row, col);
    }

    @Test
    void newScenarioHasTwoTeamsThreeIncidentsAndThreeFacilities() {
        CrisisGame game = new CrisisGame();
        assertEquals(CrisisGame.Result.PLAYING, game.result());
        assertEquals(1, game.round());
        assertEquals(2, game.actionsRemaining());
        assertEquals(at(5, 1), game.team(0));
        assertEquals(at(5, 5), game.team(1));
        assertEquals(3, game.incidentCount());
        assertEquals(3, game.sites().size());
        assertEquals(0, game.lostSites());
        assertFalse(game.endTurn());
    }

    @Test
    void humanMayMoveTwoCellsOrContainAdjacentIncident() {
        CrisisGame game = new CrisisGame();
        assertTrue(game.actionTargets(0).contains(at(3, 1)));
        assertFalse(game.actionTargets(0).contains(at(2, 1)));
        assertFalse(game.act(0, at(4, 2)));
        assertTrue(game.act(0, at(3, 1)));
        assertEquals(1, game.actionsRemaining());
        assertEquals(at(3, 1), game.team(0));
        assertTrue(game.actionTargets(0).contains(at(2, 1)));
        assertTrue(game.act(0, at(2, 1)));
        assertFalse(game.hasIncident(at(2, 1)));
        assertEquals(0, game.lostSites());
        assertEquals(3, game.incidentCount());
        assertEquals(2, game.round());
        assertEquals(2, game.actionsRemaining());
        assertTrue(game.lastComputerAction().startsWith("SPREAD TO "));
    }

    @Test
    void humanCannotTeleportEnterAnotherTeamOrPassThroughIncidents() {
        CrisisGame game = new CrisisGame(List.of(at(4, 2), at(5, 5)),
                List.of(at(3, 2)), List.of(at(0, 0)));
        assertFalse(game.act(0, at(2, 2)));
        assertFalse(game.act(0, at(1, 2)));
        assertFalse(game.act(0, at(5, 5)));
        assertFalse(game.act(0, at(4, 2)));
        assertFalse(game.act(4, at(4, 3)));
        assertEquals(2, game.actionsRemaining());
    }

    @Test
    void clearingLastIncidentWinsBeforeComputerActs() {
        CrisisGame game = new CrisisGame(List.of(at(3, 2), at(5, 5)),
                List.of(at(2, 2)), List.of(at(0, 0), at(0, 6)));
        assertTrue(game.act(0, at(2, 2)));
        assertEquals(CrisisGame.Result.HUMAN_WINS, game.result());
        assertEquals(0, game.incidentCount());
        assertEquals(1, game.round());
        assertFalse(game.endTurn());
        assertFalse(game.act(0, at(3, 2)));
    }

    @Test
    void threatComputerTargetsAdjacentCriticalSiteFirst() {
        CrisisGame game = new CrisisGame(List.of(at(4, 2), at(4, 4)),
                List.of(at(1, 2)), List.of(at(1, 3), at(6, 0)));
        assertTrue(game.act(0, at(4, 1)));
        assertTrue(game.act(1, at(4, 5)));
        assertEquals(1, game.lostSites());
        assertTrue(game.hasIncident(at(1, 3)));
        assertTrue(game.lastComputerAction().contains("SITE COMPROMISED"));
        assertEquals(CrisisGame.Result.PLAYING, game.result());
    }

    @Test
    void compromisedSecondFacilityEndsGame() {
        CrisisGame game = new CrisisGame(List.of(at(5, 1), at(5, 5)),
                List.of(at(1, 2), at(0, 4)),
                List.of(at(1, 3), at(1, 4)));
        assertTrue(game.act(0, at(4, 1)));
        assertTrue(game.act(1, at(4, 5)));
        assertEquals(1, game.lostSites());
        assertTrue(game.act(0, at(3, 1)));
        assertTrue(game.act(1, at(3, 5)));
        assertEquals(CrisisGame.Result.COMPUTER_WINS, game.result());
        assertEquals(2, game.lostSites());
    }

    @Test
    void earlyEndTurnOnlyWorksAfterAtLeastOneAction() {
        CrisisGame game = new CrisisGame();
        assertFalse(game.endTurn());
        assertTrue(game.act(0, at(4, 1)));
        assertTrue(game.endTurn());
        assertEquals(2, game.round());
        assertEquals(2, game.actionsRemaining());
        assertFalse(game.endTurn());
    }

    @Test
    void occupiedTeamTileBlocksComputerSpread() {
        CrisisGame game = new CrisisGame(List.of(at(1, 3), at(5, 5)),
                List.of(at(1, 2)), List.of(at(1, 3), at(6, 0)));
        assertTrue(game.act(1, at(5, 4)));
        assertTrue(game.act(1, at(5, 3)));
        assertEquals(at(1, 3), game.team(0));
        assertFalse(game.hasIncident(at(1, 3)));
        assertEquals(0, game.lostSites());
    }

    @Test
    void labelsAndReturnedListsAreSafe() {
        CrisisGame game = new CrisisGame();
        assertEquals("B6", game.team(0).label());
        assertThrows(UnsupportedOperationException.class,
                () -> game.incidentLocations().clear());
        assertThrows(IllegalArgumentException.class, () -> at(-1, 0));
        assertEquals(2, at(0, 0).distanceTo(at(1, 1)));
    }

    @Test
    void defaultScenarioCanBeWonByCoordinatingBothTeams() {
        CrisisGame game = new CrisisGame();
        int[][] plan = {
                {1, 3, 5, 1, 2, 5},
                {1, 1, 5, 1, 1, 3},
                {0, 3, 1, 0, 2, 1},
                {1, 1, 2, 0, 2, 1},
                {1, 2, 3, 0, 1, 1},
                {1, 2, 3, 1, 1, 2},
                {0, 1, 1, 0, 0, 1},
                {1, 2, 3, 1, 2, 3},
                {1, 3, 3, 1, 1, 3},
                {1, 1, 3, 0, 1, 2},
                {0, 1, 2, 1, 2, 2}
        };
        for (int[] actions : plan) {
            assertTrue(game.act(actions[0], at(actions[1], actions[2])));
            assertTrue(game.act(actions[3], at(actions[4], actions[5])));
        }
        assertEquals(CrisisGame.Result.HUMAN_WINS, game.result());
        assertEquals(0, game.incidentCount());
    }
}
