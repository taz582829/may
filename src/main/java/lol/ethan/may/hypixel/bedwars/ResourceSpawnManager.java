package lol.ethan.may.hypixel.bedwars;

public class ResourceSpawnManager {

    public static int timeUntilEmerald = -1;
    public static int timeUntilDiamond = -1;
    public static int emeraldSpawnCount;
    public static int diamondSpawnCount;
    public static int emeraldStage;
    public static int diamondStage;
    public static int secondsSinceGameStart;

    public static void resetSpawnCounts() {
        timeUntilEmerald = -1;
        timeUntilDiamond = -1;
        emeraldSpawnCount = 0;
        diamondSpawnCount = 0;
        emeraldStage = 0;
        diamondStage = 0;
    }

    public static void updateTimers() {
        if (0 != BedwarsUtils.ticksSinceGameStart % 20) return;

        if (0 <= secondsSinceGameStart && 1 != emeraldStage) {
            emeraldStage = 1;
            diamondStage = 1;
            timeUntilDiamond = 1;
            timeUntilEmerald = 31;
        } else if (720 == secondsSinceGameStart) {
            emeraldStage = 2;
            timeUntilEmerald = 0;
        } else if (1440 == secondsSinceGameStart) {
            emeraldStage = 3;
            timeUntilEmerald = 0;
        } else if (360 == secondsSinceGameStart) {
            diamondStage = 2;
            timeUntilDiamond = 23;
        } else if (1080 == secondsSinceGameStart) {
            diamondStage = 3;
            timeUntilDiamond = 12;
        }

        if (0 == timeUntilDiamond) {
            diamondSpawnCount++;

            if (1 == diamondStage) {
                timeUntilDiamond = 30;
            } else if (2 == diamondStage) {
                timeUntilDiamond = 23;
            } else if (3 == diamondStage) {
                timeUntilDiamond = 12;
            }
        }

        if (0 == timeUntilEmerald) {
            final BedwarsUtils.Mode mode = BedwarsUtils.currentMode;

            emeraldSpawnCount++;

            if (1 == emeraldStage) {
                if (BedwarsUtils.Mode.SOLO == mode || BedwarsUtils.Mode.DOUBLES == mode) timeUntilEmerald = 65;
                else if (BedwarsUtils.Mode.FOURS == mode || BedwarsUtils.Mode.THREES == mode) timeUntilEmerald = 55;
            } else if (2 == emeraldStage) {
                if (BedwarsUtils.Mode.SOLO == mode || BedwarsUtils.Mode.DOUBLES == mode) timeUntilEmerald = 50;
                else if (BedwarsUtils.Mode.FOURS == mode || BedwarsUtils.Mode.THREES == mode) timeUntilEmerald = 40;
            } else if (3 == emeraldStage) {
                if (BedwarsUtils.Mode.SOLO == mode || BedwarsUtils.Mode.DOUBLES == mode) timeUntilEmerald = 35;
                else if (BedwarsUtils.Mode.FOURS == mode || BedwarsUtils.Mode.THREES == mode) timeUntilEmerald = 27;
            }
        }

        timeUntilEmerald--;
        timeUntilDiamond--;
        secondsSinceGameStart++;
    }
}
