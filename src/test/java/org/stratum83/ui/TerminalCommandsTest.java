package org.stratum83.ui;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TerminalCommandsTest {
    @Test
    void resolvesDirectoryAndHelpCommands() {
        assertEquals(TerminalCommands.Action.GAMES, TerminalCommands.parse(" dir /games ").action());
        assertEquals(TerminalCommands.Action.GAMES, TerminalCommands.parse("cls").action());
        assertEquals(TerminalCommands.Action.ALGORITHMS, TerminalCommands.parse("ALG").action());
        assertEquals(TerminalCommands.Action.HELP, TerminalCommands.parse("?").action());
        assertEquals(TerminalCommands.Action.SOUND, TerminalCommands.parse("SOUND").action());
        assertEquals(TerminalCommands.Action.EXIT, TerminalCommands.parse("QUIT").action());
    }

    @Test
    void selectsAllSixGamesFromCommandLine() {
        for (int n = 1; n <= 7; n++) {
            assertEquals(n, TerminalCommands.parse("0" + n).gameNumber());
            assertEquals(n, TerminalCommands.parse("run " + n).gameNumber());
            assertEquals(n, TerminalCommands.parse("pLaY " + n).gameNumber());
            assertEquals(TerminalCommands.Action.OPEN_GAME,
                    TerminalCommands.parse(Integer.toString(n)).action());
        }
    }

    @Test
    void rejectsUnknownAndUnsafeInputs() {
        for (String input : new String[]{"", " ", "RM -RF", "DEL", "08", "RUN 9", "PLAY 1; EXIT"}) {
            assertEquals(TerminalCommands.Action.INVALID, TerminalCommands.parse(input).action());
        }
        assertEquals(TerminalCommands.Action.INVALID, TerminalCommands.parse(null).action());
        assertEquals(0, TerminalCommands.parse("hello").gameNumber());
    }
}
