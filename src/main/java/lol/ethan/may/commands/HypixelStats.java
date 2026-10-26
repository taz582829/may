package lol.ethan.may.commands;

import lol.ethan.may.Entry;
import lol.ethan.may.hypixel.HypixelUtils;
import lol.ethan.may.hypixel.api.HypixelAPI;
import lol.ethan.may.hypixel.bedwars.BedwarsUtils;
import lol.ethan.may.hypixel.duels.DuelsUtils;
import lol.ethan.may.utils.MojangAPI;

import java.util.UUID;

public class HypixelStats extends Command {
    public HypixelStats() {
        super("sc");
    }

    @Override
    public void execute(final String[] args) {
        if (1 > args.length) {
            Entry.chat("§cUsage: /sc bw|duels *player* *mode*");
        }
        final String ign = args[1];
        final String subcommand = args[0].toLowerCase();
        MojangAPI.getProfile(ign).thenAccept(profile -> {
            if (null == profile) {
                Entry.chat("§cThis player does not exist!");
                return;
            }
            final UUID id = UUID.fromString(profile.getDashedUUID());
            HypixelAPI.getPlayer(id).thenAccept(res -> {
                if (!res.player.exists()) {
                    Entry.chat("§cThis player has no stats!");
                    return;
                }
                switch (subcommand) {
                    case "bw": {
                        BedwarsUtils.showStats(res.player);
                        break;
                    }
                    case "duels": {
                        final boolean doGamemode;
                        DuelsUtils.DuelsMode mode = null;
                        final boolean bl = doGamemode = 2 < args.length;
                        if (doGamemode) {
                            mode = DuelsUtils.DuelsMode.getModeFromString(args[2].toLowerCase());
                        }
                        DuelsUtils.showDuelsStats(res.player, mode);
                        break;
                    }
                    default: {
                        HypixelUtils.showGeneralStats(res.player);
                    }
                }
            }).exceptionally(ex -> {
                Entry.debug("failed to build stats for " + profile.getDashedUUID() + ": " + ex);
                ex.printStackTrace();
                return null;
            });
        });
    }
}

