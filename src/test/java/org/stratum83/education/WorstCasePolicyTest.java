package org.stratum83.education;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WorstCasePolicyTest {

    private final WorstCasePolicy chooser = new WorstCasePolicy();

    @Test
    void choosesPolicyWithLowestWorstCaseLoss() {
        var policies = WorstCasePolicy.examplePolicies();
        assertEquals(9, policies.get(0).worstCost());
        assertEquals(4, policies.get(1).worstCost());
        assertEquals("BOUNDED RETRIES", chooser.choose(policies).name());
    }

    @Test
    void tiesPreferFirstListedPolicy() {
        var first = new WorstCasePolicy.Policy("A", 2, 5);
        var second = new WorstCasePolicy.Policy("B", 5, 1);
        assertEquals(first, chooser.choose(List.of(first, second)));
    }

    @Test
    void invalidPoliciesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> chooser.choose(List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new WorstCasePolicy.Policy("", 1, 2));
        assertThrows(IllegalArgumentException.class,
                () -> new WorstCasePolicy.Policy("A", -1, 2));
    }
}
