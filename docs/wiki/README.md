# Publishing the prepared STRATUM-83 Wiki pages

The public GitHub Wiki is stored in a **separate Git repository** from STRATUM-83 source code. This folder contains Markdown prepared for the Wiki; changes here do **not** automatically update the public Wiki.

Pages to publish or revise:

The revised **Grid Zero** page includes the approved Play → Trace → Explain → Apply learning workflow and a link to [the full chapter](../GRID-ZERO-LEARNING.md). The approved [educational principles](../EDUCATIONAL-VISION.md) should be reflected in every future game page.

| Prepared Markdown | Wiki page title |
| --- | --- |
| `Home.md` | Home (replace the default welcome message) |
| `Grid-Zero-AI-Experiment.md` | Grid Zero — Understanding the AI Experiment (update the existing page) |
| `Labyrinth.md` | Labyrinth |
| `Research-Lab.md` | Research Lab |
| `Development-Roadmap.md` | Development Roadmap |

## Manual publication

1. Open the [STRATUM-83 Wiki](https://github.com/elisseosmav/stratum-83/wiki).
2. Edit the existing Home and Grid Zero articles; use New Page for other chapters.
3. Copy the matching Markdown file from this directory into each Wiki page.
4. Review the links after saving. Existing GitHub Wiki page slugs may differ from filenames; use the actual published URLs in Home.
5. Keep copies here synchronized with code changes in future releases.

Only the repository owner should publish changes. No CLI automation or write token is required for manual Wiki editing.

**Release status:** The redesigned UI is a development candidate, not a new published GitHub Release.
