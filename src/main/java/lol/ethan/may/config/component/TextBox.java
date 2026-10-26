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

import java.awt.*;

public abstract class TextBox<T> extends Component {
    protected final int resetTextLength;
    protected final Color resetButtonColor;
    public T value;
    public T defaultValue;
    public boolean hidden;
    protected final GuiTextField field = this.generateField();
    public boolean drawingField;

    public TextBox(String name, Category category) {
        super(name, category);
        this.resetTextLength = 2 + this.mc.fontRendererObj.getStringWidth("Reset");
        this.resetButtonColor = new Color(120, 33, 43);
        this.hidden = false;
        this.drawingField = false;
    }

    public TextBox(String name, Category category, T value) {
        super(name, category);
        this.resetTextLength = 2 + this.mc.fontRendererObj.getStringWidth("Reset");
        this.resetButtonColor = new Color(120, 33, 43);
        this.hidden = false;
        this.drawingField = false;
        this.value = value;
        this.defaultValue = value;
        this.field.setText(value.toString());
    }

    public TextBox(String name, Category category, boolean hidden) {
        super(name, category);
        this.resetTextLength = 2 + this.mc.fontRendererObj.getStringWidth("Reset");
        this.resetButtonColor = new Color(120, 33, 43);
        this.hidden = false;
        this.drawingField = false;
        this.hidden = hidden;
        this.defaultValue = null;
    }

    private static String hide(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            sb.append("*");
        }
        return sb.toString();
    }

    @Override
    public void init(ClickGUI clickGUI) {
    }

    public GuiTextField generateField() {
        GuiTextField f = new GuiTextField(0, this.mc.fontRendererObj, 0, 0, 120, 20) {

            public void drawTextBox() {
                if (!TextBox.this.hidden) {
                    super.drawTextBox();
                    return;
                }
                String real = this.getText();
                int cursor = this.getCursorPosition();
                int selection = this.getSelectionEnd();
                this.setText(TextBox.hide(real));
                this.setCursorPosition(cursor);
                this.setSelectionPos(selection);
                super.drawTextBox();
                this.setText(real);
                this.setCursorPosition(cursor);
                this.setSelectionPos(selection);
            }

            public int getMaxStringLength() {
                return Integer.MAX_VALUE;
            }
        };
        f.setMaxStringLength(Integer.MAX_VALUE);
        return f;
    }

    public void drawField(int guiX, int guiY, int guiWidth, int guiHeight) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int textInfoHeight = guiY + guiHeight / 2;
        HUDHelper.drawRect(2, 2, this.resetTextLength + 2, this.componentHeight, this.resetButtonColor);
        this.mc.fontRendererObj.drawStringWithShadow("Reset", 2.0f, (float) (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2.0f, -1);
        this.field.setMaxStringLength(Integer.MAX_VALUE);
        this.field.xPosition = sr.getScaledWidth() / 2 - this.field.getWidth() / 2;
        this.field.yPosition = guiY + guiHeight / 2 - 10;
        this.mc.fontRendererObj.drawStringWithShadow(this.name, (float) sr.getScaledWidth() / 2.0f - (float) this.mc.fontRendererObj.getStringWidth(this.name) / 2.0f, (float) (this.field.yPosition - 10), -1);
        this.field.drawTextBox();
    }

    @Override
    public void drawComponent(int componentX, int componentY, int guiX, int guiY, int guiWidth, int guiHeight) {
        String shown = this.format(this.value);
        this.mc.fontRendererObj.drawStringWithShadow(this.name + " §f(" + (this.hidden ? (shown.isEmpty() ? "§cEmpty§f" : "§cHidden§f") : (shown.isEmpty() ? "§cEmpty§f" : shown)) + ")", (float) (componentX + 12), (float) componentY + (float) (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2.0f, -1);
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
    public void onKeyPress(char c, int code) {
        if (this.drawingField) {
            if (code == 1 || code == 28 && !GuiScreen.isShiftKeyDown()) {
                this.finish();
            } else {
                this.field.textboxKeyTyped(c, code);
            }
        }
    }

    @Override
    public void onMouseClick(int x, int y, int button) {
        if (this.drawingField) {
            this.field.mouseClicked(x, y, button);
            if (x >= 2 && x < this.resetTextLength + 2 && y >= 2 && y < this.componentHeight) {
                Entry.playClickSound();
                this.field.setText(this.defaultValue == null ? "" : this.defaultValue.toString());
            }
        }
    }

    protected abstract T parse(String var1);

    protected String format(T v) {
        return v == null ? "" : v.toString();
    }

    public abstract void onFinished(T var1);

    public void finish() {
        this.drawingField = false;
        this.field.setFocused(false);
        T parsed = this.parse(this.field.getText());
        if (parsed != null) {
            this.value = parsed;
            this.onFinished(parsed);
        }
        this.field.setText(this.format(this.value));
    }

    @Override
    public abstract JsonElement serialize();

    @Override
    public abstract void load(JsonElement var1);
}

