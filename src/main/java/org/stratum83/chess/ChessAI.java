package org.stratum83.chess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ChessAI {
    private static final int MATE = 100_000;
    private final int depth;

    public ChessAI(int depth) {
        if (depth < 1 || depth > 4) {
            throw new IllegalArgumentException("Depth must be from 1 to 4");
        }
        this.depth = depth;
    }

    public ChessGame.Move chooseMove(ChessGame game) {
        if (game.turn() != ChessGame.Side.BLACK
                || game.result() != ChessGame.Result.PLAYING) {
            throw new IllegalArgumentException("The computer must be to move");
        }
        List<ChessGame.Move> candidates = ordered(game);
        ChessGame.Move best = null;
        int bestValue = Integer.MIN_VALUE;
        int alpha = -MATE - 10;
        for (ChessGame.Move move : candidates) {
            ChessGame next = game.copy();
            next.play(move);
            int score = search(next, depth - 1, alpha, MATE + 10);
            if (best == null || score > bestValue) {
                bestValue = score;
                best = move;
            }
            alpha = Math.max(alpha, bestValue);
        }
        return best;
    }

    private int search(ChessGame state, int depthLeft, int alpha, int beta) {
        if (depthLeft == 0 || state.result() != ChessGame.Result.PLAYING) {
            return evaluate(state);
        }
        boolean maximize = state.turn() == ChessGame.Side.BLACK;
        int best = maximize ? -MATE - 10 : MATE + 10;
        for (ChessGame.Move move : ordered(state)) {
            ChessGame next = state.copy();
            next.play(move);
            int value = search(next, depthLeft - 1, alpha, beta);
            if (maximize) {
                best = Math.max(best, value);
                alpha = Math.max(alpha, best);
            } else {
                best = Math.min(best, value);
                beta = Math.min(beta, best);
            }
            if (alpha >= beta) break;
        }
        return best;
    }

    private static List<ChessGame.Move> ordered(ChessGame state) {
        List<ChessGame.Move> moves = new ArrayList<>(state.legalMoves());
        moves.sort(Comparator.comparingInt((ChessGame.Move m) -> {
            char taken = state.piece(m.toRow(), m.toCol());
            int value = Math.abs(pieceValue(taken));
            return value * 10 + (m.promotes() ? 800 : 0);
        }).reversed());
        return moves;
    }

    private static int evaluate(ChessGame state) {
        return switch (state.result()) {
            case BLACK_WINS -> MATE;
            case WHITE_WINS -> -MATE;
            case DRAW -> 0;
            case PLAYING -> boardScore(state);
        };
    }

    private static int boardScore(ChessGame game) {
        int score = 0;
        for (int row = 0; row < 8; row++) for (int col = 0; col < 8; col++) {
            char piece = game.piece(row, col);
            if (piece == '.') continue;
            int value = pieceValue(piece);
            int center = (3 - Math.abs(3 - row)) + (3 - Math.abs(3 - col));
            char type = Character.toLowerCase(piece);
            if (type == 'n' || type == 'b' || type == 'p') value += center * 3;
            if (type == 'p') {
                value += (Character.isUpperCase(piece) ? (6 - row) : (row - 1)) * 5;
            }
            score += Character.isLowerCase(piece) ? value : -value;
        }
        return score;
    }

    private static int pieceValue(char piece) {
        return switch (Character.toLowerCase(piece)) {
            case 'p' -> 100;
            case 'n' -> 320;
            case 'b' -> 335;
            case 'r' -> 500;
            case 'q' -> 900;
            case 'k' -> 0;
            default -> 0;
        };
    }
}
