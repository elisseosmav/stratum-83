package org.stratum83.ui;

import java.util.HashSet;
import java.util.List;
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
import javafx.scene.control.Alert;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.stratum83.game.AlgorithmCatalog;
import org.stratum83.game.CheckersAI;
import org.stratum83.game.CheckersGame;
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

    private enum View { GAMES, ALGORITHMS, GRID_ZERO, LABYRINTH, CHECKERS }

    private final SoundPlayer sound = new SoundPlayer();
    private final MinimaxPlayer minimax = new MinimaxPlayer();
    private final ChallengeMazeGenerator generator = new ChallengeMazeGenerator();
    private final MazeSolver solver = new MazeSolver();
    private final CheckersAI checkersAI = new CheckersAI(4);

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

    private CheckersGame checkersGame;
    private Button[][] checkersSquares;
    private Circle[][] checkersPieces;
    private Label[][] checkersKings;
    private Label checkersMessage;
    private Label checkersScore;
    private List<CheckersGame.Square> checkersSelection = List.of();
    private CheckersGame.Turn checkersLastMove;

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
            if (!entry.playable()) continue;
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
        setStatus("SELECT A GAME / ESC = MAIN MENU");
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
            case 3 -> startCheckers();
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
                subtitle("SHORT EXPLANATIONS OF THE METHODS USED IN THE GAMES"),
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


    private void startCheckers() {
        currentView = View.CHECKERS;
        checkersGame = new CheckersGame();
        checkersSelection = List.of();
        checkersLastMove = null;
        checkersSquares = new Button[CheckersGame.SIZE][CheckersGame.SIZE];
        checkersPieces = new Circle[CheckersGame.SIZE][CheckersGame.SIZE];
        checkersKings = new Label[CheckersGame.SIZE][CheckersGame.SIZE];

        GridPane board = new GridPane();
        board.setHgap(1);
        board.setVgap(1);
        board.setAlignment(Pos.CENTER);
        board.getStyleClass().add("checkers-board");

        for (int row = 0; row < CheckersGame.SIZE; row++) {
            for (int col = 0; col < CheckersGame.SIZE; col++) {
                int r = row;
                int c = col;
                Button cell = button("", () -> clickCheckers(r, c));
                cell.setMinSize(48, 48);
                cell.setPrefSize(48, 48);
                cell.setMaxSize(48, 48);
                Circle piece = new Circle(16);
                piece.setMouseTransparent(true);
                Label crown = new Label("K");
                crown.getStyleClass().add("checkers-king");
                crown.setMouseTransparent(true);
                StackPane square = new StackPane(cell, piece, crown);
                board.add(square, col, row);
                checkersSquares[row][col] = cell;
                checkersPieces[row][col] = piece;
                checkersKings[row][col] = crown;
            }
        }

        checkersMessage = new Label();
        checkersMessage.getStyleClass().add("game-message");
        checkersMessage.setWrapText(true);
        checkersScore = subtitle("");
        FlowPane controls = actions(
                button("NEW GAME", this::startCheckers),
                button("RULES", this::showCheckersRules),
                button("GAMES", this::showGames));
        VBox content = page(title("CHECKERS"),
                subtitle("YOU: GOLD  /  COMPUTER: DARK  /  MINIMAX + ALPHA-BETA"),
                checkersScore, checkersMessage, board, controls,
                subtitle("SELECT A PIECE, THEN A HIGHLIGHTED SQUARE. CAPTURES ARE MANDATORY."));
        showScrollable(content);
        refreshCheckers();
    }

    private void showCheckersRules() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("CHECKERS / RULES");
        dialog.setHeaderText("American checkers (8 x 8)");
        dialog.setContentText(
                "Move diagonally on dark squares. Men move and capture forward; "
                + "kings move and capture in both directions.\n\n"
                + "Captures are mandatory. When another jump is available, "
                + "you must continue with the same piece. Reaching the far row "
                + "crowns a king and ends that turn.\n\n"
                + "Select your piece, then choose the highlighted landing squares. "
                + "You win when the computer has no legal move. "
                + "The game is drawn after 80 turns without a capture or promotion.");
        dialog.showAndWait();
    }

    private void clickCheckers(int row, int col) {
        if (currentView != View.CHECKERS || checkersGame == null
                || checkersGame.outcome() != CheckersGame.Outcome.PLAYING
                || checkersGame.sideToMove() != CheckersGame.Side.HUMAN) return;

        CheckersGame.Square clicked = new CheckersGame.Square(row, col);
        List<CheckersGame.Turn> legal = checkersGame.legalMoves();
        if (!checkersSelection.isEmpty()) {
            int next = checkersSelection.size();
            for (CheckersGame.Turn turn : legal) {
                if (turn.path().size() <= next
                        || !turn.path().subList(0, next).equals(checkersSelection)
                        || !turn.path().get(next).equals(clicked)) continue;

                List<CheckersGame.Square> selected = new java.util.ArrayList<>(checkersSelection);
                selected.add(clicked);
                checkersSelection = List.copyOf(selected);
                if (turn.path().size() == checkersSelection.size()) {
                    checkersSelection = List.of();
                    checkersGame.play(turn);
                    checkersLastMove = turn;
                    sound.click();
                    if (checkersGame.outcome() == CheckersGame.Outcome.PLAYING) {
                        CheckersGame.Turn response = checkersAI.chooseMove(checkersGame);
                        if (response != null) {
                            checkersGame.play(response);
                            checkersLastMove = response;
                        }
                    }
                }
                refreshCheckers();
                return;
            }
        }

        boolean selectable = legal.stream().anyMatch(turn -> turn.start().equals(clicked));
        checkersSelection = selectable ? List.of(clicked) : List.of();
        refreshCheckers();
    }

    private void refreshCheckers() {
        List<CheckersGame.Turn> legal = checkersGame.legalMoves();
        Set<CheckersGame.Square> origins = new HashSet<>();
        Set<CheckersGame.Square> targets = new HashSet<>();
        for (CheckersGame.Turn turn : legal) {
            origins.add(turn.start());
            int nextIndex = checkersSelection.size();
            if (nextIndex > 0 && turn.path().size() > nextIndex
                    && turn.path().subList(0, nextIndex).equals(checkersSelection)) {
                targets.add(turn.path().get(nextIndex));
            }
        }

        for (int row = 0; row < CheckersGame.SIZE; row++) {
            for (int col = 0; col < CheckersGame.SIZE; col++) {
                CheckersGame.Square position = new CheckersGame.Square(row, col);
                Button cell = checkersSquares[row][col];
                boolean dark = (row + col) % 2 == 1;
                cell.getStyleClass().setAll("checkers-square",
                        dark ? "checkers-dark" : "checkers-light");
                if (checkersLastMove != null && checkersLastMove.path().contains(position)) {
                    cell.getStyleClass().add("checkers-last");
                }
                if (origins.contains(position) && checkersSelection.isEmpty()
                        && checkersGame.sideToMove() == CheckersGame.Side.HUMAN) {
                    cell.getStyleClass().add("checkers-origin");
                }
                if (checkersSelection.contains(position)) {
                    cell.getStyleClass().add("checkers-selected");
                }
                if (targets.contains(position)) {
                    cell.getStyleClass().add("checkers-target");
                }

                char piece = checkersGame.getCell(row, col);
                Circle disc = checkersPieces[row][col];
                disc.setVisible(piece != CheckersGame.EMPTY);
                if (piece != CheckersGame.EMPTY) {
                    disc.getStyleClass().setAll("checkers-piece",
                            piece == 'h' || piece == 'H'
                                    ? "checkers-human" : "checkers-computer");
                }
                Label crown = checkersKings[row][col];
                crown.setVisible(piece == 'H' || piece == 'C');
                crown.setStyle(piece == 'H' ? "-fx-text-fill: #573819;"
                        : "-fx-text-fill: #d5f4df;");
                cell.setAccessibleText("Row " + (row + 1)
                        + ", column " + (col + 1) + ": "
                        + switch (piece) {
                            case 'h' -> "human piece";
                            case 'H' -> "human king";
                            case 'c' -> "computer piece";
                            case 'C' -> "computer king";
                            default -> "empty";
                        });
            }
        }
        checkersScore.setText("YOU: " + checkersGame.countPieces(CheckersGame.Side.HUMAN)
                + "  /  COMPUTER: "
                + checkersGame.countPieces(CheckersGame.Side.COMPUTER));
        switch (checkersGame.outcome()) {
            case HUMAN_WINS -> checkersMessage.setText("YOU WIN!");
            case COMPUTER_WINS -> checkersMessage.setText("COMPUTER WINS");
            case DRAW -> checkersMessage.setText("DRAW");
            case PLAYING -> {
                if (!checkersSelection.isEmpty()) {
                    checkersMessage.setText("SELECT A HIGHLIGHTED LANDING SQUARE");
                } else if (legal.stream().anyMatch(CheckersGame.Turn::isCapture)) {
                    checkersMessage.setText("CAPTURE REQUIRED / YOUR TURN");
                } else {
                    checkersMessage.setText("YOUR TURN / SELECT A GOLD PIECE");
                }
            }
        }
        setStatus("CHECKERS / MINIMAX DEPTH 4 / ESC = GAMES");
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
