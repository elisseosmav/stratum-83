package org.stratum83.game;

/** A perfect-play computer opponent using minimax with alpha-beta pruning. */
public class MinimaxPlayer {

    public Move chooseMove(GridZeroGame game) {
        if (game == null || game.getResult() != GameResult.PLAYING) {
            throw new IllegalArgumentException("A game in progress is required");
        }

        char computer = game.getCurrentPlayer();
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        int alpha = Integer.MIN_VALUE;

        for (Move move : game.availableMoves()) {
            GridZeroGame next = game.copy();
            next.play(move);
            int score = minimax(next, computer, 1, alpha, Integer.MAX_VALUE);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
            alpha = Math.max(alpha, bestScore);
        }
        return bestMove;
    }

    private int minimax(GridZeroGame game, char computer, int depth, int alpha, int beta) {
        GameResult result = game.getResult();
        if (result != GameResult.PLAYING) {
            if (result == GameResult.DRAW) {
                return 0;
            }
            boolean computerWon = (result == GameResult.X_WINS && computer == GridZeroGame.X)
                    || (result == GameResult.O_WINS && computer == GridZeroGame.O);
            return computerWon ? 10 - depth : depth - 10;
        }

        boolean maximizing = game.getCurrentPlayer() == computer;
        int bestScore = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : game.availableMoves()) {
            GridZeroGame next = game.copy();
            next.play(move);
            int score = minimax(next, computer, depth + 1, alpha, beta);

            if (maximizing) {
                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, bestScore);
            } else {
                bestScore = Math.min(bestScore, score);
                beta = Math.min(beta, bestScore);
            }
            if (beta <= alpha) {
                break;
            }
        }
        return bestScore;
    }
}
