package student.abstracts;

import student.ClueField;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public abstract class Clues {
    private static final Logger LOGGER = Logger.getLogger(Clues.class.getName());
    protected ArrayList<ArrayList<ClueField>> clues = new ArrayList<>();
    private ArrayList<Boolean> completed = new ArrayList<>();
    private boolean complete = false;
    private final ArrayList<Collection<ClueField>> singleColors = new ArrayList<>();
    private ArrayList<ArrayList<Boolean>> savedCompleted = new ArrayList<>();

    public Clues() {
    }

    public void save() {
        ArrayList<Boolean> currentCompleted = new ArrayList<>();
        currentCompleted.addAll(completed);
        savedCompleted.add(currentCompleted);

        for (ArrayList<ClueField> clue : clues) {
            for (ClueField clueField : clue) {
                clueField.save();
            }
        }
    }

    public void load() {
        if (savedCompleted.size() == 0) LOGGER.severe("Nothing to load!");
        completed = savedCompleted.get(savedCompleted.size() - 1);
        savedCompleted.remove(completed);

        for (ArrayList<ClueField> clue : clues) {
            for (ClueField clueField : clue) {
                clueField.load();
            }
        }

        isComplete();
    }



    public Collection<ClueField> getSingleColors(int index) {
        return this.singleColors.get(index);
    }

    public void finalizeCreation() {
        for (ArrayList<ClueField> clue : clues) {
            ArrayList<Character> colors = new ArrayList<>();
            HashMap<Character, ClueField> saved = new HashMap<>();
            ArrayList<Character> removed = new ArrayList<>();
            for (ClueField aClue : clue) {
                if (!removed.contains(aClue.getColor())) {
                    if (colors.contains(aClue.getColor())) {
                        colors.remove((Character) aClue.getColor());
                        saved.remove(aClue.getColor());
                        removed.add(aClue.getColor());
                    } else {
                        colors.add(aClue.getColor());
                        saved.put(aClue.getColor(), aClue);
                    }
                }
            }

            singleColors.add(saved.values());
        }

    }

    public void addLineOfClues(String input) {
        StringTokenizer st = new StringTokenizer(input, ",");

        if (Objects.equals(input, "")) {
            addColumn(true);
        } else {
            addColumn();
        }

        boolean loadingColor = true;
        char color = 0;
        while (st.hasMoreTokens()) {
            if (loadingColor) {
                color = st.nextToken().charAt(0);
                loadingColor = false;
            } else {
                addClueFront(clues.size() - 1, new ClueField(color, Integer.parseInt(st.nextToken())));
                loadingColor = true;
            }
        }
    }

    public void setComplete(int column) {
        if (column < 0 || column >= completed.size()) {
            LOGGER.warning("Trying to access non-existent column");
            return;
        }
        completed.set(column, true);
    }

    public boolean isComplete(int column) {
        if (column < 0 || column >= completed.size()) {
            LOGGER.warning("Accessing non-existent clue column");
            return false;
        }
        return completed.get(column);
    }

    private void addColumn(boolean complete) {
        clues.add(new ArrayList<>());
        completed.add(complete);
    }

    private void addColumn() {
        addColumn(false);
    }

    public void addClueBack(int column, ClueField clue) {
        if (clues.size() <= column) addColumn();
        clues.get(column).add(clue);
    }

    public void addClueFront(int column, ClueField clue) {
        if (clues.size() <= column) addColumn();
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

    /**
     * Returns the first not done clue with specified color
     */
    public int getLongestClue(int index, boolean backwards, char color) {
        int max = -1;
        int maxVal = -1;
        ArrayList<ClueField> get = clues.get(index);
        if (backwards) {
            for (int i = 0; i < get.size(); i++) {
                ClueField cf = get.get(i);
                if (!cf.isDone() && cf.getColor() == color) {
                    if (cf.getHowMany() > maxVal) {
                        max = i;
                        maxVal = cf.getHowMany();
                    } else if (cf.getHowMany() == maxVal) {
                        max = -1;
                    }
                }
            }
        } else {
            for (int i = get.size() - 1; i >= 0; i--) {
                ClueField cf = get.get(i);
                if (!cf.isDone() && cf.getColor() == color) {
                    if (cf.getHowMany() > maxVal) {
                        max = i;
                        maxVal = cf.getHowMany();
                    } else if (cf.getHowMany() == maxVal) {
                        max = -1;
                    }
                }
            }
        }

        return get.size() - max - 1;
    }

    public boolean isComplete() {
        if (complete) return true;

        complete = true;
        for (int i = 0; i < getClueLength(); i++) {
            if (!completed.get(i)) {
                boolean done = true;
                for (int j = 0; j < getClueLength(i); j++) {
                    done = clues.get(i).get(j).isDone();
                    if (!done) break;
                }
                if (done) {
                    completed.set(i, true);
                } else {
                    complete = false;
                    return false;
                }
            }
        }
        return complete;
    }

    public void setCluesDone(int index, ArrayList<Integer> lowers, ArrayList<Integer> highers) {
        if (completed.get(index)) return;
        for (int i = 0; i < getClueLength(index); i++) {
            if (clues.get(index).get(i).isDone()) continue;
            getClue(index, i).setDone(lowers.get(i), highers.get(i));
        }
        isComplete(index);
    }
}
