package student;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class ClueField {
    private final char color;
    private final int howMany;
    private boolean done = false;

    public ClueField(char color, int howMany) {
        this.color = color;
        this.howMany = howMany;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public char getColor() {
        return color;
    }

    public int getHowMany() {
        return howMany;
    }
}
