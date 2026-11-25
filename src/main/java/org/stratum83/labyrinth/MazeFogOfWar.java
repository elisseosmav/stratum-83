package org.stratum83.labyrinth;

import java.util.Objects;

public final class MazeFogOfWar {
    private final Maze maze;
    private final int radius;
    private final boolean[][] discovered;
    private final boolean[][] visible;
    private int discoveredCells;

    public MazeFogOfWar(Maze maze, int radius) {
        this.maze = Objects.requireNonNull(maze, "maze");
        if (radius < 1) throw new IllegalArgumentException("Vision radius must be positive");
        this.radius = radius;
        this.discovered = new boolean[maze.rows()][maze.columns()];
        this.visible = new boolean[maze.rows()][maze.columns()];
        revealFrom(maze.start());
    }

    public void revealFrom(Position player) {
        if (!maze.isOpen(player)) {
            throw new IllegalArgumentException("Player must be on an open maze cell");
        }
        for (boolean[] row : visible) java.util.Arrays.fill(row, false);
        for (int row = Math.max(0, player.row() - radius);
             row <= Math.min(maze.rows() - 1, player.row() + radius); row++) {
            for (int column = Math.max(0, player.column() - radius);
                 column <= Math.min(maze.columns() - 1, player.column() + radius); column++) {
                int dr = row - player.row();
                int dc = column - player.column();
                if (dr * dr + dc * dc > radius * radius) continue;
                Position target = new Position(row, column);
                if (!hasLineOfSight(player, target)) continue;
                visible[row][column] = true;
                if (!discovered[row][column]) {
                    discovered[row][column] = true;
                    discoveredCells++;
                }
            }
        }
    }

    
    private boolean hasLineOfSight(Position source, Position target) {
        int x0 = source.column();
        int y0 = source.row();
        int x1 = target.column();
        int y1 = target.row();
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int error = dx - dy;

        while (x0 != x1 || y0 != y1) {
            int twiceError = 2 * error;
            if (twiceError > -dy) {
                error -= dy;
                x0 += sx;
            }
            if (twiceError < dx) {
                error += dx;
                y0 += sy;
            }
            if ((x0 != x1 || y0 != y1)
                    && !maze.isOpen(new Position(y0, x0))) {
                return false;
            }
        }
        return true;
    }

    public boolean isVisible(Position cell) {
        return inBounds(cell) && visible[cell.row()][cell.column()];
    }

    public boolean wasDiscovered(Position cell) {
        return inBounds(cell) && discovered[cell.row()][cell.column()];
    }

    public int discoveredCells() {
        return discoveredCells;
    }

    public int totalCells() {
        return maze.rows() * maze.columns();
    }

    private boolean inBounds(Position cell) {
        return cell != null && cell.row() >= 0 && cell.row() < maze.rows()
                && cell.column() >= 0 && cell.column() < maze.columns();
    }
}
