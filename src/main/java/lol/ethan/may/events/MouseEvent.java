package lol.ethan.may.events;

import org.lwjgl.input.Mouse;

public class MouseEvent extends Event {
    public int button = Mouse.getEventButton();
    public int x = Mouse.getEventX();
    public int y = Mouse.getEventY();
    public boolean state = Mouse.getEventButtonState();
}

