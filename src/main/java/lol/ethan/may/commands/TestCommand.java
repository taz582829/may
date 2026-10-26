package lol.ethan.may.commands;

import lol.ethan.may.config.ClickGUI;

public class TestCommand extends Command {
    public static boolean debugLogging;

    public TestCommand() {
        super("test");
    }

    @Override
    public void execute(final String[] args) {
        ClickGUI.open();
    }
}

