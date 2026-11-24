# Labyrinth — Play, Trace, Explain, Apply

**STRATUM-83 — Classic Games. Real Algorithms. Practical Engineering.**

Labyrinth introduces **graph search** through a small retro maze. Playing the maze is the invitation, not the entire lesson. The goal is to understand how BFS, Dijkstra and A* work, then recognize when these methods help solve real software-development problems.

## 1. PLAY — Explore a graph

Choose **02 LABYRINTH** or type `MAZE`.

- `P` marks the player and `G` the destination.
- Move using arrow keys or click adjacent open squares; every valid move costs one step.
- Walls and non-adjacent clicks are invalid and do not increase the move count.
- At the destination, compare your moves with the BFS optimum, then reveal your visited cells or the best route.
- `RETRY SAME MAZE` lets you repeat the same seed; `NEW MAZE` increments the seed.

The generator currently carves an **11×15 perfect maze**, meaning there is exactly one simple route between any two open cells. That makes it a simple learning example, **not** a sophisticated human navigation challenge.

## 2. TRACE — Watch the actual algorithm

Select **TRACE** inside Labyrinth, or type `TRACE` while the maze is active. The program runs the **same MazeSolver code** used by the game and by Research Lab.

Choose BFS, Dijkstra or A* and navigate the real recorded search with **PREVIOUS**, **NEXT**, or **FINAL RESULT**.

The display visualizes:

| Mark / color | Meaning |
| --- | --- |
| `C` | Cell currently being expanded |
| `F` | Frontier: discovered, not yet expanded |
| `·` | Previously explored cell |
| `*` | Final optimal path, visible at the last step |
| `S` / `G` | Start and goal |
| Solid wall | Not traversable |

For each step, inspect the frontier and explored-node counts as well as:

- `g`: cost accumulated to the current cell
- `h`: heuristic estimate of distance to the goal (0 for BFS/Dijkstra)
- `f = g + h`: priority score (especially relevant to A*)

**These are real node-expansion snapshots, not an animation invented separately from the pathfinding algorithm.** For priority-queue algorithms, frontier cells are displayed as a set rather than implying an exact queue order. Showing a trace does **not** move the human player or reset their maze.

## 3. EXPLAIN — Why three algorithms?

| Algorithm | Data structure / rule | What problem it solves |
| --- | --- | --- |
| BFS | FIFO queue; search by distance layers | Fewest edges/hops in an unweighted graph |
| Dijkstra | Priority queue; lowest known accumulated nonnegative cost | Least-cost routes with nonnegative edge weights |
| A* | Priority `g + h`; goal-directed search with an admissible heuristic | Optimal search with potentially less exploration when useful guidance exists |

All current maze edges cost 1 and Manhattan distance is an admissible A* heuristic. Consequently, BFS, Dijkstra and A* agree on the optimal distance. They may explore different numbers of nodes, but **fewer expanded nodes do not automatically mean shorter elapsed runtime**. The perfect-maze topology also limits the variety of paths available.

The program is entirely offline, with no cloud AI or machine learning.

## 4. APPLY — Real software engineering transfer

The **APPLY** view presents a fictional software-service routing problem:

| Available route | Hops | Total hypothetical cost |
| --- | ---: | ---: |
| API → DATABASE | 1 | 8 |
| API → GATEWAY → DATABASE | 2 | 2 + 2 = 4 |

The player predicts which path minimizes total cost.

- **BFS** correctly selects the one-hop route if the objective is *minimum hops*.
- **Dijkstra** correctly selects the two-hop route if the objective is *minimum total nonnegative cost*.
- **A*** could be applied if we also had a suitable admissible estimate of the remaining cost.

The numbers are **invented teaching units, not real latency measurements**. The example demonstrates why choosing the appropriate algorithm depends on how the problem is modeled.

Possible applications include dependency graph traversal, routing across weighted service or network links and planning systems with heuristics. Do not use BFS for weighted minimum-cost routing merely because it uses fewer edges.

## 5. RESEARCH — Compare repeatable results

Select **03 RESEARCH LAB → PATHFINDING BENCHMARK** or type `BENCHMARK`.

Choose a first seed and a sample count (1–100). The program runs BFS, Dijkstra and A* on the *same generated mazes*, reporting average optimal path length and expanded nodes. A fixed seed permits reproducible comparisons. Research Lab does not duplicate the interactive game.

## Exercises

1. In TRACE, compare BFS and A* on the same seed. When do their frontiers differ?
2. Why is `h = 0` for Dijkstra, but Manhattan distance is meaningful for A*?
3. In APPLY, why does the two-hop route cost less than the direct route?
4. In a dependency graph where every edge represents one dependency, which algorithm would you use to find a minimum-hop connection?
5. What additional model information would you need to make A* useful for service routing?
6. What changes would be necessary to handle negative edge weights?

## Explore the Java implementation

- [MazeSolver.java](../src/main/java/org/stratum83/labyrinth/MazeSolver.java) — actual BFS, Dijkstra and A* searches; trace shares their logic
- [MazeSearchTrace.java](../src/main/java/org/stratum83/labyrinth/MazeSearchTrace.java) — immutable real expansion snapshots
- [MazeLearningScreens.java](../src/main/java/org/stratum83/ui/MazeLearningScreens.java) — JavaFX trace/explain/apply views
- [ServiceRouteExample.java](../src/main/java/org/stratum83/education/ServiceRouteExample.java) — hypothetical weighted service graph
- [MazeSearchTraceTest.java](../src/test/java/org/stratum83/labyrinth/MazeSearchTraceTest.java) — real trace versus solver consistency
- [ServiceRouteExampleTest.java](../src/test/java/org/stratum83/education/ServiceRouteExampleTest.java) — fewest hops versus cheapest route
- [MazeChallenge.java](../src/main/java/org/stratum83/labyrinth/MazeChallenge.java) — human movement and scoring
- [MazeBenchmarkRunner.java](../src/main/java/org/stratum83/research/MazeBenchmarkRunner.java) — reproducible multi-maze experiments

**Release status:** This is a development candidate on `feature/labyrinth-learning-lab`. The live Wiki and published beta are unchanged until the maintainer reviews and publishes them. A Windows desktop visual test is still needed.
