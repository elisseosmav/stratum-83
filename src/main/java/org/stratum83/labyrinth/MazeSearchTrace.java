package org.stratum83.labyrinth;

import java.util.List;

/**
 * A real, reproducible sequence of graph-search expansions.
 * Each step contains a snapshot taken just after a node is expanded.
 * Costs are counted in maze moves; all current maze edges cost one.
 */
public record MazeSearchTrace(MazeSolver.Algorithm algorithm, PathResult result,
                              List<Step> steps) {

    public MazeSearchTrace {
        steps = List.copyOf(steps);
    }

    public record Step(Position current, List<Position> frontier,
                       List<Position> explored, int costSoFar,
                       int heuristic, String explanation) {
        public Step {
            frontier = List.copyOf(frontier);
            explored = List.copyOf(explored);
        }

        public int priority() {
            return costSoFar + heuristic;
        }
    }
}
