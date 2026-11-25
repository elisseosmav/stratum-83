# STRATUM-83 — Clean Games Windows Acceptance

**Candidate:** `feature/clean-games-algorithm-library` / unreleased v0.3.0-beta.2.

Run the Java 21 Maven/JUnit suite from the project root, then launch JavaFX:

```powershell
..\apache-maven\bin\mvn.cmd clean test
..\apache-maven\bin\mvn.cmd javafx:run
```

Alternatively, run the guided acceptance runner:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\verify-beta2-windows.ps1
```

The runner checks your branch, executes tests, launches JavaFX and asks ten **PASS / FAIL / SKIP** questions. It saves a local `reports/BETA2-WINDOWS-ACCEPTANCE.md` file ignored by Git. It does not push, merge, tag or publish anything.

## Manual checks

1. **Games screen:** six entries, first two enabled, later four disabled and visibly marked Planned/Proposed. The header contains GAMES and ALGORITHMS; no terminal command prompt, Research Lab or forced lessons.
2. **Grid Zero:** human always plays X. After a valid X move, Minimax automatically responds O. X/O are visible. Wins, losses, draws and NEW GAME work.
3. **Algorithms screen:** short, readable descriptions of Minimax, alpha-beta, randomized DFS, BFS, Dijkstra, A*, and line-of-sight fog. No complicated interactive teaching controls.
4. **Labyrinth start:** 23×31 maze; most cells are black. Player P is visible, exit G must remain hidden. Header indicates arrow-key navigation.
5. **Movement:** Arrow keys move one open cell at a time. Walls block movement without increasing the move count. Clicking a maze cell does not move.
6. **Visibility:** only near squares within sight are bright. A wall stops vision behind it. Fog opens incrementally as the player moves.
7. **Memory:** explored cells become dim after leaving the current visible area; unexplored cells remain black. Goal only appears once it is within visible/discovered space.
8. **Completion:** after reaching G, show the player's actual steps and the computer A* shortest steps. The computer route reveal button appears **only then**. Toggling it highlights/hides the A* route.
9. **Replay:** RETRY resets the player and fog but preserves the same maze seed/layout. NEW MAZE changes seed and generates a different deterministic map.
10. **Layout and controls:** Window usable at 680×600 with scrolling, arrow keys still move and scroll follows the player, Esc opens Games and F2 toggles sound.

## Automated checks

- `ChallengeMazeGeneratorTest`: reproducibility, connected loops, distant goal, BFS/A* path agreement over many seeds.
- `MazeFogOfWarTest`: visible walls, blocked sight, hidden goal, persistent explored memory, invalid positions.
- `AlgorithmCatalogTest` and `GameCatalogTest`: clear truthful listings.
- Existing `GridZero`, `Minimax`, `MazeSolver` and other preserved JUnit suites.

A passing GitHub Actions build confirms compilation and automated tests **but not desktop visual acceptance**. No Beta 2 release should be published until the checklist is completed and reviewed.
