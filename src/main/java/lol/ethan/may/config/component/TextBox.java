package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.render.huds.HUDHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public abstract class TextBox<T> extends Component {
    protected final GuiTextField field = generateField();
    protected final int resetTextLength = 2 + mc.fontRendererObj.getStringWidth("Reset");
    protected final Color resetButtonColor = new Color(120, 33, 43);
    public T value;
    public T defaultValue;
    public boolean hidden = false;
    public TextBox(String name, Category category) {
        super(name, category);
    }
    public TextBox(String name, Category category, T value) {
        super(name, category);
        this.value = value;
        this.defaultValue = value;
        field.setText(value.toString());
    }
    public TextBox(String name, Category category, boolean hidden) {
        super(name, category);
        this.hidden = hidden;
        this.defaultValue = null;
    }
    public boolean drawingField = false;

    private static String hide(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) sb.append("*");
        return sb.toString();
    }

    @Override
    public void init(ClickGUI clickGUI) {

    }

    public GuiTextField generateField() {
        GuiTextField f = new GuiTextField(
                0,
                mc.fontRendererObj,
                0,
                0,
                120,
                20
        ) {
            @Override
            public void drawTextBox() {
                if (!hidden) {
                    super.drawTextBox();
                    return;
                }
                String real = getText();
                int cursor = getCursorPosition();
                int selection = getSelectionEnd();

                setText(hide(real));
                setCursorPosition(cursor);
                setSelectionPos(selection);

                super.drawTextBox();

                setText(real);
                setCursorPosition(cursor);
                setSelectionPos(selection);
            }

            @Override
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

        HUDHelper.drawRect(2, 2, resetTextLength + 2, componentHeight, resetButtonColor);
        mc.fontRendererObj.drawStringWithShadow("Reset", 2, (componentHeight - mc.fontRendererObj.FONT_HEIGHT) / 2f, -1);
        field.setMaxStringLength(Integer.MAX_VALUE);
        field.xPosition = (sr.getScaledWidth() / 2) - (field.getWidth() / 2);
        field.yPosition = guiY + guiHeight / 2 - 10;
        mc.fontRendererObj.drawStringWithShadow(
                name,
                ((float) sr.getScaledWidth() / 2) - ((float) mc.fontRendererObj.getStringWidth(name) / 2),
                field.yPosition - 10,
                -1
        );
        field.drawTextBox();
    }
    @Override
    public void drawComponent(int componentX, int componentY, int guiX, int guiY, int guiWidth, int guiHeight) {
        String shown = format(value);
        mc.fontRendererObj.drawStringWithShadow(
                name + " §f(" + (hidden ? (shown.isEmpty() ? "§cEmpty§f" : "§cHidden§f") : (shown.isEmpty() ? "§cEmpty§f" : shown)) + ")",
                componentX + 12,
                componentY + (componentHeight - mc.fontRendererObj.FONT_HEIGHT) / 2f,
                -1
        );
    }

    @Override
    public void onClick() {
        if (drawingField) {
            finish();
        } else {
            drawingField = true;
            field.setFocused(true);
        }
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onKeyPress(char c, int code) {
        if (drawingField) {
            if (code == Keyboard.KEY_ESCAPE || (code == Keyboard.KEY_RETURN && !ClickGUI.isShiftKeyDown())) {
                finish();
            } else {
                field.textboxKeyTyped(c, code);
            }
        }
    }

    @Override
    public void onMouseClick(int x, int y, int button) {
        if (drawingField) {
            field.mouseClicked(x, y, button);
            if (x >= 2 && x < resetTextLength + 2 && y >= 2 && y < componentHeight) {
                Entry.playClickSound();
                field.setText(defaultValue == null ? "" : defaultValue.toString());
            }
        }
    }
    protected abstract T parse(String input);
    protected String format(T v) { return v == null ? "" : v.toString(); }
    public abstract void onFinished(T value);
    public void finish() {
        drawingField = false;
        field.setFocused(false);
        T parsed = parse(field.getText());
        if (parsed != null) {
            value = parsed;
            onFinished(parsed);
        }
        field.setText(format(value));
    }
    public abstract JsonElement serialize();
    public abstract void load(JsonElement element);
}
