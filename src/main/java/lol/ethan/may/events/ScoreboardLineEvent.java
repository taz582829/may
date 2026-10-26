package lol.ethan.may.events;

import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;

public class ScoreboardLineEvent extends Event {
    private final Team team;
    private String line;

    public ScoreboardLineEvent(final Team team, final String name) {
        this.team = team;
        line = ScorePlayerTeam.formatPlayerName(team, name);
    }

    public String getLine() {
        return line;
    }

    public void setLine(final String line) {
        this.line = line;
    }

    public Team getTeam() {
        return team;
    }
}

