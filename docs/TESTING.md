# Test and Acceptance Checklist

## Automated JUnit tests

Run from the project directory:

```powershell
mvn test
```

Tests cover:

- Empty board and first-player state.
- Player alternation and occupied-square handling.
- Horizontal, vertical and diagonal winning lines.
- Draw detection and finished-game handling.
- Independent copies used for search.
- Immediate Minimax win and block moves.
- Non-mutation of the board by Minimax.
- Fixed-seed experiment reproducibility and never-losing AI.

## Manual Windows desktop checks

These checks are still required for a complete Windows release:

1. Launch using `mvn javafx:run` on Windows with JDK 21.
2. Resize the window and confirm menu, board and command bar remain usable.
3. Start `PLAY`; select a square and observe the computer response.
4. Complete games yielding an X win, O win and draw where applicable.
5. Confirm completed games cannot accept further moves.
6. Start `DUEL` and make alternating X/O moves.
7. Start `EXPERIMENT`; confirm 200 games finish and counts sum to 200.
8. Re-run the experiment and check equal results with the same seed.
9. Check sound is silent at startup. Toggle F2 and verify it can be disabled again.
10. Use the text commands and keyboard shortcuts in the README.
11. Disconnect Internet and run with all Maven dependencies already cached.

## Regression test: Issue #10 — Grid Zero marks on Windows

This visual check is required because the issue concerns JavaFX rendering, not the game rules. The first attempt changed disabled-button handling but did not resolve the problem on Windows. The second attempt displays X/O in separate Label nodes above the clickable square buttons.

1. Start `PLAY` and click the top-left square (row 1, column 1).
2. Confirm a high-contrast **X** appears immediately, followed by a visible **O** in the computer's square.
3. Verify both marks remain visible after the square is no longer selectable. The occupied squares must not accept further moves. Confirm the marks are centered above the squares and are not clipped or covered.
4. Play until the game ends; ensure all X/O marks remain visible after a win or draw.
5. Select `NEW GAME`; confirm the board clears and the empty squares are selectable again.
6. Select `DUEL`; verify X and O both appear and remain visible after alternating moves.
7. Resize the window and test the same behavior with sound both OFF and ON.
8. Record the JDK, JavaFX and Windows versions. Attach a screenshot **after making moves** if the issue persists.

**Expected:** No missing or faded marks, and no change to move legality or Minimax behavior.

This regression check still needs to be performed on an actual Windows/JavaFX desktop before Issue #10 is closed.

## Build status

This source distribution is intended for a standard Java 21 / Maven / JavaFX environment. Full Windows JavaFX GUI acceptance testing is not claimed in advance. See the delivery notes in the accompanying chat for what was actually executed in the build environment.

## Labyrinth acceptance checks (v0.3 preview)

1. Start `MAZE`; confirm the board contains walls, P and G.
2. Use the arrow keys to move one step. Click an adjacent open cell to move one step.
3. Attempt to cross a wall or click a distant cell; confirm no movement occurs.
4. Select BFS, Dijkstra and A*; verify all paths reach G and the step counts agree.
5. Select COMPARE; confirm all three algorithms show shortest-path steps and explored-cell counts.
6. Select NEW MAZE; confirm the seed changes and the previous path is cleared.
7. Complete a maze; confirm the status announces completion and blocks further movement.
8. Return to Grid Zero and confirm X and O remain visible after moves (Issue #10 fix).
9. Resize the window to 680×600 and ensure the Labyrinth UI remains usable.
