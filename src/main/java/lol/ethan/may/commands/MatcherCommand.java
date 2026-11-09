package lol.ethan.may.commands;

import lol.ethan.may.Entry;
import lol.ethan.may.housing.HouseMatcher;
import lol.ethan.may.hypixel.modapi.LocationData;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.List;

public class MatcherCommand extends Command {
    public MatcherCommand() { super("matcher", "hm"); }


    @Override
    public void execute(final String[] args) {
        final List<String> targets = HouseMatcher.getTargets();
        if (args.length == 0) {
            targets.add(LocationData.getServer());
            Entry.chat(Entry.PREFIX + "Added §3" + LocationData.getServer() + " §fto the target list!");
            return;
        }
        final String arg = args[0].trim().toLowerCase();
        switch (arg) {
            case "start": {
                HouseMatcher.toggle(true);
                Entry.chat(Entry.PREFIX + "Starting with §3" + targets.size() + " §ftargets!");
                break;
            }
            case "stop": {
                HouseMatcher.toggle(false);
                break;
            }
            case "list": {
                final StringBuilder builder = new StringBuilder();
                builder.append(Entry.PREFIX).append("Targets:");
                for (final String target : targets) {
                    builder.append("\n").append(Entry.SHORT_PREFIX).append(target);
                }
                Entry.chat(builder.toString());
                break;
            }
            case "clear": {
                Entry.chat(Entry.PREFIX + "Cleared §3" + targets.size() + " §ftargets!");
                targets.clear();
                break;
            }
            case "addserver":
            case "add": {
                Entry.chat(Entry.PREFIX + "Added §3" + args[1] + " §fto the target list!");
                targets.add(args[1]);
                break;
            }
            case "remove": {
                targets.remove(args[1]);
                break;
            }
            case "exclude": {
                final String excluded = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                targets.add(excluded);
                if (HouseMatcher.getExcluded().contains(excluded)) {
                    Entry.chat("unexcluded " + excluded + "!");
                    HouseMatcher.removeExcludedHouse(excluded.toLowerCase());
                    return;
                }
                HouseMatcher.addExcludedHouse(excluded);
                Entry.chat("excluded " + excluded + "!");

                break;
            }
        }
    }

    @Override
    public String[] getTabCompletes(final String[] args) {
        return new String[]{ "start", "stop", "list", "clear", "add", "remove" };
    }
}
