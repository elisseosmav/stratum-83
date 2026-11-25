package org.stratum83.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Actual, replayable turns from one local Grid Zero match.
 * Each recorded computer decision is attached to the move it really made.
 */
public final class GridZeroHistory {
    public record Turn(int number, char player, Move move, String boardAfter,
                       MinimaxAnalysis computerDecision) {
        public boolean computerMove() {
            return computerDecision != null;
        }
    }

    private final List<Turn> turns = new ArrayList<>();

    public void record(Move move, GridZeroGame positionAfter, MinimaxAnalysis decision) {
        Objects.requireNonNull(move, "move");
        Objects.requireNonNull(positionAfter, "positionAfter");
        int expectedNumber = turns.size() + 1;
        if (positionAfter.getMoveCount() != expectedNumber
                || positionAfter.getCell(move.row(), move.column()) == GridZeroGame.EMPTY) {
            throw new IllegalArgumentException("Recorded move must match the played game");
        }

        GridZeroGame replay = replayThrough(turns.size());
        char mark = replay.getCurrentPlayer();
        if (!replay.play(move) || !sameBoard(replay, positionAfter)) {
            throw new IllegalArgumentException("The new move must follow the actual history");
        }
        if (decision != null && (decision.player() != mark
                || !decision.chosenMove().equals(move))) {
            throw new IllegalArgumentException("The recorded AI decision must match this move");
        }
        turns.add(new Turn(expectedNumber, mark, move, boardText(positionAfter), decision));
    }

    public List<Turn> turns() {
        return List.copyOf(turns);
    }

    /** Returns a new, independently playable game at the selected turn. */
    public GridZeroGame replayThrough(int moveNumber) {
        if (moveNumber < 0 || moveNumber > turns.size()) {
            throw new IllegalArgumentException("Move number is not in this game history");
        }
        GridZeroGame replay = new GridZeroGame();
        for (int i = 0; i < moveNumber; i++) {
            if (!replay.play(turns.get(i).move())) {
                throw new IllegalStateException("Invalid saved match history");
            }
        }
        return replay;
    }

    /** After human move N, retrieves the analysis used for actual computer move N+1. */
    public MinimaxAnalysis decisionFollowing(int humanMoveNumber) {
        if (humanMoveNumber < 1 || humanMoveNumber >= turns.size()) {
            throw new IllegalArgumentException("Select a human move followed by a computer move");
        }
        Turn human = turns.get(humanMoveNumber - 1);
        Turn computer = turns.get(humanMoveNumber);
        if (human.player() != GridZeroGame.X
                || computer.player() != GridZeroGame.O
                || computer.computerDecision() == null) {
            throw new IllegalArgumentException("No recorded computer reply for this move");
        }
        return computer.computerDecision();
    }

    public List<Integer> explainableMoves() {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i < turns.size(); i++) {
            Turn human = turns.get(i - 1);
            Turn computer = turns.get(i);
            if (human.player() == GridZeroGame.X && computer.computerDecision() != null) {
                result.add(human.number());
            }
        }
        return List.copyOf(result);
    }

    private static boolean sameBoard(GridZeroGame a, GridZeroGame b) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (a.getCell(row, col) != b.getCell(row, col)) {
                    return false;
                }
            }
        }
        return a.getResult() == b.getResult();
    }

    public static String boardText(GridZeroGame game) {
        StringBuilder text = new StringBuilder(9);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char cell = game.getCell(row, col);
                text.append(cell == GridZeroGame.EMPTY ? '.' : cell);
            }
        }
        return text.toString();
    }
}
