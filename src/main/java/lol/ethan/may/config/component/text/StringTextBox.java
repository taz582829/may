package lol.ethan.may.config.component.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.component.TextBox;

import java.util.function.Consumer;

public class StringTextBox extends TextBox<String> {
    public Consumer<String> consumer;
    public StringTextBox(final String name, final Category category) {
        super(name, category);
    }
    public StringTextBox(final String name, final Category category, final boolean hidden) {
        super(name, category, hidden);
        defaultValue = "";
    }
    public StringTextBox(final String name, final Category category, final String def, final Consumer<String> consumer) {
        super(name, category, def);
        this.consumer = consumer;
    }
    public StringTextBox(final String name, final Category category, final Consumer<String> consumer) {
        super(name, category);
        this.consumer = consumer;
    }
    public StringTextBox(final String name, final Category category, final Consumer<String> consumer, final boolean hidden) {
        super(name, category, hidden);
        this.consumer = consumer;
    }
    public StringTextBox(final String name, final Category category, final String def) {
        super(name, category, def);
    }

    @Override
    public void init(final ClickGUI clickGUI) {

    }

    @Override
    protected String parse(final String input) {
        return input;
    }

    @Override
    public void onScroll(final int direction) {

    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(value == null ? "" : this.value);
    }

    @Override
    public void load(final JsonElement element) {
        this.value = element.getAsString();
        this.field.setText(value == null ? "" : this.value);
        if (consumer != null) this.consumer.accept(value);
    }

    @Override
    public void onFinished(final String value) {
        this.value = value;
        if (consumer != null) consumer.accept(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
