package lol.ethan.may.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {
    public final String name;
    public String prefix = "/";
    public List<String> aliases = new ArrayList<>();

    public Command(final String name) {
        this.name = name;
    }

    public Command(final String name, final String... aliases) {
        this.name = name;
        this.aliases.addAll(Arrays.asList(aliases));
    }

    private Command() {
        throw new RuntimeException("your command must have a name!");
    }

    public abstract void execute(String[] args);

    public String[] getTabCompletes(final String[] args) {
        return new String[0];
    }

    public String toString() {
        return prefix + name;
    }

    public boolean matches(final String text) {
        if (text.toLowerCase().startsWith(name.toLowerCase())) {
            return true;
        }
        for (final String a : aliases) {
            if (!text.toLowerCase().startsWith(a.toLowerCase())) continue;
            return true;
        }
        return false;
    }
}

