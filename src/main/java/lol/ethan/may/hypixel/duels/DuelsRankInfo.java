package lol.ethan.may.hypixel.duels;

import lol.ethan.may.Entry;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S3EPacketTeams;
import lol.ethan.may.events.PacketEvent;
import lol.ethan.may.events.SubscribeEvent;
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
    public void onPacket(PacketEvent.Receive event) {
        if (!DuelsUtils.isInDuels()) return;
        if (event.getPacket() instanceof S38PacketPlayerListItem) {
            S38PacketPlayerListItem packet = (S38PacketPlayerListItem) event.getPacket();
            if (packet.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                for (S38PacketPlayerListItem.AddPlayerData data : packet.getEntries()) {
                    if (!data.getProfile().getName().startsWith("§k")) continue;
                    Rank rankProfile = getRankByIGN("§r" + data.getProfile().getName());
                    if (rankProfile != null) {
                        rankProfile.uuid = data.getProfile().getId().toString();
                    } else {
                        updateRankData(data.getProfile().getId().toString(), "§r" + data.getProfile().getName(), "", -1);
                    }
                }
            } else if (packet.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
                for (S38PacketPlayerListItem.AddPlayerData data : packet.getEntries()) {

                    Rank rankProfile = getRankByUUID(data.getProfile().getId().toString());
                    if (rankProfile != null) {
                        rankList.remove(rankProfile);
                    }
                }
            }
        } else if (event.getPacket() instanceof S0CPacketSpawnPlayer) {
            S0CPacketSpawnPlayer packet = (S0CPacketSpawnPlayer) event.getPacket();
            Rank rankProfile = getRankByUUID(packet.getPlayer().toString());
            if (rankProfile != null) {
                rankProfile.entityId = packet.getEntityID();
            }
        } else if (event.getPacket() instanceof S3EPacketTeams) {
            S3EPacketTeams packet = (S3EPacketTeams) event.getPacket();
            PlayerCount playerCount = parsePlayerCount(packet);
            if (playerCount != null) {
                this.playerCount = playerCount;
            }

            if (packet.getPlayers().size() != 0 && packet.getName().startsWith("§")) {
                for (String ign : packet.getPlayers()) {
                    if (ign.startsWith("§r§k")) {
                        Rank rankProfile = getRankByIGN(ign);
                        if (rankProfile != null) {
                            rankProfile.rank = packet.getName();
                        } else {
                            rankList.add(new Rank("", ign, packet.getName(), -1));
                        }
                        String msg = Entry.PREFIX + "You are queuing a " + Rank.parseRank(packet.getName());
                        if (playerCount != null) {
                            int nicks = Math.max(0, playerCount.current - rankList.size());

                            if (nicks == 1) {
                                msg += " §c[N]";
                            }
                        }
                        Entry.chat(msg);
                    }
                }
            }
        } else if (event.getPacket() instanceof S13PacketDestroyEntities) {
            S13PacketDestroyEntities packet = (S13PacketDestroyEntities) event.getPacket();
            rankList.removeIf(rank -> ArrayUtils.contains(packet.getEntityIDs(), rank.entityId));
        }
    }

    private PlayerCount parsePlayerCount(S3EPacketTeams packet) {
        if (packet.getPrefix() == null || packet.getSuffix() == null) return null;
        String text = packet.getPrefix() + packet.getSuffix();
        String clean = text.replaceAll("[^a-zA-Z0-9\\s:§]", "");
        Matcher matcher = playerCountPattern.matcher(clean);
        if (matcher.find()) {
            int current = Integer.parseInt(matcher.group(1));
            int max = Integer.parseInt(matcher.group(2));
            return new PlayerCount(current, max);
        }
        return null;
    }
    public Rank getRankByIGN(String ign) {
        for (Rank rank : rankList) {
            if (rank.ign.equals(ign)) return rank;
        }
        return null;
    }
    public Rank getRankByUUID(String uuid) {
        for (Rank rank : rankList) {
            if (rank.uuid.equals(uuid)) return rank;
        }
        return null;
    }

    private void updateRankData(String uuid, String IGN, String rank, int entityId) {
        for (Rank data: rankList) {
            if (data.uuid.equals(uuid)) {
                data.ign = IGN;
                data.rank = rank;
                data.entityId = entityId;
                return;
            }
        }
        rankList.add(new Rank(uuid, IGN, rank, entityId));
    }
    public static class Rank {
        public String uuid;
        public String ign;
        public String rank;
        public int entityId;

        public Rank(String uuid, String IGN, String rank, int entityId) {
            this.uuid = uuid;
            this.ign = IGN;
            this.rank = rank;
            this.entityId = entityId;
        }

        public static String parseRank(String color) {
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

        public PlayerCount(int current, int max) {
            this.current = current;
            this.max = max;
        }
    }
}
