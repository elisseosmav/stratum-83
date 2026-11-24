# Labyrinth — Game and Algorithms

**Educational chapter 02 — STRATUM-83**

## How to play

From the main menu select **02 LABYRINTH**, or type `MAZE`. `P` is your current location and `G` is the goal. Move with the arrow keys or by clicking adjacent open cells. Walls cannot be crossed. Select `NEW MAZE` to play a new reproducible layout.

The Play screen is for navigation; it no longer contains solver/comparison buttons.

## Learn the algorithms in Research Lab

Select **03 RESEARCH LAB → 02 PATHFINDING BENCHMARK**. The view contains only parameter inputs and results—**no playable maze**.

| Algorithm | Main idea | Guarantee in these unweighted mazes |
| --- | --- | --- |
| BFS | Search in layers with a queue | Shortest route |
| Dijkstra | Expand smallest cumulative cost | Shortest route with nonnegative costs |
| A* | Expand by distance so far plus Manhattan-distance estimate | Shortest route with an admissible heuristic |

The grid permits only up, down, left and right steps. Each step costs 1.

## What is the seed?

The generator uses randomized depth-first passage carving. Seed `1983` always builds the same 11×15 layout with the same implementation. In Research Lab, sample count `100` generates 100 mazes from 100 consecutive seeds, and runs all three algorithms on each.

## How to read results

`AVG STEPS` is the mean optimal route length in maze moves. `AVG EXPLORED` counts the mean number of cells processed by each solver. **A* can explore fewer cells and still find the same optimal path.** An explored-cell count is not an elapsed-time measurement.

## Exercises

1. Run a single seed twice and check that results are identical.
2. Compare average explored nodes across 100 seeds.
3. Explain why all three return the same optimal distance.
4. Describe the limits of comparing a single family of randomly generated mazes.

## Source and documentation

- [Full technical Labyrinth guide](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/docs/LABYRINTH.md)
- [MazeGenerator.java](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/src/main/java/org/stratum83/labyrinth/MazeGenerator.java)
- [MazeSolver.java](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/src/main/java/org/stratum83/labyrinth/MazeSolver.java)
- [MazeBenchmarkRunner.java](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/src/main/java/org/stratum83/research/MazeBenchmarkRunner.java)

*Research UI candidate requires Windows acceptance testing before a release.*
