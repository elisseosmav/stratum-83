package org.stratum83.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Perfect Tic-Tac-Toe player using minimax with alpha-beta pruning.
 * The same search powers both the moves and the optional learning trace.
 */
public class MinimaxPlayer {

    private static final int TRACE_LIMIT = 160;

    public Move chooseMove(GridZeroGame game) {
        return calculate(game, false).chosenMove();
    }

    /** Examines an ongoing game without changing it. */
    public MinimaxAnalysis analyzeMove(GridZeroGame game) {
        return calculate(game, true);
    }

    private MinimaxAnalysis calculate(GridZeroGame game, boolean recordSteps) {
        if (game == null || game.getResult() != GameResult.PLAYING) {
            throw new IllegalArgumentException("A game in progress is required");
        }
        char player = game.getCurrentPlayer();
        Recorder recorder = new Recorder(recordSteps);
        List<MinimaxAnalysis.Candidate> candidates = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (Move move : game.availableMoves()) {
            GridZeroGame next = game.copy();
            next.play(move);

            recorder.add("TRY MOVE", next, 1, "Evaluate row " + (move.row() + 1)
                    + ", column " + (move.column() + 1) + " for " + player + ".");

            // Independent full windows give every root candidate its exact score.
            // Internal branches still use alpha-beta pruning.
            int score = minimax(next, player, 1,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, recorder);
            candidates.add(new MinimaxAnalysis.Candidate(move, score));
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        recorder.addFinal(game, bestMove, bestScore);
        return new MinimaxAnalysis(player, bestMove, bestScore, candidates,
                recorder.steps, recorder.nodes, recorder.cutoffs, recorder.truncated);
    }

    private int minimax(GridZeroGame game, char player, int depth,
                        int alpha, int beta, Recorder recorder) {
        recorder.nodes++;
        GameResult result = game.getResult();
        if (result != GameResult.PLAYING) {
            int score = terminalScore(result, player, depth);
            recorder.add("OUTCOME", game, depth, "Terminal result: " + result
                    + ". Score from " + player + "'s perspective: " + score + ".");
            return score;
        }

        boolean maximizing = game.getCurrentPlayer() == player;
        recorder.add(maximizing ? "MAX" : "MIN", game, depth,
                "It is " + game.getCurrentPlayer() + "'s turn: "
                        + (maximizing ? "seek the highest score." : "seek the lowest score."));

        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Move move : game.availableMoves()) {
            GridZeroGame next = game.copy();
            next.play(move);
            int score = minimax(next, player, depth + 1, alpha, beta, recorder);

            if (maximizing) {
                best = Math.max(best, score);
                alpha = Math.max(alpha, best);
            } else {
                best = Math.min(best, score);
                beta = Math.min(beta, best);
            }
            if (beta <= alpha) {
                recorder.cutoffs++;
                recorder.add("PRUNE", game, depth,
                        "Alpha-beta cutoff: remaining sibling moves cannot change "
                                + "this branch's decision.");
                break;
            }
        }
        return best;
    }

    private int terminalScore(GameResult result, char player, int depth) {
        if (result == GameResult.DRAW) {
            return 0;
        }
        boolean won = (result == GameResult.X_WINS && player == GridZeroGame.X)
                || (result == GameResult.O_WINS && player == GridZeroGame.O);
        return won ? 10 - depth : depth - 10;
    }

    private static String boardText(GridZeroGame game) {
        StringBuilder text = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char mark = game.getCell(row, col);
                text.append(mark == GridZeroGame.EMPTY ? '.' : mark);
                if (col < 2) text.append("  ");
            }
            if (row < 2) text.append("\n");
        }
        return text.toString();
    }

    private static final class Recorder {
        private final boolean enabled;
        private final List<MinimaxAnalysis.Step> steps = new ArrayList<>();
        private int nodes;
        private int cutoffs;
        private boolean truncated;

        private Recorder(boolean enabled) {
            this.enabled = enabled;
        }

        private void add(String kind, GridZeroGame game, int depth, String explanation) {
            if (!enabled) return;
            // Reserve the last slot for the final selection.
            if (steps.size() >= TRACE_LIMIT - 1) {
                truncated = true;
                return;
            }
            steps.add(new MinimaxAnalysis.Step(kind, boardText(game), depth, explanation));
        }

        private void addFinal(GridZeroGame game, Move move, int score) {
            if (!enabled) return;
            steps.add(new MinimaxAnalysis.Step("CHOSEN", boardText(game), 0,
                    "Selected row " + (move.row() + 1) + ", column " + (move.column() + 1)
                            + " with score " + score + ". Ties use board order."));
        }
    }
}
