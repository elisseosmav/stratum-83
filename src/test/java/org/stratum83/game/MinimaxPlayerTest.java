package org.stratum83.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinimaxPlayerTest {

    private final MinimaxPlayer computer = new MinimaxPlayer();

    @Test
    void takesImmediateWin() {
        GridZeroGame game = moves(
                new Move(0, 0), new Move(1, 0),
                new Move(0, 1), new Move(1, 1),
                new Move(2, 2));

        assertEquals(new Move(1, 2), computer.chooseMove(game));
    }

    @Test
    void blocksImmediateLoss() {
        GridZeroGame game = moves(
                new Move(0, 0), new Move(1, 1), new Move(0, 1));

        assertEquals(new Move(0, 2), computer.chooseMove(game));
    }

    @Test
    void leavesOriginalGameUnchanged() {
        GridZeroGame game = new GridZeroGame();
        Move move = computer.chooseMove(game);

        assertNotNull(move);
        assertEquals(0, game.getMoveCount());
        assertEquals(9, game.availableMoves().size());
    }

    @Test
    void rejectsCompletedGames() {
        GridZeroGame game = moves(
                new Move(0, 0), new Move(1, 0),
                new Move(0, 1), new Move(1, 1), new Move(0, 2));
        assertThrows(IllegalArgumentException.class, () -> computer.chooseMove(game));
    }

    @Test
    void neverLosesAgainstAnyLegalOpponentSequence() {
        verifyNeverLoses(new GridZeroGame(), GridZeroGame.X);
        verifyNeverLoses(new GridZeroGame(), GridZeroGame.O);
    }

    private void verifyNeverLoses(GridZeroGame game, char computerMark) {
        if (game.getResult() != GameResult.PLAYING) {
            boolean computerLost = (computerMark == GridZeroGame.X
                    && game.getResult() == GameResult.O_WINS)
                    || (computerMark == GridZeroGame.O
                    && game.getResult() == GameResult.X_WINS);
            assertFalse(computerLost);
            return;
        }

        if (game.getCurrentPlayer() == computerMark) {
            GridZeroGame next = game.copy();
            next.play(computer.chooseMove(game));
            verifyNeverLoses(next, computerMark);
        } else {
            for (Move move : game.availableMoves()) {
                GridZeroGame next = game.copy();
                next.play(move);
                verifyNeverLoses(next, computerMark);
            }
        }
    }

    private GridZeroGame moves(Move... moves) {
        GridZeroGame game = new GridZeroGame();
        for (Move move : moves) {
            assertTrue(game.play(move));
        }
        return game;
    }
}
