package student;

import java.util.ArrayList;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class GridField {
    //private static final Logger LOGGER = Logger.getLogger(GridField.class.getName());
    private char color='_';
    private boolean locked = false;

    private ArrayList<Character> savedColor = new ArrayList<Character>();
    private ArrayList<Boolean> savedLocked = new ArrayList<Boolean>();

    GridField() {
    }

    public char getColor() {
        return color;
    }

    boolean isLocked() {
        return locked;
    }

    void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * @return if it set NEW color...
     */
    boolean setColor(char color) {
        if (this.color == color) return false;
        //if (locked) LOGGER.warning("Trying to set locked cell... Doing so...");
        this.color = color;
        this.locked = true;
        return true;
    }

    @Override
    public String toString() {
        return "GridField{" +
                "color=" + color +
                ", locked=" + locked +
                '}';
    }

    boolean isCross() {
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

    boolean crossOut() {
        if (isCross()) return false;
        color = '_';
        locked = true;
        return true;
    }

    boolean isSpace() {
        return color == '_' && !locked;
    }

    void save() {
        savedColor.add(color);
        savedLocked.add(locked);
    }

    void load() {
        locked = savedLocked.get(savedLocked.size() - 1);
        savedLocked.remove(savedLocked.size() - 1);
        color = savedColor.get(savedColor.size() - 1);
        savedColor.remove(savedColor.size() - 1);
    }

    boolean colored() {
        return color != '_';
    }
}
