package org.stratum83.ui;

import javafx.application.Application;

public final class DesktopLauncher {
    private DesktopLauncher() { }

    public static void main(String[] args) {
        Application.launch(StratumApp.class, args);
    }
}
