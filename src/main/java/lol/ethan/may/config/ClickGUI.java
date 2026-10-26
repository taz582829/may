package lol.ethan.may.config;

import lol.ethan.may.Entry;
import lol.ethan.may.config.component.Component;
import lol.ethan.may.config.component.HUDComponent;
import lol.ethan.may.config.component.TextBox;
import lol.ethan.may.render.ScissorUtils;
import lol.ethan.may.render.huds.HUDHelper;
import lol.ethan.may.utils.GuiUtils;
import lol.ethan.may.utils.JsonUtils;
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
    private int x;
    private int y;
    public static int sidebarWidth = 75;
    private final List<CategoryInfo> categoryInfo = new ArrayList<>();
    private final Color backgroundColor = new Color(25, 26, 28, 255);
    private final Color mayTextColor = new Color(0xC8A2C8);
    private final Color selectedColor = new Color(0xE666BC);
    private int categorySelected = 0;
    private final int categorySize = 20;
    public static int scrollOffset = 0;
    private final List<Component> components = new ArrayList<>();
    public static HUDComponent currentHud;

    public ClickGUI() {
        currentHud = null;
    }
    public static void open() {
        GuiUtils.openCustomScreen(new ClickGUI());
    }
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        components.clear();
        try {
            for (Field field : Config.class.getFields()) {
                Component comp = (Component) field.get(null);
                components.add(comp);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Component comp : getCurrentComponents()) {
            comp.init(this);
        }
        categoryInfo.clear();
        x = (width / 2) - (xSize / 2);
        y = (height / 2) - (ySize / 2);
        int currentCategoryY = y + categorySize;
        sidebarWidth = 75;
        for (Category cat : Category.values()) {
            sidebarWidth = Math.max(sidebarWidth, fontRendererObj.getStringWidth(cat.name()) + 18);

            categoryInfo.add(new CategoryInfo(
                    cat.name(),
                    currentCategoryY,
                    currentCategoryY + categorySize,
                    cat
            ));
            currentCategoryY += categorySize;
        }
    }

    private List<Component> getCurrentComponents() {
        return currentHud != null ? currentHud.getHudComponents() : components;
    }

    private boolean shouldShow(Component comp, Category selected) {
        if (!comp.shouldDraw()) {
//            if (comp instanceof HUDComponent) {
//                HUDComponent hud = (HUDComponent) comp;
//                hud.hud.enabled = false;
//            }
            return false;
        }

        return currentHud != null || comp.category.equals(selected);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float pt) {
        HUDHelper.drawRect(x, y, xSize, ySize, backgroundColor);
        fontRendererObj.drawStringWithShadow("May", x + 10, y + 10, mayTextColor.getRGB());

        Category[] categories = Category.values();
        for (Component comp : getCurrentComponents()) {
            if (comp instanceof TextBox && ((TextBox<?>) comp).drawingField) {
                ((TextBox<?>) comp).drawField(x, y, xSize - sidebarWidth, ySize);
                return;
            }
        }
        for (int i = 0; i < categoryInfo.size(); i++) {
            CategoryInfo info = categoryInfo.get(i);
            if (categorySelected == i) {
                HUDHelper.drawRect(x, info.startingPoint, sidebarWidth, categorySize, selectedColor);
            }
            fontRendererObj.drawStringWithShadow(
                    info.name,
                    x + 10,
                    info.startingPoint + (float) (categorySize - fontRendererObj.FONT_HEIGHT) / 2,
                    -1
            );

        }

        Category selected = categories[categorySelected];
        int categoryY = y + categorySize;
        List<Component> components = getCurrentComponents();

        ScissorUtils.begin(x + sidebarWidth, y + categorySize, xSize - sidebarWidth, ySize - categorySize);
        int visibleIndex = 0;
        try {
            for (Component comp : components) {
                if (!shouldShow(comp, selected)) continue;
                if (scrollOffset > visibleIndex++) continue;

                comp.x = x + sidebarWidth;
                comp.y = categoryY;
                boolean hovering = hoveringOn(mouseX, mouseY, (int) comp.x, (int) comp.y, xSize - sidebarWidth, comp.componentHeight);

                comp.drawComponent(
                        x + sidebarWidth,
                        categoryY,
                        x,
                        y,
                        xSize,
                        ySize
                );

                if (hovering) {
                    int left = x + sidebarWidth;
                    int right = left + xSize - sidebarWidth - 1;
                    int bottom = categoryY + comp.componentHeight - 1;
                    int color = -1;

                    drawHorizontalLine(left, right, categoryY, color);
                    drawHorizontalLine(left, right, bottom, color);
                    drawVerticalLine(left, categoryY, bottom, color);
                    drawVerticalLine(right, categoryY, bottom, color);
                }

                categoryY += comp.componentHeight;
            }
        } finally {
            ScissorUtils.end();
        }

        super.drawScreen(mouseX, mouseY, pt);
    }

    @Override
    protected void mouseClicked(int mx, int my, int button) throws IOException {
        TextBox<?> active = getActiveTextBox();

        if (active != null) {
            active.onMouseClick(mx, my, button);
            if (hoveringOn(mx, my, (int) active.x, (int) active.y, xSize - sidebarWidth, active.componentHeight)) {
                if (button == 0) {
                    active.onClick();
                } else if (button == 1) {
                    active.onRightClick();
                }
            }
            return;
        }
        Category selected = Category.values()[categorySelected];
        List<Component> components = getCurrentComponents();
        int visibleIndex = 0;
        for (Component comp : components) {
            if (shouldShow(comp, selected)) {
                if (scrollOffset > visibleIndex++) continue;
                comp.onMouseClick(mx, my, button);
                if (hoveringOn(mx, my, (int) comp.x, (int) comp.y, xSize - sidebarWidth, comp.componentHeight)) {
                    if (button == 0) {
                        comp.onClick();
                    } else if (button == 1) {
                        comp.onRightClick();
                    }
                }
            }
        }
        for (int i = 0; i < categoryInfo.size(); i++) {
            CategoryInfo info = categoryInfo.get(i);
            if (hoveringOn(mx, my, x, info.startingPoint, sidebarWidth, categorySize)) {
                categorySelected = i;
                scrollOffset = 0;
                currentHud = null;
                Entry.playClickSound();
                break;
            }
        }


        super.mouseClicked(mx, my, button);
    }
    private TextBox<?> getActiveTextBox() {
        for (Component comp : getCurrentComponents()) {
            if (comp instanceof TextBox && ((TextBox<?>) comp).drawingField) {
                return (TextBox<?>) comp;
            }
        }
        return null;
    }
    private boolean isTextBoxOpen() {
        return getActiveTextBox() != null;
    }
    public boolean hoveringOn(int mx, int my, int targetX, int targetY, int width, int height) {
        return my >= targetY && my < targetY + height && mx >= targetX && mx < targetX + width;
    }

    @Override
    protected void keyTyped(char c, int i) throws IOException {
        if (i == Keyboard.KEY_ESCAPE && currentHud != null) {
            currentHud = null;
            scrollOffset = 0;
            return;
        }
        super.keyTyped(c, i);
        Category selected = Category.values()[categorySelected];
        for (Component comp : getCurrentComponents()) {
            if (comp.category.equals(selected)) {
                comp.onKeyPress(c, i);
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        for (Component comp : components) {
            if (comp instanceof TextBox) {
                TextBox<?> box = (TextBox<?>) comp;
                box.finish();
            }
        }
        super.onGuiClosed();
        Config.save();
        Entry.debug(JsonUtils.GSON_PRETTY_PRINTING.toJson(Config.serialize()));
        currentHud = null;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            scrollOffset += (wheel > 0 ? -1 : 1);
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
