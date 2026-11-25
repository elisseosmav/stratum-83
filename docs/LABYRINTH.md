# Labyrinth — Explore Under Fog of War

**Human (arrow keys) versus the computer's A* shortest route.**

The point of Labyrinth is now a genuine spatial exploration challenge, not a simple fully visible maze.

## Play

- A seeded, connected **23×31** labyrinth is generated. Randomized depth-first carving creates long passages; extra opened walls create loops and alternative routes. The exit is placed far from the start.
- You see your player `P`, nearby open ground and walls. **Most of the map starts in complete darkness.**
- Move using the **arrow keys**. You cannot move through walls, and you cannot click the map to teleport or move.
- **Vision radius:** 3 cells. Only nearby squares with line of sight are bright; walls block vision to squares beyond them.
- **Exploration memory:** previously discovered cells remain on the map in muted colors so you can remember where you've been. Unseen cells remain black.
- Reach `G`, which is not marked until you discover it.

## Human versus computer

The computer computes an **A*** shortest route using the entire maze. You do not see the route or its length while exploring. Once you reach the goal, STRATUM-83 shows:

| Measure | Meaning |
| --- | --- |
| Your moves | Valid arrow-key steps, including backtracking |
| Computer A* moves | Fewest steps in this unweighted grid |
| Efficiency | `100 × computer steps / your steps` percent |

A* uses Manhattan distance as its heuristic and finds a shortest route here. **You cannot beat an optimal shortest-path length; you can only tie it.** The challenge is navigating with imperfect information, not competing in reaction time.

After finishing, **SHOW COMPUTER ROUTE** highlights the computer's optimal path. **RETRY** clears the fog and returns to the same seed; **NEW MAZE** chooses the next seed.

## Algorithms

The separate **ALGORITHMS** view gives a short overview of randomized DFS (generation), A* (reference path), BFS and Dijkstra (implemented graph-search alternatives), and line-of-sight visibility (fog). It is optional.

## Source

- [ChallengeMazeGenerator.java](../src/main/java/org/stratum83/labyrinth/ChallengeMazeGenerator.java)
- [MazeFogOfWar.java](../src/main/java/org/stratum83/labyrinth/MazeFogOfWar.java)
- [MazeChallenge.java](../src/main/java/org/stratum83/labyrinth/MazeChallenge.java)
- [MazeSolver.java](../src/main/java/org/stratum83/labyrinth/MazeSolver.java)
- [StratumApp.java](../src/main/java/org/stratum83/ui/StratumApp.java)

**Status:** Unpublished development candidate, pending a Windows JavaFX acceptance test.
