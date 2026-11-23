package org.stratum83.research;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExperimentRunnerTest {

    @Test
    void resultsAreReproducibleAndAccountForEveryGame() {
        ExperimentRunner runner = new ExperimentRunner();
        ExperimentResult first = runner.run(40, 1983L);
        ExperimentResult second = runner.run(40, 1983L);

        assertEquals(first, second);
        assertEquals(40, first.computerWins() + first.draws() + first.computerLosses());
        assertEquals(0, first.computerLosses());
    }

    @Test
    void requiresAtLeastOneGame() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExperimentRunner().run(0, 1L));
    }
}
