package org.stratum83.game;

import java.util.List;
import java.util.Optional;

/**
 * The retro terminal's game catalog.
 * Showing a proposed game never implies it is implemented or released.
 */
public final class GameCatalog {

    public enum Status {
        PLAYABLE, PLANNED, PROPOSED
    }

    public record Entry(int number, String title, String algorithm, Status status) {
        public boolean playable() {
            return status == Status.PLAYABLE;
        }
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry(1, "GRID ZERO", "MINIMAX / ALPHA-BETA", Status.PLAYABLE),
            new Entry(2, "LABYRINTH", "BFS / DIJKSTRA / A*", Status.PLAYABLE),
            new Entry(3, "CRISIS PROTOCOL", "RULE-BASED AGENTS", Status.PLANNED),
            new Entry(4, "CHECKERS", "MINIMAX / HEURISTICS", Status.PROPOSED),
            new Entry(5, "BLACKJACK", "PROBABILITY / MONTE CARLO", Status.PROPOSED),
            new Entry(6, "CHESS", "ALPHA-BETA / SEARCH", Status.PROPOSED)
    );

    private GameCatalog() {
    }

    public static List<Entry> entries() {
        return ENTRIES;
    }

    public static Optional<Entry> find(int number) {
        return ENTRIES.stream().filter(entry -> entry.number() == number).findFirst();
    }
}
