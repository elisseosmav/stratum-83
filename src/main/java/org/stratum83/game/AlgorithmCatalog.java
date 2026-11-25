package org.stratum83.game;

import java.util.List;

public final class AlgorithmCatalog {
    public record Entry(String name, String game, String explanation, String usefulFor) {
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry("MINIMAX", "GRID ZERO / CHECKERS",
                    "Looks ahead at possible moves and assumes the opponent responds well.",
                    "Choosing actions in small competitive, turn-based problems."),
            new Entry("ALPHA-BETA PRUNING", "GRID ZERO / CHECKERS",
                    "Skips future moves that cannot improve a Minimax decision.",
                    "Reducing search work without changing the chosen move."),
            new Entry("HEURISTIC EVALUATION", "CHECKERS",
                    "Scores pieces, kings, mobility, and board position to compare moves before the game is over.",
                    "Ranking moves in games too complex to search to the end."),
            new Entry("RANDOMIZED DEPTH-FIRST SEARCH", "LABYRINTH",
                    "Carves connected passages by exploring one corridor deeply, then backtracking.",
                    "Generating maze layouts and traversing connected structures."),
            new Entry("A* SEARCH", "LABYRINTH",
                    "Finds the computer's shortest route using distance traveled plus an estimate to the goal.",
                    "Finding routes efficiently when a safe distance estimate is available."),
            new Entry("BREADTH-FIRST SEARCH", "LABYRINTH",
                    "Explores reachable locations in layers; finds the fewest steps when every step costs the same.",
                    "Finding shortest connections in unweighted graphs."),
            new Entry("DIJKSTRA'S ALGORITHM", "LABYRINTH",
                    "Finds the lowest-cost route when connections have nonnegative costs.",
                    "Routing through networks with different connection weights."),
            new Entry("LINE-OF-SIGHT / FOG OF WAR", "LABYRINTH",
                    "Only reveals nearby squares that are not hidden behind a wall. Previously explored areas stay remembered.",
                    "Visibility checks and limited-information game maps.")
    );

    private AlgorithmCatalog() {
    }

    public static List<Entry> entries() {
        return ENTRIES;
    }
}
