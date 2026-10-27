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
    public Consumer<Number> consumer;
    public final TextBoxType type;

    public NumberTextBox(final String name, final Category category, final TextBoxType type, final Consumer<Number> consumer) {
        super(name, category);
        this.type = type;
        this.consumer = consumer;
    }
    public NumberTextBox(final String name, final Category category, final TextBoxType type, final Number def, final Consumer<Number> consumer) {
        super(name, category);
        this.type = type;
        value = def.doubleValue();
        this.consumer = consumer;
        field.setText(this.value.toString());
    }
    public NumberTextBox(final String name, final Category category, final TextBoxType type, final Number def) {
        super(name, category);
        this.type = type;
        value = def.doubleValue();
        field.setText(this.value.toString());
    }
    public NumberTextBox(final String name, final Category category, final TextBoxType type) {
        super(name, category);
        this.type = type;
    }

    public NumberTextBox(final String name, final Category category) {
        super(name, category);
        type = TextBoxType.DOUBLE;
    }

    @Override
    protected Number parse(final String input) {
        return Double.parseDouble(input);
    }

    @Override
    public void onFinished(final Number value) {
        if (value == null) return;
        this.value = value.doubleValue();
        if (consumer != null) this.consumer.accept(this.value);
    }

    @Override
    public void onScroll(final int direction) {

    }

    @Override
    public JsonElement serialize() {
        if (type == TextBoxType.INT) {
            return new JsonPrimitive(this.value.intValue());
        } else if (type == TextBoxType.BYTE) {
            return new JsonPrimitive(this.value.byteValue());
        } else if (type == TextBoxType.SHORT) {
            return new JsonPrimitive(this.value.shortValue());
        }
        return new JsonPrimitive(this.value);
    }

    @Override
    public void load(final JsonElement element) {
        this.value = element.getAsDouble();
        this.field.setText(this.value.toString());
        if (consumer != null) this.consumer.accept(value);
    }
}
