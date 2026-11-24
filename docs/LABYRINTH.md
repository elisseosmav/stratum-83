# Labyrinth — Playing and Research

**STRATUM-83 / Educational chapter 02 / v0.3 development candidate**

Labyrinth combines a **playable maze** with a **separate, non-interactive experiment**. Both use the same Java generator and search engine.

## Play mode

Select **02 LABYRINTH** from the main menu, or type `MAZE`.

- `P` is the player and `G` is the goal.
- Use arrow keys, or click an **adjacent open cell**; walls block movement.
- `NEW MAZE` uses the next seed, producing a reproducible 11×15 maze.
- Reach `G` to finish. Player moves are **not** the same metric as solver steps.
- No algorithm result buttons appear in Play mode.

## Research Lab

Select **03 RESEARCH LAB → 02 PATHFINDING** or type `BENCHMARK`. The research screen displays **settings and results only, not the maze**.

Configuration:

- **First maze seed:** a signed 64-bit integer; default `1983`.
- **Maze samples:** 1–100; default `1`.
- The runner generates mazes using `seed`, `seed + 1`, and so forth.
- All three algorithms process the **same generated maze for each seed**.
- The report shows **average shortest-path length** and **average explored cells**.

With one sample, averages equal that single maze's figures. With multiple samples, values are arithmetic means across mazes. Results are reproducible with the same first seed and count.

## Three algorithms

| Algorithm | Method | Guarantee under current rules |
| --- | --- | --- |
| BFS | Queue, exploring cell distance in layers | Shortest path in an unweighted graph |
| Dijkstra | Priority queue ordered by cumulative cost | Shortest path with nonnegative edge weights |
| A* | Cost so far plus Manhattan-distance heuristic | Shortest path because this heuristic does not overestimate |

All moves are four-directional and cost 1. No diagonal moves or weighted terrain. Each result counts **explored cells** when popped and processed from a frontier, including the goal, rather than just discovered cells. The algorithms may explore different numbers of cells but must agree on optimal path length.

## Example experiment

Use seed `1983` and `1` maze sample. Select **RUN BENCHMARK** and compare the rows. Now try `100` samples with the same starting seed: the output reflects averages across 100 different generated mazes.

Try another first seed, then rerun with the original. Why can A* sometimes explore fewer cells than BFS even when both return the same shortest path?

## Source code

- [Maze.java](../src/main/java/org/stratum83/labyrinth/Maze.java): maze state and valid neighbors
- [MazeGenerator.java](../src/main/java/org/stratum83/labyrinth/MazeGenerator.java): seeded randomized depth-first carving
- [MazeSolver.java](../src/main/java/org/stratum83/labyrinth/MazeSolver.java): BFS, Dijkstra and A*
- [MazeBenchmarkRunner.java](../src/main/java/org/stratum83/research/MazeBenchmarkRunner.java): repeated comparisons and input checks
- [MazeBenchmarkRunnerTest.java](../src/test/java/org/stratum83/research/MazeBenchmarkRunnerTest.java): reproducibility and output tests
- [StratumApp.java](../src/main/java/org/stratum83/ui/StratumApp.java): JavaFX screens and user actions

## Scope and limitations

This is an educational simulation, not a runtime benchmark. The displayed statistics are **path lengths and visited-node counts**, not statistically sound timing measurements. Mazes are generated from reproducible pseudorandom seeds, but that does not make the measured averages generalizable to all possible maze structures. There is no cloud processing or LLM involved.

**Status:** development branch implementation; Windows GUI validation is still required before a new release.
