package org.stratum83.ui;

import org.junit.jupiter.api.Test;
import org.stratum83.game.GridZeroGame;
import org.stratum83.game.MinimaxAnalysis;
import org.stratum83.game.MinimaxPlayer;
import org.stratum83.game.Move;
import static org.junit.jupiter.api.Assertions.*;

class GridZeroLearningScreensTest {

    /**
     * Reproduces a confusing screen reported by a learner:
     *
     * O X X
     * . O .
     * X O X
     *
     * O must avoid row 2/col 1 (which loses) and choose row 2/col 3 (draw).
     */
    private GridZeroGame examplePosition() {
        GridZeroGame game = new GridZeroGame();
        game.play(new Move(0, 1)); // X
        game.play(new Move(0, 0)); // O
        game.play(new Move(0, 2)); // X
        game.play(new Move(1, 1)); // O
        game.play(new Move(2, 0)); // X
        game.play(new Move(2, 1)); // O
        game.play(new Move(2, 2)); // X
        return game;
    }

    @Test
    void screenshotExampleExplainsWhyComputerChoosesDrawInsteadOfLoss() {
        GridZeroGame game = examplePosition();
        MinimaxAnalysis analysis = new MinimaxPlayer().analyzeMove(game);

        assertEquals('O', analysis.player());
        assertEquals(new Move(1, 2), analysis.chosenMove());
        assertEquals(0, analysis.bestScore());
        assertEquals(2, analysis.candidates().size());
        assertEquals(new Move(1, 0), analysis.candidates().get(0).move());
        assertEquals(-8, analysis.candidates().get(0).score());
        assertEquals(new Move(1, 2), analysis.candidates().get(1).move());
        assertEquals(0, analysis.candidates().get(1).score());

        String summary = GridZeroLearningScreens.decisionSummary(analysis);
        assertTrue(summary.contains("ROW 2, COLUMN 3"));
        assertTrue(summary.contains("DRAW"));
        assertTrue(summary.contains("AVOID LOSING"));
    }

    @Test
    void outcomeLabelsUseEverydayLanguage() {
        assertEquals("WIN", GridZeroLearningScreens.outcomeLabel(7));
        assertEquals("DRAW", GridZeroLearningScreens.outcomeLabel(0));
        assertEquals("LOSS", GridZeroLearningScreens.outcomeLabel(-8));
    }

    @Test
    void explanationsNeverChangeTheRealGame() {
        GridZeroGame game = examplePosition();
        MinimaxAnalysis analysis = new MinimaxPlayer().analyzeMove(game);
        GridZeroLearningScreens.decisionSummary(analysis);

        assertEquals(7, game.getMoveCount());
        assertEquals(GridZeroGame.EMPTY, game.getCell(1, 0));
        assertEquals(GridZeroGame.EMPTY, game.getCell(1, 2));
    }
}
