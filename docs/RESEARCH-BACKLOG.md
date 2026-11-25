# STRATUM-83 — Future Game and Algorithm Ideas

**Current product:** clean retro **human-versus-computer games** and a separate view containing short explanations of the implemented algorithms.

## Implemented games

- **Grid Zero:** human X versus Minimax O with alpha-beta pruning.
- **Labyrinth:** 23×31 seeded maze with loops, hidden exit, fog-of-war line of sight, remembered explored cells, arrow navigation and A* shortest-path comparison after completion.

## Planned or proposed games

- **Crisis Protocol (planned):** fictional rule-based opponents.
- **Checkers (proposed):** Minimax with evaluation heuristics for larger search spaces.
- **Blackjack (proposed):** probability, expected value or Monte Carlo.
- **Chess (proposed):** heuristic evaluation and depth-limited game-tree search.

These are **ideas, not released features**, and have no promised dates.

## Technical follow-ups

- Finish manual Windows JavaFX validation of the new fog-of-war maze.
- Adjust fog radius, contrast and loop density based on player testing.
- Consider small difficulty choices: larger maps, different vision radii and optional timer.
- Retire/archive source-only legacy research and game-history UI classes once regressions have been reviewed.
- Package a standalone Windows application after the GUI is stable.
- Keep the Algorithms view brief, truthful and grounded in code that is actually executed.

The user should never need to learn Minimax or A* terminology before starting a game.
