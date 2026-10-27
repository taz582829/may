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
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lol.ethan.may.config.component.Button.DISABLED_COLOR;
import static lol.ethan.may.config.component.Button.ENABLED_COLOR;

public class HUDComponent extends Component {
    public BasicHud hud;
    public final List<Component> DEFAULT_HUD_COMPONENTS;
    public HUDComponent(final String name, final Category category, final BasicHud hud) {
        super(name, category);
        this.hud = hud;
        EventBus.subscribe(hud);
        this.DEFAULT_HUD_COMPONENTS = new ArrayList<>(Arrays.asList(
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
        final List<Component> components = new ArrayList<>(this.DEFAULT_HUD_COMPONENTS);
        try {
            for (final Field field : this.hud.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                final Object value = field.get(this.hud);
                // fields are expected to be public, not static, and not transient
                if (!Modifier.isPublic(field.getModifiers()) || Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) continue;
                if (value instanceof Component) components.add((Component) value);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return components;
    }

    @Override
    public void init(final ClickGUI clickGUI) {

    }

    @Override
    public void drawComponent(final int componentX, final int componentY, final int guiX, final int guiY, final int guiWidth, final int guiHeight) {
        final int rowWidth = guiWidth - ClickGUI.sidebarWidth;
        final int boxX = componentX + rowWidth - 16;
        final int boxY = componentY + (this.componentHeight - 10) / 2;

        HUDHelper.drawRect(boxX, boxY, 10, 10, this.hud.enabled ? ENABLED_COLOR : DISABLED_COLOR);
        this.mc.fontRendererObj.drawStringWithShadow(
                this.name + " §e(Right Click)",
                componentX + 12,
                componentY + (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2f,
                -1
        );
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
        final JsonObject obj = this.getHUDObject();

        try {
            for (final Field field : this.hud.getClass().getDeclaredFields()) {
                final int mods = field.getModifiers();
                if (Modifier.isPublic(mods) && !Modifier.isTransient(mods)) {
                    final Object value = field.get(this.hud);
                    if (value instanceof Component) {
                        final Component comp = (Component) value;
                        obj.add(field.getName(), comp.serialize());
                    }
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    private @NotNull JsonObject getHUDObject() {
        final JsonObject obj = new JsonObject();
        obj.addProperty("enabled", this.hud.enabled);
        obj.addProperty("background", this.hud.background);
        obj.addProperty("shadow", this.hud.shadow);
        obj.addProperty("x", this.hud.x);
        obj.addProperty("y", this.hud.y);
        obj.addProperty("scale", this.hud.scale);
        obj.addProperty("bgColor", this.hud.bgColor);
        obj.addProperty("bgAlpha", this.hud.bgAlpha);
        obj.addProperty("padding", this.hud.padding);
        obj.addProperty("rightAlign", this.hud.rightAlign);
        return obj;
    }

    @Override
    public void load(final JsonElement element) {
        final JsonObject obj = element.getAsJsonObject();
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
            for (final Field field : this.hud.getClass().getDeclaredFields()) {
                final int mods = field.getModifiers();
                if (Modifier.isPublic(mods) && !Modifier.isTransient(mods)) {
                    final Object value = field.get(this.hud);
                    if (value instanceof Component) {
                        final Component comp = (Component) value;
                        comp.load(obj.get(field.getName()));
                    }
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
