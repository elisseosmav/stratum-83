# STRATUM-83

**Classic Games. Real Algorithms.**

An offline Java desktop application where you play against the computer and explore the algorithms behind each challenge.

## Games

- **Grid Zero** — Play X against a Minimax computer using alpha-beta pruning.
- **Labyrinth** — Navigate a challenging maze with the arrow keys. Fog of war hides unexplored areas; reach the exit to compare your path with the computer's A* route.
- **Checkers** — Play American checkers against a Minimax opponent. Captures are mandatory, kings move in both directions, and multiple jumps are supported.
- **Blackjack** — Play against a computer dealer using Hit and Stand, with a hidden dealer card, soft aces, automatic dealer play, round scores, and Monte Carlo odds estimates. No real-money betting.
- **Chess** — Play White against a computer using Minimax with alpha-beta pruning. Includes legal move highlighting, check and checkmate, castling, en passant, four promotion choices, and common draw rules.
- **Crisis Protocol** — Command two response teams against a deterministic crisis agent. Contain spreading incidents, defend critical sites, and manage two actions per round.

## Algorithms

The Algorithms view explains Minimax, alpha-beta pruning, heuristic evaluation, legal move search, rule-based agents, greedy target selection, card probability, Monte Carlo simulation, maze generation, A* search, breadth-first search, Dijkstra's algorithm, and fog of war.

## Run

Requires Java 21 and Maven.

```powershell
mvn clean test
mvn javafx:run
```

On Windows, you can also use `run-windows.cmd` and `test-windows.cmd`.

For additional information, see the [Wiki](https://github.com/elisseosmav/stratum-83/wiki).
