package lol.ethan.may.hypixel.duels;

import lol.ethan.may.Entry;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S3EPacketTeams;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DuelsRankInfo {
    public List<Rank> rankList = new ArrayList<>();
    Pattern playerCountPattern = Pattern.compile("^Players: (\\d+)/(\\d+)$");
    PlayerCount playerCount;

    @SubscribeEvent
    public void onPacket(final PacketEvent.Receive event) {
        if (!DuelsUtils.isInDuels()) return;
        if (event.getPacket() instanceof S38PacketPlayerListItem) {
            final S38PacketPlayerListItem packet = (S38PacketPlayerListItem) event.getPacket();
            if (packet.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                for (final S38PacketPlayerListItem.AddPlayerData data : packet.getEntries()) {
                    if (!data.getProfile().getName().startsWith("§k")) continue;
                    final Rank rankProfile = this.getRankByIGN("§r" + data.getProfile().getName());
                    if (rankProfile != null) {
                        rankProfile.uuid = data.getProfile().getId().toString();
                    } else {
                        this.updateRankData(data.getProfile().getId().toString(), "§r" + data.getProfile().getName(), "", -1);
                    }
                }
            } else if (packet.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
                for (final S38PacketPlayerListItem.AddPlayerData data : packet.getEntries()) {

                    final Rank rankProfile = this.getRankByUUID(data.getProfile().getId().toString());
                    if (rankProfile != null) {
                        this.rankList.remove(rankProfile);
                    }
                }
            }
        } else if (event.getPacket() instanceof S0CPacketSpawnPlayer) {
            final S0CPacketSpawnPlayer packet = (S0CPacketSpawnPlayer) event.getPacket();
            final Rank rankProfile = this.getRankByUUID(packet.getPlayer().toString());
            if (rankProfile != null) {
                rankProfile.entityId = packet.getEntityID();
            }
        } else if (event.getPacket() instanceof S3EPacketTeams) {
            final S3EPacketTeams packet = (S3EPacketTeams) event.getPacket();
            final PlayerCount playerCount = this.parsePlayerCount(packet);
            if (playerCount != null) {
                this.playerCount = playerCount;
            }

            if (packet.getPlayers().size() != 0 && packet.getName().startsWith("§")) {
                for (final String ign : packet.getPlayers()) {
                    if (ign.startsWith("§r§k")) {
                        final Rank rankProfile = this.getRankByIGN(ign);
                        if (rankProfile != null) {
                            rankProfile.rank = packet.getName();
                        } else {
                            this.rankList.add(new Rank("", ign, packet.getName(), -1));
                        }
                        String msg = Entry.PREFIX + "You are queuing a " + Rank.parseRank(packet.getName());
                        if (playerCount != null) {
                            final int nicks = Math.max(0, playerCount.current - this.rankList.size());

                            if (nicks == 1) {
                                msg += " §c[N]";
                            }
                        }
                        Entry.chat(msg);
                    }
                }
            }
        } else if (event.getPacket() instanceof S13PacketDestroyEntities) {
            final S13PacketDestroyEntities packet = (S13PacketDestroyEntities) event.getPacket();
            this.rankList.removeIf(rank -> ArrayUtils.contains(packet.getEntityIDs(), rank.entityId));
        }
    }

    private PlayerCount parsePlayerCount(final S3EPacketTeams packet) {
        if (packet.getPrefix() == null || packet.getSuffix() == null) return null;
        final String text = packet.getPrefix() + packet.getSuffix();
        final String clean = text.replaceAll("[^a-zA-Z0-9\\s:§]", "");
        final Matcher matcher = this.playerCountPattern.matcher(clean);
        if (matcher.find()) {
            final int current = Integer.parseInt(matcher.group(1));
            final int max = Integer.parseInt(matcher.group(2));
            return new PlayerCount(current, max);
        }
        return null;
    }
    public Rank getRankByIGN(final String ign) {
        for (final Rank rank : this.rankList) {
            if (rank.ign.equals(ign)) return rank;
        }
        return null;
    }
    public Rank getRankByUUID(final String uuid) {
        for (final Rank rank : this.rankList) {
            if (rank.uuid.equals(uuid)) return rank;
        }
        return null;
    }

    private void updateRankData(final String uuid, final String IGN, final String rank, final int entityId) {
        for (final Rank data: this.rankList) {
            if (data.uuid.equals(uuid)) {
                data.ign = IGN;
                data.rank = rank;
                data.entityId = entityId;
                return;
            }
        }
        this.rankList.add(new Rank(uuid, IGN, rank, entityId));
    }
    public static class Rank {
        public String uuid;
        public String ign;
        public String rank;
        public int entityId;

        public Rank(final String uuid, final String IGN, final String rank, final int entityId) {
            this.uuid = uuid;
            ign = IGN;
            this.rank = rank;
            this.entityId = entityId;
        }

        public static String parseRank(final String color) {
            if (color.equals("§7")) return "§7Non";
            else if (color.equals("§a")) return "§aVIP§f/§aVIP§6+";
            else if (color.equals("§b")) return "§bMVP§f/§bMVP§c+";
            else if (color.equals("§6")) return "§6MVP§c++";
            else if (color.equals("§c")) return "§cYoutube§f/§cStaff";
            else return color + "?";
        }
    }
    public static class PlayerCount {
        public int current;
        public int max;

        public PlayerCount(final int current, final int max) {
            this.current = current;
            this.max = max;
        }
    }
}
