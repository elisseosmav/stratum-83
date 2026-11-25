package org.stratum83.labyrinth;

import java.util.ArrayList;
import java.util.List;

public class Maze {

    private final boolean[][] walls;
    private final Position start;
    private final Position goal;

    public Maze(boolean[][] walls, Position start, Position goal) {
        if (walls == null || walls.length == 0 || walls[0] == null || walls[0].length == 0) {
            throw new IllegalArgumentException("The maze must not be empty");
        }
        int width = walls[0].length;
        this.walls = new boolean[walls.length][width];
        for (int row = 0; row < walls.length; row++) {
            if (walls[row] == null || walls[row].length != width) {
                throw new IllegalArgumentException("Maze rows must have the same width");
            }
            System.arraycopy(walls[row], 0, this.walls[row], 0, width);
        }
        if (!isOpen(start) || !isOpen(goal)) {
            throw new IllegalArgumentException("Start and goal must be open cells inside the maze");
        }
        this.start = start;
        this.goal = goal;
    }

    public int rows() {
        return walls.length;
    }

    public int columns() {
        return walls[0].length;
    }

    public Position start() {
        return start;
    }

    public Position goal() {
        return goal;
    }

    public boolean isOpen(Position position) {
        return position != null
                && position.row() >= 0 && position.row() < rows()
                && position.column() >= 0 && position.column() < columns()
                && !walls[position.row()][position.column()];
    }

    public List<Position> neighbors(Position position) {
        List<Position> result = new ArrayList<>(4);
        int row = position.row();
        int column = position.column();
        Position[] candidates = {
                new Position(row - 1, column),
                new Position(row, column + 1),
                new Position(row + 1, column),
                new Position(row, column - 1)
        };
        for (Position candidate : candidates) {
            if (isOpen(candidate)) {
                result.add(candidate);
            }
        }
        return result;
    }

    
    public static Maze fromAscii(String... lines) {
        if (lines == null || lines.length == 0 || lines[0] == null || lines[0].isEmpty()) {
            throw new IllegalArgumentException("Provide at least one nonempty maze row");
        }
        boolean[][] walls = new boolean[lines.length][lines[0].length()];
        Position start = null;
        Position goal = null;
        for (int row = 0; row < lines.length; row++) {
            if (lines[row] == null || lines[row].length() != walls[0].length) {
                throw new IllegalArgumentException("Maze rows must have equal width");
            }
            for (int col = 0; col < lines[row].length(); col++) {
                char cell = lines[row].charAt(col);
                walls[row][col] = cell == '#';
                if (cell == 'S' || cell == 'B') {
                    if (start != null) throw new IllegalArgumentException("Only one start is allowed");
                    start = new Position(row, col);
                }
                if (cell == 'G' || cell == 'B') {
                    if (goal != null) throw new IllegalArgumentException("Only one goal is allowed");
                    goal = new Position(row, col);
                }
                if (cell != '#' && cell != '.' && cell != 'S' && cell != 'G' && cell != 'B') {
                    throw new IllegalArgumentException("Unknown maze character: " + cell);
                }
            }
        }
        if (start == null || goal == null) {
            throw new IllegalArgumentException("A maze needs S and G, or a single B");
        }
        return new Maze(walls, start, goal);
    }
}
