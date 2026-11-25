# STRATUM-83

**Classic Games. Real Algorithms. Practical Engineering.**

A personal, offline Java 21 / JavaFX project. I develop it and make the decisions; there is no project team or formal reporting process.

**Latest unreleased application:** [Games + Algorithms development branch](https://github.com/elisseosmav/stratum-83/tree/feature/clean-games-algorithm-library). The published [Beta 1](https://github.com/elisseosmav/stratum-83/releases/tag/v0.3.0-beta.1) is an older snapshot. Beta 2 has **not** been released.

## Games

**Grid Zero:** Human X versus Minimax O. **Labyrinth:** Navigate a fog-covered maze using arrow keys, then compare your route with the computer's optimal A* path.

The menu also lists Checkers, Blackjack, Chess and Crisis Protocol (last). These four are disabled until built. The **Algorithms** view offers short explanations; it is optional.

## Run the latest version on Windows

Install Java 21 and Maven, then:

```powershell
cd C:\Users\eliss\Projects\STRATUM-83
git clone --branch feature/clean-games-algorithm-library https://github.com/elisseosmav/stratum-83.git stratum-83-clean
cd .\stratum-83-clean
..\apache-maven\bin\mvn.cmd clean test
..\apache-maven\bin\mvn.cmd javafx:run
```

For an existing checkout of this branch, use `git pull --ff-only` instead of cloning again.

**Reference material:** [Project Wiki](https://github.com/elisseosmav/stratum-83/wiki). **My remaining tasks:** [GitHub Issues](https://github.com/elisseosmav/stratum-83/issues).

This branch contains the redesigned application. The Windows JavaFX experience still needs a final hands-on check before release.
