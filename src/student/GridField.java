package student;

import org.junit.Test;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) {
            return false;
        } else if (getClass() == o.getClass()) {
            GridField gridField = (GridField) o;

            return color == gridField.color && locked == gridField.locked;
        } else if (Character.class == o.getClass()) {
            char i = (Character) o;
            return color == i;
        } else return false;
    }

    @Override
    public int hashCode() {
        int result = (int) color;
        result = 31 * result + (locked ? 1 : 0);
        return result;
    }
}
