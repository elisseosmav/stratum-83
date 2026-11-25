package org.stratum83.labyrinth;

import java.util.ArrayDeque;
import java.util.Random;

public final class ChallengeMazeGenerator {
    public static final int ROWS = 23;
    public static final int COLUMNS = 31;
    private static final double EXTRA_PASSAGE_CHANCE = 0.17;

    public Maze generate(long seed) {
        Maze original = new MazeGenerator().generate(ROWS, COLUMNS, seed);
        boolean[][] walls = new boolean[ROWS][COLUMNS];
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                walls[row][column] = !original.isOpen(new Position(row, column));
            }
        }

        Random random = new Random(seed ^ 0x5DEECE66DL);
        for (int row = 1; row < ROWS - 1; row++) {
            for (int column = 1; column < COLUMNS - 1; column++) {
                if (!walls[row][column]) continue;
                boolean separatesVerticalPassages =
                        row % 2 == 0 && column % 2 == 1
                                && !walls[row - 1][column] && !walls[row + 1][column];
                boolean separatesHorizontalPassages =
                        row % 2 == 1 && column % 2 == 0
                                && !walls[row][column - 1] && !walls[row][column + 1];
                if ((separatesVerticalPassages || separatesHorizontalPassages)
                        && random.nextDouble() < EXTRA_PASSAGE_CHANCE) {
                    walls[row][column] = false;
                }
            }
        }

        Position start = new Position(1, 1);
        Maze connected = new Maze(walls, start, original.goal());
        Position exit = farthestCell(connected, start);
        return new Maze(walls, start, exit);
    }

    private Position farthestCell(Maze maze, Position start) {
        int[][] distance = new int[maze.rows()][maze.columns()];
        for (int[] row : distance) java.util.Arrays.fill(row, -1);
        ArrayDeque<Position> queue = new ArrayDeque<>();
        queue.add(start);
        distance[start.row()][start.column()] = 0;
        Position farthest = start;

        while (!queue.isEmpty()) {
            Position current = queue.removeFirst();
            for (Position next : maze.neighbors(current)) {
                if (distance[next.row()][next.column()] >= 0) continue;
                distance[next.row()][next.column()] =
                        distance[current.row()][current.column()] + 1;
                queue.addLast(next);
                if (distance[next.row()][next.column()] >
                        distance[farthest.row()][farthest.column()]) {
                    farthest = next;
                }
            }
        }
        return farthest;
    }
}
