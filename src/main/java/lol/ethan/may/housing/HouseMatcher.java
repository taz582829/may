package lol.ethan.may.housing;

import lol.ethan.may.Entry;
import lol.ethan.may.config.Config;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.user.LocationEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.gui.GuiOpenEvent;
import lol.ethan.may.utils.GuiUtils;
import lol.ethan.may.utils.TimeUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HouseMatcher {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static int currentHouse = 0;
    public static final List<String> targets = new ArrayList<>();
    public static long timeStarted = 0;
    public static boolean enabled;
    public static String server;
    public static int lastHouseIdx;
    public static int cycles = 0;
    public static boolean joining = false;
    public static boolean resetting = false;
    public static long timeOfNextReset;
    public static long timeOfLimbo;
    public static long timeUntilSwap = 0;
    public static int timesJoined = 0;
    private static int houseCount;
    private static final List<String> excluded = new ArrayList<>();
    private static final File excludedFile = new File(Entry.FILE_PATH, "matcher_excluded.txt");
    public static List<String> getTargets() {
        return targets;
    }
    public static HouseMatcher INSTANCE;
    private static final Executor executor = Executors.newSingleThreadExecutor();

    public HouseMatcher() {
        INSTANCE = this;
        load();
    }
    public static boolean toggle() {
        final boolean e = !enabled;
        toggle(e);
        return e;

    }
    public static void toggle(final boolean val) {
        enabled = val;
        if (enabled) {
            //EventBus.subscribe(HouseMatcher.INSTANCE);
            timeStarted = System.currentTimeMillis();

            currentHouse = 0;
            timesJoined = 0;
            cycles = 0;
            joinHouse();
        }

    }
    private void load() {
        if (!excludedFile.exists()) return;
        executor.execute(() -> {
            try {
                final String excludedJoined = new String(
                        Files.readAllBytes(excludedFile.toPath()),
                        StandardCharsets.UTF_8
                );
                Collections.addAll(excluded, excludedJoined.split("\n"));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

        });
    }
    public static void addExcludedHouse(final String name) {
        excluded.add(name);
        executor.execute(() -> {
            try (final FileWriter writer = new FileWriter(excludedFile)) {
                 writer.write(String.join("\n", excluded));
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }

        });
    }

    public static List<String> getExcluded() {
        return excluded;
    }

    public static void removeExcludedHouse(final String name) {
        excluded.remove(name);
        executor.execute(() -> {
            try (final FileWriter writer = new FileWriter(excludedFile)) {
                writer.write(String.join("\n", excluded));
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }

        });
    }
    public static void joinHouse() {
        if (!enabled) return;

        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) {
            lol.ethan.may.utils.TimeUtils.setTimeout(HouseMatcher::joinHouse, 10);
            return;
        }
        player.sendChatMessage("/home");
        EventBus.subscribe(new Object() {
            @SubscribeEvent
            public void onGuiOpen(final GuiOpenEvent event) {
                if (event.getScreen() instanceof GuiContainer) {
                    EventBus.unsubscribe(this);
                    TimeUtils.setTickDelay(() -> {
                        final String title = GuiUtils.getTitle();
                        if (!StringUtils.stripControlCodes(title).trim().equalsIgnoreCase("My Houses")) return;
                        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiContainer)) return;
                        final GuiContainer container = (GuiContainer) Minecraft.getMinecraft().currentScreen;
                        final IInventory playerInv = Minecraft.getMinecraft().thePlayer.inventory;

                        final List<Integer> houses = new ArrayList<>();

                        matchLoop:
                        for (final Slot slot : container.inventorySlots.inventorySlots) {
                            if (slot.inventory == playerInv) continue;
                            final ItemStack item = slot.getStack();
                            if (item == null) continue;
                            final String name = StringUtils.stripControlCodes(item.getDisplayName());

                            for (final String blacklistedName : excluded) {
                                if (blacklistedName.equalsIgnoreCase(name)) {
                                    continue matchLoop;
                                }
                            }
                            if (Block.getBlockFromItem(item.getItem()) == Blocks.bedrock) continue;
                            houses.add(slot.slotNumber);
                        }

                        if (houses.isEmpty()) return;
                        if (currentHouse >= houses.size()) currentHouse = 0;
                        houseCount = houses.size();
                        GuiUtils.clickSlot(houses.get(currentHouse));
                        lastHouseIdx = currentHouse;
                    }, 2);
                }
            }
        });
    }
    @SubscribeEvent
    public void onLocation(final LocationEvent event) {
        if (!enabled) return;
        server = event.getServer();
        if (targets.contains(server)) {
            enabled = false;
            Entry.chat(Entry.PREFIX + "Successfully matched with §3" + server);
        } else {
            if (joining || resetting) return;
            joining = true;

            TimeUtils.setTickDelay(() -> {
                joining = false;
                currentHouse++;
                timesJoined++;

                if (timesJoined >= houseCount) {
                    timesJoined = 0;
                    resetting = true;
                    currentHouse = 0;
                    cycles++;

                    TimeUtils.setTickDelay(() -> {
                        timeOfLimbo = System.currentTimeMillis();
                        timeOfNextReset = timeOfLimbo + (Config.matcherTimeBetweenCycles.value.intValue() * 1000L);
                        mc.thePlayer.sendChatMessage("/l housing");
                        TimeUtils.setTickDelay(() -> {
                            resetting = false;
                            currentHouse = 0;
                            timesJoined = 0;
                            joinHouse();
                        }, Config.matcherTimeBetweenCycles.value.intValue() * 20);
                    }, 5);

                    return;
                }

                joinHouse();
            }, 10);
        }

    }

}
