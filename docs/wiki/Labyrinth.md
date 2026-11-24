# Labyrinth — Human versus Algorithm

**STRATUM-83 / Educational chapter 02**

Labyrinth is more than a maze: **can your route match the BFS shortest path?** The computer calculates an optimal route but keeps it hidden until you finish.

## How to play

Select **02 LABYRINTH** from the main menu or type `MAZE`.

- Move `P` through the maze using arrow keys or mouse clicks on adjacent open cells.
- Reach `G` while taking as few steps as possible. Walking back through earlier cells counts as additional moves.
- Invalid wall or nonadjacent moves do not count.
- Before completion, there are **no path hints or solver scores**.
- Choose **RETRY SAME MAZE** to try the identical seed again or **NEW MAZE** to advance to the next seed.

## Your score after completion

Once you reach the goal, the result panel compares:

| Metric | Definition |
| --- | --- |
| Your moves | Successful human steps, including backtracking |
| BFS optimal | Minimum possible moves from start to goal |
| Extra moves | Your moves minus BFS optimal |
| Efficiency | `100 × BFS optimal / your moves` |

**Example (illustrative):** Your 38 moves vs BFS's 24 moves yields 14 extra moves and **63.2% efficiency**. A 100% score means a shortest route—not faster thinking than a computer.

After finishing, **SHOW MY VISITED CELLS** highlights your explored cells; **SHOW OPTIMAL PATH** highlights BFS's shortest route; **HIDE PATH** clears the overlay. Revisited cells are shown once on the board but **every revisit counts** toward your move total.

## What is BFS?

Breadth-First Search (BFS) explores a graph layer by layer, using a queue. The maze allows four-direction movement and every legal step costs 1. BFS therefore finds a shortest path in this maze. It does not train or learn.

## Why use a seed?

The maze generator uses a pseudorandom seed. A given seed always produces the same maze for the same Java implementation. `RETRY SAME MAZE` gives you a fair second attempt at exactly the same problem. `NEW MAZE` increases the seed.

## Where do Dijkstra and A* fit?

The separate **03 RESEARCH LAB → 02 PATHFINDING BENCHMARK** uses BFS, Dijkstra and A* over one or many generated mazes and displays average shortest path lengths and explored-cell counts. **Research Lab does not contain an interactive maze.**

## Try it yourself

1. Complete a maze without revealing the solution and record your score.
2. Retry the same seed and try to achieve 100%.
3. Compare your visited cells with the optimal route after completion.
4. Explain why algorithm explored-node counts are not comparable to human moves.

## Implementation references

- [MazeChallenge.java](https://github.com/elisseosmav/stratum-83/blob/feature/labyrinth-human-vs-algorithm/src/main/java/org/stratum83/labyrinth/MazeChallenge.java)
- [MazeSolver.java](https://github.com/elisseosmav/stratum-83/blob/feature/labyrinth-human-vs-algorithm/src/main/java/org/stratum83/labyrinth/MazeSolver.java)
- [MazeChallengeTest.java](https://github.com/elisseosmav/stratum-83/blob/feature/labyrinth-human-vs-algorithm/src/test/java/org/stratum83/labyrinth/MazeChallengeTest.java)
- [Full Labyrinth guide](https://github.com/elisseosmav/stratum-83/blob/feature/labyrinth-human-vs-algorithm/docs/LABYRINTH.md)

*Status: development candidate, not yet Windows acceptance-tested or published as a new release.*
