package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.render.huds.HUDHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public abstract class TextBox<T> extends Component {
    protected final GuiTextField field = this.generateField();
    protected final int resetTextLength = 2 + this.mc.fontRendererObj.getStringWidth("Reset");
    protected final Color resetButtonColor = new Color(120, 33, 43);
    public T value;
    public T defaultValue;
    public boolean hidden;
    public TextBox(final String name, final Category category) {
        super(name, category);
    }
    public TextBox(final String name, final Category category, final T value) {
        super(name, category);
        this.value = value;
        defaultValue = value;
        this.field.setText(value.toString());
    }
    public TextBox(final String name, final Category category, final boolean hidden) {
        super(name, category);
        this.hidden = hidden;
        defaultValue = null;
    }
    public boolean drawingField;

    private static String hide(final String s) {
        final StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) sb.append("*");
        return sb.toString();
    }

    @Override
    public void init(final ClickGUI clickGUI) {

    }

    public GuiTextField generateField() {
        final GuiTextField f = new GuiTextField(
                0,
                this.mc.fontRendererObj,
                0,
                0,
                120,
                20
        ) {
            @Override
            public void drawTextBox() {
                if (!TextBox.this.hidden) {
                    super.drawTextBox();
                    return;
                }
                final String real = this.getText();
                final int cursor = this.getCursorPosition();
                final int selection = this.getSelectionEnd();

                this.setText(hide(real));
                this.setCursorPosition(cursor);
                this.setSelectionPos(selection);

                super.drawTextBox();

                this.setText(real);
                this.setCursorPosition(cursor);
                this.setSelectionPos(selection);
            }

            @Override
            public int getMaxStringLength() {
                return Integer.MAX_VALUE;
            }
        };
        f.setMaxStringLength(Integer.MAX_VALUE);
        return f;
    }
    public void drawField(final int guiX, final int guiY, final int guiWidth, final int guiHeight) {
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final int textInfoHeight = guiY + guiHeight / 2;

        HUDHelper.drawRect(2, 2, this.resetTextLength + 2, this.componentHeight, this.resetButtonColor);
        this.mc.fontRendererObj.drawStringWithShadow("Reset", 2, (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2f, -1);
        this.field.setMaxStringLength(Integer.MAX_VALUE);
        this.field.xPosition = (sr.getScaledWidth() / 2) - (this.field.getWidth() / 2);
        this.field.yPosition = guiY + guiHeight / 2 - 10;
        this.mc.fontRendererObj.drawStringWithShadow(
                this.name,
                ((float) sr.getScaledWidth() / 2) - ((float) this.mc.fontRendererObj.getStringWidth(this.name) / 2),
                this.field.yPosition - 10,
                -1
        );
        this.field.drawTextBox();
    }
    @Override
    public void drawComponent(final int componentX, final int componentY, final int guiX, final int guiY, final int guiWidth, final int guiHeight) {
        final String shown = this.format(this.value);
        this.mc.fontRendererObj.drawStringWithShadow(
                this.name + " §f(" + (this.hidden ? (shown.isEmpty() ? "§cEmpty§f" : "§cHidden§f") : (shown.isEmpty() ? "§cEmpty§f" : shown)) + ")",
                componentX + 12,
                componentY + (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2f,
                -1
        );
    }

    @Override
    public void onClick() {
        if (this.drawingField) {
            this.finish();
        } else {
            this.drawingField = true;
            this.field.setFocused(true);
        }
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onKeyPress(final char c, final int code) {
        if (this.drawingField) {
            if (code == Keyboard.KEY_ESCAPE || (code == Keyboard.KEY_RETURN && !GuiScreen.isShiftKeyDown())) {
                this.finish();
            } else {
                this.field.textboxKeyTyped(c, code);
            }
        }
    }

    @Override
    public void onMouseClick(final int x, final int y, final int button) {
        if (this.drawingField) {
            this.field.mouseClicked(x, y, button);
            if (x >= 2 && x < this.resetTextLength + 2 && y >= 2 && y < this.componentHeight) {
                Entry.playClickSound();
                this.field.setText(defaultValue == null ? "" : this.defaultValue.toString());
            }
        }
    }
    protected abstract T parse(String input);
    protected String format(final T v) { return v == null ? "" : v.toString(); }
    public abstract void onFinished(T value);
    public void finish() {
        this.drawingField = false;
        this.field.setFocused(false);
        final T parsed = this.parse(this.field.getText());
        if (parsed != null) {
            this.value = parsed;
            this.onFinished(parsed);
        }
        this.field.setText(this.format(this.value));
    }
    public abstract JsonElement serialize();
    public abstract void load(JsonElement element);
}
