package lol.ethan.may.config.component.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.component.TextBox;

import java.util.function.Consumer;

public class NumberTextBox extends TextBox<Number> {
    public final TextBoxType type;
    public Double value;
    public Consumer<Number> consumer = null;

    public NumberTextBox(String name, Category category, TextBoxType type, Consumer<Number> consumer) {
        super(name, category);
        this.type = type;
        this.consumer = consumer;
    }

    public NumberTextBox(String name, Category category, TextBoxType type, Number def, Consumer<Number> consumer) {
        super(name, category);
        this.type = type;
        this.value = def.doubleValue();
        this.consumer = consumer;
        this.field.setText(this.value.toString());
    }

    public NumberTextBox(String name, Category category, TextBoxType type, Number def) {
        super(name, category);
        this.type = type;
        this.value = def.doubleValue();
        this.field.setText(this.value.toString());
    }

    public NumberTextBox(String name, Category category, TextBoxType type) {
        super(name, category);
        this.type = type;
    }

    public NumberTextBox(String name, Category category) {
        super(name, category);
        this.type = TextBoxType.DOUBLE;
    }

    @Override
    protected Number parse(String input) {
        return Double.parseDouble(input);
    }

    @Override
    public void onFinished(Number value) {
        if (value == null) {
            return;
        }
        this.value = value.doubleValue();
        if (this.consumer != null) {
            this.consumer.accept(this.value);
        }
    }

    @Override
    public void onScroll(int direction) {
    }

    @Override
    public JsonElement serialize() {
        if (this.type == TextBoxType.INT) {
            return new JsonPrimitive(this.value.intValue());
        }
        if (this.type == TextBoxType.BYTE) {
            return new JsonPrimitive(this.value.byteValue());
        }
        if (this.type == TextBoxType.SHORT) {
            return new JsonPrimitive(this.value.shortValue());
        }
        return new JsonPrimitive(this.value);
    }

    @Override
    public void load(JsonElement element) {
        this.value = element.getAsDouble();
        this.field.setText(this.value.toString());
        if (this.consumer != null) {
            this.consumer.accept(this.value);
        }
    }

    public enum TextBoxType {
        INT,
        FLOAT,
        DOUBLE,
        BYTE,
        SHORT

    }
}

