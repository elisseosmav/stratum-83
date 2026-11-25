# Grid Zero — Play, Trace, Explain, Apply

**STRATUM-83 / Educational chapter 01**

This lesson uses Tic-Tac-Toe to explain **Minimax** and **alpha-beta pruning** and then transfers the worst-case decision concept to a software engineering example. The entire experience runs locally in Java 21.

## 1. PLAY — Encounter the problem

Select **01 GRID ZERO** on the main menu or type `PLAY`. You play X and the computer plays O. The computer searches the game tree using Minimax. When you place X, the computer evaluates possible O moves and responds. Local two-player mode remains accessible with `DUEL`.

After the first computer move, select **GAME HISTORY**, **EXPLAIN MOVE** or **APPLY** without resetting the current game. **EXPLAIN** starts with three short ideas (imagine, predict, decide), while **APPLY** asks you to choose between two retry policies before revealing the reasoning.

## A worked example: why the computer chooses a draw

Imagine O's turn in this almost-finished game:

```text
O X X
. O .
X O X
```

The two legal choices are easy to compare:

- **O chooses row 2, column 1:** X takes row 2, column 3 and completes the right column. **O loses** (score −8).
- **O chooses row 2, column 3:** X fills the final square, and nobody wins. **Draw** (score 0).

The computer picks **row 2, column 3** because **a draw is better than a loss**. This is Minimax in plain language: think about what the opponent might do, then choose the move with the best guaranteed result.

The updated educational screen begins with that kind of explanation, not with search metrics. You can then step through hypothetical game boards in a larger 3×3 grid. **COMPARE POSSIBLE MOVES** reveals the individual scores, and **SHOW SEARCH STATISTICS** reveals the advanced counts only when requested.

## 2. GAME HISTORY — Choose a move from your actual match

Once you have played against the computer, press **GAME HISTORY** (or type `HISTORY`). The application records every actual X and O move in order.

Choose the **third move** (your second X), for example. The next move—move 4—is the computer's O reply. Selecting that historical turn opens **EXPLAIN COMPUTER MOVE 4**. The lesson reconstructs the exact board after your move 3; it does not use a fixed sample board.

**EXPLAIN MOVE** on the play screen opens the latest actual computer decision. Use `EXPLAIN` at the terminal for the same action.

### A four-part lesson for that move

1. **Look at the real position:** See the exact board after your X move.
2. **Imagine a computer move:** Select any empty square where O could have played. Nothing changes in the saved game.
3. **Follow a possible future:** Watch each player's best response, one move at a time. These continuations are computed by the same Minimax engine, but are **hypothetical**, not the moves you necessarily played afterward.
4. **Compare with the real decision:** Reveal where O actually played and the best result it could guarantee against optimal play.

For example, if the computer can choose between a move that leads to losing and one that guarantees drawing, Minimax chooses the draw.

The saved move history and Minimax decision are never modified during these explanations. Retrying a new game starts a new history.

### Optional deeper learning

The original raw search-trace view remains available with the `ADVANCED` terminal command or **ADVANCED / SEE SEARCH EVENTS** at the end of the guided lesson. It shows MAX/MIN states, pruning and numerical scores, but is deliberately not the starting point for beginners.

## 3. EXPLAIN — Understand the search

Minimax gives terminal states scores from the computer's perspective:

| Outcome | Formula |
| --- | --- |
| Computer wins | `10 - depth` |
| Draw | `0` |
| Computer loses | `depth - 10` |

The computer prefers faster wins and delays unavoidable losses. Both sides are modeled as choosing optimally; the algorithm can therefore guarantee at least a draw in Tic-Tac-Toe if implemented correctly.

**Alpha-beta pruning** keeps a lower bound `alpha` and an upper bound `beta`. When `beta <= alpha`, other moves within that branch cannot improve the decision. Pruning avoids some search work **without changing the best move**.

No model is trained and no AI API is used. This is classical adversarial search.

## 4. APPLY — Transfer the principle to engineering

The first engineering exercise asks you to choose a retry policy for a service facing two modeled environments. **The numbers below are hypothetical teaching values, not measured network data.**

| Policy | Normal cost | Overloaded cost | Worst case |
| --- | ---: | ---: | ---: |
| Aggressive retries | 1 | 9 | 9 |
| Bounded retries | 3 | 4 | 4 |

A *worst-case* chooser considers the maximum cost for each policy, then picks the policy with the smallest maximum. That chooses **Bounded retries** (4 instead of 9).

In Java this is the `WorstCasePolicy` class. The screen asks the learner to choose first and then reveals the reasoning.

**When might this help?** Designing bounded recovery behavior or exploring worst-case outcomes in an adversarial/tested environment.

**When not?** If environment states have known probabilities and worst-case scenarios are not the design objective, probability and expected-value analysis may be more suitable. A Tic-Tac-Toe Minimax model does not automatically solve reliability engineering.

## 5. Try experiments

Go to **03 RESEARCH LAB → 01 MINIMAX VS RANDOM** or enter `EXPERIMENT`. Set seed `1983` and `200` games. Try another seed and compare the win/draw distribution; Minimax should never lose. The seed controls the random opponent, not Minimax's thinking.

## 6. Explore the Java code

- [GridZeroHistory.java](../src/main/java/org/stratum83/game/GridZeroHistory.java) — records and replays actual match moves
- [GridZeroMoveLesson.java](../src/main/java/org/stratum83/game/GridZeroMoveLesson.java) — computes alternative futures from the saved decision
- [GridZeroHistoryScreens.java](../src/main/java/org/stratum83/ui/GridZeroHistoryScreens.java) — guided Game History and Explain Move interface
- [MinimaxPlayer.java](../src/main/java/org/stratum83/game/MinimaxPlayer.java) — shared gameplay search and optional event recording
- [MinimaxAnalysis.java](../src/main/java/org/stratum83/game/MinimaxAnalysis.java) — immutable candidate scores and search events
- [GridZeroLearningScreens.java](../src/main/java/org/stratum83/ui/GridZeroLearningScreens.java) — trace/explain/apply screens
- [WorstCasePolicy.java](../src/main/java/org/stratum83/education/WorstCasePolicy.java) — tiny engineering exercise
- [MinimaxAnalysisTest.java](../src/test/java/org/stratum83/game/MinimaxAnalysisTest.java) — verifies trace/choice consistency
- [WorstCasePolicyTest.java](../src/test/java/org/stratum83/education/WorstCasePolicyTest.java) — tests the worst-case choice

## Suggested exercises

1. What happens if the opponent does *not* choose optimally?
2. Why is a draw worth zero? What would change if it were worth +1?
3. Find a search step where pruning happens. What work is skipped?
4. Change the hypothetical costs and predict which policy the worst-case chooser picks.
5. Which real software problem would require Dijkstra instead of Minimax, and why?

**Status:** educational development candidate; full Maven/JUnit and Windows JavaFX tests must pass before a release.
