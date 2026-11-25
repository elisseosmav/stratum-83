package org.stratum83.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlgorithmCatalogTest {

    @Test
    void describesImplementedAlgorithmsInShortPlainLanguage() {
        assertEquals(7, AlgorithmCatalog.entries().size());
        assertTrue(AlgorithmCatalog.entries().stream()
                .anyMatch(entry -> entry.name().equals("MINIMAX")));
        assertTrue(AlgorithmCatalog.entries().stream()
                .anyMatch(entry -> entry.name().equals("A* SEARCH")));
        for (AlgorithmCatalog.Entry entry : AlgorithmCatalog.entries()) {
            assertFalse(entry.explanation().isBlank());
            assertFalse(entry.usefulFor().isBlank());
            assertTrue(entry.game().equals("GRID ZERO") || entry.game().equals("LABYRINTH"));
        }
        assertThrows(UnsupportedOperationException.class,
                () -> AlgorithmCatalog.entries().clear());
    }
}
