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

        Label choicesTitle = note("POSSIBLE MOVES THE COMPUTER EVALUATED:");
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

        VBox comparisons = new VBox(7, choicesTitle, alternatives,
                note("POSITIVE SCORE = WIN, ZERO = DRAW, NEGATIVE SCORE = LOSS."));
        comparisons.setAlignment(Pos.CENTER);
        comparisons.setVisible(false);
        comparisons.setManaged(false);
        Button compare = button("COMPARE POSSIBLE MOVES", () -> {});
        compare.setOnAction(event -> {
            boolean open = !comparisons.isVisible();
            comparisons.setVisible(open);
            comparisons.setManaged(open);
            compare.setText(open ? "HIDE MOVE COMPARISON" : "COMPARE POSSIBLE MOVES");
        });
        content.getChildren().addAll(compare, comparisons);

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

        Label diagnostics = note("THIS SEARCH EVALUATED " + analysis.evaluatedNodes()
                + " POSITIONS AND MADE " + analysis.alphaBetaCutoffs() + " SAFE CUTS.");
        Label sampling = note(analysis.truncated()
                ? "ONLY THE FIRST SEARCH STEPS AND THE FINAL DECISION ARE SHOWN."
                : "ALL SEARCH EVENTS WERE RECORDED.");
        VBox technical = new VBox(7, diagnostics, sampling);
        technical.setAlignment(Pos.CENTER);
        technical.setVisible(false);
        technical.setManaged(false);
        Button technicalButton = button("SHOW SEARCH STATISTICS", () -> {});
        technicalButton.setOnAction(event -> {
            boolean open = !technical.isVisible();
            technical.setVisible(open);
            technical.setManaged(open);
            technicalButton.setText(open ? "HIDE SEARCH STATISTICS" : "SHOW SEARCH STATISTICS");
        });

        content.getChildren().addAll(stepTitle, board, stepExplanation,
                note("THESE BOARDS SHOW WHAT MIGHT HAPPEN. ONLY THE FINAL STEP IS THE REAL MOVE."),
                actions(previous, next, showDecision),
                technicalButton, technical,
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
        VBox content = page("HOW DOES THE COMPUTER THINK?");
        content.getChildren().add(note(
                "MINIMAX IS A WAY TO CHOOSE A MOVE BY THINKING ABOUT WHAT HAPPENS NEXT."));

        content.getChildren().addAll(
                lesson("1 / IMAGINE",
                        "The computer looks at each empty square it could choose."),
                lesson("2 / PREDICT",
                        "For each choice, it imagines your strongest possible reply."),
                lesson("3 / DECIDE",
                        "It prefers a win. If it cannot win, it tries to draw rather than lose."));

        if (analysis != null) {
            Label actual = note(decisionSummary(analysis));
            actual.getStyleClass().add("learning-conclusion");
            content.getChildren().add(actual);
        } else {
            content.getChildren().add(note(
                    "PLAY AGAINST THE COMPUTER TO SEE WHY IT CHOSE A PARTICULAR SQUARE."));
        }

        Label details = note(
                "TECHNICAL VIEW: A WIN GETS A POSITIVE SCORE, A DRAW GETS ZERO, "
                + "AND A LOSS GETS A NEGATIVE SCORE. A WIN SOONER IS BETTER. "
                + "ALPHA-BETA PRUNING SKIPS BRANCHES THAT CANNOT CHANGE THE ANSWER.");
        details.setVisible(false);
        details.setManaged(false);
        Button technical = button("SHOW TECHNICAL DETAILS", () -> {});
        technical.setOnAction(event -> {
            boolean show = !details.isVisible();
            details.setVisible(show);
            details.setManaged(show);
            technical.setText(show ? "HIDE TECHNICAL DETAILS" : "SHOW TECHNICAL DETAILS");
        });
        content.getChildren().addAll(technical, details,
                actions(button("TRACE THE MOVE", trace),
                        button("TRY AN EXAMPLE", apply),
                        button("BACK TO GAME", back)));
        return content;
    }

    public static VBox apply(Runnable explain, Runnable back) {
        VBox content = page("APPLY / CHOOSE A SAFER RETRY POLICY");
        content.getChildren().add(note(
                "YOUR SOFTWARE CALLS ANOTHER SERVICE. SOMETIMES THAT SERVICE BECOMES OVERLOADED."));
        content.getChildren().add(note(
                "YOU MUST CHOOSE HOW YOUR SOFTWARE RETRIES FAILED REQUESTS. "
                + "EACH CHOICE HAS A COST IN TWO POSSIBLE SITUATIONS."));

        content.getChildren().addAll(
                lesson("OPTION A / AGGRESSIVE RETRIES",
                        "Normal situation: cost 1. Overloaded situation: cost 9."),
                lesson("OPTION B / BOUNDED RETRIES",
                        "Normal situation: cost 3. Overloaded situation: cost 4."));

        content.getChildren().add(note(
                "THE QUESTION: WHICH OPTION HAS THE SMALLER WORST POSSIBLE COST?"));

        WorstCasePolicy chooser = new WorstCasePolicy();
        List<WorstCasePolicy.Policy> policies = WorstCasePolicy.examplePolicies();
        WorstCasePolicy.Policy best = chooser.choose(policies);
        Label feedback = note("CHOOSE A OR B. THEN THE COMPUTER WILL EXPLAIN ITS REASONING.");
        feedback.getStyleClass().add("learning-feedback");

        Button aggressive = button("CHOOSE A", () -> {});
        Button bounded = button("CHOOSE B", () -> {});
        aggressive.setOnAction(event -> reveal(feedback, policies.get(0), best));
        bounded.setOnAction(event -> reveal(feedback, policies.get(1), best));
        content.getChildren().add(actions(aggressive, bounded));

        Label limitations = note(
                "THIS IS A SMALL EXAMPLE OF WORST-CASE DECISION-MAKING. "
                + "REAL SERVICE RELIABILITY MAY REQUIRE PROBABILITY, TIMEOUTS AND MORE TESTS. "
                + "ALL COSTS HERE ARE INVENTED FOR TEACHING.");
        limitations.setVisible(false);
        limitations.setManaged(false);
        Button more = button("WHERE IS THIS USEFUL?", () -> {});
        more.setOnAction(event -> {
            boolean show = !limitations.isVisible();
            limitations.setVisible(show);
            limitations.setManaged(show);
            more.setText(show ? "HIDE APPLICATION NOTES" : "WHERE IS THIS USEFUL?");
        });

        content.getChildren().addAll(feedback, more, limitations,
                actions(button("EXPLAIN MINIMAX", explain),
                        button("BACK TO GAME", back)));
        return content;
    }

    private static VBox lesson(String heading, String description) {
        Label title = note(heading);
        title.getStyleClass().add("learning-step-heading");
        Label body = note(description);
        VBox group = new VBox(4, title, body);
        group.setAlignment(Pos.CENTER);
        group.setMaxWidth(620);
        group.getStyleClass().add("learning-step");
        return group;
    }

    private static void reveal(Label label, WorstCasePolicy.Policy choice,
                               WorstCasePolicy.Policy best) {
        boolean correct = choice.equals(best);
        label.setText((correct ? "GOOD CHOICE!" : "NOT QUITE — HERE IS WHY.")
                + "\nOPTION A: WORST COST IS 9 (OVERLOADED)."
                + "\nOPTION B: WORST COST IS 4 (OVERLOADED)."
                + "\nTHE COMPUTER CHOOSES B BECAUSE 4 IS LESS THAN 9."
                + "\nLIKE MINIMAX, IT COMPARES THE WORST POSSIBLE OUTCOME.");
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
