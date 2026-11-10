package lol.ethan.may.commands;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class Command {
    protected final Minecraft mc = Minecraft.getMinecraft();
    public final String name;
    public String prefix = "/";
    public List<String> aliases = new ArrayList<>();

    protected Command(final String name) {
        this.name = name;
    }

    protected Command(final String name, final String... aliases) {
        this.name = name;
        this.aliases.add(name);
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
        for (final String cmd : aliases) {
            if (cmd.equalsIgnoreCase(text)) return true;
        }
        return false;
    }
    public Stream<String> matching(final String text) {
        return aliases.stream().filter(a -> a.toLowerCase().startsWith(text.toLowerCase()));
    }
}

