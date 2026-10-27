package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.ColorWheel;
import lol.ethan.may.render.huds.HUDHelper;
import lol.ethan.may.utils.GuiUtils;

import java.util.function.Consumer;

public class ColorComponent extends Button {
    public int color;
    public Consumer<Integer> consumer;
    public ColorComponent(final String name, final Category category) {
        super(name, category);
    }
    public ColorComponent(final String name, final Category category, final Consumer<Integer> consumer) {
        super(name, category);
        this.consumer = consumer;
    }
    public ColorComponent(final String name, final Category category, final Consumer<Integer> consumer, final int defaultColor) {
        super(name, category);
        this.consumer = consumer;
        color = defaultColor;
    }
    public ColorComponent(final String name, final Category category, final int defaultColor) {
        super(name, category);
        color = defaultColor;
    }

    @Override
    public void drawComponent(final int componentX, final int componentY, final int guiX, final int guiY, final int guiWidth, final int guiHeight) {
        final int rowWidth = guiWidth - ClickGUI.sidebarWidth;
        final int boxX = componentX + rowWidth - 16;
        final int boxY = componentY + (this.componentHeight - 10) / 2;

        HUDHelper.drawRect(boxX, boxY, 10, 10, this.color);
        this.mc.fontRendererObj.drawStringWithShadow(
                this.name,
                componentX + 12,
                componentY + (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2f,
                -1
        );
    }

    @Override
    public void onClick() {
        this.mc.displayGuiScreen(null);
        GuiUtils.openCustomScreen(new ColorWheel(i -> {
            this.color = i;
            if (consumer != null) this.consumer.accept(i);
        }));
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.color);
    }

    @Override
    public void load(final JsonElement element) {
        this.color = element.getAsInt();

        if (consumer != null) this.consumer.accept(this.color);
    }
}
