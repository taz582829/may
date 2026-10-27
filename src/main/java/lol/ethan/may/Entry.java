package lol.ethan.may;

import lol.ethan.may.anticheat.checks.Autoblock;
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
import lol.ethan.may.hypixel.BlacklistInfo;
import lol.ethan.may.hypixel.BowDistance;
import lol.ethan.may.hypixel.Denicker;
import lol.ethan.may.hypixel.ModAPI;
import lol.ethan.may.hypixel.bedwars.BedwarsUtils;
import lol.ethan.may.hypixel.bedwars.MiddleClickShop;
import lol.ethan.may.hypixel.bedwars.PartyCounter;
import lol.ethan.may.hypixel.bedwars.stats.BedwarsStatsManager;
import lol.ethan.may.hypixel.bedwars.stats.LobbyOneHelper;
import lol.ethan.may.hypixel.duels.DuelsRankInfo;
import lol.ethan.may.hypixel.duels.DuelsStatsManager;
import lol.ethan.may.hypixel.duels.DuelsUtils;
import lol.ethan.may.hypixel.modapi.LocationData;
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
    public static File DEBUG_DIR;
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
        } catch (final Exception e) {
            try {
                return (Minecraft) Minecraft.class.getMethod("func_71410_x").invoke(null);
            } catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void load(final Instrumentation i, final ClassLoader loader) throws Exception {
        nanoTime = System.nanoTime();
        Entry.loader = loader;
        inst = i;
        Mappings.load(Entry.getMinecraft());
        Entry.chat("§dMay §8» §rmodifying game code...");
        Thread.currentThread().setContextClassLoader(loader);
        FILE_PATH = new File(Minecraft.getMinecraft().mcDataDir, "May");
        DEBUG_DIR = new File(Entry.FILE_PATH, "debug");
        if (!DEBUG_DIR.exists()) {
            DEBUG_DIR.mkdirs();
        }
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
        transformers.add(new GuiNewChatTransformer());
        transformers.add(new EntityPlayerSPTransformer());
        for (final ITransformer t : transformers) {
            inst.addTransformer(t, true);
        }
        for (final Class<?> clazz : inst.getAllLoadedClasses()) {
            for (final ITransformer t : transformers) {
                try {
                    if (!t.shouldTransform(clazz.getName().replace('/', '.')) || !inst.isModifiableClass(clazz))
                        continue;
                    inst.retransformClasses(clazz);
                } catch (final Throwable e) {
                    System.out.println(clazz.getName() + " failed to transform (" + t.getClass().getName() + ")");
                    e.printStackTrace();
                }
            }
        }
        new Entry().start(loader);
    }

    public static String addInternalCommand(final Runnable runnable) {
        final UUID uuid = UUID.randomUUID();
        commandIdMap.put(uuid, runnable);
        return "/mayinternal " + uuid;
    }

    public static void chat(final String message) {
        Entry.chat(new ChatComponentText(message.replaceAll("&([0-9A-FK-OR])", "§$1")));
    }

    public static void chat(final Object message) {
        Entry.chat(new ChatComponentText(message.toString().replaceAll("&([0-9A-FK-OR])", "§$1")));
    }

    public static void chat(final IChatComponent message) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            return;
        }
        if (mc.isCallingFromMinecraftThread()) {
            mc.thePlayer.addChatMessage(message);
            return;
        }
        mc.addScheduledTask(() -> mc.thePlayer.addChatMessage(message));
    }

    public static InputStream getResourceAsStream(final String path) {
        final ClassLoader[] loaders;
        for (final ClassLoader cl : loaders = new ClassLoader[]{ClassLoader.getSystemClassLoader(), Thread.currentThread().getContextClassLoader(), Mappings.class.getClassLoader()}) {
            final InputStream in;
            if (cl == null || (in = cl.getResourceAsStream(path)) == null) continue;
            return in;
        }
        throw new RuntimeException("couldnt find a resource");
    }

    public static void debug(final IChatComponent message) {
        if (Config.isDebugLogging()) {
            Entry.chat(message);
        }
    }

    public static void debug(final String message) {
        if (Config.isDebugLogging()) {
            Entry.chat(message);
        }
    }

    public static String getServerIP() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.isSingleplayer()) {
            return "none";
        }
        if (mc.getCurrentServerData() == null || mc.getCurrentServerData().serverIP == null) {
            return "none";
        }
        return mc.getCurrentServerData().serverIP;
    }

    public static void playSound(final String key, final float volume, final float pitch) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        mc.addScheduledTask(() -> mc.thePlayer.playSound(key, volume, pitch));
    }

    public static void playSound(final String key, final float volume) {
        Entry.playSound(key, volume, 1.0f);
    }

    public static void playSound(final String key) {
        Entry.playSound(key, 1.0f, 1.0f);
    }

    public static void playClickSound() {
        Entry.playClickSound(1.0f);
    }

    public static void playClickSound(final float pitch) {
        Entry.playClickSound(1.0f, pitch);
    }

    public static void playClickSound(final float volume, final float pitch) {
        Entry.playSound("random.click", volume, pitch);
    }

    public void start(final ClassLoader loader) {
        Thread.currentThread().setContextClassLoader(loader);

        Config.setConfigFile(new File(new File(Minecraft.getMinecraft().mcDataDir, "May"), "config.json"));
        Config.load();
        for (final Component comp : Config.getAllComponents()) {
            if (!(comp instanceof HUDComponent)) continue;
            final HUDComponent hudComponent = (HUDComponent) comp;
            huds.add(hudComponent.hud);
        }
        HypixelModAPI.getInstance().setModImplementation(new ModAPI());
        if (Entry.getServerIP().contains("hypixel")) {
            ModAPI.setIsOnHypixel(true);
            Entry.debug("detected hypixel");
        }
        EventBus.subscribe(new Object() {

            @SubscribeEvent
            public void onShutdown(final ShutdownEvent event) {
                Config.save();
            }
        });
        EventBus.subscribe(this);
        EventBus.subscribe(new CommandHandler());
        EventBus.subscribe(new KeyboardHandler());
        EventBus.subscribe(new ChatListener());
        EventBus.subscribe(new AnticheatData());
        EventBus.subscribe(new Autoblock());
        //EventBus.subscribe(new BedNuker());
        EventBus.subscribe(new LegitScaffold());
        EventBus.subscribe(new NoBreakDelay());
        EventBus.subscribe(new BedwarsUtils());
        EventBus.subscribe(new LocationData());
        EventBus.subscribe(new BedwarsStatsManager());
        EventBus.subscribe(new DuelsStatsManager());
        EventBus.subscribe(new Denicker());
        EventBus.subscribe(new DuelsUtils());
        EventBus.subscribe(new DuelsRankInfo());
        EventBus.subscribe(new HousingESP());
        EventBus.subscribe(new AntiInvis());
        EventBus.subscribe(new MiddleClickShop());
        EventBus.subscribe(new PartyCounter());
        EventBus.subscribe(new BowDistance());
        EventBus.subscribe(new LobbyOneHelper());
        EventBus.subscribe(new BlacklistInfo());
        CommandHandler.register(new TestCommand());
        CommandHandler.register(new HypixelStats());
        CommandHandler.register(new ConfigCommand());
        CommandHandler.register(new HypixelLocation());
        final long nanoTimeNow = System.nanoTime();
        final double diff = (((double) nanoTimeNow / 1000000.0) - ((double) nanoTime / 1000000.0)) / 1000.0;
        Entry.chat("§dMay §8» §rinjection finished! took " + String.format("%.2fs", diff));
    }

    @SubscribeEvent
    public void onTick(final TickEvent.Post event) {
        if (Entry.getMinecraft().theWorld == null) {
            return;
        }
        for (final BasicHud hud : huds) {
            if (!hud.shouldShow()) continue;
            hud.updateLineCache();
        }
    }

    @SubscribeEvent
    public void onChat(final ChatEvent.Send e) {
        final String msg = e.getMessage();
        if (msg.startsWith("/mayinternal")) {
            final UUID uuid = UUID.fromString(msg.split(" ")[1]);
            commandIdMap.get(uuid).run();
            e.setCancelled(true);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent.Post event) {
        if (Entry.getMinecraft().theWorld == null) {
            return;
        }
        for (final BasicHud hud : huds) {
            if (!hud.shouldShow()) continue;
            hud.draw();
        }
    }

    @SubscribeEvent
    public void onScoreboardLine(final ScoreboardLineEvent event) {
        if (Entry.getMinecraft().theWorld == null) {
            return;
        }
        if (Config.isDebugLogging()) {
            event.setLine("debug");
        }
    }
}

