package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.DragElement;
import lol.ethan.may.render.huds.BasicHud;
import lol.ethan.may.utils.GuiUtils;

public class HUDMove extends Component {
    public BasicHud hud;
    public HUDMove(final String name, final Category category, final BasicHud hud) {
        super(name, category);
        this.hud = hud;
    }

    @Override
    public void init(final ClickGUI clickGUI) {

    }

    @Override
    public void drawComponent(final int componentX, final int componentY, final int guiX, final int guiY, final int guiWidth, final int guiHeight) {
        this.mc.fontRendererObj.drawStringWithShadow(
                "§aClick to move this HUD!",
                componentX + 12,
                componentY + (this.componentHeight - this.mc.fontRendererObj.FONT_HEIGHT) / 2f,
                -1
        );
    }

    @Override
    public void onClick() {
        GuiUtils.openCustomScreen(new DragElement(
                "moving this hud",
                this.hud.x,
                this.hud.y,
                this.hud
        ));
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onKeyPress(final char c, final int code) {

    }

    @Override
    public void onMouseClick(final int x, final int y, final int button) {

    }

    @Override
    public void onScroll(final int direction) {

    }

    @Override
    public JsonElement serialize() {
        return null;
    }

    @Override
    public void load(final JsonElement element) {

    }
}
