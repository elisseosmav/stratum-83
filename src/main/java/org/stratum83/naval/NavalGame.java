package org.stratum83.naval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class NavalGame {
    public static final int SIZE = 10;

    public enum Phase { DEPLOYMENT, COMBAT, FINISHED }
    public enum Result { PLAYING, PLAYER_WINS, COMPUTER_WINS }
    public enum Difficulty { EASY, NORMAL, HARD }
    public enum Orientation { HORIZONTAL, VERTICAL }
    public enum Mark { UNKNOWN, MISS, HIT, SUNK }

    public enum Vessel {
        CARRIER(5), BATTLESHIP(4), CRUISER(3), SUBMARINE(3), DESTROYER(2);
        private final int length;
        Vessel(int length) { this.length = length; }
        public int length() { return length; }
    }

    public record Position(int row, int col) {
        public Position {
            if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
                throw new IllegalArgumentException("Position outside grid");
            }
        }

        public String label() { return "" + (char) ('A' + col) + (row + 1); }

        public static Position parse(String raw) {
            if (raw == null || !raw.trim().toUpperCase().matches("[A-J](10|[1-9])")) {
                throw new IllegalArgumentException("Coordinate must be A1 to J10");
            }
            String s = raw.trim().toUpperCase();
            return new Position(Integer.parseInt(s.substring(1)) - 1, s.charAt(0) - 'A');
        }
    }

    public record ShotReport(Position position, Mark mark, Vessel vessel) {
        public String description() {
            return position.label() + " / " + switch (mark) {
                case MISS -> "MISS";
                case HIT -> "HIT";
                case SUNK -> "SUNK " + vessel.name();
                case UNKNOWN -> "NO CONTACT";
            };
        }
    }

    private static final class Ship {
        final Vessel vessel;
        final List<Position> cells;
        Ship(Vessel vessel, List<Position> cells) {
            this.vessel = vessel;
            this.cells = List.copyOf(cells);
        }
    }

    private static final class Fleet {
        final int[][] occupancy = new int[SIZE][SIZE];
        final Mark[][] incoming = new Mark[SIZE][SIZE];
        final List<Ship> ships = new ArrayList<>();

        Fleet() {
            for (int row = 0; row < SIZE; row++) {
                Arrays.fill(occupancy[row], -1);
                Arrays.fill(incoming[row], Mark.UNKNOWN);
            }
        }

        boolean canPlace(Vessel vessel, Position start, Orientation orientation) {
            Objects.requireNonNull(vessel);
            Objects.requireNonNull(start);
            Objects.requireNonNull(orientation);
            if (ships.stream().anyMatch(s -> s.vessel == vessel)) return false;
            for (int n = 0; n < vessel.length(); n++) {
                int row = start.row() + (orientation == Orientation.VERTICAL ? n : 0);
                int col = start.col() + (orientation == Orientation.HORIZONTAL ? n : 0);
                if (row >= SIZE || col >= SIZE || occupancy[row][col] >= 0) return false;
            }
            return true;
        }

        void place(Vessel vessel, Position start, Orientation orientation) {
            if (!canPlace(vessel, start, orientation)) {
                throw new IllegalArgumentException("Invalid ship placement");
            }
            List<Position> cells = new ArrayList<>();
            for (int n = 0; n < vessel.length(); n++) {
                Position p = new Position(start.row() + (orientation == Orientation.VERTICAL ? n : 0),
                        start.col() + (orientation == Orientation.HORIZONTAL ? n : 0));
                cells.add(p);
                occupancy[p.row()][p.col()] = ships.size();
            }
            ships.add(new Ship(vessel, cells));
        }

        boolean contains(Position position) {
            return occupancy[position.row()][position.col()] >= 0;
        }

        ShotReport fire(Position target) {
            if (incoming[target.row()][target.col()] != Mark.UNKNOWN) {
                throw new IllegalArgumentException("Coordinate already fired upon");
            }
            int shipIndex = occupancy[target.row()][target.col()];
            if (shipIndex < 0) {
                incoming[target.row()][target.col()] = Mark.MISS;
                return new ShotReport(target, Mark.MISS, null);
            }
            Ship ship = ships.get(shipIndex);
            incoming[target.row()][target.col()] = Mark.HIT;
            boolean sunk = ship.cells.stream().allMatch(p ->
                    incoming[p.row()][p.col()] == Mark.HIT || incoming[p.row()][p.col()] == Mark.SUNK);
            if (sunk) {
                for (Position p : ship.cells) incoming[p.row()][p.col()] = Mark.SUNK;
                return new ShotReport(target, Mark.SUNK, ship.vessel);
            }
            return new ShotReport(target, Mark.HIT, ship.vessel);
        }

        int remaining() {
            int count = 0;
            for (Ship ship : ships) {
                if (ship.cells.stream().anyMatch(p ->
                        incoming[p.row()][p.col()] != Mark.HIT
                        && incoming[p.row()][p.col()] != Mark.SUNK)) count++;
            }
            return count;
        }

        Mark mark(Position p) {
            return incoming[p.row()][p.col()];
        }

        Mark[][] knowledge() {
            Mark[][] copy = new Mark[SIZE][SIZE];
            for (int r = 0; r < SIZE; r++) copy[r] = incoming[r].clone();
            return copy;
        }

        List<Integer> remainingLengths() {
            List<Integer> result = new ArrayList<>();
            for (Ship ship : ships) {
                if (ship.cells.stream().anyMatch(p ->
                        incoming[p.row()][p.col()] != Mark.SUNK)) {
                    result.add(ship.vessel.length());
                }
            }
            return List.copyOf(result);
        }

        List<Position> positions() {
            List<Position> result = new ArrayList<>();
            for (Ship ship : ships) result.addAll(ship.cells);
            return List.copyOf(result);
        }
    }

    private final Random random;
    private final Difficulty difficulty;
    private final Fleet playerFleet = new Fleet();
    private final Fleet computerFleet = new Fleet();
    private final List<ShotReport> playerHistory = new ArrayList<>();
    private final List<ShotReport> computerHistory = new ArrayList<>();
    private Phase phase = Phase.DEPLOYMENT;
    private Result result = Result.PLAYING;
    private int rounds = 1;
    private String playerReport = "DEPLOY YOUR FLEET";
    private String computerReport = "ENEMY POSITION UNKNOWN";
    private int[][] lastComputerHeatmap = new int[SIZE][SIZE];

    public NavalGame(Random random, Difficulty difficulty) {
        this.random = Objects.requireNonNull(random);
        this.difficulty = Objects.requireNonNull(difficulty);
        for (Vessel vessel : Vessel.values()) autoPlace(computerFleet, vessel);
    }

    public NavalGame() { this(new Random(), Difficulty.NORMAL); }

    public Difficulty difficulty() { return difficulty; }
    public Phase phase() { return phase; }
    public Result result() { return result; }
    public int rounds() { return rounds; }
    public String playerReport() { return playerReport; }
    public String computerReport() { return computerReport; }
    public int deployedShips() { return playerFleet.ships.size(); }
    public Vessel nextVessel() {
        return phase == Phase.DEPLOYMENT ? Vessel.values()[deployedShips()] : null;
    }

    public boolean canDeploy(Position start, Orientation direction) {
        return phase == Phase.DEPLOYMENT
                && playerFleet.canPlace(nextVessel(), start, direction);
    }

    public boolean placeNext(Position start, Orientation direction) {
        if (!canDeploy(start, direction)) return false;
        Vessel vessel = nextVessel();
        playerFleet.place(vessel, start, direction);
        playerReport = "DEPLOYED " + vessel.name() + " AT " + start.label()
                + " / " + direction.name();
        checkDeploymentComplete();
        return true;
    }

    public void autoDeploy() {
        if (phase != Phase.DEPLOYMENT) return;
        while (phase == Phase.DEPLOYMENT) {
            autoPlace(playerFleet, nextVessel());
            checkDeploymentComplete();
        }
        playerReport = "FLEET DEPLOYED / SELECT AN ENEMY COORDINATE";
    }

    private void checkDeploymentComplete() {
        if (playerFleet.ships.size() == Vessel.values().length) {
            phase = Phase.COMBAT;
            playerReport = "ALL VESSELS READY / FIRE AT ENEMY WATERS";
        }
    }

    private void autoPlace(Fleet fleet, Vessel vessel) {
        for (int attempt = 0; attempt < 10000; attempt++) {
            Position pos = new Position(random.nextInt(SIZE), random.nextInt(SIZE));
            Orientation orientation = random.nextBoolean()
                    ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            if (fleet.canPlace(vessel, pos, orientation)) {
                fleet.place(vessel, pos, orientation);
                return;
            }
        }
        throw new IllegalStateException("Could not place fleet");
    }

    public boolean fire(Position target) {
        if (phase != Phase.COMBAT || target == null
                || computerFleet.mark(target) != Mark.UNKNOWN) return false;
        ShotReport playerShot = computerFleet.fire(target);
        playerHistory.add(playerShot);
        playerReport = playerShot.description();
        if (computerFleet.remaining() == 0) {
            result = Result.PLAYER_WINS;
            phase = Phase.FINISHED;
            computerReport = "ALL ENEMY VESSELS SUNK";
            return true;
        }

        NavalAI.Decision decision = NavalAI.choose(
                playerFleet.knowledge(), playerFleet.remainingLengths(), difficulty, random);
        lastComputerHeatmap = decision.heatmap();
        ShotReport computerShot = playerFleet.fire(decision.target());
        computerHistory.add(computerShot);
        computerReport = computerShot.description();
        if (playerFleet.remaining() == 0) {
            result = Result.COMPUTER_WINS;
            phase = Phase.FINISHED;
        }
        rounds++;
        return true;
    }

    public boolean playerShipAt(Position position) { return playerFleet.contains(position); }
    public Mark enemyMark(Position position) { return computerFleet.mark(position); }
    public Mark ownMark(Position position) { return playerFleet.mark(position); }
    public int ownShipsRemaining() { return playerFleet.remaining(); }
    public int enemyShipsRemaining() { return computerFleet.remaining(); }
    public List<ShotReport> playerShots() { return List.copyOf(playerHistory); }
    public List<ShotReport> computerShots() { return List.copyOf(computerHistory); }

    public int[][] computerHeatmap() {
        int[][] copy = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) copy[row] = lastComputerHeatmap[row].clone();
        return copy;
    }

    public Mark[][] ownKnowledge() { return playerFleet.knowledge(); }
    public List<Integer> survivingOwnShipLengths() { return playerFleet.remainingLengths(); }
}
