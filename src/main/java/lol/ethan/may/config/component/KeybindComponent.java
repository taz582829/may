package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.Config;

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

    private KeybindComponent(String name, Category category, int defaultBind) {
        super(name, category);
        this.bind = defaultBind;
    }

    @Override
    public void init(ClickGUI clickGUI) {
    }

    @Override
    public void drawComponent(int componentX, int componentY, int guiX, int guiY, int guiWidth, int guiHeight) {
        String keybind = Config.getReadableKeybind(this.bind);
        this.mc.fontRendererObj.drawStringWithShadow(this.name + " (" + (this.listening ? "?" : keybind) + ")", (float) (componentX + 12), (float) componentY + (float) (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2.0f, -1);
    }

    @Override
    public void onClick() {
        this.listening = true;
    }

    @Override
    public void onRightClick() {
    }

    @Override
    public void onKeyPress(char c, int code) {
        if (this.listening) {
            this.bind = code == 1 || code == 211 ? 0 : code;
            this.listening = false;
        }
    }

    @Override
    public void onMouseClick(int x, int y, int button) {
    }

    @Override
    public void onScroll(int direction) {
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.bind);
    }

    @Override
    public void load(JsonElement element) {
        this.bind = element.getAsInt();
    }
}

