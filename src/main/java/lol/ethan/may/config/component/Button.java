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
    public boolean enabled;
    public Consumer<Boolean> consumer;
    public Button(final String name, final Category category) {
        super(name, category);
    }

    @Override
    public void init(final ClickGUI clickGUI) {

    }

    public Button(final String name, final Category category, final boolean enabled) {
        super(name, category);
        this.enabled = enabled;
    }
    public Button(final String name, final Category category, final Consumer<Boolean> consumer) {
        super(name, category);
        this.consumer = consumer;
    }
    public Button(final String name, final Category category, final boolean enabled, final Consumer<Boolean> consumer) {
        super(name, category);
        this.consumer = consumer;
        this.enabled = enabled;
    }
    public Button(final String name, final Category category, final boolean enabled, final Consumer<Boolean> consumer, final String description) {
        super(name, category, description);
        this.consumer = consumer;
        this.enabled = enabled;
    }
    public Button(final String name, final Category category, final boolean enabled, final String description) {
        super(name, category, description);
        this.enabled = enabled;
    }

    @Override
    public void drawComponent(final int componentX, final int componentY, final int guiX, final int guiY, final int guiWidth, final int guiHeight) {
        final int rowWidth = guiWidth - ClickGUI.sidebarWidth;
        final int boxX = componentX + rowWidth - 16;
        final int boxY = componentY + (this.componentHeight - 10) / 2;

        HUDHelper.drawRect(boxX, boxY, 10, 10, this.enabled ? ENABLED_COLOR : DISABLED_COLOR);
        this.mc.fontRendererObj.drawStringWithShadow(
                this.name,
                componentX + 12,
                componentY + (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2f,
                -1
        );
    }

    @Override
    public void onClick() {
        this.enabled = !this.enabled;
        if (null != consumer) this.consumer.accept(this.enabled);
        Entry.playClickSound();
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onKeyPress(final char c, final int code) {

    }

    @Override
    public void onMouseClick(final int x, final int y, final int button) {

    }

    @Override
    public void onScroll(final int direction) {

    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.enabled);
    }

    @Override
    public void load(final JsonElement element) {
        this.enabled = element.getAsBoolean();
        if (null != consumer) this.consumer.accept(this.enabled);
    }   
}
