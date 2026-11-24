# Labyrinth — Game and Algorithm Guide

**STRATUM-83 / Experiment 02 / Proposed release v0.3**

Labyrinth is a standalone offline maze game. Find the goal using the arrow keys, or ask one of three classical search algorithms to solve the maze and show the shortest route.

## How to play

1. Start the application and select **06 LABYRINTH** or type `MAZE`.
2. `P` marks the player; `G` marks the goal; green blocks are walls.
3. Use the arrow keys to move up, right, down or left. Or click any **adjacent open cell**.
4. Reach `G` to complete the maze. A move into a wall or non-adjacent cell does nothing.
5. Select `NEW MAZE` for a different reproducible maze seed. `MAIN MENU` returns to the terminal.

## Algorithm mode

Press **BFS**, **DIJKSTRA** or **A\*** to highlight a path from the *maze start to the goal*. `COMPARE` runs all three algorithms and displays shortest-path steps and explored-cell counts. `CLEAR PATH` removes the overlay. Players can keep exploring after a path is shown.

The three solvers share the same rules: a 4-directional grid, no diagonal movement, each step costs **1**, and no movement through walls.

| Solver | How it works | Expected result here |
| --- | --- | --- |
| **BFS (Breadth-First Search)** | Visits reachable cells layer-by-layer using a queue | Shortest path in an unweighted grid |
| **Dijkstra** | Expands the reachable cell with the lowest known cost so far | Shortest path for nonnegative edge costs |
| **A\*** | Uses Dijkstra-like path costs plus Manhattan-distance guidance | Shortest path here because Manhattan distance never overestimates the remaining cost |

All three must return the **same optimal step count** in the same maze. They might visit different numbers of cells or return different equally short routes when ties exist.

**Explored nodes** means cells removed from the search frontier and actually processed, including the start and goal when reached. This does *not* count all nodes first placed in the queue. It is algorithm work, not the number of player moves.

## Maze generation

`MazeGenerator` uses a randomized depth-first carving procedure over an odd-sized wall grid. Each run starts with the same `seed` and dimensions; the algorithm creates a connected maze with a guaranteed route from start to goal. The display initially uses **11 rows × 15 columns** and seed **1983**.

Every click of `NEW MAZE` increments the seed. The same seed and dimensions produce identical maze walls with the same Java implementation.

## Research exercise

1. Generate several mazes with consecutive seeds.
2. Compare BFS, Dijkstra and A\* for shortest-path length and explored cells.
3. Identify cases where A\* processes fewer cells. Is that always true?
4. Explain why Dijkstra cannot produce a shorter path than BFS here.
5. Discuss why algorithm timing from a single desktop run is **not** a reliable performance benchmark.

## Source code

- [`Maze.java`](../src/main/java/org/stratum83/labyrinth/Maze.java) — grid and movement rules.
- [`MazeGenerator.java`](../src/main/java/org/stratum83/labyrinth/MazeGenerator.java) — seeded generation.
- [`MazeSolver.java`](../src/main/java/org/stratum83/labyrinth/MazeSolver.java) — BFS, Dijkstra and A\*.
- [`StratumApp.java`](../src/main/java/org/stratum83/ui/StratumApp.java) — JavaFX view and keyboard/mouse controls.
- [`MazeSolverTest.java`](../src/test/java/org/stratum83/labyrinth/MazeSolverTest.java) — algorithm correctness cases.

## Limitations

- The displayed route always begins at the **original maze start**, even if the player has already moved.
- No diagonal motion, weighted terrain or animated search replay yet.
- There is no online service, database or AI model. Everything is computed locally with Java.
- The UI requires Windows desktop acceptance testing before a public release.
