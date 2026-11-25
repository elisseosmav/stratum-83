# Labyrinth — The Fog-of-War Challenge

**STRATUM-83 — Classic Games. Real Algorithms. Practical Engineering.**

A human explores an unknown map, using their own vision and memory. The computer knows the complete maze and calculates a shortest route using A*.

## How to play

1. Choose **LABYRINTH** in Games.
2. Move `P` with your keyboard **arrow keys**. The mouse does not move you.
3. The **23×31** maze is largely dark. Nearby cells in line of sight are visible; walls hide corridors behind them. Explored areas remain dim rather than disappearing.
4. Find the exit `G` by exploring. It is not revealed in advance.
5. At the exit, compare your number of moves against the **computer's A* shortest route**. Only then can you display the computer path.

**RETRY** uses the same seed, resetting fog and movement. **NEW MAZE** generates another reproducible maze.

## What algorithms are used?

- **Randomized DFS** carves the initial connected maze. Additional passages make loops, creating alternative routes.
- **A*** computes a shortest route by considering distance already traveled and a Manhattan-distance estimate of the remaining distance.
- **BFS and Dijkstra** are implemented graph-search methods retained in the codebase. The playable computer baseline uses A*, not a trained AI model.
- **Line-of-sight fog** reveals nearby places unless a wall blocks vision. This is a visibility algorithm, not learning-based AI.

The computer sees the complete map. The human sees only what they have discovered. Because A* finds a shortest route in this uniform-cost maze, a human can match but not beat its step count.

## Source and status

[ChallengeMazeGenerator.java](https://github.com/elisseosmav/stratum-83/blob/feature/clean-games-algorithm-library/src/main/java/org/stratum83/labyrinth/ChallengeMazeGenerator.java) · [MazeFogOfWar.java](https://github.com/elisseosmav/stratum-83/blob/feature/clean-games-algorithm-library/src/main/java/org/stratum83/labyrinth/MazeFogOfWar.java) · [MazeSolver.java](https://github.com/elisseosmav/stratum-83/blob/feature/clean-games-algorithm-library/src/main/java/org/stratum83/labyrinth/MazeSolver.java).

Unpublished development candidate; Windows graphical acceptance pending. This is a Wiki-ready draft, not an automatically updated live page.
