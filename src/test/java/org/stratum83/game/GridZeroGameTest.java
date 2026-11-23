package org.stratum83.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GridZeroGameTest {

    @Test
    void startsWithEmptyBoardAndXToPlay() {
        GridZeroGame game = new GridZeroGame();

        assertEquals(GridZeroGame.X, game.getCurrentPlayer());
        assertEquals(GameResult.PLAYING, game.getResult());
        assertEquals(9, game.availableMoves().size());
        assertEquals(GridZeroGame.EMPTY, game.getCell(1, 1));
    }

    @Test
    void alternatesPlayersAndRejectsOccupiedSquare() {
        GridZeroGame game = new GridZeroGame();

        assertTrue(game.play(new Move(0, 0)));
        assertEquals(GridZeroGame.O, game.getCurrentPlayer());
        assertFalse(game.play(new Move(0, 0)));
        assertEquals(1, game.getMoveCount());
        assertTrue(game.play(new Move(1, 1)));
        assertEquals(GridZeroGame.X, game.getCurrentPlayer());
    }

    @Test
    void detectsHorizontalWin() {
        GridZeroGame game = gameAfter(
                new Move(0, 0), new Move(1, 0),
                new Move(0, 1), new Move(1, 1),
                new Move(0, 2));
        assertEquals(GameResult.X_WINS, game.getResult());
    }

    @Test
    void detectsVerticalWin() {
        GridZeroGame game = gameAfter(
                new Move(0, 0), new Move(0, 1),
                new Move(1, 0), new Move(1, 1),
                new Move(2, 2), new Move(2, 1));
        assertEquals(GameResult.O_WINS, game.getResult());
    }

    @Test
    void detectsDiagonalWin() {
        GridZeroGame game = gameAfter(
                new Move(0, 0), new Move(0, 1),
                new Move(1, 1), new Move(0, 2),
                new Move(2, 2));
        assertEquals(GameResult.X_WINS, game.getResult());
    }

    @Test
    void detectsDraw() {
        GridZeroGame game = gameAfter(
                new Move(0, 0), new Move(0, 1), new Move(0, 2),
                new Move(1, 1), new Move(1, 0), new Move(1, 2),
                new Move(2, 1), new Move(2, 0), new Move(2, 2));

        assertEquals(GameResult.DRAW, game.getResult());
        assertTrue(game.availableMoves().isEmpty());
    }

    @Test
    void cannotPlayAfterGameEnds() {
        GridZeroGame game = gameAfter(
                new Move(0, 0), new Move(1, 0),
                new Move(0, 1), new Move(1, 1),
                new Move(0, 2));
        assertThrows(IllegalStateException.class, () -> game.play(new Move(2, 2)));
    }

    @Test
    void copiesAreIndependent() {
        GridZeroGame original = new GridZeroGame();
        GridZeroGame copy = original.copy();
        copy.play(new Move(0, 0));

        assertEquals(GridZeroGame.EMPTY, original.getCell(0, 0));
        assertEquals(GridZeroGame.X, copy.getCell(0, 0));
    }

    @Test
    void rejectsOutOfRangeCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> new Move(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Move(0, 3));
        assertThrows(IllegalArgumentException.class, () -> new GridZeroGame().getCell(5, 0));
    }

    private GridZeroGame gameAfter(Move... moves) {
        GridZeroGame game = new GridZeroGame();
        for (Move move : moves) {
            assertTrue(game.play(move));
        }
        return game;
    }
}
