package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.component.text.NumberTextBox;
import lol.ethan.may.render.huds.BasicHud;
import lol.ethan.may.render.huds.HUDHelper;
import lol.ethan.may.events.EventBus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lol.ethan.may.config.component.Button.ENABLED_COLOR;
import static lol.ethan.may.config.component.Button.DISABLED_COLOR;

public class HUDComponent extends Component {
    public BasicHud hud;
    public final List<Component> DEFAULT_HUD_COMPONENTS;
    public HUDComponent(String name, Category category, BasicHud hud) {
        super(name, category);
        this.hud = hud;
        EventBus.subscribe(hud);
        DEFAULT_HUD_COMPONENTS = new ArrayList<>(Arrays.asList(
                new Button("Background", category, (val) -> hud.background = val),
                new Button("Text Shadow", category, (val) -> hud.shadow = val),
                new NumberTextBox("Padding", category, NumberTextBox.TextBoxType.INT, 2, p -> hud.padding = p.intValue()),
                new Button("Right Align", category, (val) -> hud.rightAlign = val),
                new HUDMove("Move HUD", category, hud),
                new NumberTextBox("Scale", category, NumberTextBox.TextBoxType.FLOAT, 1.0, (num) -> hud.scale = num.floatValue()),
                new ColorComponent("Background Color", category, i -> hud.bgColor = i),
                new NumberTextBox("Background Alpha", category, NumberTextBox.TextBoxType.INT, 50, val -> hud.bgAlpha = val.intValue())
        ));
    }



    public List<Component> getHudComponents() {
        List<Component> components = new ArrayList<>(DEFAULT_HUD_COMPONENTS);
        try {
            for (Field field : hud.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(hud);
                // fields are expected to be public, not static, and not transient
                if (!Modifier.isPublic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) continue;
                if (value instanceof Component) components.add((Component) value);
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
        int boxY = componentY + (componentHeight - 10) / 2;

        HUDHelper.drawRect(boxX, boxY, 10, 10, hud.enabled ? ENABLED_COLOR : DISABLED_COLOR);
        mc.fontRendererObj.drawStringWithShadow(
                name + " §e(Right Click)",
                componentX + 12,
                componentY + (componentHeight - mc.fontRendererObj.FONT_HEIGHT) / 2f,
                -1
        );
    }

    @Override
    public void onClick() {
        hud.enabled = !hud.enabled;
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
        JsonObject obj = getHUDObject();

        try {
            for (Field field : hud.getClass().getDeclaredFields()) {
                int mods = field.getModifiers();
                if (Modifier.isPublic(mods) && !Modifier.isTransient(mods)) {
                    Object value = field.get(hud);
                    if (value instanceof Component) {
                        Component comp = (Component) value;
                        obj.add(field.getName(), comp.serialize());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    private @NotNull JsonObject getHUDObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty("enabled", hud.enabled);
        obj.addProperty("background", hud.background);
        obj.addProperty("shadow", hud.shadow);
        obj.addProperty("x", hud.x);
        obj.addProperty("y", hud.y);
        obj.addProperty("scale", hud.scale);
        obj.addProperty("bgColor", hud.bgColor);
        obj.addProperty("bgAlpha", hud.bgAlpha);
        obj.addProperty("padding", hud.padding);
        obj.addProperty("rightAlign", hud.rightAlign);
        return obj;
    }

    @Override
    public void load(JsonElement element) {
        JsonObject obj = element.getAsJsonObject();
        hud.enabled = obj.get("enabled").getAsBoolean();
        hud.background = obj.get("background").getAsBoolean();
        hud.shadow = obj.get("shadow").getAsBoolean();
        hud.x = obj.get("x").getAsFloat();
        hud.y = obj.get("y").getAsFloat();
        hud.scale = obj.get("scale").getAsFloat();
        hud.bgColor = obj.get("bgColor").getAsInt();
        hud.bgAlpha = obj.get("bgAlpha").getAsInt();
        hud.padding = obj.get("padding").getAsInt();
        hud.rightAlign = obj.get("rightAlign").getAsBoolean();
        try {
            for (Field field : hud.getClass().getDeclaredFields()) {
                int mods = field.getModifiers();
                if (Modifier.isPublic(mods) && !Modifier.isTransient(mods)) {
                    Object value = field.get(hud);
                    if (value instanceof Component) {
                        Component comp = (Component) value;
                        comp.load(obj.get(field.getName()));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
