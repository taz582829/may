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
            Button newBtn = new Button(childName, category, this.choice == i);
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
        this.componentHeight = 20;
        for (Component component : this.children) {
            component.init(clickGUI);
            this.componentHeight += component.componentHeight;
        }
    }

    @Override
    public void drawComponent(int componentX, int componentY, int guiX, int guiY, int guiWidth, int guiHeight) {
        this.clipBottom = guiY + guiHeight;
        this.mc.fontRendererObj.drawStringWithShadow(this.name, (float) componentX, (float) componentY + 5.5f, -1);
        int childY = componentY + 20;
        for (int i = 0; i < this.children.size(); i++) {
            if (this.scrollOffset > i) continue;
            Component comp = this.children.get(i);
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
        for (Component component : this.children) {
            if (!(component instanceof TextBox)) continue;
            TextBox<?> tb = (TextBox<?>) component;
            if (!tb.drawingField) continue;
            tb.onKeyPress(c, code);
            return;
        }
    }

    @Override
    public void onMouseClick(int x, int y, int mb) {
        Button btn;

        boolean disableAll = false;
        for (int i = 0; i < this.children.size(); i++) {
            if (ClickGUI.scrollOffset - this.scrollOffset > i) continue;
            btn = this.children.get(i);
            if (btn.y >= (float) this.clipBottom) continue;
            btn.onMouseClick(x, y, mb);
            if (!this.hoveringOn(x, y, btn, 10)) continue;
            this.choice = i;
            if (mb == 0) {
                btn.onClick();
                if (!btn.enabled) continue;
                if (this.consumer != null) {
                    this.consumer.accept(i);
                }
                disableAll = true;
                continue;
            }
            if (mb != 1) continue;
            btn.onRightClick();
        }
        if (disableAll) {
            for (int i = 0; i < this.children.size(); i++) {
                btn = this.children.get(i);
                if (this.choice == i) continue;
                btn.enabled = false;
            }
        }
    }

    @Override
    public void onScroll(int direction) {
        this.scrollOffset = Math.max(0, this.scrollOffset + direction);
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.choice);
    }

    @Override
    public void load(JsonElement element) {
        this.choice = element.getAsInt();
        for (int i = 0; i < this.children.size(); i++) {
            this.children.get(i).enabled = i == this.choice;
        }
    }
}

