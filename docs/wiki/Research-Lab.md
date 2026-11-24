# Research Lab — How to Run and Understand Experiments

**STRATUM-83 / Research Lab / Java 21**  
**Status: v0.3.0-beta.2 development candidate, not yet published**

## What is different from Play?

**Play mode** lets you challenge Minimax or navigate a maze, then compare your human route against the BFS shortest path **after finishing**. **Research mode** runs algorithms with specified inputs and reports reproducible results. Research Lab does **not** contain an interactive maze.

Choose **03 RESEARCH LAB** in the main menu or type `RESEARCH`. Then choose:

1. **MINIMAX VS RANDOM**
2. **PATHFINDING BENCHMARK**

`BACK` or Escape returns to the main menu. To begin a game instead, type `PLAY` or `MAZE`.

## Why use random seeds?

A seed initializes a pseudorandom number generator. The same seed, configuration and Java code generate the same sequence. Seeds therefore make comparisons repeatable; they do **not** make Minimax or BFS more intelligent.

## Experiment 01 — Minimax vs Random

**Inputs:** seed (64-bit integer, default `1983`); number of games (1–2000, default `200`).

- Minimax searches possible Tic-Tac-Toe outcomes with alpha-beta pruning and plays optimally.
- The random opponent picks uniformly from available legal squares.
- Minimax alternates between first player X and second player O.
- The report counts Minimax **wins, draws and losses**. These values sum to the number of games.

**Interpretation:** Perfect Minimax should never lose in Tic-Tac-Toe, but random wins and draws vary with the seed. There is no machine learning or model training.

[Read the full Grid Zero chapter](Grid-Zero-AI-Experiment).

## Experiment 02 — Pathfinding Benchmark

**Inputs:** first maze seed (64-bit integer, default `1983`); sample count (1–100, default `1`).

For sample count N, STRATUM-83 generates 11×15 mazes using the first seed, then first seed + 1, ... through N seeds. Each maze is searched by **BFS, Dijkstra and A\***. All have the same start, goal, walls and movement rules (four directions, unit edge cost).

The report shows two numbers per algorithm:

- **Average steps:** arithmetic mean of shortest path lengths across all generated mazes.
- **Average explored cells:** arithmetic mean of cells removed from the search frontier and actually processed.

All three algorithms must return **equal shortest-path lengths** on each maze. They can explore different numbers of cells. There is no execution-time benchmark in this version.

**Try this:** First seed `1983`, samples `1`. Run twice and compare. Next choose samples `100`. Interpret why average explored cells differ.

[Read the Labyrinth chapter](Labyrinth).

## What results do not prove

- Minimax does not learn and this is not an LLM comparison.
- A smaller node count does not necessarily mean universally faster execution.
- The maze generator produces a particular family of solvable mazes, not every imaginable real-world navigation problem.
- Results from one seed are not statistical evidence about an entire problem class.

## Source code

- [ExperimentRunner.java](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/src/main/java/org/stratum83/research/ExperimentRunner.java)
- [MazeBenchmarkRunner.java](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/src/main/java/org/stratum83/research/MazeBenchmarkRunner.java)
- [MazeSolver.java](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/src/main/java/org/stratum83/labyrinth/MazeSolver.java)
- [Acceptance checklist](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/docs/TESTING.md)
