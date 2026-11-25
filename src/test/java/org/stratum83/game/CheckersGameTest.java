package org.stratum83.game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class CheckersGameTest {
    @Test
    void startsWithTwelvePiecesPerSideAndHumanToMove() {
        CheckersGame game = new CheckersGame();
        assertEquals(CheckersGame.Side.HUMAN, game.sideToMove());
        assertEquals(12, game.countPieces(CheckersGame.Side.HUMAN));
        assertEquals(12, game.countPieces(CheckersGame.Side.COMPUTER));
        assertEquals(7, game.legalMoves().size());
        assertTrue(game.legalMoves().stream().noneMatch(CheckersGame.Turn::isCapture));
    }

    @Test
    void captureIsMandatoryAndRemovesTheJumpedPiece() {
        CheckersGame game = CheckersGame.fromRows(CheckersGame.Side.HUMAN,
                "........",
                "........",
                "........",
                "..c.....",
                ".h......",
                "....h...",
                "........",
                "........");

        List<CheckersGame.Turn> legal = game.legalMoves();
        assertEquals(1, legal.size());
        assertEquals(new CheckersGame.Square(4, 1), legal.getFirst().start());
        assertEquals(new CheckersGame.Square(2, 3), legal.getFirst().destination());
        assertTrue(legal.getFirst().isCapture());
        assertTrue(game.play(legal.getFirst()));
        assertEquals(CheckersGame.EMPTY, game.getCell(3, 2));
        assertEquals('h', game.getCell(2, 3));
        assertEquals(CheckersGame.Outcome.HUMAN_WINS, game.outcome());
    }

    @Test
    void mustFinishMultipleJumpsAndPromoteAtTheLastRow() {
        CheckersGame game = CheckersGame.fromRows(CheckersGame.Side.HUMAN,
                "........",
                "....c...",
                "........",
                "..c.....",
                ".h......",
                "........",
                "........",
                "........");
        CheckersGame.Turn move = game.legalMoves().getFirst();
        assertEquals(List.of(
                new CheckersGame.Square(4, 1),
                new CheckersGame.Square(2, 3),
                new CheckersGame.Square(0, 5)), move.path());
        assertEquals(2, move.captures().size());
        assertTrue(game.play(move));
        assertEquals('H', game.getCell(0, 5));
        assertEquals(0, game.countPieces(CheckersGame.Side.COMPUTER));
    }

    @Test
    void kingsCanJumpBackwards() {
        CheckersGame game = CheckersGame.fromRows(CheckersGame.Side.HUMAN,
                "........",
                "........",
                "........",
                "........",
                "...H....",
                "....c...",
                "........",
                "........");
        assertTrue(game.legalMoves().stream().anyMatch(turn ->
                turn.destination().equals(new CheckersGame.Square(6, 5))
                        && turn.isCapture()));
    }

    @Test
    void promotionStopsCaptureSequenceUnderAmericanRules() {
        CheckersGame game = CheckersGame.fromRows(CheckersGame.Side.HUMAN,
                "........",
                "..c.c...",
                ".h......",
                "........",
                "........",
                "........",
                "........",
                "........");
        CheckersGame.Turn move = game.legalMoves().getFirst();
        assertEquals(new CheckersGame.Square(0, 3), move.destination());
        assertEquals(2, move.path().size());
        assertEquals(1, move.captures().size());
    }

    @Test
    void rejectsIllegalTurnsWithoutChangingThePosition() {
        CheckersGame game = new CheckersGame();
        CheckersGame.Turn illegal = new CheckersGame.Turn(List.of(
                new CheckersGame.Square(5, 0),
                new CheckersGame.Square(4, 0)), List.of());
        assertFalse(game.play(illegal));
        assertEquals(12, game.countPieces(CheckersGame.Side.HUMAN));
        assertEquals(CheckersGame.Side.HUMAN, game.sideToMove());
    }

    @Test
    void computerChoosesALegalMove() {
        CheckersGame game = new CheckersGame();
        assertTrue(game.play(game.legalMoves().getFirst()));
        assertEquals(CheckersGame.Side.COMPUTER, game.sideToMove());
        CheckersGame.Turn choice = new CheckersAI(3).chooseMove(game);
        assertNotNull(choice);
        assertTrue(game.legalMoves().contains(choice));
        assertTrue(game.play(choice));
        assertEquals(CheckersGame.Side.HUMAN, game.sideToMove());
    }
}
