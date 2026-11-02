package lol.ethan.may;

public enum MinecraftClient {
    LUNAR("com.moonsworth.lunar.lib.adventure.text.Component"),
    FORGE("net.minecraftforge.client.MinecraftForgeClient"),
    VANILLA("ave");

    public final String className;
    MinecraftClient(String className) {
        this.className = className;
    }
}
