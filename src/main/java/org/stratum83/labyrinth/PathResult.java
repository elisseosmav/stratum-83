package org.stratum83.labyrinth;

import java.util.List;

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
