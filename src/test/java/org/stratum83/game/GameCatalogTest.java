package org.stratum83.game;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameCatalogTest {

    @Test
    void listsAllSixGamesInRoadmapOrder() {
        List<GameCatalog.Entry> games = GameCatalog.entries();
        assertEquals(6, games.size());
        assertEquals(List.of("GRID ZERO", "LABYRINTH", "CHECKERS",
                        "BLACKJACK", "CHESS", "CRISIS PROTOCOL"),
                games.stream().map(GameCatalog.Entry::title).toList());
        for (int i = 0; i < games.size(); i++) {
            assertEquals(i + 1, games.get(i).number());
            assertFalse(games.get(i).algorithm().isBlank());
        }
    }

    @Test
    void onlyImplementedGamesAreMarkedPlayable() {
        assertEquals(GameCatalog.Status.PLAYABLE, GameCatalog.find(1).orElseThrow().status());
        assertEquals(GameCatalog.Status.PLAYABLE, GameCatalog.find(2).orElseThrow().status());
        assertEquals(GameCatalog.Status.PROPOSED, GameCatalog.find(3).orElseThrow().status());
        assertEquals(GameCatalog.Status.PROPOSED, GameCatalog.find(4).orElseThrow().status());
        assertEquals(GameCatalog.Status.PROPOSED, GameCatalog.find(5).orElseThrow().status());
        assertEquals(GameCatalog.Status.PLANNED, GameCatalog.find(6).orElseThrow().status());
        assertEquals(2, GameCatalog.entries().stream().filter(GameCatalog.Entry::playable).count());
    }

    @Test
    void unknownGamesAndMutationsAreNotPermitted() {
        assertTrue(GameCatalog.find(0).isEmpty());
        assertTrue(GameCatalog.find(7).isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> GameCatalog.entries().clear());
    }
}
