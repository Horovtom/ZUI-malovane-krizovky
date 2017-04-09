package student;

import java.util.ArrayList;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class ClueField {
    //private static final Logger LOGGER = Logger.getLogger(ClueField.class.getName());

    private final char color;
    private final int howMany;
    private boolean done = false;
    private int lowerEnd, higherEnd;

    private ArrayList<Boolean> savedDone = new ArrayList<Boolean>();
    private ArrayList<int[]> savedBounds = new ArrayList<int[]>();
    private boolean valid = true;

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

        if (higherEnd - lowerEnd != howMany - 1) {
            valid = false;
            //LOGGER.severe("Trying to set this clue as done even though there are wrong bounds!");
        }
    }

    public void save() {
        savedDone.add(done);
        savedBounds.add(new int[]{lowerEnd, higherEnd});
    }

    public void load() {
        done = savedDone.get(savedDone.size() - 1);
        savedDone.remove(savedDone.size() - 1);
        int[] toLoadBounds = savedBounds.get(savedBounds.size() - 1);
        savedBounds.remove(savedBounds.size() - 1);
        lowerEnd = toLoadBounds[0];
        higherEnd = toLoadBounds[1];
        valid = true;
    }

    public boolean isValid() {
        return valid;
    }
}
