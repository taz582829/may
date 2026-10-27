package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.Config;
import org.lwjgl.input.Keyboard;

public class KeybindComponent extends Component {
    public int bind;
    public boolean listening;
    public Runnable action;

    public KeybindComponent(final String name, final Category category, final Runnable action) {
        this(name, category, 0);
        this.action = action;
    }
    public KeybindComponent(final String name, final Category category, final int defaultBind, final Runnable action) {
        this(name, category, defaultBind);
        this.action = action;
    }
    public KeybindComponent(final String name, final Category category) {
        this(name, category, 0);
    }
    public KeybindComponent(final String name, final Category category, final int defaultBind) {
        super(name, category);
        bind = defaultBind;
    }

    @Override
    public void init(final ClickGUI clickGUI) {

    }

    @Override
    public void drawComponent(final int componentX, final int componentY, final int guiX, final int guiY, final int guiWidth, final int guiHeight) {
        final String keybind = Config.getReadableKeybind(this.bind);
        this.mc.fontRendererObj.drawStringWithShadow(
                this.name + " (" + (this.listening ? "?" : keybind) + ")",
                componentX + 12,
                componentY + (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2f,
                -1
        );
    }

    @Override
    public void onClick() {
        this.listening = true;
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onKeyPress(final char c, final int code) {
        if (this.listening) {
            this.bind = code == Keyboard.KEY_ESCAPE ? 0 : code;
            this.listening = false;
        }
    }

    @Override
    public void onMouseClick(final int x, final int y, final int button) {
//        if (listening && button == 0) {
//            listening = false;
//        }
    }

    @Override
    public void onScroll(final int direction) {

    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.bind);
    }

    @Override
    public void load(final JsonElement element) {
        this.bind = element.getAsInt();
    }
}
