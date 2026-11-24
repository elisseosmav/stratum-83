package org.stratum83.labyrinth;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Creates a connected maze by carving passages with randomized depth-first search. */
public class MazeGenerator {

    public Maze generate(int rows, int columns, long seed) {
        if (rows < 5 || columns < 5 || rows % 2 == 0 || columns % 2 == 0) {
            throw new IllegalArgumentException("Use odd dimensions of at least 5 by 5");
        }
        boolean[][] walls = new boolean[rows][columns];
        for (boolean[] row : walls) {
            Arrays.fill(row, true);
        }

        Random random = new Random(seed);
        ArrayDeque<Position> stack = new ArrayDeque<>();
        walls[1][1] = false;
        stack.push(new Position(1, 1));

        int[][] directions = {{-2, 0}, {0, 2}, {2, 0}, {0, -2}};
        while (!stack.isEmpty()) {
            Position current = stack.peek();
            List<Position> candidates = new ArrayList<>();
            for (int[] direction : directions) {
                int row = current.row() + direction[0];
                int col = current.column() + direction[1];
                if (row > 0 && row < rows - 1 && col > 0 && col < columns - 1 && walls[row][col]) {
                    candidates.add(new Position(row, col));
                }
            }
            if (candidates.isEmpty()) {
                stack.pop();
                continue;
            }
            Position next = candidates.get(random.nextInt(candidates.size()));
            walls[(current.row() + next.row()) / 2][(current.column() + next.column()) / 2] = false;
            walls[next.row()][next.column()] = false;
            stack.push(next);
        }
        return new Maze(walls, new Position(1, 1), new Position(rows - 2, columns - 2));
    }
}
