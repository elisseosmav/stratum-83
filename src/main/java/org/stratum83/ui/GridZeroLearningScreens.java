package org.stratum83.ui;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.stratum83.education.WorstCasePolicy;
import org.stratum83.game.MinimaxAnalysis;
import org.stratum83.game.Move;

/**
 * Grid Zero's human-readable teaching screens.
 * All move choices, scores and search events come from the real Minimax engine.
 */
public final class GridZeroLearningScreens {

    private GridZeroLearningScreens() {
    }

    public static VBox trace(MinimaxAnalysis analysis, Runnable back,
                             Runnable explain, Runnable apply) {
        VBox content = page("WHY DID THE COMPUTER CHOOSE THIS MOVE?");
        content.getChildren().add(note("GRID ZERO / MINIMAX / THE LAST COMPUTER TURN"));

        Label conclusion = note(decisionSummary(analysis));
        conclusion.getStyleClass().add("learning-conclusion");
        content.getChildren().add(conclusion);

        Label choicesTitle = note("THE COMPUTER COMPARED THESE POSSIBLE MOVES:");
        content.getChildren().add(choicesTitle);
        VBox alternatives = new VBox(5);
        alternatives.setAlignment(Pos.CENTER);
        for (MinimaxAnalysis.Candidate candidate : analysis.candidates()) {
            Move move = candidate.move();
            boolean chosen = move.equals(analysis.chosenMove());
            String text = (chosen ? "SELECTED  " : "TESTED    ")
                    + "ROW " + (move.row() + 1) + ", COL " + (move.column() + 1)
                    + "  →  " + outcomeLabel(candidate.score())
                    + " (" + signed(candidate.score()) + ")";
            Label option = note(text);
            option.getStyleClass().add(chosen ? "learning-choice-selected" : "learning-choice");
            alternatives.getChildren().add(option);
        }
        content.getChildren().add(alternatives);
        content.getChildren().add(note(
                "SCORES ARE FROM THE COMPUTER'S VIEW: POSITIVE = WIN, 0 = DRAW, NEGATIVE = LOSS."));

        content.getChildren().add(note("NOW EXPLORE HOW IT REACHED THAT DECISION"));
        Label stepTitle = note("");
        Label stepExplanation = note("");
        stepExplanation.setWrapText(true);
        stepExplanation.setMaxWidth(650);

        GridPane board = new GridPane();
        board.setHgap(4);
        board.setVgap(4);
        board.setAlignment(Pos.CENTER);
        Label[] squares = new Label[9];
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                Label cell = new Label();
                cell.getStyleClass().add("trace-square");
                cell.setAlignment(Pos.CENTER);
                cell.setMinSize(38, 38);
                cell.setPrefSize(38, 38);
                board.add(cell, column, row);
                squares[row * 3 + column] = cell;
            }
        }

        final int[] current = {0};
        Button previous = button("PREVIOUS", () -> {});
        Button next = button("NEXT", () -> {});
        Button showDecision = button("SHOW FINAL CHOICE", () -> {});
        Runnable redraw = () -> {
            MinimaxAnalysis.Step step = analysis.steps().get(current[0]);
            boolean finalChoice = "CHOSEN".equals(step.kind());
            stepTitle.setText("SEARCH STEP " + (current[0] + 1) + " OF "
                    + analysis.steps().size() + " — " + step.kind());
            stepExplanation.setText(simpleStepExplanation(analysis, step));
            showBoard(squares, step.board());
            previous.setDisable(current[0] == 0);
            next.setDisable(current[0] == analysis.steps().size() - 1);
            showDecision.setDisable(finalChoice);
        };
        previous.setOnAction(event -> { current[0]--; redraw.run(); });
        next.setOnAction(event -> { current[0]++; redraw.run(); });
        showDecision.setOnAction(event -> {
            current[0] = analysis.steps().size() - 1;
            redraw.run();
        });
        redraw.run();

        content.getChildren().addAll(stepTitle, board, stepExplanation,
                note("BEFORE THE FINAL STEP, EACH BOARD IS A POSSIBLE FUTURE — NOT AN ACTUAL MOVE."),
                actions(previous, next, showDecision),
                note("OPTIONAL SEARCH DETAILS: " + analysis.evaluatedNodes()
                        + " POSITIONS EVALUATED / " + analysis.alphaBetaCutoffs()
                        + " ALPHA-BETA CUTOFFS."),
                note(analysis.truncated()
                        ? "LONG SEARCH: FIRST RECORDED STEPS AND FINAL DECISION ARE SHOWN."
                        : "SHORT SEARCH: ALL RECORDED STEPS ARE SHOWN."),
                actions(button("EXPLAIN", explain), button("APPLY", apply),
                        button("BACK TO GAME", back)));
        return content;
    }

    /** Translate a score into an outcome understandable without Minimax terminology. */
    static String outcomeLabel(int score) {
        if (score > 0) return "WIN";
        if (score < 0) return "LOSS";
        return "DRAW";
    }

    static String decisionSummary(MinimaxAnalysis analysis) {
        Move choice = analysis.chosenMove();
        String position = "ROW " + (choice.row() + 1) + ", COLUMN " + (choice.column() + 1);
        String outcome = outcomeLabel(analysis.bestScore());
        return switch (outcome) {
            case "WIN" -> "COMPUTER CHOSE " + position
                    + " BECAUSE IT CAN FORCE A WIN AGAINST THE BEST DEFENCE.";
            case "DRAW" -> "COMPUTER CHOSE " + position
                    + " BECAUSE IT CAN GUARANTEE A DRAW AND AVOID LOSING.";
            default -> "COMPUTER CHOSE " + position
                    + " BECAUSE EVERY MOVE CAN LOSE AGAINST PERFECT PLAY; "
                    + "THIS CHOICE DELAYS THE LOSS AS LONG AS POSSIBLE.";
        };
    }

    private static String simpleStepExplanation(MinimaxAnalysis analysis,
                                                MinimaxAnalysis.Step step) {
        char opponent = analysis.player() == 'X' ? 'O' : 'X';
        return switch (step.kind()) {
            case "TRY MOVE" -> "WHAT IF THE COMPUTER PLAYS HERE? "
                    + "THIS BOARD IS A POSSIBLE MOVE, NOT THE FINAL CHOICE. "
                    + step.explanation();
            case "MIN" -> "ASSUME " + opponent
                    + " RESPONDS AS STRONGLY AS POSSIBLE. "
                    + "WHICH REPLY WOULD BE WORST FOR THE COMPUTER?";
            case "MAX" -> "IT IS THE COMPUTER'S TURN IN THIS POSSIBLE FUTURE. "
                    + "IT WOULD CHOOSE THE MOVE WITH THE BEST RESULT.";
            case "OUTCOME" -> "THIS POSSIBLE SEQUENCE HAS FINISHED. "
                    + step.explanation();
            case "PRUNE" -> "THE COMPUTER CAN SKIP OTHER MOVES IN THIS BRANCH. "
                    + "THEY CANNOT IMPROVE ITS FINAL DECISION.";
            case "CHOSEN" -> "THIS IS THE MOVE THE COMPUTER REALLY SELECTED. "
                    + "IT COMPARED THE POSSIBLE RESULTS AND PICKED "
                    + outcomeLabel(analysis.bestScore()) + ".";
            default -> step.explanation();
        };
    }

    private static void showBoard(Label[] squares, String board) {
        String marks = board.replace(" ", "").replace("\n", "").replace("\r", "");
        if (marks.length() != 9) {
            throw new IllegalArgumentException("Expected a 3x3 search board");
        }
        for (int i = 0; i < squares.length; i++) {
            Label square = squares[i];
            square.getStyleClass().removeAll("trace-x", "trace-o", "trace-empty");
            char mark = marks.charAt(i);
            if (mark == 'X') square.getStyleClass().add("trace-x");
            else if (mark == 'O') square.getStyleClass().add("trace-o");
            else square.getStyleClass().add("trace-empty");
            square.setText(mark == '.' ? "" : String.valueOf(mark));
            square.setAccessibleText("Row " + (i / 3 + 1) + ", column "
                    + (i % 3 + 1) + ": " + (mark == '.' ? "empty" : String.valueOf(mark)));
        }
    }

    public static VBox explain(MinimaxAnalysis analysis, Runnable trace,
                               Runnable back, Runnable apply) {
        VBox content = page("EXPLAIN / MINIMAX");
        content.getChildren().add(note(
                "MINIMAX MEANS: THINK AHEAD, ASSUME YOUR OPPONENT PLAYS WELL, "
                + "THEN CHOOSE THE BEST GUARANTEED RESULT."));
        content.getChildren().add(mono(
                "1. Imagine every legal move the computer could make.\n"
                + "2. For each move, imagine the opponent's best response.\n"
                + "3. Repeat until someone wins or the game is drawn.\n"
                + "4. Prefer WIN over DRAW, and DRAW over LOSS.\n"
                + "5. Alpha-beta pruning safely skips unnecessary searches."));
        content.getChildren().add(note(
                "UNDER THE HOOD: WIN = 10 - DEPTH, DRAW = 0, LOSS = DEPTH - 10. "
                + "EARLIER WINS AND LATER LOSSES ARE PREFERRED."));
        if (analysis != null) {
            content.getChildren().add(note(decisionSummary(analysis)));
            content.getChildren().add(note(
                    "THE COMPUTER EXAMINED " + analysis.candidates().size()
                    + " POSSIBLE FIRST MOVES BEFORE CHOOSING."));
        } else {
            content.getChildren().add(note(
                    "PLAY AGAINST MINIMAX TO INSPECT AN ACTUAL COMPUTER DECISION."));
        }
        content.getChildren().add(actions(
                button("TRACE", trace), button("APPLY", apply),
                button("BACK TO GAME", back)));
        return content;
    }

    public static VBox apply(Runnable explain, Runnable back) {
        VBox content = page("APPLY / WORST-CASE ENGINEERING");
        content.getChildren().addAll(
                note("CHOOSE A RETRY POLICY FOR A SIMULATED SERVICE"),
                mono("                      NORMAL   OVERLOADED\n"
                        + "AGGRESSIVE RETRIES       1         9\n"
                        + "BOUNDED RETRIES          3         4"),
                note("THESE COSTS ARE INVENTED FOR TEACHING. LOWER IS BETTER."),
                note("WHICH POLICY MINIMIZES THE WORST-CASE LOSS?"));

        WorstCasePolicy chooser = new WorstCasePolicy();
        List<WorstCasePolicy.Policy> policies = WorstCasePolicy.examplePolicies();
        WorstCasePolicy.Policy best = chooser.choose(policies);
        Label feedback = note("MAKE YOUR CHOICE TO SEE THE WORST CASE.");
        feedback.setWrapText(true);
        Button aggressive = button("AGGRESSIVE RETRIES",
                () -> reveal(feedback, policies.get(0), best));
        Button bounded = button("BOUNDED RETRIES",
                () -> reveal(feedback, policies.get(1), best));
        content.getChildren().addAll(
                actions(aggressive, bounded), feedback,
                note("WHEN THE ENVIRONMENT IS ADVERSARIAL, MINIMAX CAN LIMIT LOSS."),
                note("WHEN FAILURES ARE PROBABILISTIC, EXPECTED VALUE MAY BE BETTER."),
                actions(button("EXPLAIN", explain), button("BACK TO GAME", back)));
        return content;
    }

    private static void reveal(Label label, WorstCasePolicy.Policy choice,
                               WorstCasePolicy.Policy best) {
        label.setText("YOUR POLICY: " + choice.name()
                + " / WORST COST: " + choice.worstCost()
                + "\nLOWEST WORST COST: " + best.worstCost()
                + " (" + best.name() + ")"
                + "\n" + (choice.equals(best) ? "CORRECT." : "THE OTHER POLICY IS SAFER IN THIS MODEL.")
                + " MINIMAX SELECTS THE BEST WORST CASE.");
    }

    private static String signed(int score) {
        return score > 0 ? "+" + score : Integer.toString(score);
    }

    private static VBox page(String title) {
        Label heading = new Label(title);
        heading.getStyleClass().add("learning-title");
        heading.setWrapText(true);
        VBox content = new VBox(12, heading);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(14));
        content.setMaxWidth(780);
        return content;
    }

    private static Text mono(String text) {
        Text result = new Text(text);
        result.getStyleClass().add("learning-mono");
        return result;
    }

    private static Label note(String text) {
        Label result = new Label(text);
        result.setWrapText(true);
        result.getStyleClass().add("learning-note");
        result.setMaxWidth(720);
        result.setAlignment(Pos.CENTER);
        return result;
    }

    private static Button button(String label, Runnable command) {
        Button result = new Button(label);
        result.getStyleClass().add("action-button");
        result.setOnAction(event -> command.run());
        return result;
    }

    private static FlowPane actions(Button... buttons) {
        FlowPane row = new FlowPane(12, 10);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(buttons);
        return row;
    }
}
