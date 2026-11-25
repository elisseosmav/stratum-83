# STRATUM-83 v0.3.0 Beta 2 — Draft Release Notes

**Not published.** This candidate is on [feature/clean-games-algorithm-library](https://github.com/elisseosmav/stratum-83/tree/feature/clean-games-algorithm-library). The published [v0.3.0-beta.1](https://github.com/elisseosmav/stratum-83/releases/tag/v0.3.0-beta.1) is unchanged.

## Rebuilt application

STRATUM-83 now has **two clean views**:

- **Games:** a catalog of classic AI games. Grid Zero and Labyrinth are playable; Crisis Protocol, Checkers, Blackjack and Chess are disabled future titles.
- **Algorithms:** short, optional descriptions of Minimax, alpha-beta pruning, randomized DFS, A*, BFS, Dijkstra and fog-of-war line of sight.

Old Research Lab, terminal command entry, human-vs-human mode and complicated mandatory teaching screens are no longer present in the active interface. Historical source modules remain for subsequent cleanup.

## Games

**Grid Zero:** Human X versus computer O, powered by Minimax with alpha-beta pruning. Visible X/O overlay marks retained.

**Labyrinth:** Human navigates with **arrow keys** through a much larger **23×31** seeded maze with loops and a distant exit. Walls obstruct vision. Unexplored passages stay dark, while previously explored areas remain dimly visible. The computer calculates an **A*** shortest route using the full maze. Its path and step count remain hidden until the player reaches the goal; then the player can compare and reveal the route. Retry same seed or generate a new maze.

The computer is a shortest-path baseline, not a simultaneously moving agent. A human cannot beat a mathematically shortest step count.

## Testing / release gate

Automated Java 21 Maven/JUnit tests include maze solvability, reproducibility, loops, fog visibility, wall occlusion and exploration memory, alongside existing game/algorithm tests. GitHub Actions CI uses Windows and Ubuntu.

**Pending:** Maintainer's Windows JavaFX GUI acceptance test and release authorization. No installer is attached, no `main` merge or live Wiki publication has been performed for this candidate.
