package lol.ethan.may.config.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ChoiceComponent extends Component {
    public List<Button> children = new ArrayList<>();
    public Consumer<Integer> consumer = null;
    public int choice = 0;
    public int scrollOffset = 0;
    private int clipBottom = Integer.MAX_VALUE;

    public ChoiceComponent(String name, Category category, Consumer<Integer> consumer, String... children) {
        super(name, category);
        this.consumer = consumer;
        for (int i = 0; i < children.length; i++) {
            String childName = children[i];
            Button newBtn = new Button(childName, category, choice == i);
            this.children.add(newBtn);
        }
    }
    public ChoiceComponent(String name, Category category, String... children) {
        this(name, category, null, children);
    }
    public ChoiceComponent(String name, Category category, Consumer<Integer> consumer, Button... children) {
        super(name, category);
        this.consumer = consumer;
        this.children.addAll(Arrays.asList(children));
    }
    public ChoiceComponent(String name, Category category, Button... children) {
        this(name, category, null, children);
    }

    @Override
    public void init(ClickGUI clickGUI) {
        componentHeight = 20;
        for (Component comp : children) {
            comp.init(clickGUI);
            componentHeight += comp.componentHeight;
        }
    }

    @Override
    public void drawComponent(int componentX, int componentY, int guiX, int guiY, int guiWidth, int guiHeight) {
        clipBottom = guiY + guiHeight;

        mc.fontRendererObj.drawStringWithShadow(this.name, componentX, componentY + 5.5f, -1);

        int childY = componentY + 20;
        for (int i = 0; i < children.size(); i++) {
            if (scrollOffset > i) continue;
            Component comp = children.get(i);
            comp.x = componentX + 10;
            comp.y = childY;
            comp.drawComponent(componentX + 10, childY, guiX, guiY, guiWidth, guiHeight);
            childY += comp.componentHeight;
        }
    }

    @Override
    public void onClick() {

    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onKeyPress(char c, int code) {
        for (Component comp : children) {
            if (comp instanceof TextBox) {
                TextBox tb = (TextBox) comp;
                if (tb.drawingField) {
                    tb.onKeyPress(c, code);
                    return;
                }
            }
        }
    }

    @Override
    public void onMouseClick(int x, int y, int mb) {
        boolean disableAll = false;
        for (int i = 0; i < children.size(); i++) {
            if (ClickGUI.scrollOffset - scrollOffset > i) continue;
            Button btn = children.get(i);
            if (btn.y >= clipBottom) continue;
            btn.onMouseClick(x, y, mb);
            if (hoveringOn(x, y, btn, 10)) {
                choice = i;
                if (mb == 0) {
                    btn.onClick();
                    if (btn.enabled) {
                        if (consumer != null) consumer.accept(i);
                        disableAll = true;
                    }
                } else if (mb == 1) {
                    btn.onRightClick();
                }
            }
        }
        if (disableAll) {
            for (int i = 0; i < children.size(); i++) {
                Button btn = children.get(i);
                if (choice != i) {
                    btn.enabled = false;
                }
            }
        }
    }

    @Override
    public void onScroll(int direction) {
        scrollOffset = Math.max(0, scrollOffset + direction);
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(choice);
    }

    @Override
    public void load(JsonElement element) {
        choice = element.getAsInt();
        for (int i = 0; i < children.size(); i++) {
            children.get(i).enabled = i == choice;
        }
    }
}
