package org.stratum83.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.stratum83.game.GameResult;
import org.stratum83.game.GridZeroGame;
import org.stratum83.game.MinimaxPlayer;
import org.stratum83.game.Move;
import org.stratum83.labyrinth.Maze;
import org.stratum83.labyrinth.MazeChallenge;
import org.stratum83.labyrinth.MazeGenerator;
import org.stratum83.labyrinth.Position;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;
import org.stratum83.research.ExperimentResult;
import org.stratum83.research.ExperimentRunner;
import org.stratum83.research.MazeBenchmarkResult;
import org.stratum83.research.MazeBenchmarkRunner;

/** STRATUM-83 desktop UI. The game rules live in the game package. */
public class StratumApp extends Application {

    private final SoundPlayer sound = new SoundPlayer();
    private final MinimaxPlayer computer = new MinimaxPlayer();

    private Stage stage;
    private StackPane screen;
    private Label consoleMessage;
    private TextField commandInput;
    private Button soundButton;

    private GridZeroGame game;
    private Button[][] squares;
    private Label[][] marks;
    private Label gameMessage;

    private Maze maze;
    private MazeChallenge mazeChallenge;
    private Label[][] mazeCells;
    private Label mazeMessage;
    private Text mazeResults;
    private HBox mazePathButtons;
    private boolean showMyRoute;
    private boolean showOptimalRoute;
    private long mazeSeed = 1983;
    private boolean vsComputer;
    private boolean researchMenuOpen;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(buildHeader());

        screen = new StackPane();
        screen.setPadding(new Insets(24));
        root.setCenter(screen);
        root.setBottom(buildConsole());

