# Grid Zero — Minimax and Alpha-Beta Pruning

**STRATUM-83 — Classic Games. Real Algorithms. Practical Engineering.**

In **GAMES**, choose **Grid Zero**. You place X and the computer responds with O.

## How the computer plays

**Minimax** looks ahead at possible moves. It imagines what could happen if both players try to make the best choices. It prefers a win over a draw and a draw over a loss.

**Alpha-beta pruning** makes the same decision with less work by skipping branches that cannot improve the result. It is a classical search technique, not a trained machine-learning model.

## Where is the idea useful?

Minimax illustrates planning in small competitive or adversarial situations: evaluating possible decisions while assuming an opponent responds intelligently. It is less appropriate for problems dominated by uncertainty and random failures, which usually need probabilities or other models.

## New user experience

The rebuilt application has **GAMES** and **ALGORITHMS** views. There is no required Game History, Trace, Minimax lesson or Research Lab screen; users can learn the concepts at their own pace by opening **ALGORITHMS**.

The original experiment and learning modules remain in development source temporarily but are not part of the current interface.

[Current candidate README](https://github.com/elisseosmav/stratum-83/blob/feature/clean-games-algorithm-library/README.md).

*This is a Wiki-ready source page, not an automatically updated live GitHub Wiki article.*
