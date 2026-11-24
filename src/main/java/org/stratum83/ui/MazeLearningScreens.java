package org.stratum83.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.stratum83.education.ServiceRouteExample;
import org.stratum83.labyrinth.Maze;
import org.stratum83.labyrinth.MazeSearchTrace;
import org.stratum83.labyrinth.MazeSolver;
import org.stratum83.labyrinth.Position;

/**
 * JavaFX-only teaching views for maze algorithms.
 * Each animation step is a real expansion recorded by MazeSolver.trace().
 */
public final class MazeLearningScreens {

    private MazeLearningScreens() {
    }

    public static VBox trace(Maze maze, MazeSolver.Algorithm algorithm,
                             Consumer<MazeSolver.Algorithm> selectAlgorithm,
                             Runnable explain, Runnable apply, Runnable back) {
        MazeSearchTrace trace = new MazeSolver().trace(maze, algorithm);
        VBox page = page("TRACE / PATHFINDING");
        page.getChildren().add(note("ACTUAL NODE EXPANSIONS / SAME MAZE / SEED CONTROLLED IN PLAY"));

        FlowPane selectors = buttons(
                button("BFS", () -> selectAlgorithm.accept(MazeSolver.Algorithm.BFS)),
                button("DIJKSTRA", () -> selectAlgorithm.accept(MazeSolver.Algorithm.DIJKSTRA)),
                button("A*", () -> selectAlgorithm.accept(MazeSolver.Algorithm.A_STAR)));
        page.getChildren().add(selectors);

        Label heading = note("");
        Label explanation = note("");
        explanation.setMaxWidth(680);
        Label totals = note("");
        Label legend = note("S START / G GOAL / C CURRENT / F FRONTIER / · EXPLORED / * FINAL PATH");

        Label[][] cells = new Label[maze.rows()][maze.columns()];
        GridPane board = new GridPane();
        board.setAlignment(Pos.CENTER);
        board.setHgap(2);
        board.setVgap(2);

        for (int row = 0; row < maze.rows(); row++) {
            for (int column = 0; column < maze.columns(); column++) {
                Label cell = new Label();
                cell.getStyleClass().add("maze-cell");
                cell.setMinSize(26, 26);
                cell.setPrefSize(26, 26);
                cell.setAlignment(Pos.CENTER);
                cells[row][column] = cell;
                board.add(cell, column, row);
            }
        }

        int[] index = {0};
        Button previous = button("PREVIOUS", () -> {});
        Button next = button("NEXT", () -> {});
        Button finalStep = button("FINAL RESULT", () -> {});

        Runnable draw = () -> {
            MazeSearchTrace.Step step = trace.steps().get(index[0]);
            boolean finished = index[0] == trace.steps().size() - 1;
            Set<Position> explored = new HashSet<>(step.explored());
            Set<Position> frontier = new HashSet<>(step.frontier());
            Set<Position> path = finished && trace.result().found()
                    ? new HashSet<>(trace.result().path()) : Set.of();

            for (int row = 0; row < maze.rows(); row++) {
                for (int col = 0; col < maze.columns(); col++) {
                    Position position = new Position(row, col);
                    Label cell = cells[row][col];
                    cell.getStyleClass().removeAll(
                            "maze-wall", "maze-open", "maze-trace-current",
                            "maze-trace-frontier", "maze-trace-explored",
                            "maze-trace-path", "maze-goal");
                    if (!maze.isOpen(position)) {
                        cell.getStyleClass().add("maze-wall");
                        cell.setText("");
                    } else if (position.equals(step.current())) {
                        cell.getStyleClass().add("maze-trace-current");
                        cell.setText(position.equals(maze.goal()) ? "G" : "C");
                    } else if (position.equals(maze.start())) {
                        cell.getStyleClass().add("maze-open");
                        cell.setText("S");
                    } else if (position.equals(maze.goal())) {
                        cell.getStyleClass().add("maze-goal");
                        cell.setText("G");
                    } else if (path.contains(position)) {
                        cell.getStyleClass().add("maze-trace-path");
                        cell.setText("*");
                    } else if (frontier.contains(position)) {
                        cell.getStyleClass().add("maze-trace-frontier");
                        cell.setText("F");
                    } else if (explored.contains(position)) {
                        cell.getStyleClass().add("maze-trace-explored");
                        cell.setText("·");
                    } else {
                        cell.getStyleClass().add("maze-open");
                        cell.setText("");
                    }
                }
            }

            heading.setText(algorithm + " / STEP " + (index[0] + 1) + " OF "
                    + trace.steps().size() + " / EXPANDING ROW "
                    + (step.current().row() + 1) + ", COL "
                    + (step.current().column() + 1));
            explanation.setText(step.explanation());
            totals.setText("g=" + step.costSoFar() + " / h=" + step.heuristic()
                    + " / f=" + step.priority() + " / EXPLORED "
                    + step.explored().size() + " / FRONTIER " + step.frontier().size()
                    + " / SHORTEST PATH " + trace.result().steps() + " MOVES"
                    + (finished ? " / SEARCH COMPLETE" : ""));
            previous.setDisable(index[0] == 0);
            next.setDisable(finished);
            finalStep.setDisable(finished);
        };
        previous.setOnAction(event -> { index[0]--; draw.run(); });
        next.setOnAction(event -> { index[0]++; draw.run(); });
        finalStep.setOnAction(event -> { index[0] = trace.steps().size() - 1; draw.run(); });
        draw.run();

        page.getChildren().addAll(heading, board, explanation, totals, legend,
                note("FRONTIER COLORS SHOW A SET OF LIVE CELLS, NOT PRIORITY QUEUE ORDER."),
                buttons(previous, next, finalStep),
                buttons(button("EXPLAIN", explain),
                        button("APPLY", apply),
                        button("BACK TO MAZE", back)));
        return page;
    }

