# Test and Acceptance Checklist

**Candidate:** STRATUM-83 v0.3.0-beta.2 on `feature/terminal-research-redesign`

## Automated tests

In the directory containing `pom.xml`, run:

```powershell
mvn clean test
```

JUnit tests cover Grid Zero rules, Minimax strategy, reproducible Grid Zero experiments, seeded maze generation, BFS/Dijkstra/A* shortest paths and the new multi-maze benchmark. **The Windows Maven/JUnit suite has not yet been verified for this candidate.**

## Windows JavaFX acceptance checklist

### Main menu and terminal

1. Launch `mvn javafx:run`; confirm there are **exactly three main menu buttons**, in order: Grid Zero, Labyrinth, Research Lab.
2. Confirm **Help**, **Exit**, and **Two Players** are **not main-menu buttons**.
3. Click Grid Zero; confirm Minimax mode. Type `DUEL`; confirm two-player mode.
4. Click Labyrinth; verify the playable maze has `P`, `G` and walls; there are **no research solver buttons**.
5. Click Research Lab; verify just two options: Minimax vs Random and Pathfinding Benchmark, plus Back.
6. Verify typed commands `PLAY`, `DUEL`, `MAZE`, `RESEARCH`, `EXPERIMENT`, `BENCHMARK`, `HELP`, `MENU`, `BACK`, `EXIT`. F1 opens Help, F2 toggles sound, Esc returns to main menu.
7. Numbered entry `1` in the main menu starts Grid Zero; numbered entry `1` in Research Lab opens Minimax experiment.

### Grid Zero

8. Complete Minimax and two-player games, verifying X/O marks remain visible even on occupied squares and after a win/draw (Issue #10 regression).
9. New Game clears the board, while Main Menu returns to program selection.

### Labyrinth play

10. Use arrow keys and click adjacent open cells. Walls/nonadjacent clicks must not move the player.
11. Reach G and confirm the victory state. Use New Maze and confirm seed changes and the route updates.

### Research Lab

12. Open Minimax vs Random; run default **seed 1983 / 200 games**; wins+draws+losses must equal 200, losses should be 0.
13. Rerun with the same seed and count; outcomes must match. Try different seed and counts, including 1 and 2000.
14. Enter invalid seed or game count (zero, 2001, letters); an error should display without crashing.
15. Open Pathfinding Benchmark; enter seed 1983, count 1. Confirm **BFS, DIJKSTRA, A_STAR** appear completely and all report the same average shortest-path steps.
16. Run the same benchmark twice and compare results, then try **100 mazes** and verify averages are displayed.
17. Enter invalid count (0, 101, letters) and invalid seed; confirm errors without crashing.
18. Confirm Research Lab shows **only configuration and results**, not the interactive maze, and Back returns to Research Lab.
19. Resize the app to 680×600 and confirm all reports remain readable without missing lines or ellipses.

### Distribution / offline

20. Confirm sound starts OFF on a fresh launch; test F2 on and off.
21. Confirm all gameplay and computations work offline *after* Maven dependencies are cached. The first source build may require Internet.

## Local checks already completed (not Maven/JUnit)

- Java 21 compiled the new independent maze benchmarking classes with the existing maze engine.
- 200 reproducible seed values produced **600** path-length comparisons where BFS/Dijkstra/A* agreed.
- Two 100-maze benchmark runs with seed 1983 returned identical aggregated values.
- Invalid sample count above 100 was rejected.

These are targeted core checks, **not** a claim that Maven/JUnit or the desktop UI has passed.

## Release gate

Do not tag this as a verified stable Windows release until the acceptance checklist and actual Maven tests have passed on Windows. The current GitHub beta 1 tag is an older, separate snapshot.
