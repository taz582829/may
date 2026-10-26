package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.component.text.NumberTextBox;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.render.huds.BasicHud;
import lol.ethan.may.render.huds.HUDHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HUDComponent extends Component {
    public final List<Component> DEFAULT_HUD_COMPONENTS;
    public BasicHud hud;

    public HUDComponent(String name, Category category, BasicHud hud) {
        super(name, category);
        this.hud = hud;
        EventBus.subscribe(hud);
        this.DEFAULT_HUD_COMPONENTS = new ArrayList<>(Arrays.asList(new Button("Background", category, val -> {
            hud.background = val;
        }), new Button("Text Shadow", category, val -> {
            hud.shadow = val;
        }), new NumberTextBox("Padding", category, NumberTextBox.TextBoxType.INT, 2, p -> {
            hud.padding = p.intValue();
        }), new Button("Right Align", category, val -> {
            hud.rightAlign = val;
        }), new HUDMove("Move HUD", category, hud), new NumberTextBox("Scale", category, NumberTextBox.TextBoxType.FLOAT, 1.0, num -> {
            hud.scale = num.floatValue();
        }), new ColorComponent("Background Color", category, i -> {
            hud.bgColor = i;
        }), new NumberTextBox("Background Alpha", category, NumberTextBox.TextBoxType.INT, 50, val -> {
            hud.bgAlpha = val.intValue();
        })));
    }

    public List<Component> getHudComponents() {
        ArrayList<Component> components = new ArrayList<>(this.DEFAULT_HUD_COMPONENTS);
        try {
            for (Field field : this.hud.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(this.hud);
                if (!Modifier.isPublic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers()) || !(value instanceof Component))
                    continue;
                components.add((Component) value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return components;
    }

    @Override
    public void init(ClickGUI clickGUI) {
    }

    @Override
    public void drawComponent(int componentX, int componentY, int guiX, int guiY, int guiWidth, int guiHeight) {
        int rowWidth = guiWidth - ClickGUI.sidebarWidth;
        int boxX = componentX + rowWidth - 16;
        int boxY = componentY + (this.componentHeight - 10) / 2;
        HUDHelper.drawRect(boxX, boxY, 10, 10, this.hud.enabled ? Button.ENABLED_COLOR : Button.DISABLED_COLOR);
        this.mc.fontRendererObj.drawStringWithShadow(this.name + " §e(Right Click)", (float) (componentX + 12), (float) componentY + (float) (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2.0f, -1);
    }

    @Override
    public void onClick() {
        this.hud.enabled = !this.hud.enabled;
        Entry.playClickSound();
    }

    @Override
    public void onRightClick() {
        ClickGUI.currentHud = this;
        ClickGUI.scrollOffset = 0;
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
        JsonObject obj = this.getHUDObject();
        try {
            for (Field field : this.hud.getClass().getDeclaredFields()) {
                Object value;
                int mods = field.getModifiers();
                if (!Modifier.isPublic(mods) || Modifier.isTransient(mods) || !((value = field.get(this.hud)) instanceof Component))
                    continue;
                Component comp = (Component) value;
                obj.add(field.getName(), comp.serialize());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    private JsonObject getHUDObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty("enabled", Boolean.valueOf(this.hud.enabled));
        obj.addProperty("background", Boolean.valueOf(this.hud.background));
        obj.addProperty("shadow", Boolean.valueOf(this.hud.shadow));
        obj.addProperty("x", Float.valueOf(this.hud.x));
        obj.addProperty("y", Float.valueOf(this.hud.y));
        obj.addProperty("scale", Float.valueOf(this.hud.scale));
        obj.addProperty("bgColor", this.hud.bgColor);
        obj.addProperty("bgAlpha", this.hud.bgAlpha);
        obj.addProperty("padding", this.hud.padding);
        obj.addProperty("rightAlign", Boolean.valueOf(this.hud.rightAlign));
        return obj;
    }

    @Override
    public void load(JsonElement element) {
        JsonObject obj = element.getAsJsonObject();
        this.hud.enabled = obj.get("enabled").getAsBoolean();
        this.hud.background = obj.get("background").getAsBoolean();
        this.hud.shadow = obj.get("shadow").getAsBoolean();
        this.hud.x = obj.get("x").getAsFloat();
        this.hud.y = obj.get("y").getAsFloat();
        this.hud.scale = obj.get("scale").getAsFloat();
        this.hud.bgColor = obj.get("bgColor").getAsInt();
        this.hud.bgAlpha = obj.get("bgAlpha").getAsInt();
        this.hud.padding = obj.get("padding").getAsInt();
        this.hud.rightAlign = obj.get("rightAlign").getAsBoolean();
        try {
            for (Field field : this.hud.getClass().getDeclaredFields()) {
                Object value;
                int mods = field.getModifiers();
                if (!Modifier.isPublic(mods) || Modifier.isTransient(mods) || !((value = field.get(this.hud)) instanceof Component))
                    continue;
                Component comp = (Component) value;
                comp.load(obj.get(field.getName()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

