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
        int boxY = componentY + (componentHeight - 10) / 2;

        HUDHelper.drawRect(boxX, boxY, 10, 10, color);
        mc.fontRendererObj.drawStringWithShadow(
                name,
                componentX + 12,
                componentY + (componentHeight - mc.fontRendererObj.FONT_HEIGHT) / 2f,
                -1
        );
    }

    @Override
    public void onClick() {
        mc.displayGuiScreen(null);
        GuiUtils.openCustomScreen(new ColorWheel(i -> {
            color = i;
            if (consumer != null) consumer.accept(i);
        }));
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(color);
    }

    @Override
    public void load(JsonElement element) {
        color = element.getAsInt();

        if (consumer != null) consumer.accept(color);
    }
}
