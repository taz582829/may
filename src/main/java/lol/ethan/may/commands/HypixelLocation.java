package lol.ethan.may.commands;

import lol.ethan.may.Entry;
import lol.ethan.may.hypixel.modapi.LocationData;

public class HypixelLocation extends Command {
    public HypixelLocation() {
        super("may:locraw");
    }

    @Override
    public void execute(String[] strings) {
        Entry.chat(LocationData.INSTANCE.toString());
    }
}

