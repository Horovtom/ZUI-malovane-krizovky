package student;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class ClueField {
    private final char color;
    private final int howMany;
    private boolean done = false;
    private int lowerEnd, higherEnd;

    public ClueField(char color, int howMany) {
        this.color = color;
        this.howMany = howMany;
    }

    public boolean isDone() {
        return done;
    }

    public char getColor() {
        return color;
    }

    public int getHowMany() {
        return howMany;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ClueField{");
        sb.append("color=").append(color);
        sb.append(", howMany=").append(howMany);
        sb.append(", done=").append(done);
        sb.append('}');
        return sb.toString();
    }

    public int getHigherEnd() {
        return higherEnd;
    }

    public int getLowerEnd() {
        return lowerEnd;
    }

    public void setDone(int end1, int end2) {
        done = true;
        if (end1 >= end2) {
            lowerEnd = end2;
            higherEnd = end1;
        } else {
            lowerEnd = end1;
            higherEnd = end2;
        }
    }
}
