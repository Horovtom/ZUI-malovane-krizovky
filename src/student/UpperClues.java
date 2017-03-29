package student;

import student.abstracts.Clues;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class UpperClues extends Clues {
    @Override
    public ClueField getClue(int column, int position) {
        if (clues.size() <= column || clues.get(column).size() <= position) {
            LOGGER.warning("Trying to access info about a column position that is non-existent yet!");
            return null;
        }
        return clues.get(column).get(position);
    }
}
