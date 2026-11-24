# Test and Acceptance Checklist

**Candidate:** STRATUM-83 v0.3.0-beta.2 on `feature/labyrinth-learning-lab`

## Grid Zero educational acceptance — Play / Trace / Explain / Apply

### Automated test expectations

- `MinimaxAnalysisTest` verifies that the trace's chosen move is **exactly** the actual Minimax move.
- The trace must not mutate the game state. Root move scores must be highest for the chosen move, and the trace must include the final `CHOSEN` event.
- `WorstCasePolicyTest` verifies that bounded retries win with the hypothetical cost matrix and that ties and invalid policies are handled.
- Run the full test suite with `mvn clean test`; GitHub Actions should also run Maven tests on Linux and Windows when enabled.

### Manual Windows/JavaFX validation

1. Open **Grid Zero**; confirm you can still play X and see computer O marks correctly after a move.
2. Select **TRACE** after the computer has moved. Confirm that **NEXT** and **PREVIOUS** display actual X/O board states with `.` for empty squares, and that the event name and depth change.
3. Confirm the trace shows each legal root move and its score, the actual chosen move, evaluated positions and cutoff count. If truncated, confirm the screen clearly says that only early events and the final choice were recorded.
4. In **EXPLAIN**, confirm scoring rules and the last actual decision are readable. Open `TRACE` using a terminal command and return to the **same game without resetting it**.
5. In **APPLY**, choose *Aggressive retries* and *Bounded retries*. With toy costs (1/9) and (3/4), the worst-case policy must favor **Bounded retries**. The screen must say the costs are illustrative.
6. Type `TRACE` before the first AI move and verify a helpful message rather than a crash.
7. Type `EXPLAIN` or `APPLY` from the main menu; the educational screen must be usable even without an active match, and Back must return to the menu.
8. Verify Help lists `TRACE`, `EXPLAIN`, and `APPLY`. The game buttons must wrap or remain reachable at 680×600.
9. After completing a match, ensure both X/O marks remain visible and the last computer decision still has a trace.
10. Verify `DUEL`, `MAZE`, and Research Lab still work, with no changes to their underlying rules.

