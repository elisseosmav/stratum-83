package org.stratum83.ui;

import java.util.Locale;
import java.util.Random;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.stratum83.naval.NavalGame;

public final class NavalScreen extends VBox {
    private final Runnable backToMenu;
    private final Runnable playSound;
    private final Consumer<String> status;
    private NavalGame game;
    private NavalGame.Orientation orientation;
    private Button[][] enemyCells;
    private Button[][] friendlyCells;
    private Label summary;
    private Label message;
    private Label instruction;
    private Label playerLog;
    private Label computerLog;
    private Label analysisLog;
    private Button heatmapButton;
    private boolean showHeatmap;

    public NavalScreen(Runnable backToMenu, Consumer<String> status, Runnable playSound) {
        this.backToMenu = backToMenu;
        this.status = status;
        this.playSound = playSound;
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(15));
        setSpacing(10);
        setMaxWidth(840);
        newGame(NavalGame.Difficulty.NORMAL);
    }

    public void newGame(NavalGame.Difficulty difficulty) {
        game = new NavalGame(new Random(), difficulty);
        orientation = NavalGame.Orientation.HORIZONTAL;
        showHeatmap = false;
        enemyCells = new Button[NavalGame.SIZE][NavalGame.SIZE];
        friendlyCells = new Button[NavalGame.SIZE][NavalGame.SIZE];

        VBox enemy = new VBox(5, subtitle("ENEMY WATERS / TARGET GRID"), board(true));
        enemy.setAlignment(Pos.TOP_CENTER);
        VBox friendly = new VBox(5, subtitle("FRIENDLY FLEET / DEPLOYMENT"), board(false));
        friendly.setAlignment(Pos.TOP_CENTER);
        HBox boards = new HBox(12, enemy, friendly);
        boards.setAlignment(Pos.CENTER);

        summary = subtitle("");
        message = subtitle("");
        message.getStyleClass().add("game-message");
        instruction = subtitle("");
        playerLog = subtitle("");
        computerLog = subtitle("");
        analysisLog = subtitle("");
        analysisLog.setWrapText(true);
        heatmapButton = action("AI ANALYSIS", () -> {
            showHeatmap = !showHeatmap;
            refresh();
        });

        FlowPane deploymentActions = actions(
                action("HORIZONTAL", () -> {
                    orientation = NavalGame.Orientation.HORIZONTAL;
                    refresh();
                }),
                action("VERTICAL", () -> {
                    orientation = NavalGame.Orientation.VERTICAL;
                    refresh();
                }),
                action("AUTO DEPLOY", this::autoDeploy));
        FlowPane commands = actions(
                action("NEW EASY", () -> newGame(NavalGame.Difficulty.EASY)),
                action("NEW NORMAL", () -> newGame(NavalGame.Difficulty.NORMAL)),
                action("NEW HARD", () -> newGame(NavalGame.Difficulty.HARD)),
                heatmapButton,
                action("RULES", this::rules),
                action("GAMES", backToMenu));

        Label title = subtitle("NAVAL COMMAND");
        title.getStyleClass().add("screen-title");
        getChildren().setAll(
                title, subtitle("1983 / TACTICAL FLEET COMPUTER / HUMAN VS AI"),
                summary, message, boards, instruction, playerLog, computerLog,
                analysisLog, deploymentActions, commands,
                subtitle("COMMANDS: AUTO / PLACE CARRIER B2 H / FIRE D7 / STATUS / RADAR / HELP"));
        getStyleClass().add("naval-screen");
        refresh();
    }

    private GridPane board(boolean enemy) {
        GridPane pane = new GridPane();
        pane.setHgap(1);
        pane.setVgap(1);
        pane.setAlignment(Pos.CENTER);
        pane.getStyleClass().add("naval-board");
        for (int i = 0; i < NavalGame.SIZE; i++) {
            Label column = subtitle("" + (char) ('A' + i));
            column.getStyleClass().add("naval-axis");
            column.setAlignment(Pos.CENTER);
            column.setPrefWidth(24);
            pane.add(column, i + 1, 0);
            Label row = subtitle(Integer.toString(i + 1));
            row.getStyleClass().add("naval-axis");
            row.setPrefWidth(24);
            row.setAlignment(Pos.CENTER);
            pane.add(row, 0, i + 1);
        }
        for (int row = 0; row < NavalGame.SIZE; row++) {
            for (int col = 0; col < NavalGame.SIZE; col++) {
                int r = row, c = col;
                Button square = action("", () -> {
                    if (enemy) fire(new NavalGame.Position(r, c));
                    else deploy(new NavalGame.Position(r, c), orientation);
                });
                square.getStyleClass().setAll("naval-cell");
                square.setMinSize(24, 24);
                square.setPrefSize(24, 24);
                square.setMaxSize(24, 24);
                pane.add(square, col + 1, row + 1);
                if (enemy) enemyCells[row][col] = square;
                else friendlyCells[row][col] = square;
            }
        }
        return pane;
    }

    private void deploy(NavalGame.Position at, NavalGame.Orientation direction) {
        if (game.placeNext(at, direction)) {
            playSound.run();
            refresh();
        } else {
            status.accept("NAVAL / SHIP MUST FIT AND CANNOT OVERLAP ANOTHER SHIP");
        }
    }

    private void fire(NavalGame.Position target) {
        if (game.fire(target)) {
            playSound.run();
            refresh();
        } else {
            status.accept("NAVAL / DEPLOY FIRST OR FIRE AT AN UNTRIED COORDINATE");
        }
    }

    private void autoDeploy() {
        game.autoDeploy();
        refresh();
    }

    public boolean execute(String raw) {
        String input = raw.trim().toUpperCase(Locale.ROOT);
        switch (input) {
            case "NEW" -> { newGame(game.difficulty()); return true; }
            case "AUTO" -> { autoDeploy(); return true; }
            case "RADAR" -> { refresh(); return true; }
            case "STATUS" -> {
                status.accept("NAVAL / ROUND " + game.rounds()
                        + " / FRIENDLY " + game.ownShipsRemaining()
                        + " / ENEMY " + game.enemyShipsRemaining());
                return true;
            }
            case "HELP" -> { rules(); return true; }
            case "EXIT" -> { backToMenu.run(); return true; }
            default -> { }
        }
        if (input.startsWith("FIRE ")) {
            try {
                fire(NavalGame.Position.parse(input.substring(5)));
            } catch (IllegalArgumentException exception) {
                status.accept("NAVAL / INVALID COORDINATE / TRY FIRE D7");
            }
            return true;
        }
        if (input.startsWith("PLACE ")) {
            String[] fields = input.split("\\s+");
            if (fields.length != 4) {
                status.accept("NAVAL / USE PLACE CARRIER B2 H");
                return true;
            }
            try {
                NavalGame.Vessel vessel = NavalGame.Vessel.valueOf(fields[1]);
                NavalGame.Position position = NavalGame.Position.parse(fields[2]);
                NavalGame.Orientation direction = switch (fields[3]) {
                    case "H" -> NavalGame.Orientation.HORIZONTAL;
                    case "V" -> NavalGame.Orientation.VERTICAL;
                    default -> throw new IllegalArgumentException("Expected H or V");
                };
                if (game.phase() != NavalGame.Phase.DEPLOYMENT || game.nextVessel() != vessel) {
                    status.accept("NAVAL / DEPLOY SHIPS IN FLEET ORDER");
                } else deploy(position, direction);
            } catch (IllegalArgumentException exception) {
                status.accept("NAVAL / INVALID PLACEMENT / USE PLACE CARRIER B2 H");
            }
            return true;
        }
        return false;
    }

    private void refresh() {
        int[][] heatmap = game.computerHeatmap();
        int max = 1;
        for (int[] row : heatmap) for (int value : row) max = Math.max(max, value);
        for (int row = 0; row < NavalGame.SIZE; row++) {
            for (int col = 0; col < NavalGame.SIZE; col++) {
                NavalGame.Position position = new NavalGame.Position(row, col);
                Button enemy = enemyCells[row][col];
                NavalGame.Mark outgoing = game.enemyMark(position);
                enemy.getStyleClass().setAll("naval-cell", "naval-unknown");
                enemy.setText(switch (outgoing) {
                    case MISS -> "·";
                    case HIT -> "X";
                    case SUNK -> "#";
                    case UNKNOWN -> " ";
                });
                if (outgoing != NavalGame.Mark.UNKNOWN) {
                    enemy.getStyleClass().add("naval-" + outgoing.name().toLowerCase(Locale.ROOT));
                }
                enemy.setDisable(game.phase() != NavalGame.Phase.COMBAT
                        || outgoing != NavalGame.Mark.UNKNOWN);
                enemy.setAccessibleText("Enemy " + position.label() + " " + outgoing.name());

                Button own = friendlyCells[row][col];
                NavalGame.Mark incoming = game.ownMark(position);
                own.getStyleClass().setAll("naval-cell", "naval-unknown");
                boolean ship = game.playerShipAt(position);
                String value = ship ? "■" : " ";
                if (ship) own.getStyleClass().add("naval-ship");
                if (incoming != NavalGame.Mark.UNKNOWN) {
                    value = switch (incoming) {
                        case MISS -> "·";
                        case HIT -> "X";
                        case SUNK -> "#";
                        default -> "";
                    };
                    own.getStyleClass().add("naval-" + incoming.name().toLowerCase(Locale.ROOT));
                }
                if (showHeatmap && incoming == NavalGame.Mark.UNKNOWN) {
                    int level = heatmap[row][col] * 9 / max;
                    value = Integer.toString(level);
                    if (level >= 7) own.getStyleClass().add("naval-risk-high");
                    else if (level >= 4) own.getStyleClass().add("naval-risk-medium");
                }
                own.setText(value);
                own.setDisable(game.phase() != NavalGame.Phase.DEPLOYMENT);
                own.setAccessibleText("Friendly " + position.label()
                        + (ship ? " ship" : " sea") + " / " + incoming.name());
            }
        }
        summary.setText("ROUND " + game.rounds() + "  /  YOUR FLEET " + game.ownShipsRemaining()
                + "/5  /  ENEMY FLEET " + game.enemyShipsRemaining()
                + "/5  /  AI " + game.difficulty().name());
        message.setText(switch (game.phase()) {
            case DEPLOYMENT -> "FLEET DEPLOYMENT / POSITION YOUR SHIPS";
            case COMBAT -> "ENGAGEMENT ACTIVE / YOUR TURN";
            case FINISHED -> game.result() == NavalGame.Result.PLAYER_WINS
                    ? "ALL ENEMY VESSELS SUNK / YOU WIN"
                    : "YOUR FLEET HAS BEEN SUNK / COMPUTER WINS";
        });
        instruction.setText(switch (game.phase()) {
            case DEPLOYMENT -> "PLACE " + game.nextVessel() + " ("
                    + game.nextVessel().length() + " CELLS) / " + orientation
                    + " / CLICK FRIENDLY GRID OR AUTO DEPLOY";
            case COMBAT -> "FIRE AT ENEMY WATERS / CLICK OR TYPE FIRE D7";
            case FINISHED -> showHeatmap ? "AI HEATMAP / 0–9 PRIORITY AT LAST SHOT"
                    : "MATCH COMPLETE / SELECT AI ANALYSIS TO INSPECT COMPUTER SEARCH";
        });
        playerLog.setText("YOUR REPORT: " + game.playerReport());
        computerLog.setText("COMPUTER REPORT: " + game.computerReport());
        heatmapButton.setDisable(game.phase() != NavalGame.Phase.FINISHED
                || game.computerShots().isEmpty());
        heatmapButton.setText(showHeatmap ? "HIDE AI ANALYSIS" : "AI ANALYSIS");
        analysisLog.setText(showHeatmap
                ? "COMPUTER SHOT HISTORY: " + game.computerShots().stream()
                        .map(shot -> shot.position().label()).toList()
                : "");
        status.accept("NAVAL / FIRE D7 / AUTO / PLACE CARRIER B2 H / ESC = GAMES");
    }

    private void rules() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("NAVAL COMMAND / RULES");
        dialog.setHeaderText("HUMAN VS COMPUTER / 1983 FLEET TERMINAL");
        dialog.setContentText(
                "Deploy five ships: Carrier (5), Battleship (4), Cruiser (3), "
                + "Submarine (3), Destroyer (2). Ships may be horizontal or vertical, "
                + "must fit on the 10x10 board, and cannot overlap.\n\n"
                + "Click friendly waters to place a vessel or use AUTO DEPLOY. "
                + "During combat, click enemy waters or type FIRE D7. "
                + "The computer responds with one shot per turn. "
                + "Sink all opposing vessels to win.\n\n"
                + "EASY uses random targets. NORMAL hunts neighboring squares after a hit. "
                + "HARD evaluates legal ship placements using observed shots. "
                + "The computer never reads your hidden ship positions.\n\n"
                + "Commands: NEW / AUTO / PLACE CARRIER B2 H / FIRE D7 "
                + "/ STATUS / RADAR / HELP / EXIT.\n\n"
                + "After the match, AI ANALYSIS displays the computer's last "
                + "probability map and its shot history.");
        dialog.showAndWait();
    }

    private static Button action(String title, Runnable callback) {
        Button button = new Button(title);
        button.getStyleClass().add("action-button");
        button.setOnAction(event -> callback.run());
        return button;
    }

    private static Label subtitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("screen-subtitle");
        label.setWrapText(true);
        return label;
    }

    private static FlowPane actions(Button... controls) {
        FlowPane pane = new FlowPane(8, 8);
        pane.setAlignment(Pos.CENTER);
        pane.getChildren().addAll(controls);
        return pane;
    }
}
