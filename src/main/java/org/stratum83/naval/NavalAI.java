package org.stratum83.naval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class NavalAI {
    public record Decision(NavalGame.Position target, int[][] heatmap) {
        public Decision {
            Objects.requireNonNull(target);
            heatmap = deepCopy(heatmap);
        }

        @Override
        public int[][] heatmap() { return deepCopy(heatmap); }
    }

    private NavalAI() { }

    public static Decision choose(NavalGame.Mark[][] knowledge,
                                  List<Integer> remainingLengths,
                                  NavalGame.Difficulty difficulty,
                                  Random random) {
        validate(knowledge);
        Objects.requireNonNull(remainingLengths);
        Objects.requireNonNull(difficulty);
        Objects.requireNonNull(random);
        int[][] probability = probabilityMap(knowledge, remainingLengths);
        List<NavalGame.Position> unknown = unknownTiles(knowledge);
        if (unknown.isEmpty()) throw new IllegalArgumentException("No targets left");
        NavalGame.Position choice;
        switch (difficulty) {
            case EASY -> choice = unknown.get(random.nextInt(unknown.size()));
            case NORMAL -> choice = huntTarget(knowledge, unknown, random);
            case HARD -> choice = chooseByHeat(probability, unknown, random);
            default -> throw new IllegalStateException("Unexpected difficulty");
        }
        return new Decision(choice, probability);
    }

    public static int[][] probabilityMap(NavalGame.Mark[][] known,
                                         List<Integer> remainingLengths) {
        validate(known);
        Objects.requireNonNull(remainingLengths);
        int[][] scores = new int[NavalGame.SIZE][NavalGame.SIZE];
        boolean hasUnresolvedHits = false;
        for (int row = 0; row < NavalGame.SIZE; row++) {
            for (int col = 0; col < NavalGame.SIZE; col++) {
                hasUnresolvedHits |= known[row][col] == NavalGame.Mark.HIT;
            }
        }
        for (int length : remainingLengths) {
            if (length < 1 || length > NavalGame.SIZE) {
                throw new IllegalArgumentException("Invalid vessel length");
            }
            for (NavalGame.Orientation direction : NavalGame.Orientation.values()) {
                for (int row = 0; row < NavalGame.SIZE; row++) {
                    for (int col = 0; col < NavalGame.SIZE; col++) {
                        int dr = direction == NavalGame.Orientation.VERTICAL ? 1 : 0;
                        int dc = direction == NavalGame.Orientation.HORIZONTAL ? 1 : 0;
                        if (row + (length - 1) * dr >= NavalGame.SIZE
                                || col + (length - 1) * dc >= NavalGame.SIZE) continue;
                        boolean possible = true;
                        boolean includesHit = false;
                        for (int n = 0; n < length; n++) {
                            NavalGame.Mark mark = known[row + n * dr][col + n * dc];
                            if (mark == NavalGame.Mark.MISS || mark == NavalGame.Mark.SUNK) {
                                possible = false;
                                break;
                            }
                            if (mark == NavalGame.Mark.HIT) includesHit = true;
                        }
                        if (!possible) continue;
                        int increment = includesHit ? 18 : (hasUnresolvedHits ? 1 : 2);
                        for (int n = 0; n < length; n++) {
                            int r = row + n * dr;
                            int c = col + n * dc;
                            if (known[r][c] == NavalGame.Mark.UNKNOWN) scores[r][c] += increment;
                        }
                    }
                }
            }
        }
        return scores;
    }

    private static NavalGame.Position huntTarget(NavalGame.Mark[][] knowledge,
                                                 List<NavalGame.Position> unknown, Random random) {
        List<NavalGame.Position> targets = new ArrayList<>();
        for (int row = 0; row < NavalGame.SIZE; row++) {
            for (int col = 0; col < NavalGame.SIZE; col++) {
                if (knowledge[row][col] != NavalGame.Mark.HIT) continue;
                for (int[] step : new int[][]{{-1, 0}, {0, -1}, {0, 1}, {1, 0}}) {
                    int r = row + step[0], c = col + step[1];
                    if (inBounds(r, c) && knowledge[r][c] == NavalGame.Mark.UNKNOWN) {
                        NavalGame.Position target = new NavalGame.Position(r, c);
                        if (!targets.contains(target)) targets.add(target);
                    }
                }
            }
        }
        if (!targets.isEmpty()) return targets.get(random.nextInt(targets.size()));
        List<NavalGame.Position> parity = unknown.stream()
                .filter(p -> (p.row() + p.col()) % 2 == 0).toList();
        List<NavalGame.Position> pool = parity.isEmpty() ? unknown : parity;
        return pool.get(random.nextInt(pool.size()));
    }

    private static NavalGame.Position chooseByHeat(int[][] scores,
                                                   List<NavalGame.Position> unknown, Random random) {
        int top = unknown.stream().mapToInt(p -> scores[p.row()][p.col()]).max().orElse(0);
        List<NavalGame.Position> best = unknown.stream()
                .filter(p -> scores[p.row()][p.col()] == top).toList();
        return best.get(random.nextInt(best.size()));
    }

    private static List<NavalGame.Position> unknownTiles(NavalGame.Mark[][] known) {
        List<NavalGame.Position> list = new ArrayList<>();
        for (int row = 0; row < NavalGame.SIZE; row++) {
            for (int col = 0; col < NavalGame.SIZE; col++) {
                if (known[row][col] == NavalGame.Mark.UNKNOWN) {
                    list.add(new NavalGame.Position(row, col));
                }
            }
        }
        return list;
    }

    private static void validate(NavalGame.Mark[][] knowledge) {
        if (knowledge == null || knowledge.length != NavalGame.SIZE) {
            throw new IllegalArgumentException("Expected a 10x10 grid");
        }
        for (NavalGame.Mark[] row : knowledge) {
            if (row == null || row.length != NavalGame.SIZE
                    || Arrays.asList(row).contains(null)) {
                throw new IllegalArgumentException("Invalid 10x10 grid");
            }
        }
    }

    private static int[][] deepCopy(int[][] input) {
        int[][] copy = new int[input.length][];
        for (int r = 0; r < input.length; r++) copy[r] = input[r].clone();
        return copy;
    }

    private static boolean inBounds(int r, int c) {
        return r >= 0 && r < NavalGame.SIZE && c >= 0 && c < NavalGame.SIZE;
    }
}
