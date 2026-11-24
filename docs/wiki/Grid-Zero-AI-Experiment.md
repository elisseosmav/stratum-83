# Grid Zero — Understanding the AI Experiment

**Educational chapter 01 — STRATUM-83**

Grid Zero is 3×3 Tic-Tac-Toe. A human can play the computer in **01 GRID ZERO** on the main menu (or use `PLAY`); two people can play locally by typing `DUEL`.

## Perfect Minimax

Minimax systematically evaluates possible future legal moves, assuming the opponent also chooses strong responses. A game result is scored as a win, draw or loss; STRATUM-83 prefers earlier wins and later unavoidable losses. **Alpha-beta pruning** eliminates parts of the search that cannot affect the decision.

Because Tic-Tac-Toe is a small game, exhaustive perfect-play search is practical. Minimax cannot be defeated if correctly implemented and another perfect player can force a draw.

## Random moves

A random player chooses uniformly from the currently empty squares. It does not search, remember prior games or learn from experience. Sometimes it plays a good move by coincidence.

## Reproducible experiment

In **03 RESEARCH LAB → 01 MINIMAX VS RANDOM** (or command `EXPERIMENT`), choose:

- **Random seed:** default `1983`.
- **Game count:** default `200`, permitted range `1–2000`.

Each game alternates which mark Minimax uses (X in even-indexed games, O in odd-indexed games). The report counts Minimax wins, draws and losses. All counts must sum to the number of games; a perfect implementation should have zero Minimax losses.

The seed controls the pseudorandom opponent, so repeating with the same seed and game count gives the same outcomes. It does **not** make Minimax adaptive or learned.

## Research questions

1. Why can an optimal agent guarantee no losses in Tic-Tac-Toe?
2. What changes when Minimax moves second?
3. Why is a fixed seed useful for comparing algorithm changes?
4. How would a simple rule-based heuristic player compare with random and Minimax?
5. Does alpha-beta pruning change the result or only the number of searched nodes?

## Java implementation

- [GridZeroGame.java](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/src/main/java/org/stratum83/game/GridZeroGame.java)
- [MinimaxPlayer.java](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/src/main/java/org/stratum83/game/MinimaxPlayer.java)
- [ExperimentRunner.java](https://github.com/elisseosmav/stratum-83/blob/feature/terminal-research-redesign/src/main/java/org/stratum83/research/ExperimentRunner.java)

This experiment is classical, deterministic search against a seeded random baseline—not machine learning or an LLM evaluation.
