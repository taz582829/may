package lol.ethan.may.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {
    public final String name;
    public String prefix = "/";
    public List<String> aliases = new ArrayList<>();

    public Command(String name) {
        this.name = name;
    }

    public Command(String name, String... aliases) {
        this.name = name;
        this.aliases.addAll(Arrays.asList(aliases));
    }

    private Command() {
        throw new RuntimeException("your command must have a name!");
    }

    public abstract void execute(String[] var1);

    public String[] getTabCompletes(String[] args) {
        return new String[0];
    }

    public String toString() {
        return this.prefix + this.name;
    }

    public boolean matches(String text) {
        if (text.toLowerCase().startsWith(this.name.toLowerCase())) {
            return true;
        }
        for (String a : this.aliases) {
            if (!text.toLowerCase().startsWith(a.toLowerCase())) continue;
            return true;
        }
        return false;
    }
}

