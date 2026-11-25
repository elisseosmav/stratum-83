# Grid Zero — Understanding the AI Experiment

**Educational chapter 01 — STRATUM-83**

Grid Zero is 3×3 Tic-Tac-Toe. A human can play the computer in **01 GRID ZERO** on the main menu (or use `PLAY`); two people can play locally by typing `DUEL`.

## Play → Trace → Explain → Apply (new candidate)

The interactive Grid Zero learning pilot adds the `TRACE`, `EXPLAIN`, and `APPLY` commands to a game against Minimax.

- **Play:** Make an X move; the computer responds with O.
- **Trace:** Step through the **real recorded Minimax search** for the previous O move, inspecting board states, maximizing/minimizing turns, terminal scores and alpha-beta cutoffs. The UI shows up to 159 initial events plus the final choice and labels truncation.
- **Explain:** Compare exact scores of every root move, see the computer's selected move, and learn the scoring model (`10-depth`, `0`, `depth-10`).
- **Apply:** Choose between aggressive and bounded retries given hypothetical costs under normal and overloaded service conditions. The worst-case chooser prefers the policy with the smallest maximum cost. The numbers are illustrative, **not** real network measurements, and not every retry problem is adversarial.

**Key limitation:** Minimax fits modeled competitive/worst-case decisions, while expected value is often better for probabilistic problems.

[Read the full Grid Zero learning guide](https://github.com/elisseosmav/stratum-83/blob/feature/grid-zero-game-history-lessons/docs/GRID-ZERO-LEARNING.md) for source links, exercises and limitations.

## A lesson from your own match

The newest development candidate uses **PLAY → GAME HISTORY → EXPLAIN MOVE**.

After playing Grid Zero, select **GAME HISTORY** and choose an actual X move such as **move 3**. STRATUM-83 reconstructs the position from your game and explains why Minimax chose the following **O move 4**.

You can examine alternative O placements and step through the future moves that would follow if both sides played optimally. These are labeled **hypothetical** and never overwrite the match's real history. Finally, reveal the move the computer truly selected.

`EXPLAIN` opens the latest recorded computer decision; `HISTORY` or `TRACE` opens the move list. The technical search-tree display is available separately through `ADVANCED`.

[Read the full updated Grid Zero learning chapter](https://github.com/elisseosmav/stratum-83/blob/feature/grid-zero-game-history-lessons/docs/GRID-ZERO-LEARNING.md).

## New beginner-friendly learning view

The TRACE, EXPLAIN and APPLY screens now teach the **answer first**, with optional technical detail.

- **TRACE:** What move did the computer choose, and why? The first screen explains the decision in ordinary words. A larger game board shows possible future moves; the final step shows the move actually chosen. Only open **COMPARE POSSIBLE MOVES** or **SHOW SEARCH STATISTICS** if you want more detail.
- **EXPLAIN:** Three steps: imagine a move, predict the opponent's best reply, and choose the safest outcome. Technical scores and alpha-beta pruning remain available behind **SHOW TECHNICAL DETAILS**.
- **APPLY:** Choose between two retry policies for a fictional overloaded service. See their worst costs, then learn how a minimax-style worst-case decision works. The costs are imaginary; real reliability design may require probability and timeouts.

**Worked example:** On board `OXX / .O. / XOX`, O chooses the last square in the middle row (row 2, column 3): that produces a draw (0), instead of choosing row 2, column 1 and eventually losing (−8).

[Full Grid Zero learning chapter](https://github.com/elisseosmav/stratum-83/blob/feature/grid-zero-game-history-lessons/docs/GRID-ZERO-LEARNING.md).

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

- [GridZeroGame.java](https://github.com/elisseosmav/stratum-83/blob/feature/grid-zero-game-history-lessons/src/main/java/org/stratum83/game/GridZeroGame.java)
- [MinimaxPlayer.java](https://github.com/elisseosmav/stratum-83/blob/feature/grid-zero-game-history-lessons/src/main/java/org/stratum83/game/MinimaxPlayer.java)
- [ExperimentRunner.java](https://github.com/elisseosmav/stratum-83/blob/feature/grid-zero-game-history-lessons/src/main/java/org/stratum83/research/ExperimentRunner.java)

This experiment is classical, deterministic search against a seeded random baseline—not machine learning or an LLM evaluation.
