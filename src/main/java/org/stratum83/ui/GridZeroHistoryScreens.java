package org.stratum83.ui;

import java.util.List;
import java.util.function.IntConsumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.stratum83.game.GameResult;
import org.stratum83.game.GridZeroHistory;
import org.stratum83.game.GridZeroMoveLesson;
import org.stratum83.game.Move;

/**
 * Guided Minimax learning based on recorded game history, not a scripted board.
 * Hypothetical optimal continuations are explicitly distinguished from real moves.
 */
public final class GridZeroHistoryScreens {

    private GridZeroHistoryScreens() {
    }

    public static VBox history(GridZeroHistory history, IntConsumer explain,
                               Runnable backToGame) {
        VBox page = page("GAME HISTORY");
        page.getChildren().add(note(
                "EVERY MOVE BELOW REALLY HAPPENED IN YOUR GAME. "
                + "CHOOSE AN X MOVE TO LEARN HOW THE COMPUTER RESPONDED."));

        List<Integer> explainable = history.explainableMoves();
        for (GridZeroHistory.Turn turn : history.turns()) {
            String who = turn.player() == 'X' ? "YOU (X)" : "COMPUTER (O)";
            String line = "MOVE " + turn.number() + " / " + who
                    + " / ROW " + (turn.move().row() + 1)
                    + ", COLUMN " + (turn.move().column() + 1);
            VBox group = new VBox(6, note(line));
            group.setAlignment(Pos.CENTER);
            if (explainable.contains(turn.number())) {
                int selected = turn.number();
                group.getChildren().add(button(
                        "EXPLAIN HOW O CHOSE MOVE " + (selected + 1),
                        () -> explain.accept(selected)));
            } else if (turn.computerMove()) {
                int selected = turn.number() - 1;
                group.getChildren().add(button(
                        "EXPLAIN THIS COMPUTER MOVE",
                        () -> explain.accept(selected)));
            }
            page.getChildren().add(group);
        }

        if (history.turns().isEmpty()) {
            page.getChildren().add(note(
                    "NO MOVES YET. PLAY GRID ZERO AGAINST THE COMPUTER FIRST."));
        } else if (explainable.isEmpty()) {
            page.getChildren().add(note(
                    "THE COMPUTER HAS NOT MADE AN ANALYZABLE RESPONSE YET."));
        }
        page.getChildren().add(button("BACK TO GAME", backToGame));
        return page;
    }

    public static VBox lesson(GridZeroMoveLesson lesson, Runnable history,
                              Runnable backToGame, Runnable advancedTrace) {
        return new LessonController(lesson, history, backToGame, advancedTrace).page;
    }

    private static final class LessonController {
        private final GridZeroMoveLesson lesson;
        private final Runnable history;
        private final Runnable back;
        private final Runnable technical;
        private final VBox page;
        private final VBox content = new VBox(12);
        private int stage;
        private GridZeroMoveLesson.Alternative example;
        private int futureIndex;

        private LessonController(GridZeroMoveLesson lesson, Runnable history,
                                 Runnable back, Runnable technical) {
            this.lesson = lesson;
            this.history = history;
            this.back = back;
            this.technical = technical;
            page = page("EXPLAIN COMPUTER MOVE " + lesson.computerMoveNumber());
            content.setAlignment(Pos.CENTER);
            page.getChildren().add(content);
            showStage();
        }

        private void showStage() {
            content.getChildren().clear();
            switch (stage) {
                case 0 -> initialPosition();
                case 1 -> chooseHypotheticalMove();
                case 2 -> followHypotheticalFuture();
                case 3 -> showHypotheticalResult();
                case 4 -> showActualChoice();
                default -> throw new IllegalStateException("Unknown lesson stage");
            }
        }

        private void initialPosition() {
            content.getChildren().add(note("STEP 1 / LOOK AT YOUR REAL GAME"));
            content.getChildren().add(note(
                    "AFTER YOUR MOVE " + lesson.afterHumanMove()
                    + " (X), THE COMPUTER (O) MUST CHOOSE MOVE "
                    + lesson.computerMoveNumber() + "."));
            content.getChildren().add(board(lesson.originalBoard(), null, false));
            content.getChildren().add(note(
                    "THIS IS THE ACTUAL BOARD AT THAT POINT IN YOUR GAME. "
                    + "WHAT COULD THE COMPUTER PLAY NEXT?"));
            content.getChildren().add(actions(
                    button("SHOW POSSIBLE O MOVES", () -> advance(1)),
                    button("GAME HISTORY", history), button("BACK TO GAME", back)));
        }

        private void chooseHypotheticalMove() {
            content.getChildren().add(note("STEP 2 / IMAGINE A COMPUTER MOVE"));
            content.getChildren().add(note(
                    "THE COMPUTER CAN PLACE O IN ANY EMPTY SQUARE. "
                    + "CHOOSE ONE AND SEE WHAT COULD HAPPEN."));
            content.getChildren().add(board(lesson.originalBoard(), null, true));
            FlowPane options = actions();
            for (GridZeroMoveLesson.Alternative candidate : lesson.alternatives()) {
                Move m = candidate.computerMove();
                options.getChildren().add(button(
                        "O: ROW " + (m.row() + 1) + ", COL " + (m.column() + 1),
                        () -> {
                            example = candidate;
                            futureIndex = 0;
                            advance(2);
                        }));
            }
            content.getChildren().add(options);
            content.getChildren().add(actions(
                    button("BACK", () -> advance(0)),
                    button("SHOW THE COMPUTER'S REAL CHOICE", () -> advance(4))));
        }

