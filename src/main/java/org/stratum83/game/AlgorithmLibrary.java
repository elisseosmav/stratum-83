package org.stratum83.game;

import java.util.List;

/**
 * Short, readable descriptions of algorithms actually available in playable games.
 * Future-game ideas are intentionally excluded until implemented.
 */
public final class AlgorithmLibrary {

    public record Entry(String name, String game, String idea, String example) {
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry("MINIMAX", "GRID ZERO",
                    "Thinks several moves ahead. It assumes the opponent will also play their best move, then chooses the safest result.",
                    "Useful for two-player games where both players have opposing goals."),
            new Entry("ALPHA-BETA PRUNING", "GRID ZERO",
                    "Helps Minimax skip possible moves that cannot change its decision. The computer reaches the same result with less searching.",
                    "Useful when searching many alternative game moves."),
            new Entry("BREADTH-FIRST SEARCH (BFS)", "LABYRINTH",
                    "Checks nearby paths first, then paths farther away. It finds the shortest route when every step costs the same.",
                    "Useful for finding the fewest steps or connections in a network."),
            new Entry("DIJKSTRA", "LABYRINTH",
                    "Chooses the route with the lowest total cost. It also works when some routes cost more than others.",
                    "Useful for finding cheap routes through a weighted graph."),
            new Entry("A* SEARCH", "LABYRINTH",
                    "Uses the cost so far and an estimate of the remaining distance to guide its search toward the goal.",
                    "Useful for navigation and route planning with a suitable distance estimate.")
    );

    private AlgorithmLibrary() {
    }

    public static List<Entry> entries() {
        return ENTRIES;
    }
}
