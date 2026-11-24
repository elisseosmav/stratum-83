package org.stratum83.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MinimaxAnalysisTest {

    private final MinimaxPlayer player = new MinimaxPlayer();

    @Test
    void traceAndActualPlayerChooseTheSameMove() {
        GridZeroGame game = new GridZeroGame();
        game.play(new Move(0, 0)); // X opens; O (computer) moves next.
        GridZeroGame before = game.copy();

        MinimaxAnalysis analysis = player.analyzeMove(game);

        assertEquals(player.chooseMove(game), analysis.chosenMove());
        assertEquals(GridZeroGame.O, analysis.player());
        assertEquals(game.availableMoves().size(), analysis.candidates().size());
        assertEquals(analysis.bestScore(), analysis.candidates().stream()
                .mapToInt(MinimaxAnalysis.Candidate::score).max().orElseThrow());
        assertEquals(before.getMoveCount(), game.getMoveCount());
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                assertEquals(before.getCell(row, column), game.getCell(row, column));
            }
        }
    }

    @Test
    void traceContainsBoardStatesAndFinalChoice() {
        GridZeroGame game = new GridZeroGame();
        game.play(new Move(0, 0));
        MinimaxAnalysis analysis = player.analyzeMove(game);

        assertFalse(analysis.steps().isEmpty());
        assertEquals("CHOSEN", analysis.steps().get(analysis.steps().size() - 1).kind());
        assertTrue(analysis.steps().size() <= 160);
        assertTrue(analysis.evaluatedNodes() >= analysis.candidates().size());
        assertTrue(analysis.alphaBetaCutoffs() >= 0);
        assertTrue(analysis.steps().get(0).board().contains("X"));
        assertThrows(UnsupportedOperationException.class, () -> analysis.steps().clear());
        assertThrows(UnsupportedOperationException.class, () -> analysis.candidates().clear());
    }

    @Test
    void immediateWinIsRecognizedAndChosen() {
        GridZeroGame game = new GridZeroGame();
        game.play(new Move(0, 0)); // X
        game.play(new Move(1, 0)); // O
        game.play(new Move(0, 1)); // X
        game.play(new Move(1, 1)); // O
        MinimaxAnalysis analysis = player.analyzeMove(game); // X wins in row 0

        assertEquals(new Move(0, 2), analysis.chosenMove());
        assertEquals(player.chooseMove(game), analysis.chosenMove());
        assertTrue(analysis.bestScore() > 0);
    }

    @Test
    void finishedGamesCannotBeAnalyzed() {
        GridZeroGame game = new GridZeroGame();
        game.play(new Move(0, 0));
        game.play(new Move(1, 0));
        game.play(new Move(0, 1));
        game.play(new Move(1, 1));
        game.play(new Move(0, 2));
        assertEquals(GameResult.X_WINS, game.getResult());
        assertThrows(IllegalArgumentException.class, () -> player.analyzeMove(game));
        assertThrows(IllegalArgumentException.class, () -> player.chooseMove(game));
    }

    @Test
    void everyRootCandidateHasARealOutcomeScore() {
        GridZeroGame game = new GridZeroGame();
        game.play(new Move(1, 1));
        MinimaxAnalysis analysis = player.analyzeMove(game);

        // All scores must come from terminal positions (draw or depth-adjusted result).
        for (MinimaxAnalysis.Candidate candidate : analysis.candidates()) {
            assertTrue(candidate.score() >= -9 && candidate.score() <= 9);
        }
    }
}
