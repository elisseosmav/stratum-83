package org.stratum83.labyrinth;

import java.util.List;

/** A path includes the start and goal. An empty path means the goal was unreachable. */
public record PathResult(List<Position> path, int exploredNodes) {

    public PathResult {
        path = List.copyOf(path);
    }

    public boolean found() {
        return !path.isEmpty();
    }

    public int steps() {
        return found() ? path.size() - 1 : -1;
    }
}
