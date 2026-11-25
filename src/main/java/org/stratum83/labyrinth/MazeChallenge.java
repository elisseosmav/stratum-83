package org.stratum83.labyrinth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MazeChallenge {

    private final Maze maze;
    private final PathResult shortestPath;
    private final List<Position> playerRoute = new ArrayList<>();

    public MazeChallenge(Maze maze) {
        this.maze = Objects.requireNonNull(maze, "maze");
        shortestPath = new MazeSolver().solve(maze, MazeSolver.Algorithm.BFS);
        if (!shortestPath.found()) {
            throw new IllegalArgumentException("The maze must contain a route to the goal");
        }
        playerRoute.add(maze.start());
    }

    public Position playerPosition() {
        return playerRoute.get(playerRoute.size() - 1);
    }

    
    public boolean moveTo(Position destination) {
        if (destination == null || finished() || !maze.isOpen(destination)) {
            return false;
        }
        int distance = Math.abs(destination.row() - playerPosition().row())
                + Math.abs(destination.column() - playerPosition().column());
        if (distance != 1) {
            return false;
        }
        playerRoute.add(destination);
        return true;
    }

    public boolean finished() {
        return playerPosition().equals(maze.goal());
    }

    public int moves() {
        return playerRoute.size() - 1;
    }

    public int optimalMoves() {
        return shortestPath.steps();
    }

    public int extraMoves() {
        return moves() - optimalMoves();
    }

    
    public double efficiencyPercent() {
        if (!finished()) {
            return 0.0;
        }
        return moves() == 0 ? 100.0 : 100.0 * optimalMoves() / moves();
    }

    public List<Position> playerRoute() {
        return List.copyOf(playerRoute);
    }

    public List<Position> optimalRoute() {
        return shortestPath.path();
    }
}
