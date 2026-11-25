package org.stratum83.game;

import java.util.List;

public final class AlgorithmCatalog {
    public record Entry(String name, String game, String explanation, String usefulFor) {
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry("MINIMAX", "GRID ZERO / CHECKERS / CHESS",
                    "Looks ahead at possible moves and assumes the opponent responds well.",
                    "Choosing actions in small competitive, turn-based problems."),
            new Entry("ALPHA-BETA PRUNING", "GRID ZERO / CHECKERS / CHESS",
                    "Skips future moves that cannot improve a Minimax decision.",
                    "Reducing search work without changing the chosen move."),
            new Entry("HEURISTIC EVALUATION", "CHECKERS / CHESS",
                    "Scores pieces, kings, mobility, and board position to compare moves before the game is over.",
                    "Ranking moves in games too complex to search to the end."),
            new Entry("LEGAL MOVE SEARCH", "CHESS",
                    "Generates possible moves and rejects those leaving the king in check.",
                    "Applying constraints while searching a game tree."),
            new Entry("CARD PROBABILITY", "BLACKJACK",
                    "Counts possible unseen cards after removing cards visible to the player.",
                    "Understanding how available card values affect the next draw."),
            new Entry("MONTE CARLO SIMULATION", "BLACKJACK",
                    "Repeatedly simulates unseen cards and dealer play to estimate risk and possible outcomes.",
                    "Estimating the risk of busting on a hit and the chance of winning when standing."),
            new Entry("RULE-BASED AGENTS", "CRISIS PROTOCOL",
                    "Follows explicit priorities: reach an unprotected critical site, or advance toward the nearest one.",
                    "Making computer decisions explainable and repeatable."),
            new Entry("GREEDY TARGET SELECTION", "CRISIS PROTOCOL",
                    "Scores all adjacent expansion squares and chooses the highest-priority target each round.",
                    "Selecting a locally attractive action without searching an entire game tree."),
            new Entry("RANDOM SEARCH", "NAVAL COMMAND",
                    "Selects an unknown square uniformly without relying on previous hits.",
                    "Providing a baseline computer opponent with no targeting knowledge."),
            new Entry("HUNT AND TARGET", "NAVAL COMMAND",
                    "Searches new squares until a hit is found, then targets adjacent unknown squares.",
                    "Using feedback to focus the next search."),
            new Entry("PROBABILITY DENSITY", "NAVAL COMMAND",
                    "Scores legal placements of unsunk ships compatible with earlier misses and hits.",
                    "Finding promising coordinates without accessing hidden enemy ship positions."),
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
