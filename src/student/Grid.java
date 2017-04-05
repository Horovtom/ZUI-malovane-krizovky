package student;

import student.abstracts.Clues;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class Grid {
    //TODO: Use getField everywhere it can be used.. Namely not used in all generateBounds() stuff
    private static final Logger LOGGER = Logger.getLogger(Grid.class.getName());
    private LeftClues leftClues;
    private UpperClues upperClues;
    private GridField[][] grid;
    private int width, height;

    public Grid(LeftClues leftClues, UpperClues upperClues) {
        this.leftClues = leftClues;
        this.upperClues = upperClues;
        init();
    }

    private void init() {
        grid = new GridField[leftClues.size()][upperClues.size()];
        for (int i = 0; i < leftClues.size(); i++) {
            for (int u = 0; u < upperClues.size(); u++) {
                grid[i][u] = new GridField();
            }
        }
        height = grid.length;
        width = grid[0].length;
    }

    public boolean fillObvious() {
        boolean changed = false;
        for (int i = 0; i < grid.length; i++) {
            if (leftClues.isComplete(i)) continue;
            changed = fillObvious(false, i, false) || changed;
            changed = fillObvious(false, i, true) || changed;
        }
        for (int i = 0; i < width; i++) {
            if (upperClues.isComplete(i)) continue;
            changed = fillObvious(true, i, false) || changed;
            changed = fillObvious(true, i, true) || changed;
        }
        return changed;
    }

    /**
     * Used only with {@link #fillObvious()}
     */
    private boolean fillObvious(boolean column, int index, boolean backwards) {
        int maximum = backwards ? 0 : column ? height : width;
        int cellCounter = backwards ? column ? height - 1 : width - 1 : 0;
        Clues cluesUsed = column ? upperClues : leftClues;
        int clueCounter = backwards ? cluesUsed.getClueLength(index) - 1 : 0;
        boolean locked = false;
        ClueField currentClue = cluesUsed.getClue(index, clueCounter);
        int counter = currentClue.getHowMany();
        GridField currCell;
        int increment = backwards ? -1 : 1;
        ArrayList<Integer> toColor = new ArrayList<>();
        boolean changed = false;

        //Skip all whitespaces
        //decrement counter
        //if there was a color, start coloring
        //if counter == 0, go to next clue

        cellCounter = findNextFreeSpace(column, index, backwards, cellCounter);
        currCell = getField(column, index, cellCounter);
        while (currentClue != null) {
            while (currCell.isCross()) {
                cellCounter += increment;
                currCell = getField(column, index, cellCounter);
            }
//            locked = false;
            while (counter > 0 && currentClue != null && currCell != null) {
                if (currCell.getColor() == '_' && !currCell.isLocked()) {
                    if (locked) {
                        toColor.add(cellCounter);
                    }
                    counter--;
                    cellCounter += increment;
                    currCell = getField(column, index, cellCounter);
                } else if (currCell.getColor() == currentClue.getColor()) {
                    locked = true;
                    counter--;
                    cellCounter += increment;
                    currCell = getField(column, index, cellCounter);
                } else if (currCell.isCross()) {
                    locked = false;
                    toColor.clear();
                    while (currCell.isCross()) {
                        cellCounter += increment;
                        currCell = getField(column, index, cellCounter);
                    }
                    counter = currentClue.getHowMany();
                } else {
                    //This cell is from the previous clue... skip
                    cellCounter += increment;
                    currCell = getField(column, index, cellCounter);
                }
            }
            if (counter == 0) {
                changed = colorBetween(column, index, toColor, currentClue.getColor()) || changed;
                toColor.clear();
            }
            locked = false;
            clueCounter += increment;
            currentClue = cluesUsed.getClue(index, clueCounter);
            if (currentClue == null) return changed;
            counter = currentClue.getHowMany();
        }
        return changed;
    }

    private boolean colorBetween(boolean column, int index, ArrayList<Integer> toColor, char color) {
        boolean changed = false;
        for (Integer integer : toColor) {
            if (column) {
                changed = grid[integer][index].setColor(color) || changed;
            } else {
                changed = grid[index][integer].setColor(color) || changed;
            }
        }
        return changed;
    }

    private int findNextFreeSpace(boolean column, int index, boolean backwards, int currentCell) {
        while (getField(column, index, currentCell).isCross()) {
            currentCell = backwards ? currentCell - 1 : currentCell + 1;
        }
        return currentCell;
    }


    private void crossOutBeforeTillWS(boolean column, int index, boolean backwards, int currentCell) {
        int max = column ? height : width;
        GridField currentField = getField(column, index, currentCell);
        int counter = 0;

        while (currentCell + counter >= 0 && currentCell + counter < max && currentField.getColor() == '_') {
            currentField.crossOut();
            counter = backwards ? counter - 1 : counter + 1;
            if (currentCell + counter < 0 || currentCell + counter >= max) break;
            currentField = getField(column, index, counter + currentCell);
        }
    }

    public void setField(boolean column, int index, int i, char color) {
        if (column) grid[i][index].setColor(color);
        else grid[index][i].setColor(color);
    }

    public GridField getField(boolean column, int rowIndex, int elemIndex) {
        if (column) {
            if (elemIndex >= height || elemIndex < 0) return null;
            return grid[elemIndex][rowIndex];
        } else {
            if (elemIndex >= width || elemIndex < 0) return null;
            return grid[rowIndex][elemIndex];
        }
    }


    public void setCompletedAsLocked() {
        for (int i = 0; i < height; i++) {
            setCompletedAsLockedRow(i);
        }
        for (int i = 0; i < width; i++) {
            setCompletedAsLockedCol(i);
        }
    }

    /**
     * Used by
     * {@link #setCompletedAsLocked()}
     */
    private void setCompletedAsLockedRow(int row) {
        //TODO:COMPLETE
    }

    /**
     * Used by
     * {@link #setCompletedAsLocked()}
     */
    private void setCompletedAsLockedCol(int col) {
        //TODO: COMPLETE
    }

    /**
     * Fills in boundaries just according to numbers
     * Does not take into consideration already placed cells
     */
    public boolean generateBounds() {
        boolean changed = false;
        for (int i = 0; i < height; i++) {
            changed = generateBounds(false, i) || changed;
        }
        for (int i = 0; i < width; i++) {
            changed = generateBounds(true, i) || changed;
        }
        return changed;
    }

    /**
     * Generates bounds for a specified column... Then it writes it into the grid
     *
     * @param column Whether the specified index is relevant to row or column
     */
    private boolean generateBounds(boolean column, int index) {
        boolean changed = false;
        int[] positions = generatePositionsArray(column, index);

        Clues usedClues;
        if (column) usedClues = upperClues;
        else usedClues = leftClues;

        int lastColor = 0;

        int lastPosition;
        if (column) lastPosition = height - 1;
        else lastPosition = width - 1;

        for (int i = positions.length - 1; i >= 0; i--) {
            ClueField currClue = usedClues.getClue(index, i);
            if (i != positions.length - 1) {
                if (currClue.getColor() == lastColor)
                    lastPosition -= 1;
            }

            int currentColoredCell = generateNextPosition(column, lastPosition, index, currClue, true);
            if (positions[i] >= currentColoredCell)
                changed = colorBetween(column, index, currentColoredCell, positions[i], currClue.getColor()) || changed;
            lastColor = currClue.getColor();
            lastPosition = currentColoredCell - 1;
        }
        return changed;
    }

    private boolean colorBetween(boolean column, int index, int start, int end, char color) {
        if (start > end) {
            LOGGER.warning("Start was bigger than end... skipping!");
            return false;
        }
        boolean changed = false;
        for (int i = start; i <= end; i++) {
            if (column) {
                changed = grid[i][index].setColor(color) || changed;
                grid[i][index].setLocked(true);
            } else {
                changed = grid[index][i].setColor(color) || changed;
                grid[index][i].setLocked(true);
            }
        }
        return changed;
    }

    public boolean crossCompleted() {
        boolean changed = false;
        for (int i = 0; i < grid.length; i++) {
            if (leftClues.isComplete(i)) continue;
            changed = crossCompleted(true, i, false) || changed;
            changed = crossCompleted(true, i, true) || changed;
        }

        for (int i = 0; i < grid[0].length; i++) {
            if (upperClues.isComplete(i)) continue;
            changed = crossCompleted(false, i, false) || changed;
            changed = crossCompleted(false, i, true) || changed;
        }
        return changed;
    }

    private boolean crossCompleted(boolean column, int index, boolean backwards) {

        boolean changed = false;
        int increment = backwards ? -1 : 1;
        GridField currentField;
        Clues cluesUsed = column ? upperClues : leftClues;
        int oldFieldCounter = 0;
        int fieldCounter = backwards ? column ? grid.length - 1 : grid[0].length - 1 : 0;
        int clueCounter = backwards ? cluesUsed.getClueLength() - 1 : 0;
        ClueField currentClue = cluesUsed.getClue(index, clueCounter);
        boolean stuffed = true;

        while (currentClue.isDone()) {
            oldFieldCounter = fieldCounter;
            fieldCounter = backwards ? currentClue.getLowerEnd() - 1 : currentClue.getHigherEnd() + 1;
            clueCounter += increment;
            currentClue = cluesUsed.getClue(index, clueCounter);
            if (currentClue == null) {
                return changed;
            }
        }

        fieldCounter = findNextSpaceForClue(column, index, backwards, fieldCounter, currentClue);
        if (fieldCounter == -1) {
            LOGGER.severe("Unsolvable, not enough space for next clue...");
            return true;
        }
        crossBetween(column, index, oldFieldCounter, fieldCounter);

        currentField = getField(column, index, fieldCounter);
        while (currentField != null) {
            if (stuffed) {
                if (currentField.getColor() == currentClue.getColor()) {
                    for (int i = 0; i < currentClue.getHowMany(); i++) {
                        getField(column, index, fieldCounter + i).setColor(currentClue.getColor());
                    }
                    setCompletedClue(column, index, fieldCounter, clueCounter);
                    changed = true;
                    clueCounter += increment;
                    currentClue = cluesUsed.getClue(index, clueCounter);
                    if (currentClue == null) break;
                    fieldCounter += currentClue.getHowMany();
                    fieldCounter = findNextSpaceForClue(column, index, backwards, fieldCounter, currentClue);
                } else if (currentField.getColor() == '_') stuffed = false;
                else {
                    LOGGER.severe("Can't fit next clue in this line... Unsolvable!");
                    return true;
                }
            } else {
                if (!currentField.isSpace() && !currentField.isLocked()) {
                    int length = howLongIsColorChain(column, index, backwards, fieldCounter);
                    int longestClue = cluesUsed.getLongestClue(index, backwards, currentField.getColor());
                    ClueField lc = cluesUsed.getClue(index, longestClue);
                    if (length == lc.getHowMany()) {
                        setCompletedClue(column, index, fieldCounter, clueCounter);
                        fieldCounter = backwards ? currentClue.getLowerEnd() - 1 : currentClue.getHigherEnd() + 1;
                        currentClue = cluesUsed.getClue(index, longestClue);
                        changed = true;
                    }
                } else {
                    fieldCounter += increment;
                }
            }
            currentField = getField(column, index, fieldCounter);
        }
        return changed;
    }

//    private boolean crossCompleted(boolean column, int index, boolean backwards) {
//        boolean changed = false;
//        int fieldCounter = backwards ? column ? grid.length - 1 : grid[0].length - 1 : 0;
//        int max = backwards ? 0 : column ? grid.length - 1 : grid[0].length - 1;
//        int increment = backwards ? -1 : 1;
//        GridField currentField;
//        Clues cluesUsed = column ? upperClues : leftClues;
//        int clueCounter = backwards ? cluesUsed.getClueLength() - 1 : 0;
//        int oldFieldCounter = 0;
//        ClueField currentClue = cluesUsed.getClue(index, clueCounter);
//        boolean stuffed = true;
//
//
//        while (currentClue.isDone()) {
//            oldFieldCounter = fieldCounter;
//            fieldCounter = backwards ? currentClue.getLowerEnd() - 1 : currentClue.getHigherEnd() + 1;
//            clueCounter += increment;
//            currentClue = cluesUsed.getClue(index, clueCounter);
//            if (currentClue == null) {
//                return changed;
//            }
//        }
//
//
//        fieldCounter = findNextSpaceForClue(column, index, backwards, fieldCounter, currentClue);
//        if (fieldCounter == -1) {
//            LOGGER.severe("Unsolvable, not enough space for next clue...");
//            return true;
//        }
//        crossBetween(column, index, oldFieldCounter, fieldCounter);
//        currentField = getField(column, index, fieldCounter);
//        if (currentField.getColor() == currentClue.getColor()) {
//            int length = howLongIsColorChain(column, index, backwards, fieldCounter);
//            if (length == currentClue.getHowMany()) {
//                if (stuffed) {
//                    changed = true;
//                    setCompletedClue(column, index, fieldCounter, clueCounter);
//                    clueCounter += increment;
//                    currentClue = cluesUsed.getClue(index, clueCounter);
//                    if (currentClue == null) return changed;
//                    fieldCounter = findNextFreeSpace(column, index, backwards, )
//                } else {
//                    //Skip length and +1?
//                    char oldColor = currentClue.getColor();
//                    clueCounter += increment;
//                    currentClue = cluesUsed.getClue(index, clueCounter);
//                    if (currentClue.getColor() == oldColor) {
//                        fieldCounter += length + 1;
//                    } else {
//                        fieldCounter += length;
//                    }
//                    currentField = getField(column, index, fieldCounter);
//                }
//            } else if (length > currentClue.getHowMany()) {
//                //It belongs to previous clue which was longer...
//                ClueField previousClue = cluesUsed.getClue(index, --clueCounter);
//                if (previousClue == null) {
//                    LOGGER.severe("Couldnt fit first clue, unsolvable");
//                    return true;
//                }
//                while (previousClue.getColor() != currentClue.getColor() && previousClue.getHowMany() <= length) {
//                    if (previousClue.isDone()) {
//                        LOGGER.severe("Couldnt fit next clue, unsolvable");
//                        return true;
//                    }
//                    previousClue = cluesUsed.getClue(index, --clueCounter);
//                    if (previousClue == null) {
//                        LOGGER.severe("Couldnt fit next clue, unsolvable");
//                        return true;
//                    }
//                }
//
//
//            } else {
//                //Go forth by clueLength
//                fieldCounter += increment * currentClue.getHowMany();
//                //Go forth till there is space
//                currentField = getField(column, index, fieldCounter);
//                while (currentField != null && currentField.getColor() != '_') {
//                    currentField = getField(column, index, ++fieldCounter);
//                }
//                char prevColor = currentClue.getColor();
//                currentClue = cluesUsed.getClue(index, ++clueCounter);
//                if (currentClue == null) return changed;
//                if (prevColor == currentClue.getColor()) {
//                    if (currentField == null) {
//                        LOGGER.severe("Couldnt fit next clue into this line... Unsolvable");
//                        return true;
//                    }
//                    currentField.crossOut();
//                    currentField = getField(column, index, ++fieldCounter);
//                }
//            }
//        } else {
//
//            if (currentField.isSpace()) {
//                //Skip this one...
//
//            } else if (currentField.getColor() == currentClue.getColor()) {
//                //Hooray, i dont know a thing... :)
//
//            } else {
//                //It is other color, which belongs to previousClue
//                ClueField previousClue = cluesUsed.getClue(index, clueCounter - increment);
//                while (previousClue != null && previousClue.isDone()) {
//                    if (previousClue.getColor() == currentField.getColor()) {
//                        if (previousClue.getHowMany() <= currentClue.getHowMany()) {
//
//                        } else {
//                            LOGGER.warning("Error?! Previous clue was longer, still i got here! Should be unreachable");
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//
//
//        return changed;
//    }

    private void setCompletedClue(boolean column, int index, int cell, int clueIndex) {
        int higherEnd = cell;
        int lowerEnd = cell;
        Clues cluesUsed = column ? upperClues : leftClues;
        ClueField currentClue = cluesUsed.getClue(index, clueIndex);
        if (currentClue.isDone()) {
            LOGGER.warning("Trying to set clue that is already complete... Skipping");
            return;
        }
        char color = getField(column, index, cell).getColor();
        if (color != currentClue.getColor()) {
            LOGGER.severe("Cell color and specified clue color were not equal!");
            return;
        }
        GridField upperField = getField(column, index, cell), lowerField = upperField;
        while (upperField != null &&
                color == upperField.getColor()) {
            upperField = getField(column, index, ++higherEnd);
        }
        while (lowerField != null &&
                color == lowerField.getColor()) {
            lowerField = getField(column, index, --lowerEnd);
        }


        ClueField nextClue = cluesUsed.getClue(index, clueIndex + 1), previousClue = cluesUsed.getClue(index, clueIndex - 1);
        if (nextClue != null) {
            if (nextClue.isDone()) {
                crossBetween(column, index, higherEnd, nextClue.getLowerEnd());
            } else if (nextClue.getColor() == color) {
                setFieldCross(column, index, higherEnd);
            }
        }
        if (previousClue != null) {
            if (previousClue.isDone()) {
                crossBetween(column, index, lowerEnd, previousClue.getHigherEnd());
            } else if (previousClue.getColor() == color) {
                setFieldCross(column, index, lowerEnd);
            }
        }
        lowerEnd++;
        higherEnd--;

        currentClue.setDone(lowerEnd, higherEnd);
        for (int i = lowerEnd; i <= higherEnd; i++) {
            getField(column, index, i).setLocked(true);
        }
    }

    /**
     * Crosses all between index1 and index2, including index1
     */
    private boolean crossBetween(boolean column, int index, int index1, int index2) {
        boolean changed = false;
        int increment = index1 > index2 ? -1 : 1;
        for (int i = index1; i != index2; i += increment) {
            if (column) {
                changed = grid[i][index].crossOut() || changed;
            } else {
                changed = grid[index][i].crossOut() || changed;
            }
        }
        return changed;
    }

    private void setFieldCross(boolean column, int index, int i) {
        if (column) {
            if (i >= height || i < 0) return;
            grid[i][index].crossOut();
        } else {
            if (i >= width || i < 0) return;
            grid[index][i].crossOut();
        }
    }

    private int howLongIsColorChain(boolean column, int index, boolean backwards, int start) {
        int max = backwards ? 0 : column ? grid.length - 1 : grid[0].length - 1;
        int counter = 0;
        int increment = backwards ? -1 : 1;
        GridField currentField = getField(column, index, start);
        char color = currentField.getColor();
        while (backwards ? start >= max : start <= max) {
            currentField = getField(column, index, start);
            if (currentField.getColor() == color) {
                counter++;
            } else {
                return counter;
            }
            start += increment;
        }
        return counter;
    }

    private int findNextSpaceForClue(boolean column, int index, boolean backwards, int start, ClueField clue) {
        int increment = backwards ? -1 : 1;
        int max = backwards ? 0 : column ? grid.length - 1 : grid[0].length - 1;
        int fieldCounter = start;
        GridField currentField = getField(column, index, start);
        int counter = clue.getHowMany();

        while (backwards ? fieldCounter >= max : fieldCounter <= max) {
            if (currentField.isCross()) {
                counter = clue.getHowMany();
            } else if (currentField.getColor() == clue.getColor() || currentField.isSpace()) {
                counter--;
            } else {
                //It is other color than mine... Cant fit in there
                return -1;
            }

            if (counter == 0) {
                return fieldCounter + increment * clue.getHowMany();
            }

            fieldCounter += increment;
            currentField = getField(column, index, start);
        }
        return -1;
    }

    private int[] generatePositionsArray(boolean column, int index) {
        Clues cluesUsed = column ? upperClues : leftClues;

        int[] positions = new int[cluesUsed.getClueLength(index)];
        char lastColor = 0;
        for (int i = 0; i < positions.length; i++) {
            ClueField currClue = cluesUsed.getClue(index, i);
            if (i == 0) {
                positions[0] = generateNextPosition(column, 0, 0, currClue, false);
                lastColor = currClue.getColor();
            } else {
                positions[i] = generateNextPosition(
                        column,
                        currClue.getColor() == lastColor ? positions[i - 1] + 2 : positions[i - 1] + 1,
                        index, currClue, false);
                lastColor = currClue.getColor();
            }
        }
        return positions;
    }

    /**
     * Used by {@link #generatePositionsArray(boolean, int)}
     */
    private int generateNextPosition(boolean column, int start, int index, ClueField clue, boolean backwards) {
        int i = start;
        int counter = 0;

        int boundary = column ? height : width;
        GridField field;
        while (i < boundary && counter < clue.getHowMany()) {
            field = column ? grid[i][index] : grid[index][i];

            if (!field.isCross()) counter++;
            if (backwards) i--;
            else i++;
        }
        if (i > boundary) LOGGER.severe("Couldnt fit this clue into line: " + clue);
        else if (i < boundary && i >= 0) {
            field = column ? grid[i][index] : grid[index][i];
            while (field.getColor() == clue.getColor()) {
                if (backwards) i--;
                else i++;
                if (i == boundary || i < 0) break;
                field = column ? grid[i][index] : grid[index][i];
            }
        }
        if (backwards) return i + 1;
        else return i - 1;
    }

    public char getCell(int x, int y) {
        return grid[y][x].getColor();
    }

    public void setCell(int x, int y, char color) {
        grid[y][x].setColor(color);
    }

    public String getRowInString(int row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grid[row].length; i++) {
            sb.append(grid[row][i].getColor());
        }
        return sb.toString();
    }

    public String getColInString(int col) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            sb.append(grid[i][col].getColor());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Grid{");
        for (int i = 0; i < height; i++) {
            sb.append('\n');
            for (int j = 0; j < width; j++) {
                sb.append(grid[i][j].getColor());
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public void setCross(int x, int y) {
        grid[y][x].crossOut();
    }
}
