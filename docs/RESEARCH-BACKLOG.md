# STRATUM-83 — Research Backlog

> **Status:** exploratory ideas, **not** approved releases or promised features  
> **Updated:** 24 November 2026

**Classic Games. Real Algorithms. Practical Engineering.** Each approved game must move from Play to Trace, Explain and Apply—with a realistic and bounded software engineering problem.

This page collects research questions that could become future STRATUM-83 games or experiments. A backlog entry is **not** a GitHub milestone and has no scheduled delivery date.

## Current implemented experiments (development candidate)

The Research Lab is deliberately **results-focused** and does not duplicate the playable screens:

- **Grid Zero / Minimax vs Random:** a configurable pseudorandom seed and number of games; report wins, draws and losses. The seed controls the random player, not Minimax.
- **Labyrinth / Pathfinding Benchmark:** select a starting seed and 1–100 generated mazes. BFS, Dijkstra and A* solve identical inputs; compare average shortest-path steps and explored cells, not an interactive maze.

Grid Zero now additionally has a trace/explain/apply learning pilot on [feature/grid-zero-learning-lab](https://github.com/elisseosmav/stratum-83/tree/feature/grid-zero-learning-lab). The candidate still requires Windows acceptance testing and is not a stable release.

## Candidate experiments

| Idea | Research question | Candidate algorithms and concepts | Status |
| --- | --- | --- | --- |
| **Checkers** | How does optimal-search reasoning change when the game tree becomes too large to explore fully? | Minimax, alpha-beta pruning, depth limits, evaluation functions | Proposed |
| **Blackjack** | What is the difference between random play and statistically informed decisions? | Probability, expected value, Monte Carlo simulation, seeded experiments | Proposed |
| **Chess** | How do move ordering, heuristics and depth influence search quality and runtime? | Minimax, alpha-beta, heuristic board evaluation, iterative deepening | Proposed |
| **Alternative Grid Zero agents** | How do a random player, a rule-based player and perfect Minimax compare? | Heuristics, game trees, reproducible trials | Proposed |
| **Maze generation comparison** | How do different generation algorithms affect route length and pathfinder effort? | Recursive backtracker, randomized Prim, BFS/A* metrics | Proposed |
| **Event Delivery Lab** | How do retries, delays and duplicated messages affect an abstract distributed system? | Discrete-event simulation, probability, idempotency | Exploratory |
| **Resilience Lab** | When can a retry policy amplify overload instead of helping service recovery? | Backoff, jitter, queueing and seeded simulation | Exploratory |

**Important:** The existing [Labyrinth milestone](https://github.com/elisseosmav/stratum-83/milestone/2) covers the first maze game. The maze-generation *comparison experiment* above is a possible later extension, not part of its current acceptance criteria.

## Selecting an experiment

A candidate should satisfy most of these questions:

1. **Educational value:** Is there a clearly explainable algorithm or engineering concept?
2. **Correctness:** Can we validate the rules using deterministic tests or mathematical properties?
3. **Reproducibility:** Can someone rerun the experiment with documented settings and seeds?
4. **Playability:** Does it work locally without an account, external API or AWS?
5. **Manageable scope:** Can we release a useful first version without unnecessary frameworks?
6. **Originality:** Can we use original code, game assets and descriptions?
7. **Documentation:** Can a Wiki chapter explain the methods and their limitations?
8. **Engineering transfer:** Can learners practice the principle on a credible non-game software problem?

The maintainer decides if an idea becomes a GitHub Issue and later an approved milestone.

## Suggested educational chapter template

Every implemented game or experiment should include:

1. **What the user sees:** controls and rules.
2. **Learning objectives:** the concept being demonstrated.
3. **Algorithms:** inputs, outputs, assumptions and an intuitive example.
4. **Java implementation:** links to the actual classes, with important design decisions.
5. **Experiment setup:** parameters, random seeds and comparison baselines.
6. **Results:** the metrics actually measured; examples labeled if synthetic.
7. **Limitations:** what the experiment does **not** prove.
8. **Try it yourself:** exercises and suggested changes for readers.

For the first example, open the [STRATUM-83 Wiki](https://github.com/elisseosmav/stratum-83/wiki) and choose **Grid Zero — Understanding the AI Experiment**.

## From idea to release

`Community Discussion → Review → Research hypothesis → GitHub Issue → Implementation → Tests → Educational Wiki chapter → Release`

A public discussion does not grant repository write access. Participants are encouraged to ask questions, share results and propose ideas in [Discussions](https://github.com/elisseosmav/stratum-83/discussions).

---

*STRATUM-83 is an independent, non-commercial, offline-first Java research project. This backlog is subject to revision.*
