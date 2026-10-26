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
    public Consumer<Integer> consumer = null;

    public ColorComponent(String name, Category category) {
        super(name, category);
    }

    public ColorComponent(String name, Category category, Consumer<Integer> consumer) {
        super(name, category);
        this.consumer = consumer;
    }

    public ColorComponent(String name, Category category, Consumer<Integer> consumer, int defaultColor) {
        super(name, category);
        this.consumer = consumer;
        this.color = defaultColor;
    }

    public ColorComponent(String name, Category category, int defaultColor) {
        super(name, category);
        this.color = defaultColor;
    }

    @Override
    public void drawComponent(int componentX, int componentY, int guiX, int guiY, int guiWidth, int guiHeight) {
        int rowWidth = guiWidth - ClickGUI.sidebarWidth;
        int boxX = componentX + rowWidth - 16;
        int boxY = componentY + (this.componentHeight - 10) / 2;
        HUDHelper.drawRect(boxX, boxY, 10, 10, this.color);
        this.mc.fontRendererObj.drawStringWithShadow(this.name, (float) (componentX + 12), (float) componentY + (float) (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2.0f, -1);
    }

    @Override
    public void onClick() {
        this.mc.displayGuiScreen(null);
        GuiUtils.openCustomScreen(new ColorWheel(i -> {
            this.color = i;
            if (this.consumer != null) {
                this.consumer.accept(i);
            }
        }));
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.color);
    }

    @Override
    public void load(JsonElement element) {
        this.color = element.getAsInt();
        if (this.consumer != null) {
            this.consumer.accept(this.color);
        }
    }
}

