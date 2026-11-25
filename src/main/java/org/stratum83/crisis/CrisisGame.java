package org.stratum83.crisis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class CrisisGame {
    public static final int SIZE = 7;
    public static final int MAX_ROUNDS = 18;
    public enum Result { PLAYING, HUMAN_WINS, COMPUTER_WINS }

    public record Position(int row, int col) {
        public Position {
            if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
                throw new IllegalArgumentException("Position outside board");
            }
        }

        public String label() {
            return "" + (char) ('A' + col) + (row + 1);
        }

        public int distanceTo(Position other) {
            return Math.abs(row - other.row) + Math.abs(col - other.col);
        }
    }

    private final Position[] teams;
    private final List<Position> sites;
    private final boolean[][] incidents = new boolean[SIZE][SIZE];
    private int round = 1;
    private int actionsUsed;
    private Result result = Result.PLAYING;
    private String lastHumanAction = "SELECT A RESPONSE TEAM";
    private String lastComputerAction = "THREAT AGENT READY";

    public CrisisGame() {
        this(List.of(new Position(5, 1), new Position(5, 5)),
                List.of(new Position(2, 1), new Position(2, 5), new Position(3, 3)),
                List.of(new Position(0, 1), new Position(0, 5), new Position(1, 3)));
    }

    CrisisGame(List<Position> startTeams, List<Position> initialIncidents, List<Position> facilities) {
        if (startTeams.size() != 2 || facilities.isEmpty() || facilities.size() > 4) {
            throw new IllegalArgumentException("Expected two teams and one to four facilities");
        }
        teams = startTeams.toArray(new Position[0]);
        if (teams[0].equals(teams[1])) throw new IllegalArgumentException("Teams overlap");
        sites = List.copyOf(facilities);
        if (sites.stream().distinct().count() != sites.size()) {
            throw new IllegalArgumentException("Facilities overlap");
        }
        for (Position incident : initialIncidents) {
            if (incident.equals(teams[0]) || incident.equals(teams[1])) {
                throw new IllegalArgumentException("Incident overlaps a response team");
            }
            incidents[incident.row()][incident.col()] = true;
        }
        if (initialIncidents.isEmpty()) {
            result = Result.HUMAN_WINS;
        } else if (lostSites() >= 2) {
            result = Result.COMPUTER_WINS;
        }
    }

    public Result result() { return result; }
    public int round() { return round; }
    public int actionsRemaining() { return 2 - actionsUsed; }
    public String lastHumanAction() { return lastHumanAction; }
    public String lastComputerAction() { return lastComputerAction; }
    public Position team(int number) { return teams[number]; }
    public List<Position> sites() { return sites; }

    public boolean hasIncident(Position position) {
        return incidents[position.row()][position.col()];
    }

    public boolean isSite(Position position) {
        return sites.contains(position);
    }

    public int incidentCount() {
        int count = 0;
        for (int row = 0; row < SIZE; row++) {
            for (boolean value : incidents[row]) if (value) count++;
        }
        return count;
    }

    public int lostSites() {
        int count = 0;
        for (Position site : sites) if (hasIncident(site)) count++;
        return count;
    }

    public List<Position> incidentLocations() {
        List<Position> positions = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (incidents[row][col]) positions.add(new Position(row, col));
            }
        }
        return List.copyOf(positions);
    }

    public List<Position> actionTargets(int teamIndex) {
        if (teamIndex < 0 || teamIndex > 1 || result != Result.PLAYING) return List.of();
        List<Position> targets = new ArrayList<>();
        Position start = teams[teamIndex];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Position candidate = new Position(row, col);
                if (validAction(teamIndex, candidate)) targets.add(candidate);
            }
        }
        return List.copyOf(targets);
    }

    public boolean act(int teamIndex, Position target) {
        if (result != Result.PLAYING || target == null || !validAction(teamIndex, target)) {
            return false;
        }
        Position origin = teams[teamIndex];
        if (hasIncident(target)) {
            incidents[target.row()][target.col()] = false;
            lastHumanAction = "TEAM " + (teamIndex + 1) + " CONTAINED " + target.label();
        } else {
            teams[teamIndex] = target;
            lastHumanAction = "TEAM " + (teamIndex + 1) + " MOVED TO " + target.label();
        }
        actionsUsed++;
        if (incidentCount() == 0) {
            result = Result.HUMAN_WINS;
        } else if (actionsUsed == 2) {
            computerTurn();
        }
        return true;
    }

    public boolean endTurn() {
        if (result != Result.PLAYING || actionsUsed == 0) return false;
        computerTurn();
        return true;
    }

    private boolean validAction(int teamIndex, Position target) {
        if (teamIndex < 0 || teamIndex > 1) return false;
        Position from = teams[teamIndex];
        if (teams[1 - teamIndex].equals(target) || from.equals(target)) return false;
        int distance = from.distanceTo(target);
        if (hasIncident(target)) return distance == 1;
        if (distance == 0 || distance > 2) return false;
        if (from.row() != target.row() && from.col() != target.col()) return false;
        if (distance == 2) {
            Position between = new Position((from.row() + target.row()) / 2,
                    (from.col() + target.col()) / 2);
            if (hasIncident(between) || teams[1 - teamIndex].equals(between)) return false;
        }
        return true;
    }

    private void computerTurn() {
        int attacks = round % 4 == 0 ? 2 : 1;
        StringBuilder report = new StringBuilder();
        for (int i = 0; i < attacks && result == Result.PLAYING; i++) {
            Position next = chooseExpansion();
            if (next == null) {
                report.append("NO VALID SPREAD");
                break;
            }
            incidents[next.row()][next.col()] = true;
            if (!report.isEmpty()) report.append(" / ");
            report.append("SPREAD TO ").append(next.label());
            if (sites.contains(next)) report.append(" (SITE COMPROMISED)");
            if (lostSites() >= 2 || incidentCount() >= 12) {
                result = Result.COMPUTER_WINS;
            }
        }
        lastComputerAction = report.isEmpty() ? "THREAT CONTAINED" : report.toString();
        if (result == Result.PLAYING && round >= MAX_ROUNDS) {
            result = Result.COMPUTER_WINS;
            lastComputerAction += " / RESPONSE WINDOW EXPIRED";
        }
        round++;
        actionsUsed = 0;
    }

    private Position chooseExpansion() {
        List<Position> candidates = new ArrayList<>();
        for (Position incident : incidentLocations()) {
            int[][] offsets = {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};
            for (int[] offset : offsets) {
                int r = incident.row() + offset[0], c = incident.col() + offset[1];
                if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) continue;
                Position target = new Position(r, c);
                if (hasIncident(target) || target.equals(teams[0]) || target.equals(teams[1])
                        || candidates.contains(target)) continue;
                candidates.add(target);
            }
        }
        return candidates.stream().max(Comparator
                .comparingInt(this::threatScore)
                .thenComparingInt(p -> -p.row())
                .thenComparingInt(p -> -p.col())).orElse(null);
    }

    private int threatScore(Position candidate) {
        if (isSite(candidate)) return 1000;
        int nearest = sites.stream().filter(site -> !hasIncident(site))
                .mapToInt(candidate::distanceTo).min().orElse(SIZE * 2);
        return 100 - nearest * 12;
    }
}
