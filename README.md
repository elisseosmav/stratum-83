# STRATUM-83

**Classic Games. Real Algorithms.**

STRATUM-83 is an offline Java desktop collection of seven human-versus-computer games. Its black-and-green interface takes inspiration from 1980s DOS terminals, with a blinking cursor, a command prompt, and an optional Algorithms view that explains how each opponent or challenge works.

## Play on Windows

**You do not need to install Java or Maven to use the portable Windows application.**

1. Open the [Windows desktop executable builds](https://github.com/elisseosmav/stratum-83/actions/workflows/windows-app.yml).
2. Select the latest **successful** run on the **main** branch.
3. Under **Artifacts**, download **STRATUM-83-Windows-Portable** (GitHub sign-in may be required).
4. Extract the downloaded artifact ZIP. It contains a second file, `STRATUM-83-Windows-Portable.zip`; extract that too.
5. Open the resulting `STRATUM-83` folder and double-click **`STRATUM-83.exe`**. Keep the other files and the `runtime` folder alongside the executable.
6. For easy access, create a desktop shortcut to `STRATUM-83.exe`.

The application is a **portable Windows app image**, not a signed installer or a single-file executable. GitHub builds it with a bundled Java 21 runtime and JavaFX dependencies. Windows may display an unrecognized-publisher warning because it is not code-signed. Verify that the package came from this repository before running it.



## Games

| No. | Game | What you play against | Algorithms |
| --- | --- | --- | --- |
| 01 | **Grid Zero** | Computer in a 3×3 X/O game | Minimax, alpha-beta pruning |
| 02 | **Labyrinth** | A fog-of-war maze; compare your route with the computer | A* search, maze generation, visibility |
| 03 | **Checkers** | Computer in American checkers | Minimax, heuristic evaluation |
| 04 | **Blackjack** | Computer dealer; no real-money betting | Card probability, Monte Carlo simulation |
| 05 | **Chess** | Computer playing Black | Minimax, alpha-beta pruning, legal move search |
| 06 | **Crisis Protocol** | Rule-based crisis agent while you direct response teams | Greedy target selection, rule-based agents |
| 07 | **Naval Command** | Computer fleet in a Battleship-style match | Random search, hunt-and-target, probability density |

All seven entries are playable in **GAMES**. The separate **ALGORITHMS** view provides short explanations of the techniques used. These are casual challenges, not competitive-grade game engines.

### Naval Command

Deploy five ships on a 10×10 board, manually or with **AUTO DEPLOY**, then fire at enemy coordinates. Choose **Easy** (random targeting), **Normal** (hunt-and-target), or **Hard** (probability-density analysis). The computer uses observed shots rather than reading your hidden fleet positions. The game also provides an end-of-match AI analysis view.

## DOS-style controls

The main menu accepts both mouse input and typed commands. Click the command prompt, type a command, and press **Enter**. The prompt uses a blinking cursor.

| Command | Action |
| --- | --- |
| `DIR`, `GAMES`, `MENU` | Show all games |
| `1` … `7` | Launch a game |
| `RUN 7`, `PLAY 7` | Launch a game by number |
| `ALG`, `ALGORITHMS` | Open the Algorithms view |
| `HELP`, `?` | Show available commands |
| `SOUND` | Toggle audio |
| `CLS`, `CLEAR` | Return to the Games menu |
| `EXIT`, `QUIT` | Close the application |

**Esc** returns to GAMES and **F2** toggles audio. Labyrinth uses the **arrow keys** for movement; board games also support mouse selection.

While in **Naval Command**, the prompt accepts additional commands:

| Command | Action |
| --- | --- |
| `AUTO` | Finish deploying your fleet automatically |
| `PLACE CARRIER B2 H` | Place the next ship, starting at B2, horizontally (`H` or `V`) |
| `FIRE D7` | Fire at D7 |
| `STATUS` | Show current fleet counts |
| `RADAR` | Refresh the naval view |
| `NEW` | Restart Naval Command at the current difficulty |
| `HELP` | Show Naval Command rules |
| `EXIT` | Return to GAMES |

Ships must be deployed in fleet order: Carrier, Battleship, Cruiser, Submarine, Destroyer.

## Build from source

Development requires **JDK 21** and **Apache Maven**.

Run the tests:

```powershell
mvn clean test
```

Run the JavaFX desktop app:

```powershell
mvn javafx:run
```

On Windows, you can use `run-windows.cmd` and `test-windows.cmd` instead if Maven is available. The portable Windows build above is the simpler option for ordinary players.

The [Java CI workflow](https://github.com/elisseosmav/stratum-83/actions/workflows/java-ci.yml) runs Maven tests on Windows and Ubuntu. The [Windows packaging workflow](https://github.com/elisseosmav/stratum-83/actions/workflows/windows-app.yml) creates the downloadable app with `jpackage`.



Project documentation is kept concise here and, where published, in the [GitHub Wiki](https://github.com/elisseosmav/stratum-83/wiki).
