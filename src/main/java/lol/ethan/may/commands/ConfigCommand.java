package lol.ethan.may.commands;

import lol.ethan.may.config.ClickGUI;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("config", "cfg");
    }

    @Override
    public void execute(final String[] args) {
        ClickGUI.open();
    }
}

