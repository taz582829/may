package lol.ethan.may.render.huds;

import java.util.Arrays;
import java.util.List;

public class TestHUD extends BasicHud {
    public TestHUD(float x, float y) {
        super(x, y);
    }

    @Override
    protected List<String> getLines(boolean example) {
        if (example) {
            return Arrays.asList("hi", "example");
        }
        return Arrays.asList("hi!", "test hud");
    }
}

