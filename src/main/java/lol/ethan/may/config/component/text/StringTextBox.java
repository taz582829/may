package lol.ethan.may.config.component.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.ClickGUI;
import lol.ethan.may.config.component.TextBox;

import java.util.function.Consumer;

public class StringTextBox extends TextBox<String> {
    public Consumer<String> consumer = null;

    public StringTextBox(String name, Category category) {
        super(name, category);
    }

    public StringTextBox(String name, Category category, boolean hidden) {
        super(name, category, hidden);
        this.defaultValue = "";
    }

    public StringTextBox(String name, Category category, String def, Consumer<String> consumer) {
        super(name, category, def);
        this.consumer = consumer;
    }

    public StringTextBox(String name, Category category, Consumer<String> consumer) {
        super(name, category);
        this.consumer = consumer;
    }

    public StringTextBox(String name, Category category, Consumer<String> consumer, boolean hidden) {
        super(name, category, hidden);
        this.consumer = consumer;
    }

    public StringTextBox(String name, Category category, String def) {
        super(name, category, def);
    }

    @Override
    protected String parse(String input) {
        return input;
    }

    @Override
    public void onScroll(int direction) {
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.value == null ? "" : this.value);
    }

    @Override
    public void load(JsonElement element) {
        this.value = element.getAsString();
        this.field.setText(this.value == null ? "" : this.value);
        if (this.consumer != null) {
            this.consumer.accept(this.value);
        }
    }

    @Override
    public void onFinished(String value) {
        this.value = value;
        if (this.consumer != null) {
            this.consumer.accept(value);
        }
    }

    public String toString() {
        return this.value;
    }
}

