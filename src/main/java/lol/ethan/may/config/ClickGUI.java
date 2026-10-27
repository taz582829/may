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
    private int categorySelected;
    private final int categorySize = 20;
    public static int scrollOffset;
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
        this.components.clear();
        try {
            for (final Field field : Config.class.getFields()) {
                final Component comp = (Component) field.get(null);
                this.components.add(comp);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        for (final Component comp : this.getCurrentComponents()) {
            comp.init(this);
        }
        this.categoryInfo.clear();
        this.x = (this.width / 2) - (xSize / 2);
        this.y = (this.height / 2) - (ySize / 2);
        int currentCategoryY = this.y + this.categorySize;
        sidebarWidth = 75;
        for (final Category cat : Category.values()) {
            sidebarWidth = Math.max(sidebarWidth, this.fontRendererObj.getStringWidth(cat.name()) + 18);

            this.categoryInfo.add(new CategoryInfo(
                    cat.name(),
                    currentCategoryY,
                    currentCategoryY + this.categorySize,
                    cat
            ));
            currentCategoryY += this.categorySize;
        }
    }

    private List<Component> getCurrentComponents() {
        return currentHud != null ? currentHud.getHudComponents() : this.components;
    }

    private boolean shouldShow(final Component comp, final Category selected) {
        if (!comp.shouldDraw()) {
//            if (comp instanceof HUDComponent) {
//                HUDComponent hud = (HUDComponent) comp;
//                hud.hud.enabled = false;
//            }
            return false;
        }

        return currentHud != null || comp.category == selected;
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float pt) {
        HUDHelper.drawRect(this.x, this.y, xSize, ySize, this.backgroundColor);
        this.fontRendererObj.drawStringWithShadow("May", this.x + 10, this.y + 10, this.mayTextColor.getRGB());

        final Category[] categories = Category.values();
        for (final Component comp : this.getCurrentComponents()) {
            if (comp instanceof TextBox && ((TextBox<?>) comp).drawingField) {
                ((TextBox<?>) comp).drawField(this.x, this.y, xSize - sidebarWidth, ySize);
                return;
            }
        }
        for (int i = 0; i < this.categoryInfo.size(); i++) {
            final CategoryInfo info = this.categoryInfo.get(i);
            if (this.categorySelected == i) {
                HUDHelper.drawRect(this.x, info.startingPoint, sidebarWidth, this.categorySize, this.selectedColor);
            }
            this.fontRendererObj.drawStringWithShadow(
                    info.name,
                    this.x + 10,
                    info.startingPoint + (float) (this.categorySize - this.fontRendererObj.FONT_HEIGHT) / 2,
                    -1
            );

        }

        final Category selected = categories[this.categorySelected];
        int categoryY = this.y + this.categorySize;
        final List<Component> components = this.getCurrentComponents();

        ScissorUtils.begin(this.x + sidebarWidth, this.y + this.categorySize, xSize - sidebarWidth, ySize - this.categorySize);
        int visibleIndex = 0;
        try {
            for (final Component comp : components) {
                if (!this.shouldShow(comp, selected)) continue;
                if (scrollOffset > visibleIndex) continue;
                visibleIndex++;

                comp.x = this.x + sidebarWidth;
                comp.y = categoryY;
                final boolean hovering = this.hoveringOn(mouseX, mouseY, (int) comp.x, (int) comp.y, xSize - sidebarWidth, comp.componentHeight);

                comp.drawComponent(
                        this.x + sidebarWidth,
                        categoryY,
                        this.x,
                        this.y,
                        xSize,
                        ySize
                );

                if (hovering) {
                    final int left = this.x + sidebarWidth;
                    final int right = left + xSize - sidebarWidth - 1;
                    final int bottom = categoryY + comp.componentHeight - 1;
                    final int color = -1;

                    this.drawHorizontalLine(left, right, categoryY, color);
                    this.drawHorizontalLine(left, right, bottom, color);
                    this.drawVerticalLine(left, categoryY, bottom, color);
                    this.drawVerticalLine(right, categoryY, bottom, color);
                }

                categoryY += comp.componentHeight;
            }
        } finally {
            ScissorUtils.end();
        }

        super.drawScreen(mouseX, mouseY, pt);
    }

    @Override
    protected void mouseClicked(final int mx, final int my, final int button) throws IOException {
        final TextBox<?> active = this.getActiveTextBox();

        if (active != null) {
            active.onMouseClick(mx, my, button);
            if (this.hoveringOn(mx, my, (int) active.x, (int) active.y, xSize - sidebarWidth, active.componentHeight)) {
                if (button == 0) {
                    active.onClick();
                } else if (button == 1) {
                    active.onRightClick();
                }
            }
            return;
        }
        final Category selected = Category.values()[this.categorySelected];
        final List<Component> components = this.getCurrentComponents();
        int visibleIndex = 0;
        for (final Component comp : components) {
            if (this.shouldShow(comp, selected)) {
                if (scrollOffset > visibleIndex) continue;
                visibleIndex++;
                comp.onMouseClick(mx, my, button);
                if (this.hoveringOn(mx, my, (int) comp.x, (int) comp.y, xSize - sidebarWidth, comp.componentHeight)) {
                    if (button == 0) {
                        comp.onClick();
                    } else if (button == 1) {
                        comp.onRightClick();
                    }
                }
            }
        }
        for (int i = 0; i < this.categoryInfo.size(); i++) {
            final CategoryInfo info = this.categoryInfo.get(i);
            if (this.hoveringOn(mx, my, this.x, info.startingPoint, sidebarWidth, this.categorySize)) {
                this.categorySelected = i;
                scrollOffset = 0;
                currentHud = null;
                Entry.playClickSound();
                break;
            }
        }


        super.mouseClicked(mx, my, button);
    }
    private TextBox<?> getActiveTextBox() {
        for (final Component comp : this.getCurrentComponents()) {
            if (comp instanceof TextBox && ((TextBox<?>) comp).drawingField) {
                return (TextBox<?>) comp;
            }
        }
        return null;
    }
    private boolean isTextBoxOpen() {
        return getActiveTextBox() != null;
    }
    public boolean hoveringOn(final int mx, final int my, final int targetX, final int targetY, final int width, final int height) {
        return my >= targetY && my < targetY + height && mx >= targetX && mx < targetX + width;
    }

    @Override
    protected void keyTyped(final char c, final int i) throws IOException {
        if (i == Keyboard.KEY_ESCAPE && currentHud != null) {
            currentHud = null;
            scrollOffset = 0;
            return;
        }
        super.keyTyped(c, i);
        final Category selected = Category.values()[this.categorySelected];
        for (final Component comp : this.getCurrentComponents()) {
            if (comp.category == selected) {
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
        for (final Component comp : this.components) {
            if (comp instanceof TextBox) {
                final TextBox<?> box = (TextBox<?>) comp;
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
        final int wheel = Mouse.getEventDWheel();
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

        public CategoryInfo(final String name, final int startingPoint, final int endingPoint, final Category category) {
            this.name = name;
            this.startingPoint = startingPoint;
            this.endingPoint = endingPoint;
            this.category = category;
        }
    }
}
