package org.stratum83.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GridZeroMoveLessonTest {

    private final MinimaxPlayer minimax = new MinimaxPlayer();

    private GridZeroHistory matchWithThirdHumanMove() {
        GridZeroGame game = new GridZeroGame();
        GridZeroHistory history = new GridZeroHistory();
        for (Move move : new Move[]{new Move(0, 0), new Move(0, 2)}) {
            assertTrue(game.play(move));
            history.record(move, game, null);
            assertEquals(GameResult.PLAYING, game.getResult());
            MinimaxAnalysis decision = minimax.analyzeMove(game);
            assertTrue(game.play(decision.chosenMove()));
            history.record(decision.chosenMove(), game, decision);
        }
        return history;
    }

    @Test
    void thirdPlayedMoveExplainsTheActualFourthComputerMove() {
        GridZeroHistory history = matchWithThirdHumanMove();
        GridZeroMoveLesson lesson = new GridZeroMoveLesson(history, 3, minimax);

        assertEquals(3, lesson.afterHumanMove());
        assertEquals(4, lesson.computerMoveNumber());
        assertEquals(history.turns().get(2).boardAfter(), lesson.originalBoard());
        assertEquals(history.turns().get(3).move(),
                lesson.actualDecision().chosenMove());
        assertEquals(lesson.actualDecision().candidates().size(),
                lesson.alternatives().size());
        assertEquals(lesson.actualDecision().chosenMove(),
                lesson.selectedByComputer().computerMove());
    }

    @Test
    void EveryHypotheticalFutureIsAValidOptimalContinuation() {
        GridZeroHistory history = matchWithThirdHumanMove();
        GridZeroMoveLesson lesson = new GridZeroMoveLesson(history, 3, minimax);

        for (GridZeroMoveLesson.Alternative alternative : lesson.alternatives()) {
            GridZeroGame replay = history.replayThrough(3);
            assertFalse(alternative.future().isEmpty());
            assertEquals(alternative.computerMove(), alternative.future().get(0).move());

            for (GridZeroMoveLesson.FutureTurn turn : alternative.future()) {
                assertEquals(replay.getCurrentPlayer(), turn.player());
                assertTrue(replay.play(turn.move()));
                assertEquals(GridZeroHistory.boardText(replay), turn.boardAfter());
                assertEquals(replay.getResult(), turn.result());
            }
            assertEquals(alternative.outcome(), replay.getResult());
            assertNotEquals(GameResult.PLAYING, alternative.outcome());
            int score = alternative.score();
            String result = GridZeroMoveLesson.outcomeForComputer(
                    alternative.outcome(), lesson.actualDecision().player());
            assertEquals(score > 0 ? "WIN" : score < 0 ? "LOSS" : "DRAW", result);
            assertThrows(UnsupportedOperationException.class,
                    () -> alternative.future().clear());
        }
    }

    @Test
    void TeachingAlternativeDoesNotChangeTheRecordedMatch() {
        GridZeroHistory history = matchWithThirdHumanMove();
        String actual = history.turns().get(3).boardAfter();
        GridZeroMoveLesson lesson = new GridZeroMoveLesson(history, 3, minimax);
        lesson.alternatives().get(0).future().get(0).boardAfter();
        assertEquals(actual, history.turns().get(3).boardAfter());
        assertEquals(4, history.turns().size());
        assertThrows(UnsupportedOperationException.class, () -> lesson.alternatives().clear());
    }

    @Test
    void RejectsTurnsWithoutAnActualComputerResponse() {
        GridZeroHistory history = matchWithThirdHumanMove();
        assertThrows(IllegalArgumentException.class,
                () -> new GridZeroMoveLesson(history, 2, minimax));
        assertThrows(IllegalArgumentException.class,
                () -> new GridZeroMoveLesson(history, 4, minimax));
    }
}
