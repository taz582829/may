package lol.ethan.may.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.ethan.may.config.component.*;
import lol.ethan.may.config.component.text.NumberTextBox;
import lol.ethan.may.config.component.text.StringTextBox;
import lol.ethan.may.housing.ItemStorage;
import lol.ethan.may.housing.NBTSpawner;
import lol.ethan.may.hypixel.TradeIndicator;
import lol.ethan.may.render.huds.TestHUD;
import lol.ethan.may.utils.GuiUtils;
import lol.ethan.may.utils.JsonUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Config {
    private static final Executor executor;
    public static Button resourceTimerTab = new Button("Resource Timer", Category.Bedwars, true, "shows resource timers in tab");
    public static Button bedwarsTabStats = new Button("Bedwars Tab Stats", Category.Bedwars);
    public static StringTextBox bedwarsTabStats_seperator = new StringTextBox("Stat Seperator", Category.Bedwars, "⎜") {

        @Override
        public boolean shouldDraw() {
            return Config.bedwarsTabStats.enabled;
        }
    };
    public static Button bedwarsTabStats_showStars = new Button("Show Stars", Category.Bedwars) {

        @Override
        public boolean shouldDraw() {
            return Config.bedwarsTabStats.enabled;
        }
    };
    public static Button bedwarsTabStats_showFKDR = new Button("Show FKDR", Category.Bedwars) {

        @Override
        public boolean shouldDraw() {
            return Config.bedwarsTabStats.enabled;
        }
    };
    public static Button bedwarsTabStats_showWinstreak = new Button("Show Winstreak", Category.Bedwars) {

        @Override
        public boolean shouldDraw() {
            return Config.bedwarsTabStats.enabled;
        }
    };
    public static Button bedwarsTabStats_showEWS = new Button("Show Estimated Winstreak", Category.Bedwars, true, "shows estimated winstreak if it cant find unestimated winstreak") {

        @Override
        public boolean shouldDraw() {
            return Config.bedwarsTabStats.enabled && Config.bedwarsTabStats_showWinstreak.enabled;
        }
    };
    public static Button bedwarsTabStats_showFinals = new Button("Show Finals", Category.Bedwars) {

        @Override
        public boolean shouldDraw() {
            return Config.bedwarsTabStats.enabled;
        }
    };
    public static KeybindComponent clickGuiBind = new KeybindComponent("Click GUI Bind", Category.General, 54, ClickGUI::open);
    public static Button flagPlaySound = new Button("Play Sound on Flag", Category.Anticheat, true);
    public static Button flagIcons = new Button("Flag Icons", Category.Anticheat, true, "shows icon ⚑ on people who flag the anticheat.");
    public static Button autoblockFlags = new Button("Autoblock Flags", Category.Anticheat, true);
    public static Button legitScaffFlags = new Button("Legit Scaffold Flags", Category.Anticheat, true);
    public static Button tellyScaffFlags = new Button("Telly Scaffold Flags", Category.Anticheat, true, "currently does nothing");
    public static Button noBreakDelayFlags = new Button("No Break Delay Flags", Category.Anticheat, true, "experimental, can false flag");
    public static HUDComponent tradeIndicator = new HUDComponent("Trade Indicator", Category.General, new TradeIndicator());
    public static Button hypixelKeyless = new Button("Hypixel Keyless", Category.General, true);
    public static StringTextBox hypixelKey = new StringTextBox("Hypixel Keyless", Category.General, true) {

        @Override
        public boolean shouldDraw() {
            return !Config.hypixelKeyless.enabled;
        }
    };
    public static Button middleClickShop = new Button("Middle Click Shop", Category.Bedwars, true);
    public static StringTextBox urchinKey = new StringTextBox("Urchin API Key", Category.General, true);
    public static Button clearNightVision = new Button("Clear Night Vision", Category.General, true);
    public static Button bowDistance = new Button("Bow Distance in Chat", Category.Bedwars, true);
    public static Button modifyPlayerHitboxes = new Button("Modify Player Hitboxes", Category.Hitboxes, true);
    public static Button modifyArrowHitboxes = new Button("Modify Arrow Hitboxes", Category.Hitboxes, true);
    public static Button accuratePlayerHitboxes = new Button("Accurate Player Hitboxes", Category.Hitboxes, false);
    public static Button accurateArrowHitboxes = new Button("Accurate Arrow Hitboxes", Category.Hitboxes, false);
    public static Button teamColoredPlayerHitboxes = new Button("Team Colored Player Hitboxes", Category.Hitboxes, true);
    public static Button teamColoredArrowHitboxes = new Button("Team Colored Arrow Hitboxes", Category.Hitboxes, true, "makes arrow hitboxes the same color as the player who shot them");
    public static ColorComponent hitboxColor = new ColorComponent("Hitbox Color", Category.Hitboxes, -1);
    public static HUDComponent testHud = new HUDComponent("Test HUD", Category.General, new TestHUD(10.0f, 10.0f));
    public static Button hideXP = new Button("Hide XP Bar", Category.General, false, "hides xp bar (useful as a nick hider)");
    public static Button antiInvis = new Button("Anti Invis", Category.Housing);
    public static Button housingEsp = new Button("Housing ESP", Category.Housing);
    public static Button housingEspInvisOnly = new Button("Invis Only ESP", Category.Housing) {

        @Override
        public boolean shouldDraw() {
            return Config.housingEsp.enabled;
        }
    };
    public static NumberTextBox matcherTimeBetweenCycles = new NumberTextBox("Time Between Matcher Cycles", Category.Housing, NumberTextBox.TextBoxType.INT, 75);
    public static NumberTextBox matcherTimeBetweenHouses = new NumberTextBox("Time Between House Cycles (ticks)", Category.Housing, NumberTextBox.TextBoxType.INT, 60);
    public static Button itemLogging = new Button("Item Logging", Category.Housing, false);
    public static KeybindComponent nbtStorageBind = new KeybindComponent("NBT Storage", Category.Housing, () -> GuiUtils.openCustomScreen(new ItemStorage()));
    public static KeybindComponent itemGiverBind = new KeybindComponent("Item Spawner", Category.Housing, () -> GuiUtils.openCustomScreen(new NBTSpawner()));
    private static File configFile;
    private static boolean debugLogging;
    public static Button debugLoggingBtn = new Button("Debug Logging", Category.General, false, Config::setDebugLogging);

    static {
        executor = Executors.newSingleThreadExecutor();
        debugLogging = false;
    }

    public static File getConfigFile() {
        return configFile;
    }

    public static void setConfigFile(final File configFile) {
        Config.configFile = configFile;
    }

    public static JsonObject serialize() {
        final JsonObject obj = new JsonObject();
        try {
            for (final Field field : Config.class.getFields()) {
                final Object value;
                if (field.isSynthetic() || !((value = field.get(null)) instanceof Component)) continue;
                final Component comp = (Component) value;
                final String category = comp.category.name();
                if (!obj.has(category)) {
                    obj.add(category, new JsonObject());
                }
                final JsonObject compInfo = obj.getAsJsonObject(category);
                final JsonElement result = comp.serialize();
                if (result == null) continue;
                compInfo.add(field.getName(), result);
            }
        } catch (final Throwable e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static void save() {
        executor.execute(() -> {
            try {
                final File newConfig = new File(new File(Minecraft.getMinecraft().mcDataDir, "May"), "config.json");
                final File parent = configFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                final Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try (final OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(newConfig.toPath()), StandardCharsets.UTF_8)) {
                    gson.toJson(Config.serialize(), writer);
                }
            } catch (final Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public static void load() {
        if (!configFile.exists()) {
            Config.save();
            return;
        }
        final File newConfig = new File(new File(Minecraft.getMinecraft().mcDataDir, "May"), "config.json");
        if (!newConfig.exists()) {
            Config.save();
            return;
        }
        try (final InputStreamReader reader = new InputStreamReader(Files.newInputStream(newConfig.toPath()), StandardCharsets.UTF_8)) {
            final JsonObject object = JsonUtils.parse(reader).getAsJsonObject();
            if (object.entrySet().isEmpty()) {
                Config.save();
                return;
            }
            for (final Field field : Config.class.getFields()) {
                if (field.isSynthetic() || !Modifier.isStatic(field.getModifiers())) continue;
                try {
                    final JsonElement saved;
                    final Component comp = (Component) field.get(null);
                    final JsonObject categoryObject = object.getAsJsonObject(comp.category.name());
                    if (categoryObject == null || (saved = categoryObject.get(field.getName())) == null || saved.isJsonNull())
                        continue;
                    comp.load(saved);
                } catch (final Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (final Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getReadableKeybind(final int key) {
        if (key == 0) {
            return "NONE";
        }
        if (key < 0) {
            final int mouseButton = -key - 1;
            return "MB" + (mouseButton + 1);
        }
        final String name = Keyboard.getKeyName(key);
        if (name == null || name.isEmpty()) {
            return "NONE";
        }
        return name;
    }

    public static List<Component> getAllComponents() {
        final ArrayList<Component> comps = new ArrayList<>();
        try {
            for (final Field field : Config.class.getDeclaredFields()) {
                final Object value;
                if (field.isSynthetic() || !Modifier.isPublic(field.getModifiers()) || !Modifier.isStatic(field.getModifiers()) || !((value = field.get(null)) instanceof Component))
                    continue;
                comps.add((Component) value);
            }
        } catch (final Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        return comps;
    }

    public static boolean isDebugLogging() {
        return debugLogging;
    }

    public static void setDebugLogging(final boolean debugLogging) {
        Config.debugLogging = debugLogging;
    }
}

