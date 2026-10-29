package lol.ethan.may.commands;

import lol.ethan.may.Entry;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.Config;

import java.awt.*;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("config", "cfg");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 0) {
            ClickGUI.open();
            return;
        }
        String arg1 = args[0].trim();
        if (arg1.equalsIgnoreCase("folder")) {
            try {
                Desktop.getDesktop().open(Entry.FILE_PATH);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (arg1.equalsIgnoreCase("file")) {
            try {
                Desktop.getDesktop().open(Config.getConfigFile());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

