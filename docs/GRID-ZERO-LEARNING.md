# Grid Zero — Play, Trace, Explain, Apply

**STRATUM-83 / Educational chapter 01**

This lesson uses Tic-Tac-Toe to explain **Minimax** and **alpha-beta pruning** and then transfers the worst-case decision concept to a software engineering example. The entire experience runs locally in Java 21.

## 1. PLAY — Encounter the problem

Select **01 GRID ZERO** on the main menu or type `PLAY`. You play X and the computer plays O. The computer searches the game tree using Minimax. When you place X, the computer evaluates possible O moves and responds. Local two-player mode remains accessible with `DUEL`.

After the first computer move, select **TRACE**, **EXPLAIN** or **APPLY** without resetting the current game.

## 2. TRACE — Watch a real decision

Select **TRACE** or type `TRACE`. The trace is captured from the **same code that chose the computer's actual move**.

- **PREVIOUS / NEXT** step through recorded search states. The text board uses `X`, `O`, and `.` for empty squares.
- **MAX** events show the computer's turn to maximize a score.
- **MIN** events show the opponent's turn to minimize the computer's score.
- **OUTCOME** records a completed branch: win, draw or loss.
- **PRUNE** marks a real alpha-beta cutoff—an opportunity to stop checking siblings because they cannot improve the branch decision.
- **CHOSEN** shows the computer's selected move after evaluation.
- The screen also shows **exact root candidate scores**, total evaluated positions and cutoff count.

**Important:** Full Tic-Tac-Toe search can visit many states. The trace intentionally displays **up to 159 early events plus the final choice** to keep the UI readable; when some events are omitted it says so. The root candidate scores and evaluated-node/cutoff totals still describe the entire search. This is a *sample of a real tree traversal*, not a complete animated tree.

Root candidates are independently evaluated with open alpha/beta bounds so that their shown scores are exact. Row-major tie breaking means equally rated moves are resolved by board order.

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
