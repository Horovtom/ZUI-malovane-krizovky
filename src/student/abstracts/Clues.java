package student.abstracts;
import student.ClueField;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public abstract class Clues {
    protected static final Logger LOGGER = Logger.getLogger(Clues.class.getName());
    protected ArrayList<ArrayList<ClueField>> clues = new ArrayList<>();

    public Clues() {}

    public void addLineOfClues(String input) {
        StringTokenizer st = new StringTokenizer(input, ",");

        clues.add(new ArrayList<>());

        boolean loadingColor = true;
        char color = 0;
        while(st.hasMoreTokens()) {
            if (loadingColor) {
                color = st.nextToken().charAt(0);
                loadingColor = false;
            } else {
                addClueFront(clues.size() - 1, new ClueField(color, Integer.parseInt(st.nextToken())));
                loadingColor = true;
            }
        }
    }


    public void addClueBack(int column, ClueField clue) {
        if (clues.size() <= column) clues.add(new ArrayList<>());
        clues.get(column).add(clue);
    }

    public void addClueFront(int column, ClueField clue){
        if (clues.size() <= column) clues.add(new ArrayList<>());
        clues.get(column).add(0, clue);
    }

    /**
     * Returns null if there is no such clue
     */
    public abstract ClueField getClue(int column, int position);

    /**
     * Returns number of columns
     */
    public int getClueLength() {
        return clues.size();
    }

    /**
     * {@link #getClueLength()}
     */
    public int size() {
        return getClueLength();
    }

    /**
     * Returns position count for specified column
     */
    public int getClueLength(int column) {
        if (clues.size() <= column) {
            LOGGER.warning("Trying to access info about clue column, that is non-existent yet!");
            return 0;
        }
        return clues.get(column).size();
    }
}
