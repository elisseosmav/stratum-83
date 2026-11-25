package org.stratum83.game;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameCatalogTest {

    @Test
    void listsGamesInMenuOrder() {
        List<GameCatalog.Entry> entries = GameCatalog.entries();
        assertEquals(List.of("GRID ZERO", "LABYRINTH", "CHECKERS",
                "BLACKJACK", "CHESS", "CRISIS PROTOCOL"),
                entries.stream().map(GameCatalog.Entry::title).toList());
        for (int i = 0; i < entries.size(); i++) {
            assertEquals(i + 1, entries.get(i).number());
            assertFalse(entries.get(i).algorithm().isBlank());
        }
    }

    @Test
    void onlyFinishedGamesArePlayable() {
        assertTrue(GameCatalog.find(1).orElseThrow().playable());
        assertTrue(GameCatalog.find(2).orElseThrow().playable());
        for (int i = 3; i <= 6; i++) {
            assertFalse(GameCatalog.find(i).orElseThrow().playable());
        }
    }

    @Test
    void rejectsMissingGamesAndMutations() {
        assertTrue(GameCatalog.find(0).isEmpty());
        assertTrue(GameCatalog.find(7).isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> GameCatalog.entries().clear());
    }
}
