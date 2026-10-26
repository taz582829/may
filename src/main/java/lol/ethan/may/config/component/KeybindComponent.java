package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.Config;
import org.lwjgl.input.Keyboard;

public class KeybindComponent extends Component {
    public int bind;
    public boolean listening = false;
    public Runnable action;

    public KeybindComponent(String name, Category category, Runnable action) {
        this(name, category, 0);
        this.action = action;
    }
    public KeybindComponent(String name, Category category, int defaultBind, Runnable action) {
        this(name, category, defaultBind);
        this.action = action;
    }
    public KeybindComponent(String name, Category category) {
        this(name, category, 0);
    }
    public KeybindComponent(String name, Category category, int defaultBind) {
        super(name, category);
        this.bind = defaultBind;
    }

    @Override
    public void init(ClickGUI clickGUI) {

    }

    @Override
    public void drawComponent(int componentX, int componentY, int guiX, int guiY, int guiWidth, int guiHeight) {
        String keybind = Config.getReadableKeybind(bind);
        mc.fontRendererObj.drawStringWithShadow(
                name + " (" + (listening ? "?" : keybind) + ")",
                componentX + 12,
                componentY + (componentHeight - mc.fontRendererObj.FONT_HEIGHT) / 2f,
                -1
        );
    }

    @Override
    public void onClick() {
        listening = true;
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onKeyPress(char c, int code) {
        if (listening) {
            bind = code == Keyboard.KEY_ESCAPE ? 0 : code;
            listening = false;
        }
    }

    @Override
    public void onMouseClick(int x, int y, int button) {
//        if (listening && button == 0) {
//            listening = false;
//        }
    }

    @Override
    public void onScroll(int direction) {

    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(bind);
    }

    @Override
    public void load(JsonElement element) {
        bind = element.getAsInt();
    }
}
