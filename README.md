# STRATUM-83

**Development candidate: v0.3.0-beta.2 — Java 21 / JavaFX offline research terminal**

**Classic Games. Real Algorithms. Practical Engineering.**

STRATUM-83 combines two playable games with a separate Research Lab for repeatable classical-AI experiments. It is an independent, non-commercial educational project; no account, cloud service, telemetry or LLM subscription is needed.

**Release status:** These changes live on the [terminal research redesign branch](https://github.com/elisseosmav/stratum-83/tree/feature/grid-zero-learning-lab), **not on `main`**. The [existing published beta](https://github.com/elisseosmav/stratum-83/releases/tag/v0.3.0-beta.1) is a prior snapshot. This candidate is not an official release until the maintainer publishes a new tag.

## The educational experience: Play → Trace → Explain → Apply

Games introduce a problem, but **learning how the algorithm works and when it applies to software engineering is the main purpose**.

- **PLAY:** Play Grid Zero against Minimax using the same AI that generates the trace.
- **TRACE:** After the computer moves, step through recorded board states, Minimax/MAX/MIN outcomes and alpha-beta pruning. See exact candidate move scores, evaluated positions and cutoffs. The UI samples at most 159 early search events plus the final selection and tells you when the trace is truncated.
- **EXPLAIN:** Learn how the score is computed and why the computer chooses a particular move.
- **APPLY:** Choose a retry policy in a small hypothetical worst-case engineering exercise, then see why minimizing maximum loss selects the bounded policy.

The `TRACE`, `EXPLAIN`, and `APPLY` commands are accessible in the terminal, as well as buttons inside Grid Zero. `DUEL` remains a human-versus-human mode. All lessons are local and do not require an LLM.

- [Approved vision and educational principles](docs/EDUCATIONAL-VISION.md)
- [Grid Zero full educational chapter](docs/GRID-ZERO-LEARNING.md)
- [Java 21 CI workflow](.github/workflows/java-ci.yml)

## Play and Research are separate

The main menu has **three options**:

| Menu | Program | Purpose |
| --- | --- | --- |
| 01 | **GRID ZERO** | Play 3×3 tic-tac-toe against perfect-play Minimax |
| 02 | **LABYRINTH** | Human-versus-algorithm challenge: compare your route with BFS |
| 03 | **RESEARCH LAB** | Configure and run experiments without gameplay |

Two-player Grid Zero remains available with `DUEL` in the command line. `HELP` and `EXIT` are **commands**, not main-menu buttons.

The **Research Lab** has two result-only experiments:

1. **Minimax vs Random:** choose a random seed and 1–2000 games. Minimax alternates between X and O; report wins, draws and losses.
2. **Pathfinding Benchmark:** choose a first maze seed and 1–100 maze samples. BFS, Dijkstra and A* solve the **same generated mazes**. Report mean shortest-path length and mean explored cells. The benchmark does **not** show a playable maze.

**Labyrinth Challenge:** reach the goal `G` before seeing the computer's answer. After you finish, the terminal reveals **your moves**, the **BFS shortest route**, **extra moves**, and an **efficiency percentage** (`optimal / your moves × 100`). You can highlight your visited cells or the optimal route, **RETRY SAME MAZE** to improve, or select **NEW MAZE** for the next seed. Invalid moves do not count, but backtracking does. The score is shown only after completion.

`RESEARCH LAB` stays separate: its pathfinding benchmark compares BFS, Dijkstra and A* over one or many seeded mazes without a human player. Both modes share the same Java maze and solver classes.

- [Labyrinth algorithm guide](docs/LABYRINTH.md)
- [Research Lab guide (Wiki-ready)](docs/wiki/Research-Lab.md)
- [Release candidate notes](docs/RELEASE-NOTES-v0.3.0-beta.2.md)
- [Maintainer publishing checklist for GitHub Releases and Wiki](docs/PUBLISHING.md)
- [All updated Wiki-ready pages](docs/wiki/README.md)

The code uses original games and assets; it is not affiliated with or endorsed by any film producer or rights holder.

## Development roadmap and research ideas

STRATUM-83 follows a **public, release-based roadmap** rather than fixed calendar deadlines.

- **[Development Roadmap](docs/DEVELOPMENT-ROADMAP.md)** — approved milestones, current work, future planned releases and acceptance criteria.
- **[Research Backlog](docs/RESEARCH-BACKLOG.md)** — candidate games and algorithms; ideas are not release commitments.
- **[GitHub Milestones](https://github.com/elisseosmav/stratum-83/milestones)** — track work toward v0.2, v0.3 and v0.4.
- **[Educational Wiki](https://github.com/elisseosmav/stratum-83/wiki)** — explanations of the games and their algorithms.
- **[Discussions](https://github.com/elisseosmav/stratum-83/discussions)** — share suggestions and ask questions.

Proposed future games include Checkers, Blackjack and Chess. These are **research ideas only** until reviewed and formally planned.

## Windows setup (Java 21 and Maven)

STRATUM-83 is currently distributed as a **source project**. Building and playing it locally requires **JDK 21** and **Apache Maven 3.9+**. JavaFX is downloaded by Maven, so you do not need to install JavaFX yourself.

### Step 1 — Install Java 21

1. Download [Eclipse Temurin JDK 21 for Windows (official Adoptium)](https://adoptium.net/temurin/releases/?version=21&os=windows&arch=x64&package=jdk). Select **JDK 21**, **Windows x64**, **MSI** (or the architecture matching your computer).
2. Run the MSI installer. The default installation location is usually under `C:\Program Files\Eclipse Adoptium\` (the JDK directory includes its version).
3. If offered, enable **Add to PATH** and **Set JAVA_HOME** during installation.
4. Open a **new PowerShell window** and verify:

   ```powershell
   java -version
   javac -version
   ```

Both commands must report Java 21. If needed, follow the [official Windows Java installation instructions](https://adoptium.net/installation/windows/) to set `JAVA_HOME` to the JDK directory (not `bin`) and add `%JAVA_HOME%\bin` to your Windows `Path`.

### Step 2 — Install Apache Maven

1. Download the **binary ZIP** of Apache Maven 3.9+ from the [official Maven download page](https://maven.apache.org/download.cgi). Do not download the source archive.
2. Extract it to a stable location, such as `C:\Tools\apache-maven-3.9.x\` (replace the version number as appropriate). Maven is unpacked, not installed via an MSI.
3. In Windows search, open **Edit the system environment variables → Environment Variables**. Add the Maven `bin` folder, e.g. `C:\Tools\apache-maven-3.9.x\bin`, to your user `Path`.
4. Open a **new PowerShell window** and verify:

   ```powershell
   mvn -version
   ```

Maven should report version 3.9 or later and **Java 21**. See the [official Maven installation guide](https://maven.apache.org/install.html).

### Step 3 — Download the source code

Option A: clone with Git (requires Git installed):

```powershell
cd "$HOME\Projects"
git clone --branch feature/grid-zero-learning-lab https://github.com/elisseosmav/stratum-83.git stratum-83-learning
cd .\stratum-83-learning
```

Option B: open [STRATUM-83 on GitHub](https://github.com/elisseosmav/stratum-83), select the **feature/grid-zero-learning-lab** branch and choose **Code → Download ZIP**, extract it, and open PowerShell **inside the extracted source directory**.

You **must** run Maven from the directory that contains `pom.xml`, not the Maven `bin` directory. Verify:

```powershell
Test-Path .\pom.xml
```

Expected output: `True`.

### Step 4 — Compile, test and play

From the directory containing `pom.xml`:

```powershell
mvn clean test
mvn javafx:run
```

- `mvn clean test` compiles the sources and runs JUnit tests.
- `mvn javafx:run` starts the resizable STRATUM-83 terminal.
- Alternatively, double-click `test-windows.cmd` or `run-windows.cmd` (requires Maven on `Path`).

### Step 5 — Already have Maven next to the source? (No PATH changes needed)

For this directory structure:

```text
STRATUM-83\
├── apache-maven\
│   └── bin\
│       └── mvn.cmd
└── stratum-83-learning\
    └── pom.xml
```

Use these PowerShell commands:

```powershell
cd "$HOME\Projects\STRATUM-83\stratum-83-learning"
Test-Path .\pom.xml
& "..\apache-maven\bin\mvn.cmd" -version
& "..\apache-maven\bin\mvn.cmd" clean test
& "..\apache-maven\bin\mvn.cmd" javafx:run
```

If your Maven folder is called `apache-maven-3.10.2` instead, replace `apache-maven` in the commands. The PowerShell `&` operator starts an executable at a given path.

### Troubleshooting

| Symptom | Solution |
| --- | --- |
| `No POM in this directory` / `MissingProjectException` | Find the project with `Get-ChildItem -Recurse -Filter pom.xml` and run Maven from that file's parent folder. |
| `mvn` is not recognized | Add Maven's `bin` folder to `Path`, reopen PowerShell, or execute `mvn.cmd` by path. |
| Compiler reports wrong Java version | Check `java -version`, `javac -version` and `mvn -version`. Make sure Maven is using JDK 21. |
| Maven fails while downloading dependencies | Internet access is required on first build for JavaFX, JUnit and Maven plugins. |
| JavaFX fails to launch | Save the console output and report the error with your Java and Maven version information. |

**Offline operation:** The application itself requires no Internet connection. The first Maven build downloads dependencies from Maven Central. Future Maven runs may also require the local Maven cache to contain all dependencies and plugins.

**Not a packaged Windows installer:** This development candidate contains source code only. A self-contained Windows executable is planned for a later release. Full Windows/JavaFX acceptance testing remains pending; see [build notes](docs/BUILD-NOTES.md).

## Linux and macOS

Use JDK 21 and Maven 3.9+, then run `mvn test` and `mvn javafx:run`. A desktop environment and graphics support are required. Windows remains the priority platform; the others are not yet acceptance-tested.

## Terminal commands

| Command | Action |
| --- | --- |
| `PLAY` | Grid Zero against Minimax |
| `TRACE` | Step through the last computer search |
| `EXPLAIN` | Learn the live move's reasoning and Minimax rules |
| `APPLY` | Try a worst-case software engineering exercise |
| `DUEL` | Grid Zero, two local players |
| `MAZE` | Play Labyrinth |
| `RESEARCH` | Open Research Lab |
| `EXPERIMENT` | Open Minimax vs Random experiment directly |
| `BENCHMARK` | Open result-only BFS/Dijkstra/A* benchmark |
| `HELP` | Display command reference |
| `BACK` / `MENU` | Return to the main menu |
| `SOUND ON` / `SOUND OFF` | Toggle sound (off at startup) |
| `EXIT` | Close the terminal |

Main menu uses keys `1`–`3`; Research Lab uses `1`–`2`. Shortcuts: **F1** help, **F2** sound, **Esc** main menu.

## Algorithms and experiment setup

**Grid Zero:** `MinimaxPlayer` performs exhaustive minimax search with alpha-beta pruning; `ExperimentRunner` uses a seeded pseudorandom legal-move opponent. Changing the seed changes the random choices, not the Minimax strategy. No training is involved.

**Labyrinth:** `MazeGenerator` creates a seeded 11×15 connected maze. `MazeSolver` supports unweighted BFS, Dijkstra and Manhattan-heuristic A*. `MazeBenchmarkRunner` executes all three algorithms on the **same set of seeds**, so comparisons are fair. With multiple samples, results are arithmetic means, not total steps or simulated player moves. No elapsed-time benchmark is claimed.

## Code organization

```text
src/main/java/org/stratum83/
  game/       GridZeroGame, Move, MinimaxPlayer, GameResult
  labyrinth/  Maze, MazeGenerator, MazeSolver, PathResult, Position
  research/   ExperimentRunner, ExperimentResult,
              MazeBenchmarkRunner, MazeBenchmarkResult
  ui/         StratumApp, SoundPlayer
src/main/resources/terminal.css
src/test/java/org/stratum83/  JUnit tests
docs/                       Design, testing, roadmap, Wiki-ready pages
```

The game and research engines have no JavaFX dependencies. The UI handles presentation only. All experiments are local and reproducible.

## Development guidelines

- Clear and conventional Java; no speculative abstraction layers.
- Keep game rules separate from the display.
- Prefer ordinary loops and descriptive names.
- Test algorithms and edge cases.
- No artificial complexity or misleading claims of validation.

See [docs/DESIGN.md](docs/DESIGN.md) for the design, [docs/TESTING.md](docs/TESTING.md) for the acceptance checklist, and [docs/BUILD-NOTES.md](docs/BUILD-NOTES.md) for verified and unverified build steps.

## Scope and limitations

- This is a development branch, **not** a verified native Windows executable.
- The latest UI changes still require Maven/JUnit and Windows JavaFX acceptance testing.
- A native `.exe` and CI pipeline are planned, but are not included in this candidate.
- The Labyrinth research view intentionally reports statistics without displaying a maze.
- Checkers, Blackjack, Chess and Crisis Protocol remain future concepts or milestones, not implemented games.
- STRATUM-83 has an original retro aesthetic; it does not copy movie visuals or dialogue.

## Licence

Source code: **Apache License 2.0**. See [LICENSE](LICENSE). The copyright notice refers to the STRATUM-83 contributors.
