# STRATUM-83 — Development Roadmap

> **Status:** public planning document | **Updated:** 24 November 2026  
> **Purpose:** non-commercial, educational Java research and development  
> **Schedule:** release sequence, **not a promise of delivery dates**

**Classic Games. Real Algorithms. Practical Engineering.**

STRATUM-83 is an offline-first Java 21 / JavaFX terminal for playable games and reproducible experiments. Each game is an entry point to **PLAY → TRACE → EXPLAIN → APPLY** and a practical engineering transfer exercise. Each game should teach an algorithm or an engineering idea. Releases are deliberately small, independently testable and developed using straightforward, maintainable Java code.

## Main menu strategy

The initial product is a **retro game terminal** where users select classic games that use fundamental AI algorithms. The top-level menu is a **game catalog**, not a mandatory algorithm lesson.

- **Playable:** Grid Zero (Minimax / alpha-beta) and Labyrinth (BFS / Dijkstra / A*).
- **Planned:** Crisis Protocol (rule-based strategic simulation).
- **Proposed, not scheduled:** Checkers (Minimax/heuristics), Blackjack (probability/Monte Carlo), Chess (alpha-beta/heuristic search).
- **Research Lab:** remains implemented and accessible by terminal command, not as a game or top-level menu item.
- **Advanced explanations:** kept in source for experimentation, but not required to play.

Future titles are visible with disabled menu entries so their inclusion never implies that they are implemented.

## Release overview

| Release | Focus | Deliverables | Status |
| --- | --- | --- | --- |
| **v0.1** | Initial source build | Grid Zero (tic-tac-toe), Minimax, local two-player mode, seeded AI experiment and terminal UI | **Source published**; Windows play confirmed, complete acceptance testing pending |
| **v0.2** | Reliability and distribution | Windows acceptance tests, CI, standalone Windows packaging, display polish, local preferences | **Planned / work underway** |
| **v0.3** | Algorithm learning pilot | Grid Zero Minimax and Labyrinth BFS/Dijkstra/A* Play → Trace → Explain → Apply lessons; Research Lab benchmarks | **Beta 1 published; Beta 2 candidate in review** |
| **v0.4** | Strategic simulation research | Crisis Protocol prototype with fictional agents, uncertainty, transparent rules and reproducible results | **Planned** |
| **v0.5** | Two-player game AI | Checkers and heuristic evaluation of larger game trees | **Proposed idea — not scheduled** |
| **v0.6** | Probability and decision-making | Blackjack and Monte Carlo experiments, simulated chips only | **Proposed idea — not scheduled** |
| **v0.7** | Advanced search | Chess and performance comparisons of search heuristics | **Proposed idea — not scheduled** |

The proposed v0.5–v0.7 entries do **not** have approved milestones or implementation commitments.

## Current development preview

- [v0.3.0-beta.1](https://github.com/elisseosmav/stratum-83/releases/tag/v0.3.0-beta.1) is a **published source-only prerelease**, with Grid Zero, Labyrinth and initial Research Lab.
- **v0.3.0-beta.2** is an **unpublished educational candidate** on [feature/retro-ai-game-catalog](https://github.com/elisseosmav/stratum-83/tree/feature/retro-ai-game-catalog), based on prior Labyrinth and terminal work. Grid Zero now adds **game history and move-specific Minimax lessons** to the Play → Trace → Explain → Apply curriculum; Labyrinth retains its graph-search visualization. Labyrinth traces actual BFS, Dijkstra and A* search expansions, and its engineering exercise compares a shortest-hop route with a lower-cost weighted route. The separate Research Lab remains results-only. Its new six-entry game catalog shows two playable games and four explicitly unfinished concepts; Research Lab remains available through the `RESEARCH` command. `DUEL`, `HELP` and `EXIT` are command-line actions.
- Experiments now accept configurable seeds. The maze benchmark compares BFS, Dijkstra and A* over 1–100 identically seeded mazes; Grid Zero compares Minimax against random over 1–2000 games.
- The candidate needs Windows build and GUI acceptance testing before any new tag or release is announced. There is **no Windows installer**.

## Approved GitHub milestones

### [v0.2 — Quality and Windows distribution](https://github.com/elisseosmav/stratum-83/milestone/1)

- [#2 Verify JavaFX build and gameplay on Windows 11](https://github.com/elisseosmav/stratum-83/issues/2)
- [#3 Add GitHub Actions for Java 21 Maven build and tests](https://github.com/elisseosmav/stratum-83/issues/3)
- [#4 Package a standalone Windows app with jpackage](https://github.com/elisseosmav/stratum-83/issues/4)
- [#5 Polish terminal display and keyboard navigation](https://github.com/elisseosmav/stratum-83/issues/5)
- [#6 Add locally stored application settings](https://github.com/elisseosmav/stratum-83/issues/6)

**Windows field report:** [#10 — X/O marks not visible](https://github.com/elisseosmav/stratum-83/issues/10) was closed after a successful maintainer retest. The proposed fix was developed on branch `fix/issue-10-visible-marks`; **verify it has been merged into the default branch before calling it part of a public source release**.

### [v0.3 — Algorithm research and Labyrinth](https://github.com/elisseosmav/stratum-83/milestone/2)

- [#7 Extend Grid Zero Minimax experiments with search metrics](https://github.com/elisseosmav/stratum-83/issues/7)
- [#8 Implement Labyrinth with BFS, Dijkstra and A*](https://github.com/elisseosmav/stratum-83/issues/8)

Educational objective: separate interactive games from repeatable experiments. Track outcomes, visited nodes and path lengths with reproducible seeds; avoid claiming runtime benchmarks until timing methodology is implemented. The algorithms must remain independent of the JavaFX interface.

### [v0.4 — Crisis Protocol](https://github.com/elisseosmav/stratum-83/milestone/3)

- [#9 Design and prototype Crisis Protocol simulation](https://github.com/elisseosmav/stratum-83/issues/9)

Educational objective: explore rules-based decisions, incomplete information, agent behavior and Monte Carlo analysis in a **fictional** strategic crisis. It is not a war predictor and does not model operational targeting or weapons effects.

## How a milestone is completed

A planned feature is **not** done merely because code exists. At minimum:

1. The game or experiment runs locally with no account or cloud service.
2. The game rules and core algorithms have automated tests.
3. Windows behavior is verified for the public release.
4. The Wiki explains the algorithms, setup, experiment and limitations.
5. The GitHub Issues linked to the release are resolved or deliberately deferred.
6. The owner decides when to publish the release.

## How community feedback works

- **[Discussions](https://github.com/elisseosmav/stratum-83/discussions):** suggest a game, propose an experiment or ask a question.
- **[Issues](https://github.com/elisseosmav/stratum-83/issues):** maintainer-managed development tasks and reproducible bug reports.
- **[Research Backlog](RESEARCH-BACKLOG.md):** ideas under consideration; not delivery promises.

Development decisions and source-code changes are controlled by the repository owner. The repository is public for learning, testing and feedback.

## Project constraints

- Non-commercial R&D purpose; source distributed under the repository's stated licence.
- Java 21, simple architecture, no speculative overengineering.
- Fully playable locally; AWS and LLM services are never prerequisites.
- Original game implementations and assets; no copied movie graphics or dialogue.
- No fixed deadlines. Milestone status is tracked through GitHub, not manually calculated percentages.

---

*This roadmap documents current intentions. Order and scope may change based on testing, research findings and maintainer decisions.*
