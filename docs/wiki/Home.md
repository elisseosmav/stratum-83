# STRATUM-83 — Classic Games. Real Algorithms. Practical Engineering.

Welcome to **STRATUM-83**, an independent, non-commercial, offline Java research terminal.

**Play → Trace → Explain → Apply.** The application separates interactive games from reproducible research so that you can learn what happens behind every decision.

## Game catalog

The main menu contains **six game concepts** connected to fundamental AI algorithms:

| Game | Algorithm | Availability |
| --- | --- | --- |
| Grid Zero | Minimax / alpha-beta | Playable |
| Labyrinth | BFS / Dijkstra / A* | Playable |
| Crisis Protocol | Rule-based agents | Planned |
| Checkers | Minimax / heuristic evaluation | Proposed |
| Blackjack | Probability / Monte Carlo | Proposed |
| Chess | Alpha-beta / search heuristics | Proposed |

Only playable titles can be selected. Planned and proposed titles are shown as future roadmap ideas, not released games. For experiments, enter `RESEARCH` at the terminal.

## Play

- **Grid Zero** — play classic tic-tac-toe, choose a move from your actual game history, discover how Minimax chose its reply, and explore real-world worst-case decisions. Two players can use `DUEL`.
- **Labyrinth** — play a seeded maze, visualize actual BFS/Dijkstra/A* graph search, understand their differences and solve a weighted software-routing exercise.

## Research Lab

- **Minimax vs Random:** choose a seed and game count; compare wins, draws and losses.
- **Pathfinding Benchmark:** choose a seed and maze count; compare BFS, Dijkstra and A* using equal inputs and report average shortest-path steps and explored cells.

[Read the Research Lab explanation](Research-Lab)

## Educational chapters

- [Grid Zero — Understanding the AI Experiment](Grid-Zero-AI-Experiment)
- [Labyrinth — Game and Algorithms](Labyrinth)
- [Development Roadmap](Development-Roadmap)

## Get the source

[STRATUM-83 GitHub repository](https://github.com/elisseosmav/stratum-83) · [Installation instructions](https://github.com/elisseosmav/stratum-83/blob/feature/retro-ai-game-catalog/README.md) · [Community discussions](https://github.com/elisseosmav/stratum-83/discussions)

*Note: the revised Play / Research interface is a **development candidate** until a new release is tested and published. The current beta 1 release is an earlier snapshot.*
