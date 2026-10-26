package lol.ethan.may.config;

import lol.ethan.may.Entry;
import lol.ethan.may.config.component.Component;
import lol.ethan.may.config.component.HUDComponent;
import lol.ethan.may.config.component.KeybindComponent;
import lol.ethan.may.config.component.TextBox;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.KeyboardEvent;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.render.ScissorUtils;
import lol.ethan.may.render.huds.BasicHud;
import lol.ethan.may.render.huds.HUDHelper;
import lol.ethan.may.utils.GuiUtils;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends GuiScreen {
    public static final int xSize = 300;
    public static final int ySize = 185;
    public static int sidebarWidth = 75;
    public static int scrollOffset = 0;
    public static HUDComponent currentHud;
    private final List<CategoryInfo> categoryInfo = new ArrayList<>();
    private final Color backgroundColor = new Color(25, 26, 28, 255);
    private final Color frostTextColor = new Color(162, 103, 216);
    private final Color hoveringOverColor = new Color(204, 203, 253, 255);
    private final Color enabledColor = new Color(108, 181, 131);
    private final Color selectedColor = new Color(15961562);
    private final Color blank = new Color(0, 0, 0, 0);
    private final int categorySize = 20;
    private final List<Component> components = new ArrayList<>();
    private int x;
    private int y;
    private int categorySelected = 0;

    public ClickGUI() {
        currentHud = null;
    }

    public static void open() {
        GuiUtils.openCustomScreen(new ClickGUI());
    }

    public void initGui() {
        EventBus.subscribe(this);
        Keyboard.enableRepeatEvents(true);
        this.components.clear();
        try {
            for (Field field : Config.class.getFields()) {
                Component comp = (Component) field.get(null);
                this.components.add(comp);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Component comp : this.getCurrentComponents()) {
            comp.init(this);
        }
        this.categoryInfo.clear();
        this.x = this.width / 2 - 150;
        this.y = this.height / 2 - 92;
        int currentCategoryY = this.y + 20;
        sidebarWidth = 75;
        for (Category cat : Category.values()) {
            sidebarWidth = Math.max(sidebarWidth, this.fontRendererObj.getStringWidth(cat.name()) + 18);
            this.categoryInfo.add(new CategoryInfo(cat.name(), currentCategoryY, currentCategoryY + 20, cat));
            currentCategoryY += 20;
        }
    }

    private List<Component> getCurrentComponents() {
        return currentHud != null ? currentHud.getHudComponents() : this.components;
    }

    private boolean shouldShow(Component comp, Category selected) {
        if (!comp.shouldDraw()) {
            return false;
        }
        return currentHud != null || comp.category.equals(selected);
    }

    public void drawScreen(int mouseX, int mouseY, float pt) {
        HUDHelper.drawRect(this.x, this.y, 300, 185, this.backgroundColor);
        this.fontRendererObj.drawStringWithShadow("May", (float) (this.x + 10), (float) (this.y + 10), this.frostTextColor.getRGB());
        Category[] categories = Category.values();
        for (Component comp : this.getCurrentComponents()) {
            if (!(comp instanceof TextBox) || !((TextBox) comp).drawingField) continue;
            ((TextBox) comp).drawField(this.x, this.y, 300 - sidebarWidth, 185);
            return;
        }
        for (int i = 0; i < this.categoryInfo.size(); ++i) {
            CategoryInfo info = this.categoryInfo.get(i);
            if (this.categorySelected == i) {
                HUDHelper.drawRect(this.x, info.startingPoint, sidebarWidth, 20, this.selectedColor);
            }
            this.fontRendererObj.drawStringWithShadow(info.name, (float) (this.x + 10), (float) info.startingPoint + (float) (20 - this.fontRendererObj.FONT_HEIGHT) / 2.0f, -1);
        }
        Category selected = categories[this.categorySelected];
        int categoryY = this.y + 20;
        List<Component> components = this.getCurrentComponents();
        ScissorUtils.begin(this.x + sidebarWidth, this.y + 20, 300 - sidebarWidth, 165);
        int visibleIndex = 0;
        try {
            for (Component comp : components) {
                if (!this.shouldShow(comp, selected) || scrollOffset > visibleIndex++) continue;
                comp.x = this.x + sidebarWidth;
                comp.y = categoryY;
                boolean hovering = this.hoveringOn(mouseX, mouseY, (int) comp.x, (int) comp.y, 300 - sidebarWidth, comp.componentHeight);
                comp.drawComponent(this.x + sidebarWidth, categoryY, this.x, this.y, 300, 185);
                if (hovering) {
                    int left = this.x + sidebarWidth;
                    int right = left + 300 - sidebarWidth - 1;
                    int bottom = categoryY + comp.componentHeight - 1;
                    int color = -1;
                    this.drawHorizontalLine(left, right, categoryY, color);
                    this.drawHorizontalLine(left, right, bottom, color);
                    this.drawVerticalLine(left, categoryY, bottom, color);
                    this.drawVerticalLine(right, categoryY, bottom, color);
                    if (comp.description != null && comp.description.size() > 0) {
                        this.drawHoveringText(comp.description, mouseX, mouseY);
                    }
                }
                categoryY += comp.componentHeight;
            }
        } finally {
            ScissorUtils.end();
        }
        super.drawScreen(mouseX, mouseY, pt);
    }

    protected void mouseClicked(int mx, int my, int button) throws IOException {
        TextBox<?> active = this.getActiveTextBox();
        if (active != null) {
            active.onMouseClick(mx, my, button);
            if (this.hoveringOn(mx, my, (int) active.x, (int) active.y, 300 - sidebarWidth, active.componentHeight)) {
                if (button == 0) {
                    active.onClick();
                } else if (button == 1) {
                    active.onRightClick();
                }
            }
            return;
        }
        Category selected = Category.values()[this.categorySelected];
        List<Component> components = this.getCurrentComponents();
        int visibleIndex = 0;
        for (Component comp : components) {
            int height;
            int[] dims;
            int width;
            if (!this.shouldShow(comp, selected) || scrollOffset > visibleIndex++) continue;
            comp.onMouseClick(mx, my, button);
            if (this.hoveringOn(mx, my, (int) comp.x, (int) comp.y, 300 - sidebarWidth, comp.componentHeight)) {
                if (button == 0) {
                    comp.onClick();
                } else if (button == 1) {
                    comp.onRightClick();
                }
            }
            if (!(comp instanceof HUDComponent)) continue;
            HUDComponent hudComponent = (HUDComponent) comp;
            BasicHud hud = hudComponent.hud;
            float[] coords = hud.overrideCoords(hud.x, hud.y);
            if (!this.hoveringOn(mx, my, (int) coords[0], (int) coords[1], width = (dims = hud.getDimensions())[0], height = dims[1]))
                continue;
            GuiUtils.openCustomScreen(new DragElement(String.join("\n", hud.lineCache), hud.x, hud.y, hud));
        }
        for (int i = 0; i < this.categoryInfo.size(); ++i) {
            CategoryInfo info = this.categoryInfo.get(i);
            if (!this.hoveringOn(mx, my, this.x, info.startingPoint, sidebarWidth, 20)) continue;
            this.categorySelected = i;
            scrollOffset = 0;
            currentHud = null;
            Entry.playClickSound();
            break;
        }
        super.mouseClicked(mx, my, button);
    }

    private TextBox<?> getActiveTextBox() {
        for (Component comp : this.getCurrentComponents()) {
            if (!(comp instanceof TextBox) || !((TextBox) comp).drawingField) continue;
            return (TextBox) comp;
        }
        return null;
    }

    private boolean isTextBoxOpen() {
        return this.getActiveTextBox() != null;
    }

    public boolean hoveringOn(int mx, int my, int targetX, int targetY, int width, int height) {
        return my >= targetY && my < targetY + height && mx >= targetX && mx < targetX + width;
    }

    @SubscribeEvent
    public void onKeyPress(KeyboardEvent event) throws IOException {
        Category selected;
        char c = (char) event.getBind();
        int code = event.getBind();
        if (code == 1 && currentHud != null) {
            selected = Category.values()[this.categorySelected];
            for (Component comp : this.getCurrentComponents()) {
                if (!comp.category.equals(selected) || !(comp instanceof KeybindComponent)) continue;
                KeybindComponent keybind = (KeybindComponent) comp;
                if (!keybind.listening) continue;
                keybind.onKeyPress('\u0000', 0);
                return;
            }
            currentHud = null;
            scrollOffset = 0;
        }
        selected = Category.values()[this.categorySelected];
        for (Component comp : this.getCurrentComponents()) {
            if (!comp.category.equals(selected)) continue;
            comp.onKeyPress(c, code);
            if (!(comp instanceof KeybindComponent) || !((KeybindComponent) comp).listening) continue;
            return;
        }
        super.keyTyped(c, code);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void onGuiClosed() {
        EventBus.unsubscribe(this);
        Keyboard.enableRepeatEvents(false);
        for (Component comp : this.components) {
            if (!(comp instanceof TextBox)) continue;
            TextBox box = (TextBox) comp;
            box.finish();
        }
        super.onGuiClosed();
        Config.save();
        currentHud = null;
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            scrollOffset += wheel > 0 ? -1 : 1;
            scrollOffset = Math.max(0, scrollOffset);
        }
    }

    private static class CategoryInfo {
        public String name;
        public int startingPoint;
        public int endingPoint;
        public Category category;

        public CategoryInfo(String name, int startingPoint, int endingPoint, Category category) {
            this.name = name;
            this.startingPoint = startingPoint;
            this.endingPoint = endingPoint;
            this.category = category;
        }
    }
}

