# Grid Zero — Human versus Minimax

**Latest development candidate:** [Games + Algorithms rebuild](https://github.com/elisseosmav/stratum-83/tree/feature/clean-games-algorithm-library). This page describes the playable game in that branch, **not the older Game History or Play → Trace → Explain → Apply prototypes**.

## Play

Open **GAMES → GRID ZERO**. You play **X**; the computer plays **O**. Click an empty cell to make a move. The computer immediately chooses its response using Minimax with alpha-beta pruning. You can play until someone wins or the game ends in a draw. **NEW GAME** resets the board.

There is no human-versus-human option, move-history lesson, compulsory trace, quiz or experimental dashboard in the rebuilt interface.

## What is Minimax?

Minimax examines the possible next moves, then imagines how an opponent might respond. It selects the move with the best guaranteed result assuming the opponent also makes strong choices. In Grid Zero it prefers **a win over a draw, and a draw over a loss**.

**Alpha-beta pruning** stops examining possibilities that cannot affect that choice. It reduces unnecessary work without changing the selected optimal move.

These methods are *classical search algorithms*, not trained machine learning. Their plain-language explanations are available in the separate **ALGORITHMS** view; you do not have to read them before playing.

## Implementation and verification

- [GridZeroGame.java](../src/main/java/org/stratum83/game/GridZeroGame.java) — game rules and winner/draw detection
- [MinimaxPlayer.java](../src/main/java/org/stratum83/game/MinimaxPlayer.java) — computer move selection and alpha-beta pruning
- [StratumApp.java](../src/main/java/org/stratum83/ui/StratumApp.java) — JavaFX board with reliable X/O overlay labels
- [Windows acceptance checklist](TESTING.md) — desktop validation still required before a release

The older Grid Zero history, trace, quiz and research source files remain in the development tree temporarily but are **not reachable through the current user interface**. This is a development candidate, not a new published release.
