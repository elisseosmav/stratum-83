# STRATUM-83 — Classic Games. Real Algorithms. Practical Engineering.

Welcome to STRATUM-83, an original, offline Java retro game collection.

## Games

The main **GAMES** screen lists current and future titles. Only the first two are playable:

| Game | Human vs computer | Status |
| --- | --- | --- |
| **Grid Zero** | X against Minimax O | Playable |
| **Labyrinth** | Arrow-key explorer against optimal A* reference route | Playable in development candidate |
| Checkers | Minimax and heuristics | Proposed |
| Blackjack | Probability-based computer strategy | Proposed |
| Chess | Search and evaluation | Proposed |
| Crisis Protocol | Rule-based agents | Planned |

## Labyrinth: fog of war

Navigate a larger **23×31** maze using arrow keys. Only nearby corridors in line of sight become visible; walls block sight. The rest of the map is black. Explored areas remain dim when you move away. You must discover the exit yourself.

The computer computes a shortest A* route, hidden during your exploration. After reaching the goal, compare your move count and reveal the computer route if you want.

## Algorithms

The separate **ALGORITHMS** screen provides short explanations of Minimax, alpha-beta pruning, randomized DFS, BFS, Dijkstra, A* and fog-of-war visibility. You do not need to read them before playing.

## Development status

The clean two-view application is an **unpublished Beta 2 candidate**. [Source branch](https://github.com/elisseosmav/stratum-83/tree/feature/clean-games-algorithm-library) · [README](https://github.com/elisseosmav/stratum-83/blob/feature/clean-games-algorithm-library/README.md) · [Development Roadmap](2.-Development-Roadmap).

The separately published GitHub Beta 1 release is an older snapshot. The Wiki is updated independently of the source branch.
