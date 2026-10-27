package lol.ethan.may.render.huds;

import java.util.Arrays;
import java.util.List;

public class TestHUD extends BasicHud {
    public TestHUD(final float x, final float y) {
        super(x, y);
    }

    @Override
    protected List<String> getLines(final boolean example) {
        return Arrays.asList("hi!", "test hud");
    }
}
