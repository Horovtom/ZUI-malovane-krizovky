package student;

import student.abstracts.Clues;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class LeftClues extends Clues {
    @Override
    public ClueField getClue(int row, int position) {
        if (clues.size() <= row || row < 0 || position < 0 || clues.get(row).size() <= position) {
            //LOGGER.info("Trying to access info about a column position that is non-existent yet!");
            return null;
        }
        return clues.get(row).get(clues.get(row).size() - position - 1);
    }
}
