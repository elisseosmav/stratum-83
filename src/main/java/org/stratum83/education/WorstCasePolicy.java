package org.stratum83.education;

import java.util.List;

/**
 * Toy worst-case policy decision. Costs are invented for teaching, not
 * measurements of a real network. Lower cost is better.
 */
public class WorstCasePolicy {

    public record Policy(String name, int normalCost, int overloadedCost) {
        public Policy {
            if (name == null || name.isBlank() || normalCost < 0 || overloadedCost < 0) {
                throw new IllegalArgumentException("Policy name and nonnegative costs required");
            }
        }

        public int worstCost() {
            return Math.max(normalCost, overloadedCost);
        }
    }

    public Policy choose(List<Policy> policies) {
        if (policies == null || policies.isEmpty()) {
            throw new IllegalArgumentException("At least one policy is required");
        }
        Policy best = null;
        for (Policy policy : policies) {
            if (policy == null) throw new IllegalArgumentException("Null policy");
            if (best == null || policy.worstCost() < best.worstCost()) {
                best = policy;
            }
        }
        return best;
    }

    public static List<Policy> examplePolicies() {
        return List.of(
                new Policy("AGGRESSIVE RETRIES", 1, 9),
                new Policy("BOUNDED RETRIES", 3, 4));
    }
}