        Scene scene = new Scene(root, 940, 710);
        scene.getStylesheets().add(getClass().getResource("/terminal.css").toExternalForm());
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                showMenu();
            } else if (event.getCode() == KeyCode.F1) {
                showHelp();
            } else if (event.getCode() == KeyCode.F2) {
                toggleSound();
            } else if (maze != null && !(scene.getFocusOwner() instanceof TextField)) {
                int rowStep = 0;
                int colStep = 0;
                switch (event.getCode()) {
                    case UP -> rowStep = -1;
                    case DOWN -> rowStep = 1;
                    case LEFT -> colStep = -1;
                    case RIGHT -> colStep = 1;
                    default -> { return; }
                }
                moveMaze(new Position(mazeChallenge.playerPosition().row() + rowStep,
                        mazeChallenge.playerPosition().column() + colStep));
                event.consume();
            }
        });

        stage.setTitle("STRATUM-83 | Local Research Terminal");
        stage.setMinWidth(680);
        stage.setMinHeight(600);
        stage.setScene(scene);
        showMenu();
        stage.show();
    }

    private HBox buildHeader() {
        Label identity = new Label("STRATUM-83  /  RESEARCH TERMINAL");
        identity.getStyleClass().add("header-title");

        Label offline = new Label("● OFFLINE");
        offline.getStyleClass().add("offline-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        soundButton = new Button("SOUND: OFF");
        soundButton.getStyleClass().add("small-button");
        soundButton.setOnAction(event -> toggleSound());

        HBox header = new HBox(18, identity, spacer, offline, soundButton);
        header.setPadding(new Insets(17, 22, 17, 22));
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header");
        return header;
    }

    private VBox buildConsole() {
        Label instructions = new Label("COMMANDS: PLAY | DUEL | MAZE | RESEARCH | EXPERIMENT | BENCHMARK | HELP | MENU | EXIT     F1 HELP / F2 SOUND / ESC MENU");
        instructions.getStyleClass().add("hint");
        instructions.setWrapText(true);

        Label prompt = new Label(">");
        prompt.getStyleClass().add("prompt");
        commandInput = new TextField();
        commandInput.setPromptText("Enter a command...");
        commandInput.getStyleClass().add("command-input");
        commandInput.setOnAction(event -> {
            String command = commandInput.getText();
            commandInput.clear();
            runCommand(command);
        });
        HBox.setHgrow(commandInput, Priority.ALWAYS);
        HBox commandRow = new HBox(12, prompt, commandInput);
        commandRow.setAlignment(Pos.CENTER_LEFT);

        consoleMessage = new Label("READY. ALL EXPERIMENTS RUN ON THIS COMPUTER.");
        consoleMessage.getStyleClass().add("console-message");
        consoleMessage.setWrapText(true);

        VBox console = new VBox(10, instructions, commandRow, consoleMessage);
        console.setPadding(new Insets(14, 22, 18, 22));
        console.getStyleClass().add("console");
        return console;
    }

    private void showMenu() {
        game = null;
        maze = null;
        researchMenuOpen = false;

        Label heading = makeTitle("SELECT PROGRAM");
        Label introduction = makeDescription(
                "LOCAL GAMES / ALGORITHM EXPERIMENTS / NO NETWORK REQUIRED");
        VBox menu = new VBox(11,
                menuButton("01  GRID ZERO       HUMAN VS COMPUTER", () -> startGame(true)),
                menuButton("02  LABYRINTH       HUMAN VS ALGORITHM", this::showMaze),
                menuButton("03  RESEARCH LAB    ALGORITHMS AND RESULTS", this::showResearchMenu));
        menu.setMaxWidth(570);

        Label note = makeDescription(
                "TYPE DUEL FOR TWO PLAYERS  /  F1 HELP  /  EXIT TO CLOSE");
        VBox content = new VBox(24, heading, introduction, menu, note);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setMaxWidth(600);
        screen.getChildren().setAll(content);
        consoleMessage.setText("READY. SELECT A PROGRAM OR TYPE A COMMAND.");
    }

    private void showResearchMenu() {
        game = null;
        maze = null;
        researchMenuOpen = true;

        Label heading = makeTitle("RESEARCH LAB");
        Label introduction = makeDescription(
                "REPRODUCIBLE EXPERIMENTS / CONFIGURE PARAMETERS / COMPARE RESULTS");
        VBox menu = new VBox(11,
                menuButton("01  GRID ZERO       MINIMAX VS RANDOM", this::showExperiment),
                menuButton("02  PATHFINDING     BFS / DIJKSTRA / A*", this::showMazeBenchmark));
        menu.setMaxWidth(570);

        Label note = makeDescription("TYPE BACK OR PRESS ESC TO RETURN TO MAIN MENU");
        VBox content = new VBox(24, heading, introduction, menu, note,
                actionButton("BACK TO MAIN MENU", this::showMenu));
        content.setAlignment(Pos.CENTER_LEFT);
        content.setMaxWidth(600);
        screen.getChildren().setAll(content);
        consoleMessage.setText("SELECT AN EXPERIMENT OR TYPE A NUMBER.");
    }

    private void startGame(boolean againstComputer) {
        maze = null;
        researchMenuOpen = false;
        game = new GridZeroGame();
        vsComputer = againstComputer;
        squares = new Button[3][3];
        marks = new Label[3][3];

        Label title = makeTitle("GRID ZERO");
        Label description = makeDescription(againstComputer
                ? "YOU ARE X  /  COMPUTER IS O  /  COMPUTER USES MINIMAX"
                : "LOCAL TWO-PLAYER MODE  /  X MOVES FIRST");
        gameMessage = new Label();
        gameMessage.getStyleClass().add("game-message");
        gameMessage.setWrapText(true);

        GridPane board = new GridPane();
        board.setHgap(10);
        board.setVgap(10);
        board.setAlignment(Pos.CENTER);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                final int selectedRow = row;
                final int selectedColumn = column;
                Button square = new Button();
                square.getStyleClass().add("board-square");
                square.setPrefSize(118, 118);
                square.setMinSize(95, 95);
                square.setOnAction(event -> playAt(selectedRow, selectedColumn));

                // The mark sits above the button, avoiding button-skin text rendering problems.
                Label markLabel = new Label();
                markLabel.getStyleClass().add("board-mark");
                markLabel.setMouseTransparent(true);
                StackPane cell = new StackPane(square, markLabel);
                board.add(cell, column, row);
                squares[row][column] = square;
                marks[row][column] = markLabel;
            }
        }

        HBox buttons = new HBox(12,
                actionButton("NEW GAME", () -> startGame(vsComputer)),
                actionButton("MAIN MENU", this::showMenu));
        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(16, title, description, gameMessage, board, buttons);
        content.setAlignment(Pos.CENTER);
        screen.getChildren().setAll(content);
        refreshBoard();
        consoleMessage.setText("GAME STARTED. CLICK AN EMPTY SQUARE.");
    }

    private void playAt(int row, int column) {
        if (game == null || game.getResult() != GameResult.PLAYING) {
            return;
        }
        if (!game.play(new Move(row, column))) {
            consoleMessage.setText("THAT SQUARE IS ALREADY OCCUPIED.");
            return;
        }
        sound.click();

        if (vsComputer && game.getResult() == GameResult.PLAYING) {
            Move computerMove = computer.chooseMove(game);
            game.play(computerMove);
        }
        refreshBoard();
    }

    private void refreshBoard() {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                char mark = game.getCell(row, column);
                Button square = squares[row][column];
                marks[row][column].setText(
                        mark == GridZeroGame.EMPTY ? "" : String.valueOf(mark));
                boolean occupied = mark != GridZeroGame.EMPTY;
                boolean canPlay = !occupied && game.getResult() == GameResult.PLAYING;
                square.setMouseTransparent(!canPlay);
                square.setFocusTraversable(canPlay);
                if (occupied && !square.getStyleClass().contains("filled")) {
                    square.getStyleClass().add("filled");
                }
                square.setAccessibleText("Row " + (row + 1) + ", column " + (column + 1)
                        + (mark == GridZeroGame.EMPTY ? ", empty" : ", " + mark));
            }
        }

        switch (game.getResult()) {
            case PLAYING -> gameMessage.setText("CURRENT PLAYER: " + game.getCurrentPlayer());
            case X_WINS -> gameMessage.setText(vsComputer ? "YOU WIN." : "PLAYER X WINS.");
            case O_WINS -> gameMessage.setText(vsComputer ? "COMPUTER WINS." : "PLAYER O WINS.");
            case DRAW -> gameMessage.setText("DRAW. NO MORE LEGAL MOVES.");
        }
    }

    private void showExperiment() {
        game = null;
        maze = null;
        researchMenuOpen = false;

        Label title = makeTitle("MINIMAX VS RANDOM");
        Label description = makeDescription(
                "THE SEED CONTROLS THE RANDOM OPPONENT / MINIMAX NEVER LEARNS");

        TextField seedField = researchField("1983");
        TextField countField = researchField("200");
        HBox settings = new HBox(20,
                new VBox(6, makeDescription("RANDOM SEED"), seedField),
                new VBox(6, makeDescription("NUMBER OF GAMES (1-2000)"), countField));
        settings.setAlignment(Pos.CENTER);

        Text report = new Text("CHOOSE PARAMETERS AND RUN THE EXPERIMENT.");
        report.getStyleClass().add("research-result");

        Button runButton = new Button("RUN EXPERIMENT");
        runButton.getStyleClass().add("action-button");
        runButton.setOnAction(event -> {
            Long seed = readSeed(seedField, report);
            Integer games = readCount(countField, 2000, "GAME COUNT", report);
            if (seed == null || games == null) return;

            sound.click();
            runButton.setDisable(true);
            report.setText("CALCULATING " + games + " GAMES...");

            Task<ExperimentResult> task = new Task<>() {
                @Override
                protected ExperimentResult call() {
                    return new ExperimentRunner().run(games, seed);
                }
            };
            task.setOnSucceeded(done -> {
                ExperimentResult result = task.getValue();
                report.setText("GAMES: " + result.games()
                        + "\nMINIMAX WINS: " + result.computerWins()
                        + "\nDRAWS: " + result.draws()
                        + "\nMINIMAX LOSSES: " + result.computerLosses()
                        + "\nSEED: " + result.seed()
                        + "\n\nREPEAT WITH THE SAME SEED TO VERIFY THESE RESULTS.");
                runButton.setDisable(false);
                consoleMessage.setText("MINIMAX EXPERIMENT FINISHED.");
            });
            task.setOnFailed(done -> {
                report.setText("EXPERIMENT FAILED: " + task.getException().getMessage());
                runButton.setDisable(false);
            });
            Thread worker = new Thread(task, "minimax-experiment");
            worker.setDaemon(true);
            worker.start();
        });

        HBox buttons = new HBox(12, runButton,
                actionButton("RESEARCH LAB", this::showResearchMenu),
                actionButton("MAIN MENU", this::showMenu));
        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(22, title, description, settings, buttons, report);
        content.setAlignment(Pos.CENTER);
        ScrollPane scrolling = new ScrollPane(content);
        scrolling.setFitToWidth(true);
        scrolling.getStyleClass().add("maze-scroll");
        screen.getChildren().setAll(scrolling);
        consoleMessage.setText("READY TO RUN A REPRODUCIBLE MINIMAX EXPERIMENT.");
    }

    private void showHelp() {
        game = null;
        maze = null;
        researchMenuOpen = false;
        Label title = makeTitle("HELP / SYSTEM GUIDE");
        Label description = makeDescription("STRATUM-83 IS A LOCAL, OFFLINE JAVA EXPERIMENT.");
        Label help = new Label(
                "PLAY         Start Grid Zero against perfect Minimax.\n"
                + "DUEL         Start local two-player Grid Zero.\n"
                + "MAZE         Play Labyrinth using arrow keys or mouse.\n"
                + "RESEARCH     Open the Research Lab menu.\n"
                + "EXPERIMENT   Configure Minimax versus random results.\n"
                + "BENCHMARK    Compare maze solvers without playing.\n"
                + "BACK / MENU  Return to the main menu.\n"
                + "SOUND ON     Enable generated click sounds.\n"
                + "SOUND OFF    Disable sounds (default).\n"
                + "EXIT         Close the terminal.\n\n"
                + "F1 Help   F2 Toggle sound   ESC Main menu\n\n"
                + "Main menu has 3 numbered entries. Research Lab has 2.\n"
                + "All games and experiments run locally on your computer.");
        help.getStyleClass().add("help-text");
        help.setWrapText(true);
        VBox content = new VBox(24, title, description, help,
                actionButton("MAIN MENU", this::showMenu));
        content.setAlignment(Pos.CENTER_LEFT);
        content.setMaxWidth(720);
        screen.getChildren().setAll(content);
        consoleMessage.setText("HELP DISPLAYED.");
    }

    private void runCommand(String rawCommand) {
        String command = rawCommand.trim().toUpperCase(Locale.ROOT);
        sound.click();

        if (researchMenuOpen) {
            switch (command) {
                case "1" -> { showExperiment(); return; }
                case "2" -> { showMazeBenchmark(); return; }
                default -> { } // Named commands still work.
            }
        }

        switch (command) {
            case "PLAY", "1", "SOLO" -> startGame(true);
            case "DUEL" -> startGame(false);
            case "MAZE", "LABYRINTH", "2" -> showMaze();
            case "RESEARCH", "LAB", "3" -> showResearchMenu();
            case "EXPERIMENT" -> showExperiment();
            case "BENCHMARK", "PATHFINDING" -> showMazeBenchmark();
            case "HELP" -> showHelp();
            case "BACK", "MENU", "HOME" -> showMenu();
            case "EXIT", "QUIT" -> stage.close();
            case "SOUND ON" -> setSound(true);
            case "SOUND OFF" -> setSound(false);
            case "" -> { }
            default -> consoleMessage.setText("UNKNOWN COMMAND: " + command + ". TYPE HELP.");
        }
    }

    private void showMaze() {
        game = null;
        researchMenuOpen = false;
        maze = new MazeGenerator().generate(11, 15, mazeSeed);
        mazeChallenge = new MazeChallenge(maze);
        showMyRoute = false;
        showOptimalRoute = false;
        mazeCells = new Label[maze.rows()][maze.columns()];

        Label title = makeTitle("LABYRINTH CHALLENGE");
        Label instructions = makeDescription(
                "REACH G WITH AS FEW MOVES AS POSSIBLE / ARROWS OR CLICK ADJACENT CELLS");
        mazeMessage = new Label();
        mazeMessage.getStyleClass().add("game-message");

        GridPane board = new GridPane();
        board.setHgap(2);
        board.setVgap(2);
        board.setAlignment(Pos.CENTER);
        board.setFocusTraversable(true);
        for (int row = 0; row < maze.rows(); row++) {
            for (int column = 0; column < maze.columns(); column++) {
                int selectedRow = row;
                int selectedColumn = column;
                Label cell = new Label();
                cell.getStyleClass().add("maze-cell");
                cell.setPrefSize(27, 27);
                cell.setMinSize(27, 27);
                cell.setAlignment(Pos.CENTER);
                cell.setOnMouseClicked(event -> moveMaze(new Position(selectedRow, selectedColumn)));
                board.add(cell, column, row);
                mazeCells[row][column] = cell;
            }
        }

        HBox buttons = new HBox(12,
                actionButton("RETRY SAME MAZE", this::showMaze),
                actionButton("NEW MAZE", () -> { mazeSeed++; showMaze(); }),
                actionButton("MAIN MENU", this::showMenu));
        buttons.setAlignment(Pos.CENTER);

        mazePathButtons = new HBox(10,
                actionButton("SHOW MY VISITED CELLS", () -> {
                    showMyRoute = true;
                    showOptimalRoute = false;
                    refreshMaze();
                }),
                actionButton("SHOW OPTIMAL PATH", () -> {
                    showMyRoute = false;
                    showOptimalRoute = true;
                    refreshMaze();
                }),
                actionButton("HIDE PATH", () -> {
                    showMyRoute = false;
                    showOptimalRoute = false;
                    refreshMaze();
                }));
        mazePathButtons.setAlignment(Pos.CENTER);
        mazePathButtons.setVisible(false);
        mazePathButtons.setManaged(false);

        mazeResults = new Text();
        mazeResults.getStyleClass().add("challenge-results");
        mazeResults.setVisible(false);
        mazeResults.setManaged(false);

        Label seedNote = makeDescription("MAZE SEED: " + mazeSeed
                + " / THE BFS REFERENCE ROUTE IS REVEALED AFTER FINISHING");

        VBox content = new VBox(14, title, instructions, mazeMessage, board,
                buttons, mazeResults, mazePathButtons, seedNote);
        content.setAlignment(Pos.CENTER);
        ScrollPane scrolling = new ScrollPane(content);
        scrolling.setFitToWidth(true);
        scrolling.getStyleClass().add("maze-scroll");
        screen.getChildren().setAll(scrolling);
        refreshMaze();
        consoleMessage.setText("LABYRINTH CHALLENGE STARTED. REACH G WITH AS FEW MOVES AS POSSIBLE.");
        Platform.runLater(board::requestFocus);
    }

    private void moveMaze(Position destination) {
        if (maze == null || !mazeChallenge.moveTo(destination)) {
            return;
        }
        sound.click();
        refreshMaze();
        if (mazeChallenge.finished()) {
            showChallengeResults();
        }
    }

    private void showChallengeResults() {
        String efficiency = String.format(Locale.ROOT, "%.1f", mazeChallenge.efficiencyPercent());
        String verdict = mazeChallenge.extraMoves() == 0
                ? "PERFECT! YOUR ROUTE IS OPTIMAL."
                : "YOU CAN DO BETTER. TRY THE SAME MAZE AGAIN.";
        mazeResults.setText("YOUR MOVES:       " + mazeChallenge.moves()
                + "\nBFS OPTIMAL:      " + mazeChallenge.optimalMoves()
                + "\nEXTRA MOVES:      +" + mazeChallenge.extraMoves()
                + "\nEFFICIENCY:       " + efficiency + "%"
                + "\n\n" + verdict
                + "\nCOMPARE VISITED CELLS WITH THE SHORTEST PATH.");
        mazeResults.setVisible(true);
        mazeResults.setManaged(true);
        mazePathButtons.setVisible(true);
        mazePathButtons.setManaged(true);
        consoleMessage.setText("CHALLENGE COMPLETE. COMPARE YOUR ROUTE OR RETRY THE SAME SEED.");
    }

    private void refreshMaze() {
        Set<Position> humanVisited = showMyRoute
                ? new HashSet<>(mazeChallenge.playerRoute()) : Set.of();
        Set<Position> optimalCells = showOptimalRoute
                ? new HashSet<>(mazeChallenge.optimalRoute()) : Set.of();

        for (int row = 0; row < maze.rows(); row++) {
            for (int column = 0; column < maze.columns(); column++) {
                Position place = new Position(row, column);
                Label cell = mazeCells[row][column];
                cell.getStyleClass().removeAll(
                        "maze-wall", "maze-open", "maze-player", "maze-goal",
                        "maze-human-route", "maze-optimal-route");
                if (!maze.isOpen(place)) {
                    cell.getStyleClass().add("maze-wall");
                    cell.setText("");
                } else if (place.equals(maze.goal()) && mazeChallenge.finished()) {
                    cell.getStyleClass().add("maze-goal");
                    cell.setText("G");
                } else if (place.equals(mazeChallenge.playerPosition())) {
                    cell.getStyleClass().add("maze-player");
                    cell.setText("P");
                } else if (place.equals(maze.goal())) {
                    cell.getStyleClass().add("maze-goal");
                    cell.setText("G");
                } else if (optimalCells.contains(place)) {
                    cell.getStyleClass().add("maze-optimal-route");
                    cell.setText("·");
                } else if (humanVisited.contains(place)) {
                    cell.getStyleClass().add("maze-human-route");
                    cell.setText("·");
                } else {
                    cell.getStyleClass().add("maze-open");
                    cell.setText("");
                }
                cell.setAccessibleText("Row " + (row + 1) + ", column " + (column + 1)
                        + (maze.isOpen(place) ? ", open" : ", wall"));
            }
        }

        if (mazeChallenge.finished()) {
            mazeMessage.setText("GOAL REACHED / " + mazeChallenge.moves()
                    + " MOVES / BFS BEST: " + mazeChallenge.optimalMoves());
        } else {
            mazeMessage.setText("MOVES: " + mazeChallenge.moves() + " / GOAL: G / PLAYER: P");
        }
    }

    private void showMazeBenchmark() {
        game = null;
        maze = null;
        researchMenuOpen = false;

        Label title = makeTitle("PATHFINDING BENCHMARK");
        Label description = makeDescription(
                "BFS / DIJKSTRA / A*  -  SAME MAZES AND SEEDS FOR EVERY ALGORITHM");
        TextField seedField = researchField("1983");
        TextField sampleField = researchField("1");
        HBox settings = new HBox(20,
                new VBox(6, makeDescription("FIRST MAZE SEED"), seedField),
                new VBox(6, makeDescription("MAZE SAMPLES (1-100)"), sampleField));
        settings.setAlignment(Pos.CENTER);

        Text report = new Text(
                "CHOOSE PARAMETERS AND RUN THE BENCHMARK.\n"
                + "A SINGLE SEED EXAMINES ONE MAZE; MORE SAMPLES COMPARE AVERAGES.");
        report.getStyleClass().add("research-result");

        Button runButton = new Button("RUN BENCHMARK");
        runButton.getStyleClass().add("action-button");
        runButton.setOnAction(event -> {
            Long seed = readSeed(seedField, report);
            Integer count = readCount(sampleField, 100, "MAZE COUNT", report);
            if (seed == null || count == null) return;

            sound.click();
            runButton.setDisable(true);
            report.setText("ANALYZING " + count + " MAZE(S)...");

            Task<MazeBenchmarkResult> task = new Task<>() {
                @Override
                protected MazeBenchmarkResult call() {
                    return new MazeBenchmarkRunner().run(seed, count);
                }
            };
            task.setOnSucceeded(done -> {
                MazeBenchmarkResult result = task.getValue();
                StringBuilder text = new StringBuilder();
                text.append("FIRST SEED: ").append(result.firstSeed())
                        .append("\nMAZE SAMPLES: ").append(result.mazeCount())
                        .append("\nGRID SIZE: 11 x 15 / STEP COST: 1\n\n")
                        .append("ALGORITHM       AVG STEPS     AVG EXPLORED\n");
                for (MazeBenchmarkResult.AlgorithmResult item : result.algorithms()) {
                    text.append(String.format(Locale.ROOT, "%-13s %10.2f %16.2f\n",
                            item.algorithm(), item.averageSteps(), item.averageExplored()));
                }
                text.append("\nALL SOLVERS FOUND EQUAL SHORTEST PATHS.\n")
                        .append("SAME SEED AND SAMPLE COUNT = SAME RESULTS.");
                report.setText(text.toString());
                runButton.setDisable(false);
                consoleMessage.setText("PATHFINDING BENCHMARK FINISHED.");
            });
            task.setOnFailed(done -> {
                report.setText("BENCHMARK FAILED: " + task.getException().getMessage());
                runButton.setDisable(false);
            });
            Thread worker = new Thread(task, "maze-benchmark");
            worker.setDaemon(true);
            worker.start();
        });

        HBox buttons = new HBox(12, runButton,
                actionButton("RESEARCH LAB", this::showResearchMenu),
                actionButton("MAIN MENU", this::showMenu));
        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(22, title, description, settings, buttons, report);
        content.setAlignment(Pos.CENTER);
        ScrollPane scrolling = new ScrollPane(content);
        scrolling.setFitToWidth(true);
        scrolling.getStyleClass().add("maze-scroll");
        screen.getChildren().setAll(scrolling);
        consoleMessage.setText("READY TO RUN A REPRODUCIBLE PATHFINDING BENCHMARK.");
    }

    private TextField researchField(String defaultValue) {
        TextField field = new TextField(defaultValue);
        field.getStyleClass().add("research-input");
        field.setPrefColumnCount(12);
        return field;
    }

    private Long readSeed(TextField field, Text report) {
        try {
            return Long.parseLong(field.getText().trim());
        } catch (NumberFormatException exception) {
            report.setText("ENTER A VALID INTEGER SEED (64-BIT NUMBER).");
            return null;
        }
    }

    private Integer readCount(TextField field, int maximum, String name, Text report) {
        try {
            int count = Integer.parseInt(field.getText().trim());
            if (count >= 1 && count <= maximum) return count;
        } catch (NumberFormatException exception) {
            // Display the same instruction for invalid text and out-of-range values.
        }
        report.setText(name + " MUST BE BETWEEN 1 AND " + maximum + ".");
        return null;
    }

    private void toggleSound() {
        setSound(!sound.isEnabled());
    }

    private void setSound(boolean enabled) {
        sound.setEnabled(enabled);
        soundButton.setText(enabled ? "SOUND: ON" : "SOUND: OFF");
        consoleMessage.setText(enabled ? "SOUND ENABLED." : "SOUND DISABLED.");
        sound.click();
    }

    private Button menuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("menu-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> {
            sound.click();
            action.run();
        });
        return button;
    }

    private Button actionButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("action-button");
        button.setOnAction(event -> {
            sound.click();
            action.run();
        });
        return button;
    }

    private Label makeTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("screen-title");
        return label;
    }

    private Label makeDescription(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("screen-subtitle");
        label.setWrapText(true);
        return label;
    }

    @Override
    public void stop() {
        sound.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
