package lol.ethan.may;

import lol.ethan.may.anticheat.checks.Autoblock;
import lol.ethan.may.anticheat.checks.BedNuker;
import lol.ethan.may.anticheat.checks.LegitScaffold;
import lol.ethan.may.anticheat.checks.NoBreakDelay;
import lol.ethan.may.anticheat.data.AnticheatData;
import lol.ethan.may.asm.*;
import lol.ethan.may.commands.*;
import lol.ethan.may.config.Config;
import lol.ethan.may.config.component.Component;
import lol.ethan.may.config.component.HUDComponent;
import lol.ethan.may.events.*;
import lol.ethan.may.housing.AntiInvis;
import lol.ethan.may.housing.HousingESP;
import lol.ethan.may.hypixel.BowDistance;
import lol.ethan.may.hypixel.Denicker;
import lol.ethan.may.hypixel.ModAPI;
import lol.ethan.may.hypixel.NickHider;
import lol.ethan.may.hypixel.bedwars.BedwarsUtils;
import lol.ethan.may.hypixel.bedwars.MiddleClickShop;
import lol.ethan.may.hypixel.bedwars.PartyCounter;
import lol.ethan.may.hypixel.bedwars.stats.BedwarsStatsManager;
import lol.ethan.may.hypixel.bedwars.stats.LobbyOneHelper;
import lol.ethan.may.hypixel.duels.DuelsRankInfo;
import lol.ethan.may.hypixel.duels.DuelsStatsManager;
import lol.ethan.may.hypixel.duels.DuelsUtils;
import lol.ethan.may.hypixel.modapi.LocationData;
import lol.ethan.may.hypixel.skywars.SkywarsStatsManager;
import lol.ethan.may.mappings.Mappings;
import lol.ethan.may.render.huds.BasicHud;
import net.hypixel.modapi.HypixelModAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.io.File;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.*;

public class Entry {

    public static final String PREFIX = "§dMay §8» §r";
    public static final String SHORT_PREFIX = "§5» §r";
    public static final List<ITransformer> transformers = new ArrayList<>();
    private static final List<BasicHud> huds;
    private static final Map<UUID, Runnable> commandIdMap;
    public static Instrumentation inst;
    public static ClassLoader loader;
    public static Entry INSTANCE;
    public static File FILE_PATH;
    private static long nanoTime;

    static {
        huds = new ArrayList<>();
        commandIdMap = new HashMap<>();
    }

    public Entry() {
        INSTANCE = this;
    }

    private static Minecraft getMinecraft() {
        try {
            return (Minecraft) Minecraft.class.getMethod("getMinecraft").invoke(null);
        } catch (Exception e) {
            try {
                return (Minecraft) Minecraft.class.getMethod("func_71410_x").invoke(null);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void load(Instrumentation i, ClassLoader loader) throws Exception {
        nanoTime = System.nanoTime();
        Entry.loader = loader;
        inst = i;
        Mappings.load(Entry.getMinecraft());
        Entry.chat("§dMay §8» §rmodifying game code...");
        Thread.currentThread().setContextClassLoader(loader);
        FILE_PATH = new File(Minecraft.getMinecraft().mcDataDir, "May");
        if (Mappings.getMappingType() == Mappings.MappingType.MCP) {
            transformers.add(new Remapper());
        }
        transformers.add(new MinecraftTransformer());
        transformers.add(new NetworkManagerHook());
        transformers.add(new WorldTransformer());
        transformers.add(new RendererLivingEntityTransformer());
        transformers.add(new EntityPlayerTransformer());
        transformers.add(new HitboxesHook());
        transformers.add(new GuiIngameHook());
        transformers.add(new NetworkPlayerInfoTransformer());
        transformers.add(new NetHandlerPlayClientTransformer());
        transformers.add(new EntityRendererTransformer());
        transformers.add(new GuiContainerTransformer());
        transformers.add(new ItemBucketTransformer());
        for (ITransformer t : transformers) {
            inst.addTransformer(t, true);
        }
        for (Class clazz : inst.getAllLoadedClasses()) {
            for (ITransformer t : transformers) {
                try {
                    if (!t.shouldTransform(clazz.getName().replace('/', '.')) || !inst.isModifiableClass(clazz))
                        continue;
                    inst.retransformClasses(clazz);
                } catch (Throwable e) {
                    System.out.println(clazz.getName() + " failed to transform (" + t.getClass().getName() + ")");
                    e.printStackTrace();
                }
            }
        }
        new Entry().start(loader);
    }

    public static String addInternalCommand(Runnable runnable) {
        UUID uuid = UUID.randomUUID();
        commandIdMap.put(uuid, runnable);
        return "/frostmodinternal " + uuid;
    }

    public static void chat(String message) {
        Entry.chat(new ChatComponentText(message.replaceAll("&([0-9A-FK-OR])", "§$1")));
    }

    public static void chat(Object message) {
        Entry.chat(new ChatComponentText(message.toString().replaceAll("&([0-9A-FK-OR])", "§$1")));
    }

    public static void chat(IChatComponent message) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            return;
        }
        if (mc.isCallingFromMinecraftThread()) {
            mc.thePlayer.addChatMessage(message);
            return;
        }
        mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(message));
    }

