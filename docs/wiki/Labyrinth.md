# Labyrinth — Play, Trace, Explain, Apply

**STRATUM-83 — Classic Games. Real Algorithms. Practical Engineering.**

The Labyrinth lesson teaches graph algorithms through a retro maze and a practical software-routing example. The educational goal is **not merely finding the exit**: it is recognizing when BFS, Dijkstra and A* are appropriate.

## 1. PLAY — Navigate a graph

Select **02 LABYRINTH**, or enter `MAZE`. Move from `P` to `G` using arrows or adjacent-cell clicks. Every valid step costs one. After completing a maze you can compare your route with the BFS shortest path, reveal the route, retry the same seed or generate a new maze.

**Limitation:** The generated maze is currently a perfect maze (one simple path between two open cells), with identical costs for every move. It is intentionally a small introduction, not a difficult puzzle.

## 2. TRACE — Watch the real search

Press **TRACE** in Labyrinth. Choose **BFS**, **DIJKSTRA** or **A***. Use **PREVIOUS**, **NEXT**, and **FINAL RESULT** to inspect the solver's **actual recorded expansions**.

| Symbol | Meaning |
| --- | --- |
| `C` | Current cell being expanded |
| `F` | Frontier: discovered but not yet expanded |
| `·` | Explored |
| `*` | Final optimal path |
| `S` / `G` | Start and goal |

The view also explains the search's accumulated cost `g`, estimated cost `h`, priority `f = g+h`, number of explored cells and frontier size. BFS and Dijkstra have `h = 0`; A* uses Manhattan distance on this unit-cost grid.

The **same Java solver** handles gameplay, batch experiments and the educational trace. Switching algorithms does not restart the human game.

## 3. EXPLAIN — Choose the right search

| Algorithm | How it chooses the next node | When to use it |
| --- | --- | --- |
| BFS | First-in-first-out queue, distance layers | Shortest route by hops in an unweighted graph |
| Dijkstra | Lowest accumulated nonnegative edge cost | Least-cost routes in weighted graphs |
| A* | Lowest `g+h` with an admissible heuristic | Optimal goal-directed routing when a good heuristic exists |

All three return the same shortest distance on the present equal-cost maze; the interesting differences are how they search and how many cells they expand. Fewer expansions do not necessarily mean lower runtime. Dijkstra becomes more useful when routes have different, nonnegative costs.

## 4. APPLY — Software-service route selection

A fictional service graph offers two routes to a database:

| Route | Hops | Hypothetical cost |
| --- | ---: | ---: |
| API → DATABASE | 1 | 8 |
| API → GATEWAY → DATABASE | 2 | 4 |

Choose the route with the lower total cost. **BFS** would select one hop when minimizing hops; **Dijkstra** finds two hops when minimizing total cost.

These are **invented educational costs**, not measured network latency. The lesson shows how to model a software problem as a graph and select the appropriate algorithm for the actual objective.

## 5. RESEARCH — Compare experiments

The separate **03 RESEARCH LAB → PATHFINDING BENCHMARK** accepts reproducible seeds, runs BFS/Dijkstra/A* on identical maze inputs, and reports average shortest distance and explored cells. It does not duplicate the playable game.

## Learn more

- [Full Labyrinth learning chapter](https://github.com/elisseosmav/stratum-83/blob/feature/labyrinth-learning-lab/docs/LABYRINTH.md)
- [MazeSolver.java — actual graph search](https://github.com/elisseosmav/stratum-83/blob/feature/labyrinth-learning-lab/src/main/java/org/stratum83/labyrinth/MazeSolver.java)
- [MazeSearchTrace.java — immutable event snapshots](https://github.com/elisseosmav/stratum-83/blob/feature/labyrinth-learning-lab/src/main/java/org/stratum83/labyrinth/MazeSearchTrace.java)
- [MazeLearningScreens.java — step-by-step visualization](https://github.com/elisseosmav/stratum-83/blob/feature/labyrinth-learning-lab/src/main/java/org/stratum83/ui/MazeLearningScreens.java)
- [ServiceRouteExample.java — weighted routing exercise](https://github.com/elisseosmav/stratum-83/blob/feature/labyrinth-learning-lab/src/main/java/org/stratum83/education/ServiceRouteExample.java)
- [Search trace tests](https://github.com/elisseosmav/stratum-83/blob/feature/labyrinth-learning-lab/src/test/java/org/stratum83/labyrinth/MazeSearchTraceTest.java)

**Status:** Development candidate. Java 21 Maven/JUnit CI passed on Windows and Linux; the graphical interface needs a Windows acceptance test. These are Wiki-ready source files, **not yet published into the separate live GitHub Wiki**.
