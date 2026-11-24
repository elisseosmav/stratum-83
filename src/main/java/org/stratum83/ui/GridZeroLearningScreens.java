package org.stratum83.ui;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.stratum83.education.WorstCasePolicy;
import org.stratum83.game.MinimaxAnalysis;
import org.stratum83.game.Move;

/** Small JavaFX teaching screens; algorithms stay in plain Java classes. */
public final class GridZeroLearningScreens {

    private GridZeroLearningScreens() {
    }

    public static VBox trace(MinimaxAnalysis analysis, Runnable back,
                             Runnable explain, Runnable apply) {
        VBox content = page("TRACE / MINIMAX DECISION TREE");
        content.getChildren().add(note(
                "REAL SEARCH EVENTS / DOTS MEAN EMPTY CELLS / SCORES FAVOR THE COMPUTER"));

        Label heading = note("");
        Text board = mono("");
        Label detail = note("");
        detail.setWrapText(true);

        final int[] current = {0};
        Button previous = button("PREVIOUS", () -> {});
        Button next = button("NEXT", () -> {});
        Runnable update = () -> {
            MinimaxAnalysis.Step step = analysis.steps().get(current[0]);
            heading.setText("STEP " + (current[0] + 1) + " OF "
                    + analysis.steps().size() + " / " + step.kind()
                    + " / DEPTH " + step.depth());
            board.setText(step.board());
            detail.setText(step.explanation());
            previous.setDisable(current[0] == 0);
            next.setDisable(current[0] == analysis.steps().size() - 1);
        };
        previous.setOnAction(event -> { current[0]--; update.run(); });
        next.setOnAction(event -> { current[0]++; update.run(); });
        update.run();

        HBox controls = new HBox(12, previous, next);
        controls.setAlignment(Pos.CENTER);

        content.getChildren().addAll(heading, board, detail, controls,
                note("VISITED POSITIONS: " + analysis.evaluatedNodes()
                        + " / ALPHA-BETA CUTOFFS: " + analysis.alphaBetaCutoffs()),
                mono(candidateScores(analysis)),
                note(analysis.truncated()
                        ? "ONLY THE FIRST SEARCH EVENTS AND FINAL CHOICE ARE SHOWN."
                        : "ALL RECORDED SEARCH EVENTS ARE SHOWN."),
                actions(button("EXPLAIN", explain), button("APPLY", apply),
                        button("BACK TO GAME", back)));
        return content;
    }

    public static VBox explain(MinimaxAnalysis analysis, Runnable trace,
                               Runnable back, Runnable apply) {
        VBox content = page("EXPLAIN / MINIMAX");
        content.getChildren().add(mono(
                "1. MAX looks for the highest score for the computer.\n"
                + "2. MIN assumes the opponent chooses the lowest score.\n"
                + "3. A win scores 10 - depth, a loss depth - 10.\n"
                + "4. A draw scores 0. Early wins are preferred.\n"
                + "5. Alpha-beta pruning skips branches that cannot change\n"
                + "   a decision. It does NOT change the optimal outcome."));

        if (analysis != null) {
            Move move = analysis.chosenMove();
            content.getChildren().addAll(
                    note("LAST COMPUTER TURN: " + analysis.player()),
                    note("SELECTED ROW " + (move.row() + 1)
                            + ", COLUMN " + (move.column() + 1)
                            + " / SCORE " + analysis.bestScore()),
                    mono(candidateScores(analysis)),
                    note("EQUAL SCORES ARE RESOLVED USING BOARD ORDER."));
        } else {
            content.getChildren().add(note(
                    "PLAY GRID ZERO AGAINST MINIMAX TO INSPECT AN ACTUAL DECISION."));
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

    private static String candidateScores(MinimaxAnalysis analysis) {
        StringBuilder output = new StringBuilder("ROOT MOVES / EXACT MINIMAX SCORES\n");
        for (MinimaxAnalysis.Candidate option : analysis.candidates()) {
            output.append(option.move().equals(analysis.chosenMove()) ? "* " : "  ")
                    .append("ROW ").append(option.move().row() + 1)
                    .append(" / COL ").append(option.move().column() + 1)
                    .append(" : ").append(option.score()).append('\n');
        }
        output.append("* SELECTED / HIGHEST SCORE / FIRST ON TIES");
        return output.toString();
    }

    private static VBox page(String title) {
        Label heading = new Label(title);
        heading.getStyleClass().add("screen-title");
        VBox content = new VBox(18, heading);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
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
