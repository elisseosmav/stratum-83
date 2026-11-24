package org.stratum83.labyrinth;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * BFS, Dijkstra and A* on a four-directional, unit-cost grid.
 * Gameplay, benchmarks and the teaching trace use the SAME search routines.
 */
public class MazeSolver {

    public enum Algorithm {
        BFS, DIJKSTRA, A_STAR
    }

    public PathResult solve(Maze maze, Algorithm algorithm) {
        return search(maze, algorithm, false).result();
    }

    /** Records every actual expanded node for an educational step-by-step replay. */
    public MazeSearchTrace trace(Maze maze, Algorithm algorithm) {
        return search(maze, algorithm, true);
    }

    private MazeSearchTrace search(Maze maze, Algorithm algorithm, boolean record) {
        if (maze == null || algorithm == null) {
            throw new IllegalArgumentException("Maze and algorithm must be provided");
        }

        Recorder recorder = new Recorder(algorithm, record);
        PathResult path = switch (algorithm) {
            case BFS -> breadthFirstSearch(maze, recorder);
            case DIJKSTRA -> prioritySearch(maze, false, recorder);
            case A_STAR -> prioritySearch(maze, true, recorder);
        };
        return new MazeSearchTrace(algorithm, path, recorder.steps);
    }

    private PathResult breadthFirstSearch(Maze maze, Recorder recorder) {
        boolean[][] seen = new boolean[maze.rows()][maze.columns()];
        int[][] distance = new int[maze.rows()][maze.columns()];
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
                recorder.record(current, List.copyOf(queue), distance[current.row()][current.column()],
                        0, true);
                return new PathResult(buildPath(previous, current), explored);
            }
            for (Position next : maze.neighbors(current)) {
                if (!seen[next.row()][next.column()]) {
                    seen[next.row()][next.column()] = true;
                    previous[next.row()][next.column()] = current;
                    distance[next.row()][next.column()] = distance[current.row()][current.column()] + 1;
                    queue.add(next);
                }
            }
            recorder.record(current, List.copyOf(queue), distance[current.row()][current.column()],
                    0, false);
        }
        return new PathResult(List.of(), explored);
    }

    private PathResult prioritySearch(Maze maze, boolean useHeuristic, Recorder recorder) {
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
            int estimate = heuristic(current, maze.goal(), useHeuristic);
            if (current.equals(maze.goal())) {
                recorder.record(current, activeFrontier(queue, settled, distance),
                        node.cost(), estimate, true);
                return new PathResult(buildPath(previous, current), explored);
            }

            for (Position next : maze.neighbors(current)) {
                int newDistance = node.cost() + 1;
                if (newDistance < distance[next.row()][next.column()]) {
                    distance[next.row()][next.column()] = newDistance;
                    previous[next.row()][next.column()] = current;
                    int newEstimate = heuristic(next, maze.goal(), useHeuristic);
                    queue.add(new SearchNode(next, newDistance, newDistance + newEstimate));
                }
            }
            recorder.record(current, activeFrontier(queue, settled, distance),
                    node.cost(), estimate, false);
        }
        return new PathResult(List.of(), explored);
    }

    /**
     * A priority queue can contain outdated entries; only live, unsettled
     * candidates belong to the frontier. Sort by location to keep the display
     * deterministic; this list is NOT a promise of priority-queue pop order.
     */
    private List<Position> activeFrontier(PriorityQueue<SearchNode> queue,
                                          boolean[][] settled, int[][] distance) {
        List<Position> cells = new ArrayList<>();
        for (SearchNode node : queue) {
            Position position = node.position();
            if (!settled[position.row()][position.column()]
                    && node.cost() == distance[position.row()][position.column()]
                    && !cells.contains(position)) {
                cells.add(position);
            }
        }
        cells.sort(Comparator.comparingInt(Position::row).thenComparingInt(Position::column));
        return cells;
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

    private static class Recorder {
        private final Algorithm algorithm;
        private final boolean enabled;
        private final List<MazeSearchTrace.Step> steps = new ArrayList<>();
        private final List<Position> explored = new ArrayList<>();

        private Recorder(Algorithm algorithm, boolean enabled) {
            this.algorithm = algorithm;
            this.enabled = enabled;
        }

        private void record(Position current, List<Position> frontier,
                            int cost, int estimate, boolean goalFound) {
            if (!enabled) return;
            explored.add(current);
            String reason = switch (algorithm) {
                case BFS -> "BFS removes the next cell from its FIFO queue. "
                        + "All edges cost 1, so cells are expanded in distance layers.";
                case DIJKSTRA -> "Dijkstra removes a cell with the lowest accumulated "
                        + "distance g from its priority queue.";
                case A_STAR -> "A* uses priority f = g + h, where h is Manhattan "
                        + "distance to the goal (admissible in this unit-cost grid).";
            };
            String explanation = "Expanded row " + (current.row() + 1)
                    + ", column " + (current.column() + 1) + ". "
                    + "g=" + cost + ", h=" + estimate + ", f=" + (cost + estimate)
                    + ". " + reason + (goalFound ? " GOAL REACHED." : "");
            steps.add(new MazeSearchTrace.Step(current, frontier,
                    explored, cost, estimate, explanation));
        }
    }
}
