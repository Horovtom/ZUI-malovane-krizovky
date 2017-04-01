package student;

import java.util.logging.Logger;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class GridField {
    private static final Logger LOGGER = Logger.getLogger(GridField.class.getName());
    private char color='_';
    private boolean locked = false;

    public GridField() {}

    public char getColor() {
        return color;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setColor(char color) {
        if (this.color == color) return;
        if (locked) LOGGER.warning("Trying to set locked cell... Doing so...");
        this.color = color;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public String toString() {
        return "GridField{" +
                "color=" + color +
                ", locked=" + locked +
                '}';
    }

    public boolean isCross() {
        return color == '_' && locked;
    }
}
