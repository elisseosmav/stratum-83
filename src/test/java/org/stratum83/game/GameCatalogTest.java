package org.stratum83.game;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameCatalogTest {

    @Test
    void listsGamesInMenuOrder() {
        List<GameCatalog.Entry> entries = GameCatalog.entries();
        assertEquals(List.of("GRID ZERO", "LABYRINTH", "CHECKERS",
                "BLACKJACK", "CHESS", "CRISIS PROTOCOL", "NAVAL COMMAND"),
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
        assertTrue(GameCatalog.find(3).orElseThrow().playable());
        assertTrue(GameCatalog.find(4).orElseThrow().playable());
        assertTrue(GameCatalog.find(5).orElseThrow().playable());
        assertTrue(GameCatalog.find(6).orElseThrow().playable());
        assertTrue(GameCatalog.find(7).orElseThrow().playable());
    }

    @Test
    void rejectsMissingGamesAndMutations() {
        assertTrue(GameCatalog.find(0).isEmpty());
        assertTrue(GameCatalog.find(8).isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> GameCatalog.entries().clear());
    }
}
