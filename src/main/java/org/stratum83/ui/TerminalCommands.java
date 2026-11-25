package org.stratum83.ui;

import java.util.Locale;

public final class TerminalCommands {
    public enum Action {
        OPEN_GAME, GAMES, ALGORITHMS, HELP, SOUND, EXIT, INVALID
    }

    public record Command(Action action, int gameNumber) {
    }

    private TerminalCommands() {
    }

    public static Command parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return new Command(Action.INVALID, 0);
        }
        String command = raw.trim().toUpperCase(Locale.ROOT).replaceAll("\\s+", " ");
        return switch (command) {
            case "DIR", "DIR /GAMES", "GAMES", "MENU", "BACK", "CLS", "CLEAR" ->
                    new Command(Action.GAMES, 0);
            case "ALG", "ALGORITHMS", "DIR /ALGORITHMS" ->
                    new Command(Action.ALGORITHMS, 0);
            case "HELP", "?", "COMMANDS" -> new Command(Action.HELP, 0);
            case "SOUND", "SOUND TOGGLE" -> new Command(Action.SOUND, 0);
            case "EXIT", "QUIT" -> new Command(Action.EXIT, 0);
            default -> parseGame(command);
        };
    }

    private static Command parseGame(String command) {
        String number = command;
        if (number.startsWith("PLAY ")) number = number.substring(5);
        else if (number.startsWith("RUN ")) number = number.substring(4);
        if (number.matches("0?[1-7]")) {
            return new Command(Action.OPEN_GAME, Integer.parseInt(number));
        }
        return new Command(Action.INVALID, 0);
    }
}
