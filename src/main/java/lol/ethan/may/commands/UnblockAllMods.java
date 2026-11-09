package lol.ethan.may.commands;

import com.google.protobuf.NullValue;
import com.google.protobuf.Value;
import com.lunarclient.apollo.mods.impl.ModAutoTextHotkey;
import com.lunarclient.apollo.mods.impl.ModFreelook;
import lol.ethan.may.Entry;
import lol.ethan.may.lunar.ApolloUtils;

public class UnblockAllMods extends Command {
    public UnblockAllMods() {
        super("unblocklunar");
    }

    @Override
    public void execute(final String[] args) {
        ApolloUtils.setModSetting(ModFreelook.ENABLED, Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build());
        ApolloUtils.setModSetting(ModAutoTextHotkey.ENABLED, Value.newBuilder().setNullValue(NullValue.NULL_VALUE).build());
        ApolloUtils.setModStatus("speclook", null);
        ApolloUtils.setModStatus("freecam", null);

        Entry.chat("unblocked mods");
    }
}
