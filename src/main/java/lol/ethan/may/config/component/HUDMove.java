package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.DragElement;
import lol.ethan.may.render.huds.BasicHud;
import lol.ethan.may.utils.GuiUtils;

public class HUDMove extends Component {
    public BasicHud hud;

    public HUDMove(String name, Category category, BasicHud hud) {
        super(name, category);
        this.hud = hud;
    }

    @Override
    public void init(ClickGUI clickGUI) {
    }

    @Override
    public void drawComponent(int componentX, int componentY, int guiX, int guiY, int guiWidth, int guiHeight) {
        this.mc.fontRendererObj.drawStringWithShadow("§aClick to move this HUD!", (float) (componentX + 12), (float) componentY + (float) (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2.0f, -1);
    }

    @Override
    public void onClick() {
        this.hud.updateLineCache();
        GuiUtils.openCustomScreen(new DragElement(String.join("\n", this.hud.lineCache), this.hud.x, this.hud.y, this.hud));
    }

    @Override
    public void onRightClick() {
    }

    @Override
    public void onKeyPress(char c, int code) {
    }

    @Override
    public void onMouseClick(int x, int y, int button) {
    }

    @Override
    public void onScroll(int direction) {
    }

    @Override
    public JsonElement serialize() {
        return null;
    }

    @Override
    public void load(JsonElement element) {
    }
}

