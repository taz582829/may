package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.ethan.may.Entry;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.ConfigEvent;
import org.lwjgl.input.Keyboard;

public class ChoiceComponent extends Component {
    private final String[] choices;
    public int value;

    public ChoiceComponent(final String name, final Category category, final String... choices) {
        super(name, category);
        this.choices = choices;
    }

    @Override
    public void init(final ClickGUI clickGUI) {

    }

    @Override
    public void drawComponent(final int componentX, final int componentY, final int guiX, final int guiY, final int guiWidth, final int guiHeight) {
        drawName(componentX, componentY);
        final int rowWidth = guiWidth - ClickGUI.sidebarWidth;
        final int textX = componentX + rowWidth - 16 - mc.fontRendererObj.getStringWidth(choices[value]);

        mc.fontRendererObj.drawStringWithShadow(
                choices[value],
                textX,
                componentY + (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2.0f,
                -1
        );
    }

    @Override
    public void onClick() {
        Entry.playClickSound();
        value++;
        if (value > choices.length) {
            value = 0;
        }
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onKeyPress(final char c, final int code, final ConfigEvent event) {
        if (code == Keyboard.KEY_UP) {
            value++;
            if (value > choices.length - 1) {
                value = 0;
            }
        }
        if (code == Keyboard.KEY_DOWN) {
            value--;
            if (value < 0) {
                value = choices.length - 1;
            }
        }
    }

    @Override
    public void onMouseClick(final int x, final int y, final int button) {

    }

    @Override
    public void onScroll(final int direction) {

    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(value);
    }

    @Override
    public void load(final JsonElement element) {
        this.value = element.getAsInt();
    }
}
