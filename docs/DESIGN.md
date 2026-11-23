# STRATUM-83 v0.1 Design

## Purpose

A small offline desktop game application that demonstrates classical AI techniques using original code and a retro terminal-style user interface. The research value is in repeatable, verifiable behaviour rather than generative AI.

## Runtime architecture

```text
JavaFX view + command input
          |
          v
    StratumApp controller
          |
      +---+----------------+
      |                    |
      v                    v
 GridZeroGame         ExperimentRunner
      |                    |
      +---- MinimaxPlayer -+
```

The game state is owned by `GridZeroGame`. The UI cannot edit board cells directly; it only calls `play(Move)`. The game engine controls legal moves, turn order, win conditions and draws. `MinimaxPlayer` searches copies of the current state and returns an optimal legal move, without mutating the caller's board.

## Minimax

- All legal moves are examined, with alpha-beta pruning.
- Terminal wins score `10 - depth`, losses score `depth - 10`, and draws score zero.
- Earlier wins are preferable; later losses are preferable when a loss is unavoidable.
- Row-major move enumeration provides deterministic tie-breaking.
- For a 3x3 board, the full search is cheap enough that caching or concurrency is unnecessary.

## Experiment

`ExperimentRunner` uses one seeded `java.util.Random` for repeatable random-player choices. Minimax plays X in even-numbered games and O in odd-numbered games. Every game reaches a win or draw; results sum to the configured number of games. A running experiment executes in a background JavaFX `Task` so it does not freeze the GUI.

## UI and privacy

- JavaFX controls and a bundled CSS stylesheet create an original green-on-black presentation.
- The application makes **no network requests**.
- The click tone is synthesized locally using the Java Sound API, never downloaded from an asset service.
- Sound starts disabled. An unavailable audio device does not stop gameplay.
- No user profiles, cloud storage, analytics, or telemetry.

## Future research, not in v0.1

- Labyrinth: BFS, Dijkstra and A* comparisons.
- Strategy games with fully specified, fictional and non-operational decision rules.
- JSON/CSV experiment exports and user-configured experiment seeds.
- Windows `jpackage` distribution with an included Java runtime.
- Accessibility improvements and automated UI acceptance tests.

## Licensing and attribution

All code, styling and sound synthesis are originally written for this sample. No film-owned assets or text are included. Apache-2.0 permits commercial reuse of code by others even though the authors' purpose is non-commercial R&D.
