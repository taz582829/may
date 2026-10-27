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
    public Consumer<Integer> consumer;
    public int choice;
    public int scrollOffset;
    private int clipBottom = Integer.MAX_VALUE;

    public ChoiceComponent(final String name, final Category category, final Consumer<Integer> consumer, final String... children) {
        super(name, category);
        this.consumer = consumer;
        for (int i = 0; i < children.length; i++) {
            final String childName = children[i];
            final Button newBtn = new Button(childName, category, this.choice == i);
            this.children.add(newBtn);
        }
    }
    public ChoiceComponent(final String name, final Category category, final String... children) {
        this(name, category, null, children);
    }
    public ChoiceComponent(final String name, final Category category, final Consumer<Integer> consumer, final Button... children) {
        super(name, category);
        this.consumer = consumer;
        this.children.addAll(Arrays.asList(children));
    }
    public ChoiceComponent(final String name, final Category category, final Button... children) {
        this(name, category, null, children);
    }

    @Override
    public void init(final ClickGUI clickGUI) {
        this.componentHeight = 20;
        for (final Component comp : this.children) {
            comp.init(clickGUI);
            this.componentHeight += comp.componentHeight;
        }
    }

    @Override
    public void drawComponent(final int componentX, final int componentY, final int guiX, final int guiY, final int guiWidth, final int guiHeight) {
        this.clipBottom = guiY + guiHeight;

        this.mc.fontRendererObj.drawStringWithShadow(name, componentX, componentY + 5.5f, -1);

        int childY = componentY + 20;
        for (int i = 0; i < this.children.size(); i++) {
            if (this.scrollOffset > i) continue;
            final Component comp = this.children.get(i);
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
    public void onKeyPress(final char c, final int code) {
        for (final Component comp : this.children) {
            if (comp instanceof TextBox) {
                final TextBox tb = (TextBox) comp;
                if (tb.drawingField) {
                    tb.onKeyPress(c, code);
                    return;
                }
            }
        }
    }

    @Override
    public void onMouseClick(final int x, final int y, final int mb) {
        boolean disableAll = false;
        for (int i = 0; i < this.children.size(); i++) {
            if (ClickGUI.scrollOffset - this.scrollOffset > i) continue;
            final Button btn = this.children.get(i);
            if (btn.y >= this.clipBottom) continue;
            btn.onMouseClick(x, y, mb);
            if (this.hoveringOn(x, y, btn, 10)) {
                this.choice = i;
                if (mb == 0) {
                    btn.onClick();
                    if (btn.enabled) {
                        if (consumer != null) this.consumer.accept(i);
                        disableAll = true;
                    }
                } else if (mb == 1) {
                    btn.onRightClick();
                }
            }
        }
        if (disableAll) {
            for (int i = 0; i < this.children.size(); i++) {
                final Button btn = this.children.get(i);
                if (this.choice != i) {
                    btn.enabled = false;
                }
            }
        }
    }

    @Override
    public void onScroll(final int direction) {
        this.scrollOffset = Math.max(0, this.scrollOffset + direction);
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.choice);
    }

    @Override
    public void load(final JsonElement element) {
        this.choice = element.getAsInt();
        for (int i = 0; i < this.children.size(); i++) {
            this.children.get(i).enabled = i == this.choice;
        }
    }
}
