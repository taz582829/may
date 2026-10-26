package lol.ethan.may.events;

import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;

public class ScoreboardLineEvent extends Event {
    private final Team team;
    private String line;

    public ScoreboardLineEvent(Team team, String name) {
        this.team = team;
        this.line = ScorePlayerTeam.formatPlayerName(team, name);
    }

    public String getLine() {
        return this.line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public Team getTeam() {
        return this.team;
    }
}

