package org.stratum83.game;

import java.util.List;

/**
 * Read-only explanation of a Minimax decision. All coordinates start at zero.
 * A recorded search is a bounded sample, not necessarily the whole search tree.
 */
public record MinimaxAnalysis(
        char player, Move chosenMove, int bestScore,
        List<Candidate> candidates, List<Step> steps,
        int evaluatedNodes, int alphaBetaCutoffs, boolean truncated) {

    public MinimaxAnalysis {
        candidates = List.copyOf(candidates);
        steps = List.copyOf(steps);
    }

    /** An exact minimax score for one legal root move. */
    public record Candidate(Move move, int score) {
    }

    /** One event recorded while evaluating a possible future board. */
    public record Step(String kind, String board, int depth, String explanation) {
    }
}
