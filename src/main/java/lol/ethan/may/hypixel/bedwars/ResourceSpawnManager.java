package lol.ethan.may.hypixel.bedwars;

public class ResourceSpawnManager {
    public static int timeUntilEmerald = -1;
    public static int timeUntilDiamond = -1;
    public static int emeraldSpawnCount = 0;
    public static int diamondSpawnCount = 0;
    public static int emeraldStage = 0;
    public static int diamondStage = 0;
    public static int secondsSinceGameStart = 0;

    public static void resetSpawnCounts() {
        timeUntilEmerald = -1;
        timeUntilDiamond = -1;
        emeraldSpawnCount = 0;
        diamondSpawnCount = 0;
        emeraldStage = 0;
        diamondStage = 0;
    }

    public static void updateTimers() {
        if (BedwarsUtils.ticksSinceGameStart % 20 != 0) {
            return;
        }
        if (secondsSinceGameStart >= 0 && emeraldStage != 1) {
            emeraldStage = 1;
            diamondStage = 1;
            timeUntilDiamond = 1;
            timeUntilEmerald = 31;
        } else if (secondsSinceGameStart == 720) {
            emeraldStage = 2;
            timeUntilEmerald = 0;
        } else if (secondsSinceGameStart == 1440) {
            emeraldStage = 3;
            timeUntilEmerald = 0;
        } else if (secondsSinceGameStart == 360) {
            diamondStage = 2;
            timeUntilDiamond = 23;
        } else if (secondsSinceGameStart == 1080) {
            diamondStage = 3;
            timeUntilDiamond = 12;
        }
        if (timeUntilDiamond == 0) {
            ++diamondSpawnCount;
            if (diamondStage == 1) {
                timeUntilDiamond = 30;
            } else if (diamondStage == 2) {
                timeUntilDiamond = 23;
            } else if (diamondStage == 3) {
                timeUntilDiamond = 12;
            }
        }
        if (timeUntilEmerald == 0) {
            BedwarsUtils.Mode mode = BedwarsUtils.currentMode;
            ++emeraldSpawnCount;
            if (emeraldStage == 1) {
                if (mode == BedwarsUtils.Mode.SOLO || mode == BedwarsUtils.Mode.DOUBLES) {
                    timeUntilEmerald = 65;
                } else if (mode == BedwarsUtils.Mode.FOURS || mode == BedwarsUtils.Mode.THREES) {
                    timeUntilEmerald = 55;
                }
            } else if (emeraldStage == 2) {
                if (mode == BedwarsUtils.Mode.SOLO || mode == BedwarsUtils.Mode.DOUBLES) {
                    timeUntilEmerald = 50;
                } else if (mode == BedwarsUtils.Mode.FOURS || mode == BedwarsUtils.Mode.THREES) {
                    timeUntilEmerald = 40;
                }
            } else if (emeraldStage == 3) {
                if (mode == BedwarsUtils.Mode.SOLO || mode == BedwarsUtils.Mode.DOUBLES) {
                    timeUntilEmerald = 35;
                } else if (mode == BedwarsUtils.Mode.FOURS || mode == BedwarsUtils.Mode.THREES) {
                    timeUntilEmerald = 27;
                }
            }
        }
        --timeUntilEmerald;
        --timeUntilDiamond;
        ++secondsSinceGameStart;
    }
}

