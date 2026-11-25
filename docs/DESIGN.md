# STRATUM-83 — Clean Game Architecture

**Development branch:** `feature/clean-games-algorithm-library` / Java 21 / JavaFX / offline.

## Product

Two top-level views: **Games** (default) and **Algorithms** (brief, optional explanations). The user plays against the computer in every currently playable game. There is no mandatory TRACE, EXPLAIN, APPLY, Research Lab or DUEL navigation.

**GameCatalog** lists two playable games and four disabled future entries. **AlgorithmCatalog** owns readable descriptions without affecting gameplay. **StratumApp** is the small JavaFX shell that switches between views, displays boards and forwards input to the engine classes.

## Grid Zero

- **GridZeroGame** owns board rules and valid turns.
- Human clicks X; **MinimaxPlayer** chooses O using Minimax with alpha-beta pruning.
- The UI draws X/O through overlaid labels to prevent button-skin rendering bugs.
- There is no two-player interface or history lesson in the rebuilt application.

## Labyrinth — partial visibility against an optimal computer

- **ChallengeMazeGenerator** creates a deterministic, connected **23×31** maze by randomized DFS carving plus occasional extra passages for loops. It places the exit at a farthest reachable cell.
- **MazeFogOfWar** reveals nearby cells using a circular radius of 3 cells and Bresenham line-of-sight tests. Walls are visible but block the squares behind them. Discovered areas are remembered, drawn dimly when no longer in sight.
- **MazeChallenge** validates four-direction human arrow steps, keeps the exact movement history and prevents movement after goal.
- **MazeSolver** computes the computer's optimal A* route with a Manhattan-distance heuristic. **The shortest route, number of computer steps and overlay are hidden until the human finishes.** A* is optimal on the current uniform-cost four-direction grid.
- The UI tracks moves, discovered cells, seed and completion. On completion it shows the human step count versus the computer step count and allows revealing the optimal path.
- **RETRY** regenerates the same seed. **NEW MAZE** increments it. The map has no mouse-movement controls.

The player's visual knowledge and the computer's perfect-map knowledge are intentionally different. This is a **human-versus-algorithm benchmark**, not a simultaneous moving computer adversary. A human cannot use fewer steps than a shortest route, only match it.

## Performance and honesty

- Maze generation, fog calculations and pathfinding live in plain Java and are covered by JUnit tests.
- Older Research Lab/teaching classes remain in source temporarily but have **no entries in the current UI**. Their presence does not imply those tools are part of the new product.
- JavaFX interactive acceptance on Windows remains necessary. Passing Maven tests alone is not sufficient to publish Beta 2.
- Do not conflate algorithms such as BFS, DFS and A* with trained machine learning: these are classical search algorithms.

## Navigation

`GAMES`, `ALGORITHMS`, `ESC` for Games and `F2` for sound. Maze navigation is through arrow keys only. The retro UI has no terminal command prompt in this rebuild.
