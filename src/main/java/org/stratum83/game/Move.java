package org.stratum83.game;

public record Move(int row, int column) {

    public Move {
        if (row < 0 || row > 2 || column < 0 || column > 2) {
            throw new IllegalArgumentException("Row and column must be between 0 and 2");
        }
    }
}
