# STRATUM-83 — Educational Design Principles

**Classic Games. Real Algorithms. Practical Engineering.**

STRATUM-83 is an independent, non-commercial, offline Java 21 learning terminal. Classic games are accessible examples for teaching software engineers how algorithms work, how to inspect their decisions and when they are useful in real projects.

## Four stages of every lesson

1. **PLAY** — Interact with a small original game and encounter the problem.
2. **TRACE** — Inspect actual algorithm decisions and intermediate states rather than fictional animations. Recorded traces may be bounded to keep the UI responsive; any sampling must be disclosed.
3. **EXPLAIN** — Learn the rules, assumptions, complexity, scores, tradeoffs and limitations of the algorithm.
4. **APPLY** — Solve a small engineering problem that genuinely uses the same idea; explain when the algorithm would *not* be appropriate.

The **Research Lab** remains the location for reproducible experiments with configurable seeds and metrics. It complements—not replaces—the four-stage learning experience.

## Teach from the player's actual game

Grid Zero's preferred learning journey is now **Play → Game History → Explain Move → Apply**. The learner chooses a turn from their own match, e.g. X on move 3, to learn why Minimax chose the next O move 4.

An explanation begins with a replay of the actual position. The learner predicts alternative O moves, sees hypothetical future responses under perfect play, and finally compares the possibilities with the computer's recorded decision. **Actual moves and simulated futures must never be conflated.**

Technical search-tree events and numerical scores remain optional advanced material. This is a refinement of the approved **Play → Trace → Explain → Apply** vision, not a change in the project's fundamental purpose.

## Quality rules

- **One source of truth:** game AI and recorded trace must use the same algorithm, scoring scheme and tie-breaking.
- **Independent computation:** algorithms and educational examples remain in plain Java without JavaFX dependencies.
- **No invented measurements:** distinguish illustrative problem values from benchmark data.
- **Explain limitations:** e.g. Minimax assumes a modeled opponent and worst-case decisions; random network failures usually require probabilistic models.
- **Human-readable code:** conventional Java names, simple loops, small classes and no speculative design patterns.
- **Offline:** no cloud services, logins, LLMs or telemetry.
- **Visual accessibility:** readable monospace text, clear colors and usable window resizing.
- **Validation:** JUnit for algorithm correctness and manual Windows JavaFX acceptance before marking a release stable.
- **Licensing:** all new content and code must be original or legally reusable.

## Curriculum

| Game | Algorithms | Engineering transfer |
| --- | --- | --- |
| Grid Zero | Minimax and alpha-beta pruning | Adversarial search, worst-case decisions and pruning |
| Labyrinth | BFS, Dijkstra, A* | Graph traversal and routing problems |
| Blackjack (proposal) | Probability, expected value, Monte Carlo | Decisions under uncertainty |
| Checkers (proposal) | Heuristic evaluation and depth-limited search | Compute-budget trade-offs |
| Chess (proposal) | Search optimization | Managing large decision spaces |

**Grid Zero** implements Minimax decision tracing and a hypothetical worst-case policy exercise. **Labyrinth** now implements real BFS/Dijkstra/A* expansion tracing, written algorithm explanations and a weighted service-routing exercise. Both rely on the exact same Java algorithms used in gameplay/research. The current maze remains unweighted and loop-free, so its comparative limitations must be explained rather than obscured.

Every implemented game needs a corresponding Wiki chapter with Play, Trace, Explain, Apply and links to the source.

## Grid Zero first implementation

The JavaFX terminal now supports `TRACE`, `EXPLAIN` and `APPLY` from a Grid Zero game. A pure-Java `MinimaxAnalysis` exposes candidate scores, search states, evaluated positions and alpha-beta cutoffs. The UI shows a **bounded first segment** of events plus the final selected move, and clearly identifies the truncation when it occurs.

The `APPLY` exercise illustrates choosing a retry policy using hypothetical normal/overload costs, minimizing the maximum possible loss. These numbers are **invented for education**, not network measurements. This is appropriate only when a worst-case/adversarial model is justified; for independent probabilistic failures, expected-value approaches can be better.

**Development status:** the learning flow is implemented on an isolated feature branch and requires Windows JavaFX acceptance testing. No new GitHub Release is implied by this document.
