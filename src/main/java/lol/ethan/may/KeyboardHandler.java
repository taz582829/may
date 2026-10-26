package lol.ethan.may;

import lol.ethan.may.config.Config;
import lol.ethan.may.config.component.Component;
import lol.ethan.may.config.component.KeybindComponent;
import lol.ethan.may.events.KeyboardEvent;
import lol.ethan.may.events.SubscribeEvent;

public class KeyboardHandler {
    @SubscribeEvent
    public void onKeyPress(KeyboardEvent event) {
        for (Component comp : Config.getAllComponents()) {
            if (!(comp instanceof KeybindComponent)) continue;
            KeybindComponent keybind = (KeybindComponent) comp;
            if (keybind.bind != event.getBind() || !event.getState()) continue;
            keybind.action.run();
        }
    }
}

