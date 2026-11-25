package org.stratum83.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlgorithmCatalogTest {

    @Test
    void describesImplementedAlgorithmsInShortPlainLanguage() {
        assertEquals(16, AlgorithmCatalog.entries().size());
        assertTrue(AlgorithmCatalog.entries().stream()
                .anyMatch(entry -> entry.name().equals("MINIMAX")));
        assertTrue(AlgorithmCatalog.entries().stream()
                .anyMatch(entry -> entry.name().equals("A* SEARCH")));
        for (AlgorithmCatalog.Entry entry : AlgorithmCatalog.entries()) {
            assertFalse(entry.explanation().isBlank());
            assertFalse(entry.usefulFor().isBlank());
            assertTrue(entry.game().contains("GRID ZERO") || entry.game().contains("LABYRINTH") || entry.game().contains("CHECKERS") || entry.game().contains("BLACKJACK") || entry.game().contains("CHESS") || entry.game().contains("CRISIS PROTOCOL") || entry.game().contains("NAVAL COMMAND"));
        }
        assertThrows(UnsupportedOperationException.class,
                () -> AlgorithmCatalog.entries().clear());
    }
}
