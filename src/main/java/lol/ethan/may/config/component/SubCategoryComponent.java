package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryComponent extends Component {
    public List<Component> components = new ArrayList<>();
    public SubCategoryComponent(final String name, final Category category) {
        super(name, category);
    }

    @Override
    public void init(final ClickGUI clickGUI) {

    }

    @Override
    public void drawComponent(final int componentX, final int componentY, final int guiX, final int guiY, final int guiWidth, final int guiHeight) {

    }

    @Override
    public void onClick() {

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
