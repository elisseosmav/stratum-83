package org.stratum83.research;

import java.util.List;
import java.util.Random;
import org.stratum83.game.GameResult;
import org.stratum83.game.GridZeroGame;
import org.stratum83.game.MinimaxPlayer;
import org.stratum83.game.Move;

/** Runs a seeded experiment against a random opponent with alternating first player. */
public class ExperimentRunner {

    public ExperimentResult run(int games, long seed) {
        if (games < 1) {
            throw new IllegalArgumentException("Number of games must be positive");
        }

        Random random = new Random(seed);
        MinimaxPlayer computer = new MinimaxPlayer();
        int wins = 0;
        int draws = 0;
        int losses = 0;

        for (int i = 0; i < games; i++) {
            GridZeroGame game = new GridZeroGame();
            char computerMark = i % 2 == 0 ? GridZeroGame.X : GridZeroGame.O;

            while (game.getResult() == GameResult.PLAYING) {
                Move move;
                if (game.getCurrentPlayer() == computerMark) {
                    move = computer.chooseMove(game);
                } else {
                    List<Move> options = game.availableMoves();
                    move = options.get(random.nextInt(options.size()));
                }
                game.play(move);
            }

            if (game.getResult() == GameResult.DRAW) {
                draws++;
            } else if ((game.getResult() == GameResult.X_WINS && computerMark == GridZeroGame.X)
                    || (game.getResult() == GameResult.O_WINS && computerMark == GridZeroGame.O)) {
                wins++;
            } else {
                losses++;
            }
        }
        return new ExperimentResult(games, wins, draws, losses, seed);
    }
}
