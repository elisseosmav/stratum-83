package org.stratum83.labyrinth;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public final class MazeSolver {

    public enum Algorithm {
        BFS, DIJKSTRA, A_STAR
    }

    public PathResult solve(Maze maze, Algorithm algorithm) {
        if (maze == null || algorithm == null) {
            throw new IllegalArgumentException("Maze and algorithm are required");
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
        seen[start.row()][start.column()] = true;
        queue.addLast(start);
        int explored = 0;

        while (!queue.isEmpty()) {
            Position current = queue.removeFirst();
            explored++;
            if (current.equals(maze.goal())) {
                return new PathResult(buildPath(previous, current), explored);
            }
            for (Position next : maze.neighbors(current)) {
                if (!seen[next.row()][next.column()]) {
                    seen[next.row()][next.column()] = true;
                    previous[next.row()][next.column()] = current;
                    queue.addLast(next);
                }
            }
        }
        return new PathResult(List.of(), explored);
    }

    private PathResult prioritySearch(Maze maze, boolean useHeuristic) {
        int[][] distance = new int[maze.rows()][maze.columns()];
        boolean[][] settled = new boolean[maze.rows()][maze.columns()];
        Position[][] previous = new Position[maze.rows()][maze.columns()];
        for (int[] row : distance) Arrays.fill(row, Integer.MAX_VALUE);
        PriorityQueue<SearchNode> queue = new PriorityQueue<>(
                Comparator.comparingInt(SearchNode::priority).thenComparingInt(SearchNode::cost));
        Position start = maze.start();
        distance[start.row()][start.column()] = 0;
        queue.add(new SearchNode(start, 0, heuristic(start, maze.goal(), useHeuristic)));
        int explored = 0;

        while (!queue.isEmpty()) {
            SearchNode node = queue.remove();
            Position current = node.position();
            if (settled[current.row()][current.column()]
                    || distance[current.row()][current.column()] != node.cost()) {
                continue;
            }
            settled[current.row()][current.column()] = true;
            explored++;
            if (current.equals(maze.goal())) {
                return new PathResult(buildPath(previous, current), explored);
            }
            for (Position next : maze.neighbors(current)) {
                int nextCost = node.cost() + 1;
                if (nextCost < distance[next.row()][next.column()]) {
                    distance[next.row()][next.column()] = nextCost;
                    previous[next.row()][next.column()] = current;
                    queue.add(new SearchNode(next, nextCost,
                            nextCost + heuristic(next, maze.goal(), useHeuristic)));
                }
            }
        }
        return new PathResult(List.of(), explored);
    }

    private int heuristic(Position source, Position goal, boolean enabled) {
        return enabled
                ? Math.abs(source.row() - goal.row()) + Math.abs(source.column() - goal.column())
                : 0;
    }

    private List<Position> buildPath(Position[][] previous, Position goal) {
        List<Position> path = new ArrayList<>();
        for (Position at = goal; at != null; at = previous[at.row()][at.column()]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    private record SearchNode(Position position, int cost, int priority) {
    }
}