    public static VBox explain(Runnable trace, Runnable apply, Runnable back) {
        VBox page = page("EXPLAIN / GRAPH SEARCH");
        page.getChildren().addAll(
                note("THE MAZE IS A GRAPH: EACH PASSAGE CELL IS A NODE."),
                note("UP, DOWN, LEFT AND RIGHT MOVES ARE EDGES. EVERY EDGE CURRENTLY COSTS 1."),
                note("BFS / BREADTH-FIRST SEARCH"),
                note("FIFO QUEUE: EXPANDS CELLS IN HOP-DISTANCE LAYERS. SHORTEST PATH WHEN ALL EDGES HAVE EQUAL COST."),
                note("DIJKSTRA / MINIMUM-COST ROUTES"),
                note("PRIORITY QUEUE: EXPANDS THE LOWEST ACCUMULATED COST g. USE WHEN EDGES HAVE NONNEGATIVE, DIFFERENT COSTS."),
                note("A* / INFORMED SEARCH"),
                note("PRIORITY f = g + h. HERE h IS MANHATTAN DISTANCE; IT NEVER OVERESTIMATES THE REMAINING UNIT-COST ROUTE."),
                note("IMPORTANT LIMITATION"),
                note("THE CURRENT MAZE HAS EQUAL-COST STEPS AND IS GENERATED WITHOUT LOOPS. ALL THREE ALGORITHMS FIND THE SAME PATH LENGTH."),
                note("NODE EXPANSIONS MAY DIFFER. FEWER EXPANSIONS DO NOT BY THEMSELVES PROVE LOWER RUNTIME."),
                buttons(button("TRACE", trace),
                        button("APPLY", apply),
                        button("BACK TO MAZE", back)));
        return page;
    }

    public static VBox apply(Runnable trace, Runnable explain, Runnable back) {
        VBox page = page("APPLY / SOFTWARE SERVICE ROUTING");
        ServiceRouteExample network = new ServiceRouteExample();
        ServiceRouteExample.Route fewest = network.fewestHops();
        ServiceRouteExample.Route cheapest = network.lowestCost();
        Label feedback = note("WHICH ROUTE HAS THE LOWEST TOTAL COST?");
        feedback.setWrapText(true);
        page.getChildren().addAll(
                note("A FICTIONAL SOFTWARE SYSTEM CAN REACH ITS DATABASE BY TWO ROUTES."),
                note("API -> DATABASE                 COST 8"),
                note("API -> GATEWAY -> DATABASE      COST 2 + 2 = 4"),
                note("EDGE COSTS ARE HYPOTHETICAL UNITS, NOT REAL LATENCY OR NETWORK MEASUREMENTS."),
                note("BFS OPTIMIZES FEWEST HOPS. DIJKSTRA OPTIMIZES SUM OF NONNEGATIVE EDGE COSTS."),
                buttons(
                        button("DIRECT / 1 HOP", () -> reveal(feedback, fewest, cheapest)),
                        button("VIA GATEWAY / 2 HOPS", () -> reveal(feedback, cheapest, cheapest))),
                feedback,
                note("ENGINEERING TAKEAWAY: CHOOSE A GRAPH ALGORITHM BASED ON WHAT THE EDGE WEIGHTS MEAN."),
                note("BFS FOR UNWEIGHTED DEPENDENCY LINKS; DIJKSTRA FOR WEIGHTED ROUTES; A* WHEN A SAFE, INFORMATIVE HEURISTIC EXISTS."),
                buttons(button("TRACE MAZE", trace),
                        button("EXPLAIN", explain),
                        button("BACK TO MAZE", back)));
        return page;
    }

    private static void reveal(Label feedback, ServiceRouteExample.Route selection,
                               ServiceRouteExample.Route optimum) {
        feedback.setText("YOU CHOSE " + String.join(" -> ", selection.nodes())
                + " / " + selection.hops() + " HOP(S), COST " + selection.cost()
                + "\nMINIMUM COST: " + String.join(" -> ", optimum.nodes())
                + " / TOTAL " + optimum.cost()
                + "\n" + (selection.equals(optimum)
                ? "CORRECT: DIJKSTRA FINDS THE LOWER TOTAL COST."
                : "NOT LOWEST COST: FEWER HOPS DOES NOT ALWAYS MEAN CHEAPER."));
    }

    private static VBox page(String title) {
        Label heading = new Label(title);
        heading.getStyleClass().add("screen-title");
        VBox page = new VBox(12, heading);
        page.setPadding(new Insets(18));
        page.setAlignment(Pos.CENTER);
        page.setMaxWidth(780);
        return page;
    }

    private static Label note(String value) {
        Label label = new Label(value);
        label.getStyleClass().add("learning-note");
        label.setWrapText(true);
        label.setMaxWidth(740);
        return label;
    }

    private static Button button(String label, Runnable action) {
        Button button = new Button(label);
        button.getStyleClass().add("action-button");
        button.setOnAction(event -> action.run());
        return button;
    }

    private static FlowPane buttons(Button... buttons) {
        FlowPane row = new FlowPane(10, 8);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(buttons);
        return row;
    }
}
