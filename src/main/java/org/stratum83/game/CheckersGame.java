package org.stratum83.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CheckersGame {
    public static final int SIZE = 8;
    public static final char EMPTY = '.';

    public enum Side {
        HUMAN, COMPUTER;

        public Side opposite() {
            return this == HUMAN ? COMPUTER : HUMAN;
        }
    }

    public enum Outcome {
        PLAYING, HUMAN_WINS, COMPUTER_WINS, DRAW
    }

    public record Square(int row, int column) {
    }

    public record Turn(List<Square> path, List<Square> captures) {
        public Turn {
            path = List.copyOf(path);
            captures = List.copyOf(captures);
            if (path.size() < 2) {
                throw new IllegalArgumentException("A turn needs a start and a destination");
            }
        }

        public Square start() {
            return path.getFirst();
        }

        public Square destination() {
            return path.getLast();
        }

        public boolean isCapture() {
            return !captures.isEmpty();
        }
    }

    private final char[][] board;
    private Side sideToMove;
    private Outcome outcome;
    private int quietPlies;

    public CheckersGame() {
        board = new char[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            Arrays.fill(board[row], EMPTY);
            for (int column = 0; column < SIZE; column++) {
                if ((row + column) % 2 == 1) {
                    if (row < 3) board[row][column] = 'c';
                    if (row > 4) board[row][column] = 'h';
                }
            }
        }
        sideToMove = Side.HUMAN;
        outcome = Outcome.PLAYING;
    }

    private CheckersGame(char[][] board, Side sideToMove, Outcome outcome, int quietPlies) {
        this.board = board;
        this.sideToMove = sideToMove;
        this.outcome = outcome;
        this.quietPlies = quietPlies;
    }

    static CheckersGame fromRows(Side sideToMove, String... rows) {
        if (rows.length != SIZE) throw new IllegalArgumentException("Expected eight rows");
        char[][] position = new char[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            if (rows[row].length() != SIZE) throw new IllegalArgumentException("Expected eight columns");
            for (int col = 0; col < SIZE; col++) {
                char piece = rows[row].charAt(col);
                if (piece != EMPTY && piece != 'h' && piece != 'H' && piece != 'c' && piece != 'C') {
                    throw new IllegalArgumentException("Unknown piece");
                }
                if (piece != EMPTY && (row + col) % 2 == 0) {
                    throw new IllegalArgumentException("Pieces must occupy dark squares");
                }
                position[row][col] = piece;
            }
        }
        return new CheckersGame(position, sideToMove, Outcome.PLAYING, 0);
    }

    CheckersGame copy() {
        return new CheckersGame(copyBoard(board), sideToMove, outcome, quietPlies);
    }

    public char getCell(int row, int column) {
        return board[row][column];
    }

    public Side sideToMove() {
        return sideToMove;
    }

    public Outcome outcome() {
        return outcome;
    }

    public int countPieces(Side side) {
        int count = 0;
        for (char[] row : board) {
            for (char piece : row) {
                if (belongsTo(piece, side)) count++;
            }
        }
        return count;
    }

    public List<Turn> legalMoves() {
        if (outcome != Outcome.PLAYING) return List.of();
        List<Turn> captures = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (belongsTo(board[row][col], sideToMove)) {
                    collectCaptures(board, row, col, List.of(new Square(row, col)), List.of(), captures);
                }
            }
        }
        if (!captures.isEmpty()) return List.copyOf(captures);

        List<Turn> simple = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                char piece = board[row][col];
                if (!belongsTo(piece, sideToMove)) continue;
                for (int[] direction : directions(piece)) {
                    int nextRow = row + direction[0];
                    int nextCol = col + direction[1];
                    if (inBounds(nextRow, nextCol) && board[nextRow][nextCol] == EMPTY) {
                        simple.add(new Turn(List.of(new Square(row, col),
                                new Square(nextRow, nextCol)), List.of()));
                    }
                }
            }
        }
        return List.copyOf(simple);
    }

    public boolean play(Turn turn) {
        if (turn == null || !legalMoves().contains(turn)) return false;

        Square start = turn.start();
        char piece = board[start.row()][start.column()];
        board[start.row()][start.column()] = EMPTY;
        for (Square captured : turn.captures()) {
            board[captured.row()][captured.column()] = EMPTY;
        }
        Square finish = turn.destination();
        boolean promoted = false;
        if (piece == 'h' && finish.row() == 0) {
            piece = 'H';
            promoted = true;
        } else if (piece == 'c' && finish.row() == SIZE - 1) {
            piece = 'C';
            promoted = true;
        }
        board[finish.row()][finish.column()] = piece;
        quietPlies = turn.isCapture() || promoted ? 0 : quietPlies + 1;

        Side winner = sideToMove;
        sideToMove = sideToMove.opposite();
        if (legalMoves().isEmpty()) {
            outcome = winner == Side.HUMAN ? Outcome.HUMAN_WINS : Outcome.COMPUTER_WINS;
        } else if (quietPlies >= 80) {
            outcome = Outcome.DRAW;
        }
        return true;
    }

    private static void collectCaptures(char[][] position, int row, int col,
                                        List<Square> path, List<Square> taken, List<Turn> turns) {
        char piece = position[row][col];
        boolean jumped = false;
        for (int[] direction : directions(piece)) {
            int midRow = row + direction[0];
            int midCol = col + direction[1];
            int endRow = row + 2 * direction[0];
            int endCol = col + 2 * direction[1];
            if (!inBounds(endRow, endCol)
                    || !isOpponent(position[midRow][midCol], piece)
                    || position[endRow][endCol] != EMPTY) continue;
            jumped = true;

            char[][] next = copyBoard(position);
            next[row][col] = EMPTY;
            next[midRow][midCol] = EMPTY;
            next[endRow][endCol] = piece;

            List<Square> nextPath = new ArrayList<>(path);
            nextPath.add(new Square(endRow, endCol));
            List<Square> nextTaken = new ArrayList<>(taken);
            nextTaken.add(new Square(midRow, midCol));

            boolean crowned = (piece == 'h' && endRow == 0)
                    || (piece == 'c' && endRow == SIZE - 1);
            if (crowned) {
                turns.add(new Turn(nextPath, nextTaken));
            } else {
                collectCaptures(next, endRow, endCol, nextPath, nextTaken, turns);
            }
        }
        if (!jumped && !taken.isEmpty()) turns.add(new Turn(path, taken));
    }

    static boolean belongsTo(char piece, Side side) {
        return side == Side.HUMAN ? piece == 'h' || piece == 'H'
                : piece == 'c' || piece == 'C';
    }

    private static boolean isOpponent(char other, char piece) {
        if (piece == 'h' || piece == 'H') return other == 'c' || other == 'C';
        return other == 'h' || other == 'H';
    }

    private static int[][] directions(char piece) {
        if (piece == 'H' || piece == 'C') {
            return new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        }
        if (piece == 'h') return new int[][]{{-1, -1}, {-1, 1}};
        return new int[][]{{1, -1}, {1, 1}};
    }

    private static char[][] copyBoard(char[][] original) {
        char[][] result = new char[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            result[row] = original[row].clone();
        }
        return result;
    }

    private static boolean inBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
}
