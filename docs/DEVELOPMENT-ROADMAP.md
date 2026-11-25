# STRATUM-83 — Development Roadmap

> Updated: 25 November 2026
>
> **Non-commercial, offline Java 21 game collection.** Plans below are not delivery commitments.

**Classic Games. Real Algorithms. Practical Engineering.**

## Product direction

The application has two simple user-facing views:

- **Games:** human versus computer only, with playable Grid Zero and Labyrinth. Future games appear but are disabled.
- **Algorithms:** short, optional descriptions of the algorithms actually implemented in the games.

The earlier command console, local two-player mode, mandatory lessons and Research Lab are **no longer in the active interface**. Their historical source code is temporarily retained on the feature branch for safe cleanup after Windows acceptance.

## Game roadmap

| Game | Computer technique | Scope | Status |
| --- | --- | --- | --- |
| **Grid Zero** | Minimax / alpha-beta pruning | Human X versus computer O | Playable |
| **Labyrinth** | A* shortest route / line-of-sight fog | Human explores a large hidden maze with arrows | Playable in development candidate |
| Checkers | Minimax / evaluation heuristics | Human versus computer | Proposed; not scheduled |
| Blackjack | Probability / simulated decisions | Human versus computer/house | Proposed; not scheduled |
| Chess | Search and evaluation | Human versus computer | Proposed; not scheduled |
| Crisis Protocol | Rule-based agents | Fictional strategic simulation | Planned |

The updated Labyrinth generator creates **23×31** connected mazes with loops and faraway exits. Player vision only reveals nearby cells not blocked by walls; discovered regions stay dimly visible. The A* reference route is revealed **after** reaching the goal.

## Release status

| Release | Focus | Status |
| --- | --- | --- |
| v0.1 | Initial Grid Zero source | Source published |
| v0.2 | Windows reliability and packaging | Planned / incomplete |
| **v0.3.0-beta.1** | Earlier Labyrinth candidate | **Published prerelease** |
| **v0.3.0-beta.2** | Clean Games / Algorithms interface and fog-of-war maze | **Unpublished development candidate**, Windows GUI acceptance pending |
| v0.4 | Crisis Protocol | Planned |
| v0.5–v0.7 | Checkers, Blackjack, Chess | Proposed, not scheduled |

**Current feature branch:** [feature/clean-games-algorithm-library](https://github.com/elisseosmav/stratum-83/tree/feature/clean-games-algorithm-library).

No current game code in this branch has been merged to the default `main` branch; the existing published release remains untouched.

## Engineering quality gates

1. Core algorithms and visibility mechanics remain testable in plain Java.
2. Every available game must be genuinely playable **human versus computer**, without login or network dependencies at runtime.
3. Future games must remain disabled until implemented, tested and approved.
4. GitHub Actions Java 21 tests must pass on Windows and Linux.
5. The maintainer must verify the actual JavaFX experience (including fog, arrows, window resizing and endgame A* comparison) before publishing a new release.
6. Wiki material should be updated after GUI acceptance; its separate Git repository is not updated automatically by modifying `docs/wiki`.
