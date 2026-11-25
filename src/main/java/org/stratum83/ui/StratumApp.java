package org.stratum83.ui;

import java.util.HashSet;
import java.util.Set;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.stratum83.game.AlgorithmCatalog;
import org.stratum83.game.GameCatalog;
import org.stratum83.game.GameResult;
import org.stratum83.game.GridZeroGame;
import org.stratum83.game.MinimaxPlayer;
import org.stratum83.game.Move;
import org.stratum83.labyrinth.ChallengeMazeGenerator;
import org.stratum83.labyrinth.Maze;
import org.stratum83.labyrinth.MazeChallenge;
import org.stratum83.labyrinth.MazeFogOfWar;
import org.stratum83.labyrinth.MazeSolver;
import org.stratum83.labyrinth.PathResult;
import org.stratum83.labyrinth.Position;

public final class StratumApp extends Application {

    private enum View { GAMES, ALGORITHMS, GRID_ZERO, LABYRINTH }

    private final SoundPlayer sound = new SoundPlayer();
    private final MinimaxPlayer minimax = new MinimaxPlayer();
    private final ChallengeMazeGenerator generator = new ChallengeMazeGenerator();
    private final MazeSolver solver = new MazeSolver();

    private View currentView;
    private StackPane screen;
    private Label status;
    private Button soundButton;

    private GridZeroGame gridGame;
    private Button[][] gridButtons;
    private Label[][] gridMarks;
    private Label gridMessage;

    private long mazeSeed = 1983L;
    private Maze maze;
    private MazeChallenge player;
    private MazeFogOfWar fog;
    private PathResult computerRoute;
    private Label[][] mazeCells;
    private Label mazeMessage;
    private Button routeButton;
    private ScrollPane mazeScroll;
    private boolean routeRevealed;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(header());
        screen = new StackPane();
        screen.setPadding(new Insets(12));
        root.setCenter(screen);

        status = new Label("READY / SELECT A GAME");
        status.getStyleClass().add("footer-status");
        status.setWrapText(true);
        HBox footer = new HBox(14, status);
        footer.getStyleClass().add("footer");
        footer.setPadding(new Insets(12, 20, 12, 20));
        root.setBottom(footer);

