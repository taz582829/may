package lol.ethan.may.hypixel.bedwars;

public class DisconnectTimer {
//    Pattern pattern = Pattern.compile("§.(\\w) (\\w+): §a✓");
//
//    List<String> emptyTeams = new ArrayList<>();
//    Map<String, Long> timeOfTeamEnd = new HashMap<>();
//
//    @SubscribeEvent
//    public void onRenderScoreboard(RenderScoreboardLineEvent event) {
//        if (!BedwarsUtils.isInBedwars()) return;
//        HouseMatcher matcher = pattern.matcher(event.line);
//        if (matcher.find()) {
//            String team = matcher.group(2);
//            if (timeOfTeamEnd.containsKey(team)) {
//                long time = timeOfTeamEnd.get(team);
//                event.line += " §c" + time + "s";
//            }
//        }
//    }
//    @SubscribeEvent
//    public void onChat(ChatEvent.Received event) {
//        if (!BedwarsUtils.isInBedwars()) return;
//        String formatted = event.getMessage().getFormattedText().trim();
//        if (formatted.endsWith("disconnected.")) {
//            String teamColor = formatted.substring(0, 2);
//            TimeUtils.setTickDelay(() -> {
//                if (!BedwarsUtils.isInBedwars()) return;
//                for (Map.Entry<BedwarsTeam, List<UUID>> entry : BedwarsUtils.getAllPlayerTeams().entrySet()) {
//                    if (entry.getValue().size() == 0) {
//                        Entry.debug(entry.getKey().formattedDisplayName + " §fdisconnected!");
//                        emptyTeams.add(entry.getKey().formattedDisplayName);
//                        timeOfTeamEnd.put(entry.getKey().formattedDisplayName, System.currentTimeMillis() + 119);
//                    } else {
//                        emptyTeams.remove(entry.getKey().formattedDisplayName);
//                        timeOfTeamEnd.remove(entry.getKey().formattedDisplayName);
//                    }
//                }
//            }, 20);
//        }
//    }


}