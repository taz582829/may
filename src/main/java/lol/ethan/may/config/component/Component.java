package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Component {
    public final Minecraft mc = Minecraft.getMinecraft();
    public String name;
    public List<String> description = new ArrayList<>();
    public int componentHeight = 20;
    public Category category;
    public float x;
    public float y;

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

    public abstract void init(ClickGUI var1);

    public abstract void drawComponent(int var1, int var2, int var3, int var4, int var5, int var6);

    public abstract void onClick();

    public abstract void onRightClick();

    public abstract void onKeyPress(char var1, int var2);

    public abstract void onMouseClick(int var1, int var2, int var3);

    public abstract void onScroll(int var1);

    public boolean hoveringOn(int mx, int my, int targetX, int targetY, int width, int height) {
        return my >= targetY && my < targetY + height && mx >= targetX && mx < targetX + width;
    }

    public boolean hoveringOn(int mx, int my, Component comp) {
        return (float) my >= comp.y && (float) my < comp.y + (float) comp.componentHeight && (float) mx >= comp.x && (float) mx < comp.x + (float) (300 - ClickGUI.sidebarWidth);
    }

    public boolean hoveringOn(int mx, int my, Component comp, int indent) {
        return (float) my >= comp.y && (float) my < comp.y + (float) comp.componentHeight && (float) mx >= comp.x && (float) mx < comp.x + (float) (300 - ClickGUI.sidebarWidth - indent);
    }

    public abstract JsonElement serialize();

    public abstract void load(JsonElement var1);
}

