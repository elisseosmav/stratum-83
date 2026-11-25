package org.stratum83.game;

public final class MinimaxPlayer {

    public Move chooseMove(GridZeroGame game) {
        if (game == null || game.getResult() != GameResult.PLAYING) {
            throw new IllegalArgumentException("A game in progress is required");
        }
        char computer = game.getCurrentPlayer();
        int bestScore = Integer.MIN_VALUE;
        Move chosen = null;
        for (Move move : game.availableMoves()) {
            GridZeroGame next = game.copy();
            next.play(move);
            int score = score(next, computer, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (score > bestScore) {
                bestScore = score;
                chosen = move;
            }
        }
        return chosen;
    }

    private int score(GridZeroGame game, char computer, int depth,
                      int alpha, int beta) {
        GameResult result = game.getResult();
        if (result != GameResult.PLAYING) {
            if (result == GameResult.DRAW) return 0;
            boolean win = (result == GameResult.X_WINS && computer == GridZeroGame.X)
                    || (result == GameResult.O_WINS && computer == GridZeroGame.O);
            return win ? 10 - depth : depth - 10;
        }
        boolean maximizing = game.getCurrentPlayer() == computer;
        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Move move : game.availableMoves()) {
            GridZeroGame next = game.copy();
            next.play(move);
            int value = score(next, computer, depth + 1, alpha, beta);
            if (maximizing) {
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
}
