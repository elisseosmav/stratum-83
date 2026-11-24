package org.stratum83.labyrinth;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/** Shortest-path algorithms for a four-directional, unweighted grid. */
public class MazeSolver {

    public enum Algorithm {
        BFS, DIJKSTRA, A_STAR
    }

    public PathResult solve(Maze maze, Algorithm algorithm) {
        if (maze == null || algorithm == null) {
            throw new IllegalArgumentException("Maze and algorithm must be provided");
        }
        return switch (algorithm) {
            case BFS -> breadthFirstSearch(maze);
            case DIJKSTRA -> prioritySearch(maze, false);
            case A_STAR -> prioritySearch(maze, true);
        };
    }

    private PathResult breadthFirstSearch(Maze maze) {
        boolean[][] seen = new boolean[maze.rows()][maze.columns()];
        Position[][] previous = new Position[maze.rows()][maze.columns()];
        ArrayDeque<Position> queue = new ArrayDeque<>();
        Position start = maze.start();
        queue.add(start);
        seen[start.row()][start.column()] = true;
        int explored = 0;

        while (!queue.isEmpty()) {
            Position current = queue.remove();
            explored++;
            if (current.equals(maze.goal())) {
                return new PathResult(buildPath(previous, current), explored);
            }
            for (Position next : maze.neighbors(current)) {
                if (!seen[next.row()][next.column()]) {
                    seen[next.row()][next.column()] = true;
                    previous[next.row()][next.column()] = current;
                    queue.add(next);
                }
            }
        }
        return new PathResult(List.of(), explored);
    }

    private PathResult prioritySearch(Maze maze, boolean useHeuristic) {
        int[][] distance = new int[maze.rows()][maze.columns()];
        boolean[][] settled = new boolean[maze.rows()][maze.columns()];
        Position[][] previous = new Position[maze.rows()][maze.columns()];
        for (int[] row : distance) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        PriorityQueue<SearchNode> queue = new PriorityQueue<>(
                Comparator.comparingInt(SearchNode::priority).thenComparingInt(SearchNode::cost));
        Position start = maze.start();
        distance[start.row()][start.column()] = 0;
        queue.add(new SearchNode(start, 0, heuristic(start, maze.goal(), useHeuristic)));
        int explored = 0;

        while (!queue.isEmpty()) {
            SearchNode node = queue.poll();
            Position current = node.position();
            if (settled[current.row()][current.column()]
                    || node.cost() != distance[current.row()][current.column()]) {
                continue;
            }
            settled[current.row()][current.column()] = true;
            explored++;
            if (current.equals(maze.goal())) {
                return new PathResult(buildPath(previous, current), explored);
            }
            for (Position next : maze.neighbors(current)) {
                int newDistance = node.cost() + 1;
                if (newDistance < distance[next.row()][next.column()]) {
                    distance[next.row()][next.column()] = newDistance;
                    previous[next.row()][next.column()] = current;
                    int estimate = heuristic(next, maze.goal(), useHeuristic);
                    queue.add(new SearchNode(next, newDistance, newDistance + estimate));
                }
            }
        }
        return new PathResult(List.of(), explored);
    }

    private int heuristic(Position from, Position goal, boolean enabled) {
        if (!enabled) return 0;
        return Math.abs(from.row() - goal.row()) + Math.abs(from.column() - goal.column());
    }

    private List<Position> buildPath(Position[][] previous, Position goal) {
        List<Position> path = new ArrayList<>();
        Position current = goal;
        while (current != null) {
            path.add(current);
            current = previous[current.row()][current.column()];
        }
        Collections.reverse(path);
        return path;
    }

    private record SearchNode(Position position, int cost, int priority) {
    }
}