        private void followHypotheticalFuture() {
            GridZeroMoveLesson.FutureTurn step = example.future().get(futureIndex);
            boolean first = futureIndex == 0;
            content.getChildren().add(note("STEP 3 / THINK ABOUT WHAT COULD HAPPEN NEXT"));
            content.getChildren().add(note(first
                    ? "SUPPOSE THE COMPUTER PLACES O AT ROW "
                        + (example.computerMove().row() + 1) + ", COL "
                        + (example.computerMove().column() + 1) + "."
                    : "NOW ASSUME PLAYER " + step.player()
                        + " CHOOSES THE BEST POSSIBLE REPLY."));
            content.getChildren().add(board(step.boardAfter(), step.move(), false));
            content.getChildren().add(note("POSSIBLE FUTURE MOVE "
                    + (futureIndex + 1) + " OF " + example.future().size()
                    + ". THIS IS A PREDICTION, NOT A MOVE FROM YOUR REAL GAME."));

            FlowPane buttons = actions();
            if (futureIndex > 0) {
                buttons.getChildren().add(button("PREVIOUS POSSIBLE MOVE",
                        () -> { futureIndex--; showStage(); }));
            }
            if (futureIndex < example.future().size() - 1) {
                buttons.getChildren().add(button("NEXT POSSIBLE MOVE",
                        () -> { futureIndex++; showStage(); }));
            } else {
                buttons.getChildren().add(button("SEE THE OUTCOME", () -> advance(3)));
            }
            buttons.getChildren().add(button("TRY ANOTHER O MOVE", () -> advance(1)));
            content.getChildren().add(buttons);
        }

        private void showHypotheticalResult() {
            String result = GridZeroMoveLesson.outcomeForComputer(
                    example.outcome(), lesson.actualDecision().player());
            GridZeroMoveLesson.FutureTurn finalStep = example.future()
                    .get(example.future().size() - 1);
            content.getChildren().add(note("STEP 4 / WHAT WOULD BE THE RESULT?"));
            content.getChildren().add(board(finalStep.boardAfter(), null, false));
            content.getChildren().add(note("IF BOTH PLAYERS MAKE THEIR BEST MOVES "
                    + "AFTER THIS CHOICE, THE COMPUTER GETS: " + result + "."));
            content.getChildren().add(note(
                    "THIS IS THE OUTCOME OF A POSSIBLE FUTURE, "
                    + "NOT NECESSARILY WHAT REALLY HAPPENED IN YOUR GAME."));
            content.getChildren().add(actions(
                    button("TRY A DIFFERENT O MOVE", () -> advance(1)),
                    button("COMPARE WITH REAL CHOICE", () -> advance(4))));
        }

        private void showActualChoice() {
            GridZeroMoveLesson.Alternative actual = lesson.selectedByComputer();
            Move m = actual.computerMove();
            String result = GridZeroMoveLesson.outcomeForComputer(
                    actual.outcome(), lesson.actualDecision().player());
            content.getChildren().add(note("FINAL STEP / WHAT THE COMPUTER REALLY DID"));
            content.getChildren().add(board(actual.future().get(0).boardAfter(), m, false));
            content.getChildren().add(note("IN YOUR REAL GAME, COMPUTER O PLAYED ROW "
                    + (m.row() + 1) + ", COLUMN " + (m.column() + 1) + "."));
            content.getChildren().add(note(
                    "MINIMAX COMPARED EVERY POSSIBLE O MOVE, ASSUMED YOU WOULD "
                    + "RESPOND WELL, AND CHOSE THE BEST RESULT IT COULD GUARANTEE: "
                    + result + "."));
            content.getChildren().add(note(
                    "THE COMPUTER'S FIRST MOVE ABOVE IS REAL. THE FUTURE OUTCOME "
                    + "IS A PREDICTION UNDER PERFECT PLAY."));
            content.getChildren().add(actions(
                    button("TRY ANOTHER POSSIBLE MOVE", () -> advance(1)),
                    button("GAME HISTORY", history),
                    button("BACK TO GAME", back)));
            content.getChildren().add(button(
                    "ADVANCED / SEE SEARCH EVENTS", technical));
        }

        private void advance(int next) {
            stage = next;
            showStage();
        }
    }

    private static GridPane board(String nineCells, Move highlight,
                                  boolean markEmptyChoices) {
        if (nineCells == null || nineCells.length() != 9) {
            throw new IllegalArgumentException("Expected nine board cells");
        }
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);
        for (int i = 0; i < 9; i++) {
            int row = i / 3;
            int column = i % 3;
            char cell = nineCells.charAt(i);
            Label mark = new Label(cell == '.' ? (markEmptyChoices ? "?" : "") : String.valueOf(cell));
            mark.getStyleClass().add("history-square");
            if (highlight != null && highlight.row() == row && highlight.column() == column) {
                mark.getStyleClass().add("history-highlight");
            }
            mark.setAlignment(Pos.CENTER);
            mark.setMinSize(54, 54);
            mark.setPrefSize(54, 54);
            mark.setAccessibleText("Row " + (row + 1) + ", column " + (column + 1)
                    + (cell == '.' ? ", empty" : ", " + cell));
            grid.add(mark, column, row);
        }
        return grid;
    }

    private static VBox page(String title) {
        VBox root = new VBox(14);
        Label heading = note(title);
        heading.getStyleClass().add("learning-title");
        root.getChildren().add(heading);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);
        root.setMaxWidth(760);
        return root;
    }

    private static Label note(String text) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        label.setMaxWidth(710);
        label.getStyleClass().add("learning-note");
        return label;
    }

    private static Button button(String label, Runnable command) {
        Button button = new Button(label);
        button.getStyleClass().add("action-button");
        button.setOnAction(event -> command.run());
        return button;
    }

    private static FlowPane actions(Button... buttons) {
        FlowPane row = new FlowPane(10, 8);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(buttons);
        return row;
    }
}
