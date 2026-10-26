package lol.ethan.may.config.component.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lol.ethan.may.config.Category;
import lol.ethan.may.config.component.TextBox;

import java.util.function.Consumer;

public class NumberTextBox extends TextBox<Number> {
    public enum TextBoxType {
        INT,
        FLOAT,
        DOUBLE,
        BYTE,
        SHORT
    }
    public Double value;
    public Consumer<Number> consumer = null;
    public final TextBoxType type;

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
        this.field.setText(value.toString());
    }
    public NumberTextBox(String name, Category category, TextBoxType type, Number def) {
        super(name, category);
        this.type = type;
        this.value = def.doubleValue();
        this.field.setText(value.toString());
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
        if (value == null) return;
        this.value = value.doubleValue();
        if (consumer != null) consumer.accept(this.value);
    }

    @Override
    public void onScroll(int direction) {

    }

    @Override
    public JsonElement serialize() {
        if (type == TextBoxType.INT) {
            return new JsonPrimitive(value.intValue());
        } else if (type == TextBoxType.BYTE) {
            return new JsonPrimitive(value.byteValue());
        } else if (type == TextBoxType.SHORT) {
            return new JsonPrimitive(value.shortValue());
        }
        return new JsonPrimitive(value);
    }

    @Override
    public void load(JsonElement element) {
        value = element.getAsDouble();
        field.setText(value.toString());
        if (consumer != null) consumer.accept(this.value);
    }
}
