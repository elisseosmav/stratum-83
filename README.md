# STRATUM-83

**Classic Games. Real Algorithms. Practical Engineering.**

An original, non-commercial **offline Java 21 / JavaFX retro game collection**. You play **against the computer**. A separate **Algorithms** view offers a few simple sentences about the methods used by each game—no compulsory lessons, search trees or research dashboards.

> **Status:** Work in progress on [feature/clean-games-algorithm-library](https://github.com/elisseosmav/stratum-83/tree/feature/clean-games-algorithm-library). This work is **not** in `main` or the [published v0.3.0-beta.1 release](https://github.com/elisseosmav/stratum-83/releases/tag/v0.3.0-beta.1). The future v0.3.0-beta.2 release is not published yet.

## Two simple views

**GAMES** is the default screen. Choose a playable title and start immediately:

| Game | Your role | Computer | Status |
| --- | --- | --- | --- |
| **Grid Zero** | Human plays X | Minimax with alpha-beta chooses O | Playable |
| **Labyrinth** | Human uses arrow keys to explore in fog | A* finds a shortest reference route | Playable |
| Checkers | Future human opponent | Minimax and heuristics | Proposed |
| Blackjack | Future human player | Probabilistic strategy | Proposed |
| Chess | Future human opponent | Search and evaluation | Proposed |
| Crisis Protocol | Future human/agent interaction | Rule-based agents | Planned |

Unavailable titles remain disabled without showing Planned or Proposed labels in the game menu—not working demos. There is **no local human-vs-human mode** in the rebuilt interface.

**ALGORITHMS** is a separate screen with short explanations of Minimax, alpha-beta pruning, randomized depth-first maze generation, BFS, Dijkstra, A* and line-of-sight visibility. It explains what each method does and a practical use. The explanations are optional; playing games does not require reading them.

## Labyrinth: fog-of-war challenge

The playable maze is now **23 rows × 31 columns**, with connected passages, loops and multiple possible routes. Each seed creates a reproducible layout; the goal is placed far from the entrance.

- **Use the keyboard arrow keys** to move your player (`P`). Clicking the map does not move the player.
- **Fog of war:** only nearby squares within the vision radius and line of sight are visible. Walls obstruct sight. Previously explored places stay **dimly visible**; unknown areas remain black.
- Find the exit (`G`), which stays hidden until discovered. You cannot reveal the computer's full route before finishing.
- After reaching the goal, compare **your number of moves** with the **computer's A* shortest route**. You can then reveal or hide that route.
- **RETRY** replays the same seed; **NEW MAZE** advances to a new seed.

The computer's path length is an optimal baseline. You can **match**, but cannot beat, a shortest path by taking fewer steps. Navigating under partial visibility is the human challenge; the computer computes with the full map.

## Grid Zero

Play tic-tac-toe as **X**; the computer responds as **O** using exact Minimax search with alpha-beta pruning. The retro board retains the known fix that renders X/O marks above the square buttons. Choose **NEW GAME** to reset.

## Install and run (Windows)

Requirements: **JDK 21** and **Apache Maven**. From your STRATUM-83 project folder:

```powershell
cd C:\Users\eliss\Projects\STRATUM-83
git clone --branch feature/clean-games-algorithm-library https://github.com/elisseosmav/stratum-83.git stratum-83-clean
cd .\stratum-83-clean
..\apache-maven\bin\mvn.cmd clean test
..\apache-maven\bin\mvn.cmd javafx:run
```

If you already have a checkout of this exact branch, run `git pull --ff-only` from it instead of cloning again. On other platforms, use `mvn clean test` and `mvn javafx:run`.

Navigation: **GAMES** / **ALGORITHMS** in the header; **Esc** returns to Games; **F2** toggles sound. The default is sound off. No network connection or cloud account is required to run the program after dependencies are installed.

## Development

- [Windows acceptance checklist](docs/TESTING.md)
- [Design and algorithm architecture](docs/DESIGN.md)
- [Development roadmap](docs/DEVELOPMENT-ROADMAP.md)
- [Beta 2 candidate release notes](docs/RELEASE-NOTES-v0.3.0-beta.2.md)
- [Wiki-ready content](docs/wiki)
- [Source tests](src/test/java/org/stratum83)

The older experimental and teaching classes are retained in the development source for now, but **no longer appear in the rebuilt application navigation**. Future cleanup can remove them after desktop acceptance.

The project uses original source and styling, stays non-commercial, and accepts suggestions through GitHub discussions. Published games and new releases remain under the maintainer's control.
