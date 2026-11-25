# STRATUM-83 — Product Vision

**Classic Games. Real Algorithms. Practical Engineering.**

STRATUM-83 is an original, offline, non-commercial Java 21 retro game collection. Its primary experience is **playing against a computer**.

## The two screens

**GAMES** offers classic titles. The user plays immediately, with clear controls and no required tutorial. **ALGORITHMS** is a separate reference library with a few plain-language sentences about how the relevant algorithms work and where their ideas are useful.

The program must **not** force interactive traces, search trees, history replays, engineering quizzes or experimental reports between the user and a game.

## Current games

**Grid Zero** pairs a human X player against a Minimax O computer. **Labyrinth** pairs a human navigator (arrow keys and limited vision) with an A* shortest-path baseline. The maze's fog is not artificial intelligence; it is a line-of-sight visibility algorithm used to create the human challenge.

Future titles—Crisis Protocol, Checkers, Blackjack and Chess—remain clearly marked unimplemented until they are tested.

## Principles

- Games must be fun and understandable before any educational extras.
- Be accurate about which algorithm the computer actually uses.
- Explain algorithms with minimal, readable text in the separate view.
- Prefer original assets, simple Java, local execution and tests.
- Do not claim a human can beat an optimal shortest path; the challenge is navigating without the full map.
- Maintain sole maintainer control over code merges and releases.

The historic experimental modules can be kept in source until cleanly retired, but are not part of the current application interface.
