package org.stratum83.chess;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ChessGameTest {
    private static ChessGame.Move move(String from, String to) {
        return new ChessGame.Move(8 - (from.charAt(1) - '0'), from.charAt(0) - 'a',
                8 - (to.charAt(1) - '0'), to.charAt(0) - 'a');
    }

    private static ChessGame.Move promote(String from, String to, char piece) {
        ChessGame.Move simple = move(from, to);
        return new ChessGame.Move(simple.fromRow(), simple.fromCol(),
                simple.toRow(), simple.toCol(), piece);
    }

    @Test
    void initialPositionHasTwentyMovesAndExpectedPieces() {
        ChessGame game = new ChessGame();
        assertEquals(ChessGame.Side.WHITE, game.turn());
        assertEquals(ChessGame.Result.PLAYING, game.result());
        assertEquals(20, game.legalMoves().size());
        assertEquals('K', game.piece(7, 4));
        assertEquals('k', game.piece(0, 4));
        assertFalse(game.inCheck(ChessGame.Side.WHITE));
        assertTrue(game.legalMoves().contains(move("e2", "e4")));
        assertFalse(game.legalMoves().contains(move("e2", "e5")));
        assertEquals('.', game.piece(4, 4));
    }

    @Test
    void legalPawnMoveAndAlternatingTurns() {
        ChessGame game = new ChessGame();
        assertTrue(game.play(move("e2", "e4")));
        assertEquals('P', game.piece(4, 4));
        assertEquals('.', game.piece(6, 4));
        assertEquals(ChessGame.Side.BLACK, game.turn());
        assertEquals(20, game.legalMoves().size());
        assertFalse(game.play(move("d2", "d4")));
        assertTrue(game.play(move("e7", "e5")));
        assertEquals(ChessGame.Side.WHITE, game.turn());
    }

    @Test
    void foolsMateGivesComputerCheckmate() {
        ChessGame game = new ChessGame();
        assertTrue(game.play(move("f2", "f3")));
        assertTrue(game.play(move("e7", "e5")));
        assertTrue(game.play(move("g2", "g4")));
        assertTrue(game.play(move("d8", "h4")));
        assertEquals(ChessGame.Result.BLACK_WINS, game.result());
        assertTrue(game.inCheck(ChessGame.Side.WHITE));
        assertTrue(game.legalMoves().isEmpty());
    }

    @Test
    void recognizesStalemateAndInsufficientMaterial() {
        ChessGame stalemate = ChessGame.fromFen("7k/5Q2/6K1/8/8/8/8/8 b - - 0 1");
        assertEquals(ChessGame.Result.DRAW, stalemate.result());
        assertFalse(stalemate.inCheck(ChessGame.Side.BLACK));

        ChessGame kings = ChessGame.fromFen("4k3/8/8/8/8/8/8/4K3 w - - 0 1");
        assertEquals(ChessGame.Result.DRAW, kings.result());
        ChessGame minor = ChessGame.fromFen("4k3/8/8/8/8/8/8/3BK3 w - - 0 1");
        assertEquals(ChessGame.Result.DRAW, minor.result());
    }

    @Test
    void supportsBothCastlesAndMovesTheRook() {
        ChessGame game = ChessGame.fromFen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
        assertTrue(game.legalMoves().contains(move("e1", "g1")));
        assertTrue(game.legalMoves().contains(move("e1", "c1")));
        assertTrue(game.play(move("e1", "g1")));
        assertEquals('K', game.piece(7, 6));
        assertEquals('R', game.piece(7, 5));
        assertEquals('.', game.piece(7, 7));
    }

    @Test
    void forbidsCastlingOutOfCheckAndThroughAttack() {
        ChessGame checked = ChessGame.fromFen("4r1k1/8/8/8/8/8/8/R3K2R w KQ - 0 1");
        assertFalse(checked.legalMoves().contains(move("e1", "g1")));
        assertFalse(checked.legalMoves().contains(move("e1", "c1")));

        ChessGame attacked = ChessGame.fromFen("5rk1/8/8/8/8/8/8/R3K2R w KQ - 0 1");
        assertFalse(attacked.legalMoves().contains(move("e1", "g1")));
        assertTrue(attacked.legalMoves().contains(move("e1", "c1")));
    }

    @Test
    void enPassantRemovesCapturedPawn() {
        ChessGame game = ChessGame.fromFen("4k3/8/8/3pP3/8/8/8/4K3 w - d6 0 1");
        assertTrue(game.legalMoves().contains(move("e5", "d6")));
        assertTrue(game.play(move("e5", "d6")));
        assertEquals('P', game.piece(2, 3));
        assertEquals('.', game.piece(3, 3));
        assertEquals('.', game.piece(3, 4));
    }

    @Test
    void enPassantCannotExposeOwnKing() {
        ChessGame game = ChessGame.fromFen("k3r3/8/8/3pP3/8/8/8/4K3 w - d6 0 1");
        assertFalse(game.legalMoves().contains(move("e5", "d6")));
        assertFalse(game.play(move("e5", "d6")));
    }

    @Test
    void promotionOffersAllFourPieces() {
        ChessGame game = ChessGame.fromFen("4k3/P7/8/8/8/8/8/4K3 w - - 0 1");
        for (char piece : new char[]{'Q', 'R', 'B', 'N'}) {
            assertTrue(game.legalMoves().contains(promote("a7", "a8", piece)));
        }
        assertFalse(game.play(move("a7", "a8")));
        assertTrue(game.play(promote("a7", "a8", 'N')));
        assertEquals('N', game.piece(0, 0));
    }

    @Test
    void kingMayNotMoveIntoCheckOrCaptureEnemyKing() {
        ChessGame game = ChessGame.fromFen("4k3/8/8/7b/8/8/4r3/4K3 w - - 0 1");
        assertTrue(game.inCheck(ChessGame.Side.WHITE));
        assertFalse(game.legalMoves().contains(move("e1", "e2")));
        assertFalse(game.legalMoves().contains(move("e1", "e1")));
    }

    @Test
    void threefoldRepetitionEndsInDraw() {
        ChessGame game = new ChessGame();
        for (int cycle = 0; cycle < 2; cycle++) {
            assertTrue(game.play(move("g1", "f3")));
            assertTrue(game.play(move("g8", "f6")));
            assertTrue(game.play(move("f3", "g1")));
            assertTrue(game.play(move("f6", "g8")));
        }
        assertEquals(ChessGame.Result.DRAW, game.result());
    }

    @Test
    void fiftyMoveRuleEndsInDraw() {
        ChessGame game = ChessGame.fromFen("4k2r/8/8/8/8/8/8/4K2R w Kk - 99 1");
        assertEquals(ChessGame.Result.PLAYING, game.result());
        assertTrue(game.play(move("e1", "d1")));
        assertEquals(ChessGame.Result.DRAW, game.result());
    }

    @Test
    void computerChoosesLegalMoveWithoutMutatingOriginal() {
        ChessGame game = new ChessGame();
        assertTrue(game.play(move("e2", "e4")));
        List<ChessGame.Move> original = game.legalMoves();
        ChessGame.Move choice = new ChessAI(2).chooseMove(game);
        assertNotNull(choice);
        assertTrue(original.contains(choice));
        assertEquals(original, game.legalMoves());
        assertEquals(ChessGame.Side.BLACK, game.turn());
        assertTrue(game.play(choice));
        assertEquals(ChessGame.Side.WHITE, game.turn());
    }
}
