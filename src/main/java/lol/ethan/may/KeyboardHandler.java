package lol.ethan.may;

import lol.ethan.may.config.Config;
import lol.ethan.may.config.component.Component;
import lol.ethan.may.config.component.KeybindComponent;
import lol.ethan.may.events.KeyboardEvent;
import lol.ethan.may.events.SubscribeEvent;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class KeyboardHandler {
    @SubscribeEvent
    public void onKeyPress(final KeyboardEvent event) {
        if (event.getBind() == Keyboard.KEY_NONE || Minecraft.getMinecraft().currentScreen != null) return;
        for (final Component comp : Config.getAllComponents()) {
            if (!(comp instanceof KeybindComponent)) continue;
            final KeybindComponent keybind = (KeybindComponent) comp;
            if (keybind.bind != event.getBind() || !event.getState()) continue;
            keybind.action.run();
        }
    }
}

