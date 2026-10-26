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

    public Component(String name, Category category) {
        this.name = name;
        this.category = category;
    }
    public Component(String name, Category category, String description) {
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

    public boolean hoveringOn(int mx, int my, int targetX, int targetY, int width, int height) {
        return my >= targetY && my < targetY + height && mx >= targetX && mx < targetX + width;
    }
    public boolean hoveringOn(int mx, int my, Component comp) {
        return my >= comp.y && my < comp.y + comp.componentHeight && mx >= comp.x && mx < comp.x + (300 - ClickGUI.sidebarWidth);
    }
    public boolean hoveringOn(int mx, int my, Component comp, int indent) {
        return my >= comp.y && my < comp.y + comp.componentHeight && mx >= comp.x && mx < comp.x + (300 - ClickGUI.sidebarWidth - indent);
    }
    public abstract JsonElement serialize();
    public abstract void load(JsonElement element);
}