        Scene scene = new Scene(root, 940, 710);
        scene.getStylesheets().add(getClass().getResource("/terminal.css").toExternalForm());
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                showGames();
                event.consume();
                return;
            }
            if (event.getCode() == KeyCode.F2) {
                toggleSound();
                event.consume();
                return;
            }
            if (currentView != View.LABYRINTH || player == null) return;
            int dr = 0;
            int dc = 0;
            switch (event.getCode()) {
                case UP -> dr = -1;
                case DOWN -> dr = 1;
                case LEFT -> dc = -1;
                case RIGHT -> dc = 1;
                default -> { return; }
            }
            movePlayer(dr, dc);
            event.consume();
        });

        stage.setTitle("STRATUM-83 | Classic AI Games");
        stage.setMinWidth(680);
        stage.setMinHeight(600);
        stage.setScene(scene);
        showGames();
        stage.show();
    }

    private HBox header() {
        Label brand = new Label("STRATUM-83  /  CLASSIC AI GAMES");
        brand.getStyleClass().add("header-title");

        Button games = button("GAMES", this::showGames);
        Button algorithms = button("ALGORITHMS", this::showAlgorithms);
        soundButton = button("SOUND OFF", this::toggleSound);
        soundButton.getStyleClass().add("small-button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(12, brand, spacer, games, algorithms, soundButton);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15, 18, 15, 18));
        row.getStyleClass().add("header");
        return row;
    }

    private void showGames() {
        currentView = View.GAMES;
        Label title = title("SELECT GAME");
        Label intro = subtitle("HUMAN VS COMPUTER / RETRO GAMES / OFFLINE JAVA 21");

        VBox catalog = new VBox(8);
        catalog.setMaxWidth(720);
        for (GameCatalog.Entry entry : GameCatalog.entries()) {
            String label = String.format("%02d  %-16s  %s",
                    entry.number(), entry.title(), entry.algorithm());
            Button gameButton = button(label, () -> openGame(entry.number()));
            gameButton.getStyleClass().add("catalog-game");
            gameButton.setMaxWidth(Double.MAX_VALUE);
            gameButton.setDisable(!entry.playable());
            catalog.getChildren().add(gameButton);
        }
        VBox page = page(title, intro, catalog);
        showScrollable(page);
        setStatus("CHOOSE GRID ZERO OR LABYRINTH.  ESC = MAIN MENU");
    }

    private void openGame(int number) {
        GameCatalog.Entry entry = GameCatalog.find(number).orElse(null);
        if (entry == null || !entry.playable()) {
            setStatus("THIS GAME IS NOT AVAILABLE YET.");
            return;
        }
        switch (number) {
            case 1 -> startGridZero();
            case 2 -> startMaze();
            default -> throw new IllegalStateException("No game for catalog entry " + number);
        }
    }

    private void showAlgorithms() {
        currentView = View.ALGORITHMS;
        VBox cards = new VBox(10);
        cards.setMaxWidth(740);
        for (AlgorithmCatalog.Entry entry : AlgorithmCatalog.entries()) {
            Label name = new Label(entry.name() + "  /  " + entry.game());
            name.getStyleClass().add("algorithm-name");
            Label explanation = subtitle(entry.explanation());
            Label practical = subtitle("USEFUL FOR: " + entry.usefulFor());
            VBox card = new VBox(6, name, explanation, practical);
            card.getStyleClass().add("algorithm-card");
            cards.getChildren().add(card);
        }
        VBox content = page(title("ALGORITHMS"),
                subtitle("SHORT EXPLANATIONS OF THE METHODS USED IN OUR GAMES"),
                cards, button("BACK TO GAMES", this::showGames));
        showScrollable(content);
        setStatus("ALGORITHM LIBRARY / SHORT EXPLANATIONS / ESC = GAMES");
    }

    private void startGridZero() {
        currentView = View.GRID_ZERO;
        gridGame = new GridZeroGame();
        gridButtons = new Button[3][3];
        gridMarks = new Label[3][3];

        GridPane board = new GridPane();
        board.setHgap(8);
        board.setVgap(8);
        board.setAlignment(Pos.CENTER);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                int r = row;
                int c = column;
                Button cell = button("", () -> makeGridMove(r, c));
                cell.getStyleClass().add("board-square");
                cell.setPrefSize(105, 105);
                cell.setMinSize(85, 85);
                Label mark = new Label();
                mark.getStyleClass().add("board-mark");
                mark.setMouseTransparent(true);
                board.add(new StackPane(cell, mark), column, row);
                gridButtons[row][column] = cell;
                gridMarks[row][column] = mark;
            }
        }

        gridMessage = new Label();
        gridMessage.getStyleClass().add("game-message");
        FlowPane controls = actions(
                button("NEW GAME", this::startGridZero),
                button("GAMES", this::showGames));
        VBox content = page(title("GRID ZERO"),
                subtitle("YOU ARE X / COMPUTER IS O / MINIMAX WITH ALPHA-BETA"),
                gridMessage, board, controls);
        showScrollable(content);
        refreshGrid();
        setStatus("GRID ZERO / CLICK AN EMPTY SQUARE / COMPUTER USES MINIMAX");
    }

    private void makeGridMove(int row, int column) {
        if (currentView != View.GRID_ZERO || gridGame == null
                || gridGame.getResult() != GameResult.PLAYING) return;
        Move humanMove = new Move(row, column);
        if (!gridGame.play(humanMove)) return;
        sound.click();
        if (gridGame.getResult() == GameResult.PLAYING) {
            gridGame.play(minimax.chooseMove(gridGame));
        }
        refreshGrid();
    }

    private void refreshGrid() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char mark = gridGame.getCell(row, col);
                gridMarks[row][col].setText(mark == GridZeroGame.EMPTY
                        ? "" : String.valueOf(mark));
                boolean canMove = mark == GridZeroGame.EMPTY
                        && gridGame.getResult() == GameResult.PLAYING;
                gridButtons[row][col].setDisable(!canMove);
                gridButtons[row][col].setAccessibleText(
                        "Row " + (row + 1) + ", column " + (col + 1)
                                + (mark == GridZeroGame.EMPTY ? ", empty" : ", " + mark));
            }
        }
        switch (gridGame.getResult()) {
            case PLAYING -> gridMessage.setText("YOUR TURN / PLACE X");
            case X_WINS -> gridMessage.setText("YOU WIN!");
            case O_WINS -> gridMessage.setText("COMPUTER WINS!");
            case DRAW -> gridMessage.setText("DRAW / WELL PLAYED");
        }
    }

    private void startMaze() {
        currentView = View.LABYRINTH;
        maze = generator.generate(mazeSeed);
        player = new MazeChallenge(maze);
        fog = new MazeFogOfWar(maze, 3);
        computerRoute = solver.solve(maze, MazeSolver.Algorithm.A_STAR);
        if (!computerRoute.found()) throw new IllegalStateException("Generated maze is unreachable");
        routeRevealed = false;
        mazeCells = new Label[maze.rows()][maze.columns()];

        GridPane board = new GridPane();
        board.setHgap(1);
        board.setVgap(1);
        board.setAlignment(Pos.CENTER);
        for (int row = 0; row < maze.rows(); row++) {
            for (int col = 0; col < maze.columns(); col++) {
                Label cell = new Label();
                cell.getStyleClass().add("maze-cell");
                cell.setMinSize(18, 18);
                cell.setPrefSize(18, 18);
                cell.setMaxSize(18, 18);
                cell.setAlignment(Pos.CENTER);
                board.add(cell, col, row);
                mazeCells[row][col] = cell;
            }
        }

        mazeMessage = new Label();
        mazeMessage.getStyleClass().add("game-message");
        mazeMessage.setWrapText(true);
        routeButton = button("SHOW COMPUTER ROUTE", this::toggleRoute);
        routeButton.setVisible(false);
        routeButton.setManaged(false);
        FlowPane controls = actions(
                button("RETRY", this::startMaze),
                button("NEW MAZE", () -> { mazeSeed++; startMaze(); }),
                routeButton,
                button("GAMES", this::showGames));
        VBox content = page(title("LABYRINTH / FOG OF WAR"),
                subtitle("YOU: ARROW KEYS TO MOVE / FIND THE HIDDEN EXIT G"),
                mazeMessage, board, controls,
                subtitle("DARK = UNSEEN / DIM = EXPLORED / BRIGHT = VISIBLE / WALLS BLOCK SIGHT"),
                subtitle("COMPUTER A* ROUTE IS HIDDEN UNTIL YOU REACH THE GOAL."));
        mazeScroll = showScrollable(content);
        refreshMaze();
        setStatus("LABYRINTH / USE ARROW KEYS / SEED " + mazeSeed + " / ESC = GAMES");
    }

    private void movePlayer(int rowChange, int colChange) {
        if (currentView != View.LABYRINTH || player == null || player.finished()) return;
        Position current = player.playerPosition();
        Position destination = new Position(current.row() + rowChange,
                current.column() + colChange);
        if (!player.moveTo(destination)) return;
        sound.click();
        fog.revealFrom(destination);
        if (player.finished()) {
            routeButton.setVisible(true);
            routeButton.setManaged(true);
        }
        refreshMaze();
        if (mazeScroll != null) {
            mazeScroll.setVvalue((double) destination.row() / (maze.rows() - 1));
        }
    }

    private void toggleRoute() {
        if (!player.finished()) return;
        routeRevealed = !routeRevealed;
        routeButton.setText(routeRevealed ? "HIDE COMPUTER ROUTE" : "SHOW COMPUTER ROUTE");
        refreshMaze();
    }

    private void refreshMaze() {
        Set<Position> optimal = routeRevealed
                ? new HashSet<>(computerRoute.path()) : Set.of();
        for (int row = 0; row < maze.rows(); row++) {
            for (int col = 0; col < maze.columns(); col++) {
                Position position = new Position(row, col);
                Label cell = mazeCells[row][col];
                cell.getStyleClass().setAll("maze-cell");
                cell.setText("");

                if (position.equals(player.playerPosition())) {
                    cell.getStyleClass().add("maze-player");
                    cell.setText("P");
                } else if (!fog.wasDiscovered(position) && !routeRevealed) {
                    cell.getStyleClass().add("fog-hidden");
                } else if (optimal.contains(position)) {
                    cell.getStyleClass().add("maze-optimal-route");
                    cell.setText("*");
                } else if (!fog.isVisible(position)) {
                    cell.getStyleClass().add(maze.isOpen(position)
                            ? "fog-memory-floor" : "fog-memory-wall");
                } else if (!maze.isOpen(position)) {
                    cell.getStyleClass().add("maze-wall");
                } else {
                    cell.getStyleClass().add("maze-open");
                }

                if (fog.wasDiscovered(position) || routeRevealed) {
                    if (position.equals(maze.goal())) {
                        cell.getStyleClass().removeAll("maze-optimal-route",
                                "maze-open", "fog-memory-floor");
                        cell.getStyleClass().add("maze-goal");
                        cell.setText("G");
                    }
                }
                cell.setAccessibleText(fog.wasDiscovered(position) || routeRevealed
                        ? (maze.isOpen(position) ? "Open cell" : "Wall")
                        : "Unexplored");
            }
        }
        if (player.finished()) {
            double efficiency = 100.0 * computerRoute.steps() / player.moves();
            mazeMessage.setText(String.format(java.util.Locale.ROOT,
                    "YOU FOUND THE EXIT!  YOUR MOVES: %d  /  COMPUTER A*: %d  /  EFFICIENCY: %.1f%%",
                    player.moves(), computerRoute.steps(), efficiency));
            setStatus("MAZE COMPLETE / COMPARE YOUR ROUTE WITH A* / SEED " + mazeSeed);
        } else {
            mazeMessage.setText("YOUR MOVES: " + player.moves()
                    + "  /  REVEALED CELLS: " + fog.discoveredCells()
                    + "  /  EXIT: UNKNOWN");
        }
    }

    private void toggleSound() {
        sound.setEnabled(!sound.isEnabled());
        soundButton.setText(sound.isEnabled() ? "SOUND ON" : "SOUND OFF");
    }

    private void setStatus(String value) {
        if (status != null) status.setText(value);
    }

    private ScrollPane showScrollable(VBox content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("game-scroll");
        screen.getChildren().setAll(scroll);
        scroll.setVvalue(0.0);
        return scroll;
    }

    private static VBox page(javafx.scene.Node... children) {
        VBox page = new VBox(12, children);
        page.setAlignment(Pos.TOP_CENTER);
        page.setPadding(new Insets(16));
        page.setMaxWidth(780);
        return page;
    }

    private static FlowPane actions(Button... controls) {
        FlowPane row = new FlowPane(10, 8);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(controls);
        return row;
    }

    private static Label title(String value) {
        Label label = new Label(value);
        label.getStyleClass().add("screen-title");
        label.setWrapText(true);
        return label;
    }

    private static Label subtitle(String value) {
        Label label = new Label(value);
        label.getStyleClass().add("screen-subtitle");
        label.setWrapText(true);
        label.setMaxWidth(730);
        return label;
    }

    private static Button button(String label, Runnable action) {
        Button control = new Button(label);
        control.getStyleClass().add("action-button");
        control.setWrapText(true);
        control.setOnAction(event -> action.run());
        return control;
    }

    @Override
    public void stop() {
        sound.close();
    }
}
