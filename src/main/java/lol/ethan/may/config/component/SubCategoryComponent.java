package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryComponent extends Component {
    public List<Component> components = new ArrayList<>();
    public SubCategoryComponent(String name, Category category) {
        super(name, category);
    }

    @Override
    public void init(ClickGUI clickGUI) {

    }

    @Override
    public void drawComponent(int componentX, int componentY, int guiX, int guiY, int guiWidth, int guiHeight) {

    }

    @Override
    public void onClick() {

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
