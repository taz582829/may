package lol.ethan.may.commands;

import lol.ethan.may.config.ClickGUI;

public class TestCommand extends Command {
    public static boolean debugLogging = false;

    public TestCommand() {
        super("test");
    }

    @Override
    public void execute(String[] args) {
        ClickGUI.open();
    }
}

