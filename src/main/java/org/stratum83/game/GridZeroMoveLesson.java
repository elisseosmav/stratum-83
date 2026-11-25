package org.stratum83.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Guided Minimax lesson grounded in one actual human turn and the computer's
 * recorded next move. Other routes are clearly labeled hypothetical.
 */
public final class GridZeroMoveLesson {

    public record FutureTurn(char player, Move move, String boardAfter,
                             GameResult result) {
    }

    public record Alternative(Move computerMove, int score,
                              List<FutureTurn> future, GameResult outcome) {
        public Alternative {
            future = List.copyOf(future);
        }
    }

    private final int afterHumanMove;
    private final String originalBoard;
    private final MinimaxAnalysis actualDecision;
    private final List<Alternative> alternatives;

    public GridZeroMoveLesson(GridZeroHistory history, int afterHumanMove,
                              MinimaxPlayer player) {
        Objects.requireNonNull(history, "history");
        Objects.requireNonNull(player, "player");
        this.afterHumanMove = afterHumanMove;
        this.actualDecision = history.decisionFollowing(afterHumanMove);
        GridZeroGame position = history.replayThrough(afterHumanMove);
        this.originalBoard = GridZeroHistory.boardText(position);

        List<Alternative> possibilities = new ArrayList<>();
        for (MinimaxAnalysis.Candidate candidate : actualDecision.candidates()) {
            GridZeroGame imagined = position.copy();
            List<FutureTurn> future = new ArrayList<>();
            appendTurn(imagined, candidate.move(), future);

            // Following turns are optimal replies from both sides, not actual
            // subsequent human moves in the saved match.
            while (imagined.getResult() == GameResult.PLAYING) {
                Move optimalReply = player.chooseMove(imagined);
                appendTurn(imagined, optimalReply, future);
            }
            possibilities.add(new Alternative(candidate.move(), candidate.score(),
                    future, imagined.getResult()));
        }
        this.alternatives = List.copyOf(possibilities);
    }

    private static void appendTurn(GridZeroGame game, Move move,
                                   List<FutureTurn> future) {
        char mark = game.getCurrentPlayer();
        if (!game.play(move)) {
            throw new IllegalStateException("Minimax predicted an occupied cell");
        }
        future.add(new FutureTurn(mark, move, GridZeroHistory.boardText(game),
                game.getResult()));
    }

    public int afterHumanMove() {
        return afterHumanMove;
    }

    public int computerMoveNumber() {
        return afterHumanMove + 1;
    }

    public String originalBoard() {
        return originalBoard;
    }

    public MinimaxAnalysis actualDecision() {
        return actualDecision;
    }

    public List<Alternative> alternatives() {
        return alternatives;
    }

    public Alternative selectedByComputer() {
        return alternatives.stream()
                .filter(a -> a.computerMove().equals(actualDecision.chosenMove()))
                .findFirst().orElseThrow();
    }

    public static String outcomeForComputer(GameResult result, char computer) {
        if (result == GameResult.DRAW) return "DRAW";
        boolean wins = (result == GameResult.O_WINS && computer == GridZeroGame.O)
                || (result == GameResult.X_WINS && computer == GridZeroGame.X);
        return wins ? "WIN" : "LOSS";
    }
}
