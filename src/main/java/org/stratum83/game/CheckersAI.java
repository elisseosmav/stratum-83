package org.stratum83.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class CheckersAI {
    private static final int WIN_SCORE = 100_000;
    private final int depth;

    public CheckersAI(int depth) {
        if (depth < 1 || depth > 8) {
            throw new IllegalArgumentException("Search depth must be between 1 and 8");
        }
        this.depth = depth;
    }

    public CheckersGame.Turn chooseMove(CheckersGame game) {
        if (game.sideToMove() != CheckersGame.Side.COMPUTER
                || game.outcome() != CheckersGame.Outcome.PLAYING) {
            throw new IllegalArgumentException("It must be the computer's turn");
        }

        CheckersGame.Turn best = null;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        for (CheckersGame.Turn move : ordered(game.legalMoves())) {
            CheckersGame next = game.copy();
            next.play(move);
            int score = search(next, depth - 1, alpha, Integer.MAX_VALUE);
            if (best == null || score > bestScore) {
                best = move;
                bestScore = score;
            }
            alpha = Math.max(alpha, bestScore);
        }
        return best;
    }

    private int search(CheckersGame position, int depthLeft, int alpha, int beta) {
        if (position.outcome() != CheckersGame.Outcome.PLAYING || depthLeft == 0) {
            return evaluate(position);
        }
        boolean maximizing = position.sideToMove() == CheckersGame.Side.COMPUTER;
        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (CheckersGame.Turn move : ordered(position.legalMoves())) {
            CheckersGame next = position.copy();
            next.play(move);
            int score = search(next, depthLeft - 1, alpha, beta);
            if (maximizing) {
                best = Math.max(best, score);
                alpha = Math.max(alpha, best);
            } else {
                best = Math.min(best, score);
                beta = Math.min(beta, best);
            }
            if (alpha >= beta) break;
        }
        return best;
    }

    private static int evaluate(CheckersGame game) {
        return switch (game.outcome()) {
            case COMPUTER_WINS -> WIN_SCORE;
            case HUMAN_WINS -> -WIN_SCORE;
            case DRAW -> 0;
            case PLAYING -> evaluateBoard(game);
        };
    }

    private static int evaluateBoard(CheckersGame game) {
        int score = 0;
        for (int row = 0; row < CheckersGame.SIZE; row++) {
            for (int col = 0; col < CheckersGame.SIZE; col++) {
                char piece = game.getCell(row, col);
                if (piece == CheckersGame.EMPTY) continue;
                int value = Character.isUpperCase(piece) ? 175 : 100;
                if (piece == 'c') value += row * 4;
                if (piece == 'h') value += (CheckersGame.SIZE - 1 - row) * 4;
                if (col >= 2 && col <= 5) value += 5;
                score += CheckersGame.belongsTo(piece, CheckersGame.Side.COMPUTER)
                        ? value : -value;
            }
        }
        int mobility = game.legalMoves().size();
        score += (game.sideToMove() == CheckersGame.Side.COMPUTER ? 1 : -1)
                * Math.min(mobility, 12) * 2;
        return score;
    }

    private static List<CheckersGame.Turn> ordered(List<CheckersGame.Turn> moves) {
        List<CheckersGame.Turn> result = new ArrayList<>(moves);
        result.sort(Comparator.comparingInt(
                (CheckersGame.Turn turn) -> turn.captures().size()).reversed());
        return result;
    }
}
