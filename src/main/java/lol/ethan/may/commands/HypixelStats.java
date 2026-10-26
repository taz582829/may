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
    public void execute(String[] args) {
        if (args.length < 1) {
            Entry.chat("§cUsage: /sc bw|duels *player* *mode*");
        }
        String ign = args[1];
        String subcommand = args[0].toLowerCase();
        MojangAPI.getProfile(ign).thenAccept(profile -> {
            if (profile == null) {
                Entry.chat("§cThis player does not exist!");
                return;
            }
            UUID id = UUID.fromString(profile.getDashedUUID());
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
                        boolean doGamemode;
                        DuelsUtils.DuelsMode mode = null;
                        boolean bl = doGamemode = args.length > 2;
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

