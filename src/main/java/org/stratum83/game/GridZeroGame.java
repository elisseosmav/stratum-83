package org.stratum83.game;

import java.util.ArrayList;
import java.util.List;

public class GridZeroGame {

    public static final char EMPTY = ' ';
    public static final char X = 'X';
    public static final char O = 'O';

    private final char[][] board = new char[3][3];
    private char currentPlayer = X;
    private GameResult result = GameResult.PLAYING;
    private int moveCount;

    public GridZeroGame() {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                board[row][column] = EMPTY;
            }
        }
    }

    private GridZeroGame(GridZeroGame original) {
        for (int row = 0; row < 3; row++) {
            System.arraycopy(original.board[row], 0, board[row], 0, 3);
        }
        currentPlayer = original.currentPlayer;
        result = original.result;
        moveCount = original.moveCount;
    }

    public GridZeroGame copy() {
        return new GridZeroGame(this);
    }

    public char getCell(int row, int column) {
        if (row < 0 || row > 2 || column < 0 || column > 2) {
            throw new IllegalArgumentException("Row and column must be between 0 and 2");
        }
        return board[row][column];
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public GameResult getResult() {
        return result;
    }

    public List<Move> availableMoves() {
        List<Move> moves = new ArrayList<>();
        if (result != GameResult.PLAYING) {
            return moves;
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (board[row][column] == EMPTY) {
                    moves.add(new Move(row, column));
                }
            }
        }
        return moves;
    }

    
    public boolean play(Move move) {
        if (move == null) {
            throw new IllegalArgumentException("Move cannot be null");
        }
        if (result != GameResult.PLAYING) {
            throw new IllegalStateException("The game is already finished");
        }
        if (board[move.row()][move.column()] != EMPTY) {
            return false;
        }

        board[move.row()][move.column()] = currentPlayer;
        moveCount++;

        if (hasWinningLine(currentPlayer)) {
            result = currentPlayer == X ? GameResult.X_WINS : GameResult.O_WINS;
        } else if (moveCount == 9) {
            result = GameResult.DRAW;
        } else {
            currentPlayer = currentPlayer == X ? O : X;
        }
        return true;
    }

    private boolean hasWinningLine(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true;
            }
        }

        return (board[0][0] == player && board[1][1] == player && board[2][2] == player)
                || (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }
}
