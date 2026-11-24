# STRATUM-83 — Architecture and UI Design

**Current design candidate: v0.3.0-beta.2** (not yet an official release)

## Purpose

A small, offline Java 21/JavaFX educational application demonstrating classical search and reproducible experimentation. Core algorithms remain independent of JavaFX.

## Two experiences

1. **Play:** Grid Zero (against Minimax) and Labyrinth human-versus-algorithm challenge (navigate, then compare your route with BFS).
2. **Research Lab:** Minimax vs Random and Pathfinding Benchmark, both displaying configurable inputs and results rather than duplicated game screens.

The main menu has just three buttons: Grid Zero, Labyrinth, Research Lab. Two-player Grid Zero is available through `DUEL`. `HELP` and `EXIT` are terminal commands / keyboard shortcuts, not menu buttons.

## Runtime architecture

```text
StratumApp (JavaFX views, keyboard and terminal commands)
  |
  +-- Play: GridZeroGame -- MinimaxPlayer
  |
  +-- Play: Maze / MazeGenerator -- MazeChallenge -- MazeSolver (BFS)
  |
  +-- Research Lab: ExperimentRunner -- GridZeroGame / MinimaxPlayer
  |
  +-- Research Lab: MazeBenchmarkRunner
                          |
                          +-- MazeGenerator / MazeSolver (BFS, Dijkstra, A*)
```

The JavaFX screen handles input and draws output. It never computes paths or AI decisions itself. Game state is updated through the game-engine API. The latest Grid Zero layout uses **independent text labels above its buttons** so occupied marks remain visible after moves.

## Human-versus-algorithm Labyrinth challenge

The pure-Java `MazeChallenge` class owns the player's visited route and validates every movement. It computes the BFS optimum once per attempt using the existing `MazeSolver`. It does **not** reveal the reference route during play.

After the player reaches the goal, JavaFX displays **moves, optimal BFS moves, extra moves and efficiency** (`100 × optimal / actual`). Backtracking counts as real movement. Both the player's visited-cell set and the BFS route can then be highlighted separately; visited-cell coloring does not attempt to show revisit order. `RETRY SAME MAZE` starts again from the same seed and clears all score and overlay state, while `NEW MAZE` increments the seed.

A score of 100% means shortest route, not faster computation or a learning AI. The Research Lab keeps its pathfinding benchmark separate and uses the same solver classes.

## Experiment semantics

**Minimax vs Random:** `ExperimentRunner` uses `java.util.Random(seed)` to select uniformly among legal random-player moves. Minimax alternates between X and O across games. Scores are always wins, draws and losses for Minimax, and results should be repeatable for the same seed and game count.

**Pathfinding:** `MazeBenchmarkRunner` generates one 11×15 maze per seed, running BFS, Dijkstra and A* against that **same maze**. Sample count may be 1–100. It checks that all three methods return equal optimal lengths and reports the arithmetic mean of steps and explored nodes. It deliberately does not claim timing or real-world performance conclusions.

## Windows user experience

- JDK 21, JavaFX 21, Maven to build from source; packaged Windows installer not yet provided.
- Full keyboard/terminal commands remain available: `PLAY`, `DUEL`, `MAZE`, `RESEARCH`, `EXPERIMENT`, `BENCHMARK`, `HELP`, `MENU`, `EXIT`.
- Research input fields validate integer seeds and bounded sample counts.
- JavaFX `Text` renders multiline experiment reports without `Label` ellipsis truncation.
- Sound starts OFF, can be toggled through the header or F2.

## Style and scope

Prefer plain Java methods and simple classes over speculative abstractions. This project is original, non-commercial research. There are no cloud requests, user accounts, external AI models or telemetry. No third-party film art, dialogue or audio assets are used.

## Unverified items

A full `mvn clean test` with the real JUnit runner and Windows JavaFX screenshots are still required for this candidate. The research core has been compiled separately with OpenJDK 21 and checked on reproducible seeded mazes, but that is not GUI acceptance testing.
