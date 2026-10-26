package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Component {
    public String name;
    public List<String> description = new ArrayList<>();
    public int componentHeight = 20;
    public Category category;
    public final Minecraft mc = Minecraft.getMinecraft();
    public float x, y;

    public Component(final String name, final Category category) {
        this.name = name;
        this.category = category;
    }
    public Component(final String name, final Category category, final String description) {
        this.name = name;
        this.category = category;
        this.description = new ArrayList<>(Arrays.asList(description.split("\n")));
    }
    public boolean shouldDraw() {
        return true;
    }
    public abstract void init(ClickGUI clickGUI);
    public abstract void drawComponent(int componentX, int componentY, int guiX, int guiY, int guiWidth, int guiHeight);
    public abstract void onClick();
    public abstract void onRightClick();
    public abstract void onKeyPress(char c, int code);
    public abstract void onMouseClick(int x, int y, int button);
    public abstract void onScroll(int direction);

    public boolean hoveringOn(final int mx, final int my, final int targetX, final int targetY, final int width, final int height) {
        return my >= targetY && my < targetY + height && mx >= targetX && mx < targetX + width;
    }
    public boolean hoveringOn(final int mx, final int my, final Component comp) {
        return my >= comp.y && my < comp.y + comp.componentHeight && mx >= comp.x && mx < comp.x + (300 - ClickGUI.sidebarWidth);
    }
    public boolean hoveringOn(final int mx, final int my, final Component comp, final int indent) {
        return my >= comp.y && my < comp.y + comp.componentHeight && mx >= comp.x && mx < comp.x + (300 - ClickGUI.sidebarWidth - indent);
    }
    public abstract JsonElement serialize();
    public abstract void load(JsonElement element);
}
