package lol.ethan.may.events.user;

import lol.ethan.may.events.Event;
import org.lwjgl.input.Mouse;

public class MouseEvent extends Event {
    public final int button = Mouse.getEventButton();
    public final int x = Mouse.getEventX();
    public final int y = Mouse.getEventY();
    public final boolean state = Mouse.getEventButtonState();
}