**Status:** [Java 21 Maven/JUnit CI passed on Linux and Windows](https://github.com/elisseosmav/stratum-83/actions/runs/45471437228). The **manual Windows JavaFX acceptance checklist remains pending**.

## Automated tests

In the directory containing `pom.xml`, run:

```powershell
mvn clean test
```

JUnit tests cover Grid Zero rules, Minimax strategy, reproducible Grid Zero experiments, seeded maze generation, BFS/Dijkstra/A* shortest paths, the multi-maze benchmark and the new `MazeChallenge` route/score model. **Java 21 Maven/JUnit tests passed in GitHub Actions on windows-latest and ubuntu-latest; local graphical acceptance testing is still pending.**

## Windows JavaFX acceptance checklist

### Main menu and terminal

1. Launch `mvn javafx:run`; confirm there are **exactly three main menu buttons**, in order: Grid Zero, Labyrinth, Research Lab.
2. Confirm **Help**, **Exit**, and **Two Players** are **not main-menu buttons**.
3. Click Grid Zero; confirm Minimax mode. Type `DUEL`; confirm two-player mode.
4. Click Labyrinth; verify the playable maze has `P`, `G`, walls, and buttons **TRACE / EXPLAIN / APPLY**. The separate Research Lab is still results-only.
5. Click Research Lab; verify just two options: Minimax vs Random and Pathfinding Benchmark, plus Back.
6. Verify typed commands `PLAY`, `DUEL`, `MAZE`, `RESEARCH`, `EXPERIMENT`, `BENCHMARK`, `HELP`, `MENU`, `BACK`, `EXIT`. F1 opens Help, F2 toggles sound, Esc returns to main menu.
7. Numbered entry `1` in the main menu starts Grid Zero; numbered entry `1` in Research Lab opens Minimax experiment.

### Grid Zero

8. Complete Minimax and two-player games, verifying X/O marks remain visible even on occupied squares and after a win/draw (Issue #10 regression).
9. New Game clears the board, while Main Menu returns to program selection.

### Labyrinth play

10. Use arrow keys and click adjacent open cells. Valid moves increment the score; walls and nonadjacent clicks must **not** change it.
11. Before reaching G, verify **no optimal path, score/efficiency or route-reveal buttons** are visible.
12. Finish the maze and verify **YOUR MOVES**, **BFS OPTIMAL**, **EXTRA MOVES**, and **EFFICIENCY** (optimal / actual × 100). A shortest route must score 100%.
13. Click **SHOW MY VISITED CELLS**, **SHOW OPTIMAL PATH**, and **HIDE PATH** after completion. Overlays must be visible, visually distinct and must not obscure G. My visited cells reflect all visited positions, not visit order.
14. Verify arrows/clicks cannot move after completion.
15. Click **RETRY SAME MAZE**; confirm the same seed/layout and zero moves with no route revealed.
16. Click **NEW MAZE**; confirm seed advances and a different layout is generated.
17. Backtrack deliberately in one attempt; ensure those steps increase actual moves and reduce efficiency if the final route is longer than BFS's shortest path.

### Labyrinth educational acceptance — Play / Trace / Explain / Apply

1. Open a maze, make at least three human moves, and record the count and seed.
2. Select **TRACE**. Verify the display offers **BFS**, **DIJKSTRA**, and **A*** as selectable algorithms.
3. Use **PREVIOUS** and **NEXT** to inspect real current (`C`), frontier (`F`), explored (`·`) and goal cells. Verify the explored-node count changes at each expansion. Verify the UI displays `g`, `h`, and `f` and explains how these values are used.
4. Select **FINAL RESULT**. Confirm a shortest path (`*`) appears. It must match the selected algorithm's shortest-path length in Research Lab when the seed is the same.
5. Change from **BFS** to **DIJKSTRA** and **A***; verify the algorithm label, expansion count and trace are updated. Manhattan `h` must be nonzero for A* on a nontrivial start.
6. Select **EXPLAIN**. Verify the differences between unweighted BFS, nonnegative weighted Dijkstra and admissible-heuristic A* are explained, including the perfect-maze / unit-cost limitations.
7. Select **APPLY**. With fictional network costs API → DATABASE 8 and API → GATEWAY → DATABASE 2+2, select both route options. Confirm the direct path is **1 hop/8 cost** and Dijkstra picks **2 hops/4 cost**. The values must be labeled illustrative.
8. Return to the maze. Verify the same seed, player position and move count were preserved. **Arrow keys must not move the player while TRACE/EXPLAIN/APPLY screens are visible.**
9. Test terminal commands `TRACE`, `EXPLAIN`, `APPLY` while in Labyrinth, and `MTRACE`, `MEXPLAIN`, `MAPPLY`, `BFS`, `DIJKSTRA`, `ASTAR`. Verify Grid Zero still routes its own commands correctly.
10. Resize to **680×600**. Verify scrolling, path colors and captions remain readable. Retest Grid Zero marks, Labyrinth scoring and Research Lab to detect regressions.

**Automatic tests:** `MazeSearchTraceTest` checks trace consistency across 30 seeded mazes, path lengths, snapshots and immutability. `ServiceRouteExampleTest` checks BFS hop minimization versus weighted Dijkstra costs. GitHub Actions' Linux/Windows Java 21 builds passed; manual Windows GUI acceptance is still pending.

### Research Lab

18. Open Minimax vs Random; run default **seed 1983 / 200 games**; wins+draws+losses must equal 200, losses should be 0.
19. Rerun with the same seed and count; outcomes must match. Try different seed and counts, including 1 and 2000.
20. Enter invalid seed or game count (zero, 2001, letters); an error should display without crashing.
21. Open Pathfinding Benchmark; enter seed 1983, count 1. Confirm **BFS, DIJKSTRA, A_STAR** appear completely and all report the same average shortest-path steps.
22. Run the same benchmark twice and compare results, then try **100 mazes** and verify averages are displayed.
23. Enter invalid count (0, 101, letters) and invalid seed; confirm errors without crashing.
24. Confirm Research Lab shows **only configuration and results**, not the interactive maze, and Back returns to Research Lab.
25. Resize the app to 680×600 and confirm all reports remain readable without missing lines or ellipses.

### Distribution / offline

26. Confirm sound starts OFF on a fresh launch; test F2 on and off.
27. Confirm all gameplay and computations work offline *after* Maven dependencies are cached. The first source build may require Internet.

## Local checks already completed (not Maven/JUnit)

- Java 21 compiled the new independent maze benchmarking classes with the existing maze engine.
- 200 reproducible seed values produced **600** path-length comparisons where BFS/Dijkstra/A* agreed.
- Two 100-maze benchmark runs with seed 1983 returned identical aggregated values.
- Invalid sample count above 100 was rejected.

These are targeted core checks, **not** a claim that Maven/JUnit or the desktop UI has passed.

## Release gate

Do not tag this as a verified stable Windows release until the acceptance checklist and actual Maven tests have passed on Windows. The current GitHub beta 1 tag is an older, separate snapshot.

## Independent Java 21 core verification (human challenge)

The new `MazeChallenge` model compiled with Java 21. It was exercised on 200 seeded 11×15 mazes, following the BFS optimal route each time, and achieved 100% efficiency. Additional checks covered backtracking (6 human moves versus a 4-move optimum, 66.7%), illegal steps, finished-game locking and a start-equals-goal case. This was **not a Maven/JUnit or JavaFX acceptance run**.
