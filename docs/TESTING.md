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

## Build status

This source distribution is intended for a standard Java 21 / Maven / JavaFX environment. Full Windows JavaFX GUI acceptance testing is not claimed in advance. See the delivery notes in the accompanying chat for what was actually executed in the build environment.
