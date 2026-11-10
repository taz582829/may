package lol.ethan.may.commands;

import lol.ethan.may.Entry;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.Config;

import java.awt.*;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("mayconfig", "cfg");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 0) {
            ClickGUI.open();
            return;
        }
        final String arg1 = args[0].trim();
        if ("folder".equalsIgnoreCase(arg1)) {
            try {
                Desktop.getDesktop().open(Entry.FILE_PATH);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("file".equalsIgnoreCase(arg1)) {
            try {
                Desktop.getDesktop().open(Config.getConfigFile());
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