    public static InputStream getResourceAsStream(String path) {
        ClassLoader[] loaders;
        for (ClassLoader cl : loaders = new ClassLoader[]{ClassLoader.getSystemClassLoader(), Thread.currentThread().getContextClassLoader(), Mappings.class.getClassLoader()}) {
            InputStream in;
            if (cl == null || (in = cl.getResourceAsStream(path)) == null) continue;
            return in;
        }
        throw new RuntimeException("couldnt find a resource");
    }

    public static void debug(IChatComponent message) {
        if (Config.isDebugLogging()) {
            Entry.chat(message);
        }
    }

    public static void debug(String message) {
        if (Config.isDebugLogging()) {
            Entry.chat(message);
        }
    }

    public static String getServerIP() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.isSingleplayer()) {
            return "none";
        }
        if (mc.getCurrentServerData() == null || mc.getCurrentServerData().serverIP == null) {
            return "none";
        }
        return mc.getCurrentServerData().serverIP;
    }

    public static void playSound(String key, float volume, float pitch) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        mc.addScheduledTask(() -> mc.thePlayer.playSound(key, volume, pitch));
    }

    public static void playSound(String key, float volume) {
        Entry.playSound(key, volume, 1.0f);
    }

    public static void playSound(String key) {
        Entry.playSound(key, 1.0f, 1.0f);
    }

    public static void playClickSound() {
        Entry.playClickSound(1.0f);
    }

    public static void playClickSound(float pitch) {
        Entry.playClickSound(1.0f, pitch);
    }

    public static void playClickSound(float volume, float pitch) {
        Entry.playSound("random.click", volume, pitch);
    }

    public void start(ClassLoader loader) {
        Thread.currentThread().setContextClassLoader(loader);
        Config.setConfigFile(new File(new File(Minecraft.getMinecraft().mcDataDir, "May"), "config.json"));
        Config.load();
        for (Component comp : Config.getAllComponents()) {
            if (!(comp instanceof HUDComponent)) continue;
            HUDComponent hudComponent = (HUDComponent) comp;
            huds.add(hudComponent.hud);
        }
        HypixelModAPI.getInstance().setModImplementation(new ModAPI());
        if (Entry.getServerIP().contains("hypixel")) {
            ModAPI.setIsOnHypixel(true);
            Entry.debug("detected hypixel");
        }
        EventBus.subscribe(new Object() {

            @SubscribeEvent
            public void onShutdown(ShutdownEvent event) {
                Config.save();
            }
        });
        EventBus.subscribe(this);
        EventBus.subscribe(new CommandHandler());
        EventBus.subscribe(new KeyboardHandler());
        EventBus.subscribe(new ChatListener());
        EventBus.subscribe(new AnticheatData());
        EventBus.subscribe(new Autoblock());
        EventBus.subscribe(new BedNuker());
        EventBus.subscribe(new LegitScaffold());
        EventBus.subscribe(new NoBreakDelay());
        EventBus.subscribe(new BedwarsUtils());
        EventBus.subscribe(new LocationData());
        EventBus.subscribe(new BedwarsStatsManager());
        EventBus.subscribe(new DuelsStatsManager());
        EventBus.subscribe(new Denicker());
        EventBus.subscribe(new DuelsUtils());
        EventBus.subscribe(new DuelsRankInfo());
        EventBus.subscribe(new SkywarsStatsManager());
        EventBus.subscribe(new NickHider());
        EventBus.subscribe(new HousingESP());
        EventBus.subscribe(new AntiInvis());
        EventBus.subscribe(new MiddleClickShop());
        EventBus.subscribe(new PartyCounter());
        EventBus.subscribe(new MiddleClickShop());
        EventBus.subscribe(new BowDistance());
        EventBus.subscribe(new LobbyOneHelper());
        CommandHandler.register(new TestCommand());
        CommandHandler.register(new HypixelStats());
        CommandHandler.register(new ConfigCommand());
        CommandHandler.register(new HypixelLocation());
        long nanoTimeNow = System.nanoTime();
        double diff = ((double) nanoTimeNow / 1000000.0 - (double) nanoTime / 1000000.0) / 1000.0;
        Entry.chat("§dMay §8» §rinjection finished! took " + String.format("%.2fs", diff));
    }

    @SubscribeEvent
    public void onTick(TickEvent.Post event) {
        if (Entry.getMinecraft().theWorld == null) {
            return;
        }
        for (BasicHud hud : huds) {
            if (!hud.shouldShow()) continue;
            hud.updateLineCache();
        }
    }

    @SubscribeEvent
    public void onChat(ChatEvent.Send e) {
        String msg = e.getMessage();
        if (msg.startsWith("/frostmodinternal")) {
            UUID uuid = UUID.fromString(msg.split(" ")[1]);
            commandIdMap.get(uuid).run();
            e.setCancelled(true);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (Entry.getMinecraft().theWorld == null) {
            return;
        }
        for (BasicHud hud : huds) {
            if (!hud.shouldShow()) continue;
            hud.draw();
        }
    }

    @SubscribeEvent
    public void onScoreboardLine(ScoreboardLineEvent event) {
        if (Entry.getMinecraft().theWorld == null) {
            return;
        }
        if (Config.isDebugLogging()) {
            event.setLine("debug");
        }
    }
}

