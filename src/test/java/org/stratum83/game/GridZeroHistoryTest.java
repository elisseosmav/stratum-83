package org.stratum83.game;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GridZeroHistoryTest {

    private final MinimaxPlayer computer = new MinimaxPlayer();

    private GridZeroHistory playFourTurns() {
        GridZeroGame game = new GridZeroGame();
        GridZeroHistory history = new GridZeroHistory();

        Move first = new Move(0, 0);
        game.play(first);
        history.record(first, game, null);
        MinimaxAnalysis firstDecision = computer.analyzeMove(game);
        game.play(firstDecision.chosenMove());
        history.record(firstDecision.chosenMove(), game, firstDecision);

        Move third = new Move(0, 2);
        assertTrue(game.play(third));
        history.record(third, game, null);
        MinimaxAnalysis secondDecision = computer.analyzeMove(game);
        game.play(secondDecision.chosenMove());
        history.record(secondDecision.chosenMove(), game, secondDecision);
        return history;
    }

    @Test
    void storesActualFirstFourTurnsAndBothDecisions() {
        GridZeroHistory history = playFourTurns();
        assertEquals(4, history.turns().size());
        assertEquals(List.of(1, 3), history.explainableMoves());
        assertEquals('X', history.turns().get(2).player());
        assertEquals('O', history.turns().get(3).player());
        assertEquals(new Move(0, 2), history.turns().get(2).move());
        assertEquals(history.turns().get(3).move(),
                history.decisionFollowing(3).chosenMove());
        assertEquals(history.turns().get(2).boardAfter(),
                GridZeroHistory.boardText(history.replayThrough(3)));
    }

    @Test
    void replayNeverMutatesActualRecordedHistory() {
        GridZeroHistory history = playFourTurns();
        GridZeroGame oldPosition = history.replayThrough(3);
        oldPosition.play(new Move(0, 1));
        assertEquals(4, history.turns().size());
        assertEquals("X.X.O....", history.turns().get(2).boardAfter());
        assertEquals(new Move(0, 2), history.turns().get(2).move());
        assertThrows(UnsupportedOperationException.class, () -> history.turns().clear());
    }

    @Test
    void invalidSelectionsDoNotInventAComputerDecision() {
        GridZeroHistory history = playFourTurns();
        assertThrows(IllegalArgumentException.class, () -> history.decisionFollowing(2));
        assertThrows(IllegalArgumentException.class, () -> history.decisionFollowing(4));
        assertThrows(IllegalArgumentException.class, () -> history.replayThrough(5));
    }

    @Test
    void rejectsAnAnalysisThatDoesNotBelongToTheRecordedMove() {
        GridZeroGame game = new GridZeroGame();
        GridZeroHistory history = new GridZeroHistory();
        Move human = new Move(0, 0);
        game.play(human);
        history.record(human, game, null);

        MinimaxAnalysis decision = computer.analyzeMove(game);
        Move wrong = game.availableMoves().stream()
                .filter(move -> !move.equals(decision.chosenMove()))
                .findFirst().orElseThrow();
        game.play(wrong);
        assertThrows(IllegalArgumentException.class,
                () -> history.record(wrong, game, decision));
        assertEquals(1, history.turns().size());
    }
}
