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
import javafx.stage.Stage;
import org.stratum83.game.GameResult;
import org.stratum83.game.GridZeroGame;
import org.stratum83.game.MinimaxPlayer;
import org.stratum83.game.Move;
import org.stratum83.labyrinth.Maze;
import org.stratum83.labyrinth.MazeGenerator;
import org.stratum83.labyrinth.MazeSolver;
import org.stratum83.labyrinth.PathResult;
import org.stratum83.labyrinth.Position;
import java.util.HashSet;
import java.util.Set;
import org.stratum83.research.ExperimentResult;
import org.stratum83.research.ExperimentRunner;

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
    private Position mazePlayer;
    private Label[][] mazeCells;
    private Label mazeMessage;
    private Label mazeReport;
    private Set<Position> shownPath = Set.of();
    private long mazeSeed = 1983;
    private int mazeSteps;
    private boolean vsComputer;

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
                moveMaze(new Position(mazePlayer.row() + rowStep, mazePlayer.column() + colStep));
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
        Label instructions = new Label("COMMANDS: PLAY  |  DUEL  |  EXPERIMENT  |  HELP  |  MENU  |  EXIT     F1 HELP  /  F2 SOUND  /  ESC MENU");
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
        Label heading = makeTitle("SELECT PROGRAM");
        Label introduction = makeDescription(
                "LOCAL GAMES / ALGORITHM EXPERIMENTS / NO NETWORK REQUIRED");

        VBox menu = new VBox(11,
                menuButton("01  GRID ZERO       HUMAN VS COMPUTER", () -> startGame(true)),
                menuButton("02  GRID ZERO       TWO PLAYERS", () -> startGame(false)),
                menuButton("03  AI EXPERIMENT   MINIMAX VS RANDOM", this::showExperiment),
                menuButton("04  HELP            COMMAND REFERENCE", this::showHelp),
                menuButton("05  EXIT            CLOSE TERMINAL", () -> stage.close()),
                menuButton("06  LABYRINTH       PATHFINDING LAB", this::showMaze)
        );
        menu.setMaxWidth(570);

        Label note = makeDescription("RESEARCH BUILD 0.1  ·  ORIGINAL GAME CODE  ·  JAVA 21");
        VBox content = new VBox(24, heading, introduction, menu, note);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setMaxWidth(600);
        screen.getChildren().setAll(content);
        consoleMessage.setText("READY. SELECT A PROGRAM OR TYPE A COMMAND.");
    }

    private void startGame(boolean againstComputer) {
        maze = null;
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
        Label title = makeTitle("AI EXPERIMENT");
        Label details = makeDescription(
                "200 SEEDED GAMES  /  PERFECT MINIMAX VS RANDOM MOVES  /  ALTERNATING START POSITION");
        Label report = new Label("RUN THE EXPERIMENT TO VIEW THE RESULTS.");
        report.getStyleClass().add("report");
        report.setWrapText(true);

        Button runButton = actionButton("RUN 200 GAMES", () -> { });
        runButton.setOnAction(event -> {
            sound.click();
            runButton.setDisable(true);
            report.setText("CALCULATING... THIS EXPERIMENT RUNS LOCALLY.");

            Task<ExperimentResult> task = new Task<>() {
                @Override
                protected ExperimentResult call() {
                    return new ExperimentRunner().run(200, 1983L);
                }
            };
            task.setOnSucceeded(done -> {
                ExperimentResult result = task.getValue();
                report.setText("GAMES: " + result.games()
                        + "\nMINIMAX WINS: " + result.computerWins()
                        + "\nDRAWS: " + result.draws()
                        + "\nMINIMAX LOSSES: " + result.computerLosses()
                        + "\nSEED: " + result.seed()
                        + "\n\nTHE SAME SEED PRODUCES THE SAME RESULTS.");
                runButton.setDisable(false);
                consoleMessage.setText("EXPERIMENT FINISHED.");
            });
            task.setOnFailed(done -> {
                report.setText("EXPERIMENT FAILED: " + task.getException().getMessage());
                runButton.setDisable(false);
            });
            Thread worker = new Thread(task, "experiment-runner");
            worker.setDaemon(true);
            worker.start();
        });

        HBox buttons = new HBox(12, runButton, actionButton("MAIN MENU", this::showMenu));
        buttons.setAlignment(Pos.CENTER);
        VBox content = new VBox(24, title, details, report, buttons);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(720);
        screen.getChildren().setAll(content);
        consoleMessage.setText("READY TO RUN AN OFFLINE EXPERIMENT.");
    }

    private void showHelp() {
        game = null;
        maze = null;
        Label title = makeTitle("HELP / SYSTEM GUIDE");
        Label description = makeDescription("STRATUM-83 IS A LOCAL, OFFLINE JAVA EXPERIMENT.");
        Label text = new Label(
                "PLAY         Start Grid Zero against a perfect-play computer.\n"
                + "DUEL         Start Grid Zero for two people at one keyboard.\n"
                + "EXPERIMENT   Compare Minimax against a seeded random player.\n"
                + "MAZE         Explore Labyrinth and compare pathfinding algorithms.\n"
                + "MENU         Return to the program selection screen.\n"
                + "SOUND ON     Enable generated click sounds.\n"
                + "SOUND OFF    Disable sounds (the default).\n"
                + "EXIT         Close the program.\n\n"
                + "F1 = Help    F2 = Toggle sound    ESC = Menu\n\n"
                + "Grid Zero rules: three in a row wins. X moves first.\n"
                + "All game rules and AI algorithms run locally in Java.");
        text.getStyleClass().add("help-text");
        text.setWrapText(true);
        VBox content = new VBox(25, title, description, text,
                actionButton("BACK TO MENU", this::showMenu));
        content.setAlignment(Pos.CENTER_LEFT);
        content.setMaxWidth(720);
        screen.getChildren().setAll(content);
        consoleMessage.setText("HELP DISPLAYED.");
    }

    private void runCommand(String rawCommand) {
        String command = rawCommand.trim().toUpperCase();
        sound.click();
        switch (command) {
            case "PLAY", "1", "SOLO" -> startGame(true);
            case "DUEL", "2" -> startGame(false);
            case "EXPERIMENT", "3" -> showExperiment();
            case "MAZE", "LABYRINTH", "6" -> showMaze();
            case "HELP", "4" -> showHelp();
            case "MENU", "HOME" -> showMenu();
            case "EXIT", "QUIT", "5" -> stage.close();
            case "SOUND ON" -> setSound(true);
            case "SOUND OFF" -> setSound(false);
            case "" -> { }
            default -> consoleMessage.setText("UNKNOWN COMMAND: " + command + ". TYPE HELP.");
        }
    }

    private void showMaze() {
        game = null;
        maze = new MazeGenerator().generate(11, 15, mazeSeed);
        mazePlayer = maze.start();
        mazeSteps = 0;
        shownPath = Set.of();
        mazeCells = new Label[maze.rows()][maze.columns()];

        Label title = makeTitle("LABYRINTH");
        Label instructions = makeDescription(
                "FIND G  /  ARROW KEYS OR CLICK ADJACENT CELLS  /  WALLS BLOCK MOVEMENT");
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

        HBox solverButtons = new HBox(10,
                actionButton("BFS", () -> showMazeSolution(MazeSolver.Algorithm.BFS)),
                actionButton("DIJKSTRA", () -> showMazeSolution(MazeSolver.Algorithm.DIJKSTRA)),
                actionButton("A*", () -> showMazeSolution(MazeSolver.Algorithm.A_STAR)),
                actionButton("COMPARE", this::compareMazeAlgorithms));
        solverButtons.setAlignment(Pos.CENTER);
        HBox navigationButtons = new HBox(10,
                actionButton("NEW MAZE", () -> { mazeSeed++; showMaze(); }),
                actionButton("CLEAR PATH", () -> { shownPath = Set.of(); refreshMaze(); }),
                actionButton("MAIN MENU", this::showMenu));
        navigationButtons.setAlignment(Pos.CENTER);

        mazeReport = new Label("SEED: " + mazeSeed + "  |  EXPLORE OR SELECT A SOLVER");
        mazeReport.getStyleClass().add("maze-report");
        mazeReport.setWrapText(true);

        VBox content = new VBox(11, title, instructions, mazeMessage, board,
                solverButtons, navigationButtons, mazeReport);
        content.setAlignment(Pos.CENTER);
        ScrollPane scrollingBoard = new ScrollPane(content);
        scrollingBoard.setFitToWidth(true);
        scrollingBoard.getStyleClass().add("maze-scroll");
        screen.getChildren().setAll(scrollingBoard);
        refreshMaze();
        consoleMessage.setText("LABYRINTH READY. USE ARROW KEYS OR CLICK AN ADJACENT OPEN CELL.");
        Platform.runLater(board::requestFocus);
    }

    private void moveMaze(Position destination) {
        if (maze == null || mazePlayer.equals(maze.goal())) return;
        int distance = Math.abs(destination.row() - mazePlayer.row())
                + Math.abs(destination.column() - mazePlayer.column());
        if (distance != 1 || !maze.isOpen(destination)) return;
        mazePlayer = destination;
        mazeSteps++;
        shownPath = Set.of();
        sound.click();
        refreshMaze();
        if (mazePlayer.equals(maze.goal())) {
            consoleMessage.setText("GOAL REACHED IN " + mazeSteps + " MOVES.");
        }
    }

    private void refreshMaze() {
        for (int row = 0; row < maze.rows(); row++) {
            for (int column = 0; column < maze.columns(); column++) {
                Position place = new Position(row, column);
                Label cell = mazeCells[row][column];
                cell.getStyleClass().removeAll(
                        "maze-wall", "maze-open", "maze-player", "maze-goal", "maze-path");
                if (!maze.isOpen(place)) {
                    cell.getStyleClass().add("maze-wall");
                    cell.setText("");
                } else if (place.equals(mazePlayer)) {
                    cell.getStyleClass().add("maze-player");
                    cell.setText("P");
                } else if (place.equals(maze.goal())) {
                    cell.getStyleClass().add("maze-goal");
                    cell.setText("G");
                } else if (shownPath.contains(place)) {
                    cell.getStyleClass().add("maze-path");
                    cell.setText("·");
                } else {
                    cell.getStyleClass().add("maze-open");
                    cell.setText("");
                }
                cell.setAccessibleText("Row " + (row + 1) + ", column " + (column + 1)
                        + (maze.isOpen(place) ? ", open" : ", wall"));
            }
        }
        mazeMessage.setText(mazePlayer.equals(maze.goal())
                ? "MAZE COMPLETE IN " + mazeSteps + " MOVES"
                : "MOVES: " + mazeSteps + "  /  GOAL: G  /  PLAYER: P");
    }

    private void showMazeSolution(MazeSolver.Algorithm algorithm) {
        if (maze == null) return;
        PathResult result = new MazeSolver().solve(maze, algorithm);
        shownPath = new HashSet<>(result.path());
        refreshMaze();
        mazeReport.setText(algorithm + "  /  SHORTEST PATH: " + result.steps()
                + " MOVES  /  EXPLORED CELLS: " + result.exploredNodes()
                + "  /  SEED: " + mazeSeed);
        consoleMessage.setText("PATH HIGHLIGHTED. ALL ALGORITHMS RUN LOCALLY.");
    }

    private void compareMazeAlgorithms() {
        if (maze == null) return;
        MazeSolver solver = new MazeSolver();
        StringBuilder report = new StringBuilder("ALGORITHM      STEPS   EXPLORED\n");
        for (MazeSolver.Algorithm algorithm : MazeSolver.Algorithm.values()) {
            PathResult result = solver.solve(maze, algorithm);
            report.append(String.format("%-14s %5d  %5d\n",
                    algorithm, result.steps(), result.exploredNodes()));
        }
        mazeReport.setText(report.toString());
        consoleMessage.setText("COMPARISON COMPLETE. ALL METHODS MUST FIND THE SHORTEST PATH.");
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
