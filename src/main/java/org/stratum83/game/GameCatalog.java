package org.stratum83.game;

import java.util.List;
import java.util.Optional;

public final class GameCatalog {

    public record Entry(int number, String title, String algorithm, boolean playable) {
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry(1, "GRID ZERO", "MINIMAX / ALPHA-BETA", true),
            new Entry(2, "LABYRINTH", "A* / FOG OF WAR", true),
            new Entry(3, "CHECKERS", "MINIMAX / HEURISTICS", true),
            new Entry(4, "BLACKJACK", "PROBABILITY / MONTE CARLO", false),
            new Entry(5, "CHESS", "ALPHA-BETA / SEARCH", false),
            new Entry(6, "CRISIS PROTOCOL", "RULE-BASED AGENTS", false)
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
