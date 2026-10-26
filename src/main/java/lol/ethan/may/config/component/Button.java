package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.render.huds.HUDHelper;

import java.awt.*;
import java.util.function.Consumer;

public class Button extends Component {
    public static final Color ENABLED_COLOR = new Color(45, 180, 100, 160);
    public static final Color DISABLED_COLOR = new Color(255, 0, 0, 160);
    public boolean enabled = false;
    public Consumer<Boolean> consumer = null;
    public Button(String name, Category category) {
        super(name, category);
    }

    @Override
    public void init(ClickGUI clickGUI) {

    }

    public Button(String name, Category category, boolean enabled) {
        super(name, category);
        this.enabled = enabled;
    }
    public Button(String name, Category category, Consumer<Boolean> consumer) {
        super(name, category);
        this.consumer = consumer;
    }
    public Button(String name, Category category, boolean enabled, Consumer<Boolean> consumer) {
        super(name, category);
        this.consumer = consumer;
        this.enabled = enabled;
    }
    public Button(String name, Category category, boolean enabled, Consumer<Boolean> consumer, String description) {
        super(name, category, description);
        this.consumer = consumer;
        this.enabled = enabled;
    }
    public Button(String name, Category category, boolean enabled, String description) {
        super(name, category, description);
        this.enabled = enabled;
    }

    @Override
    public void drawComponent(int componentX, int componentY, int guiX, int guiY, int guiWidth, int guiHeight) {
        int rowWidth = guiWidth - ClickGUI.sidebarWidth;
        int boxX = componentX + rowWidth - 16;
        int boxY = componentY + (componentHeight - 10) / 2;

        HUDHelper.drawRect(boxX, boxY, 10, 10, enabled ? ENABLED_COLOR : DISABLED_COLOR);
        mc.fontRendererObj.drawStringWithShadow(
                name,
                componentX + 12,
                componentY + (componentHeight - mc.fontRendererObj.FONT_HEIGHT) / 2f,
                -1
        );
    }

    @Override
    public void onClick() {
        enabled = !enabled;
        if (consumer != null) consumer.accept(enabled);
        Entry.playClickSound();
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onKeyPress(char c, int code) {

    }

    @Override
    public void onMouseClick(int x, int y, int button) {

    }

    @Override
    public void onScroll(int direction) {

    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(enabled);
    }

    @Override
    public void load(JsonElement element) {
        enabled = element.getAsBoolean();
        if (consumer != null) consumer.accept(enabled);
    }   
}
