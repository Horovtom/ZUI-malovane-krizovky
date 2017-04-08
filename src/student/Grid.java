package student;

import student.abstracts.Clues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class Grid {
    private static final Logger LOGGER = Logger.getLogger(Grid.class.getName());
    private LeftClues leftClues;
    private UpperClues upperClues;
    private GridField[][] grid;
    private int width, height;
    private String solution = null;

    public void save() {
        leftClues.save();
        upperClues.save();

        for (GridField[] gridFields : grid) {
            for (GridField gridField : gridFields) {
                gridField.save();
            }
        }
    }

    public void load() {
        leftClues.load();
        upperClues.load();

        for (GridField[] gridFields : grid) {
            for (GridField gridField : gridFields) {
                gridField.load();
            }
        }


    }

    public Grid(LeftClues leftClues, UpperClues upperClues) {
        this.leftClues = leftClues;
        leftClues.finalizeCreation();
        this.upperClues = upperClues;
        upperClues.finalizeCreation();
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

        findEmptyLines();
    }

    private void findEmptyLines() {
        for (int i = 0; i < height; i++) {
            if (leftClues.isComplete(i)) {
                crossBetween(false, i, 0, width);
            }
        }
        for (int i = 0; i < width; i++) {
            if (upperClues.isComplete(i)) {
                crossBetween(true, i, 0, height);
            }
        }
    }

    public boolean fillObvious() {
        boolean changed = false;
        for (int i = 0; i < height; i++) {
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

    private boolean fillObvious(boolean column, int index, boolean backwards) {
        int increment = backwards ? -1 : 1;
        int bound = backwards ? 0 : column ? height - 1 : width - 1;
        int fieldCounter = backwards ? bound : 0;
        Clues cluesUsed = column ? upperClues : leftClues;
        int cluesCounter = backwards ? cluesUsed.getClueLength(index) - 1 : 0;
        boolean changed = false;
        ClueField currentClue = cluesUsed.getClue(index, cluesCounter);
        GridField currentField = getField(column, index, fieldCounter);
        int counter = currentClue.getHowMany();
        boolean inColor = false;
        int firstColor = -1;
        ClueField lastClue = null;
        while (currentClue.isDone()) {
            if (lastClue != null) {
                int lowerBound = backwards ? currentClue.getHigherEnd() : lastClue.getHigherEnd();
                int higherBound = backwards ? lastClue.getLowerEnd() : currentClue.getLowerEnd();
                crossBetween(column, index, lowerBound + 1, higherBound);
            } else {
                changed = crossBetween(column, index, backwards ? column ? height - 1 : width - 1 : 0, backwards ? currentClue.getHigherEnd() : currentClue.getLowerEnd()) || changed;
            }
            fieldCounter = backwards ? currentClue.getLowerEnd() : currentClue.getHigherEnd();
            char lastColor = currentClue.getColor();
            lastClue = currentClue;
            cluesCounter += increment;
            currentClue = cluesUsed.getClue(index, cluesCounter);
            if (currentClue == null) return changed;
            if (lastColor == currentClue.getColor()) {
                fieldCounter += increment;
                currentField = getField(column, index, fieldCounter);
                if (currentField == null) return changed;
                if (!currentField.isCross()) {
                    changed = true;
                    currentField.crossOut();
                    fieldCounter += increment;
                }
            }
        }

        while (currentField != null) {
            if (currentField.isCross()) {
                counter = currentClue.getHowMany();
                firstColor = -1;
                inColor = false;

            } else if (currentField.getColor() == currentClue.getColor()) {
                inColor = true;
                if (firstColor == -1) firstColor = fieldCounter;
                counter--;

            } else if (!inColor && currentField.isSpace()) {
                break;
            }

            if (counter == 0) {
                if (inColor) {
                    changed = currentClue.isDone() || changed;
                    colorBetween(column, index, firstColor, fieldCounter, currentClue.getColor());
                    char lastColor = currentClue.getColor();
                    cluesCounter += increment;
                    currentClue = cluesUsed.getClue(index, cluesCounter);

                    if (currentClue == null) return true;
                    if (currentClue.getColor() == lastColor) {
                        fieldCounter += increment;
                    }
                    inColor = false;
                    firstColor = -1;
                    counter = currentClue.getHowMany();
                }
            }
            fieldCounter += increment;
            currentField = getField(column, index, fieldCounter);
        }

        //TODO: Missing function for this situation: [1 2] [__B_B___]

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


    public GridField getField(boolean column, int rowIndex, int elemIndex) {
        if (column) {
            if (elemIndex >= height || elemIndex < 0) return null;
            return grid[elemIndex][rowIndex];
        } else {
            if (elemIndex >= width || elemIndex < 0) return null;
            return grid[rowIndex][elemIndex];
        }
    }

    /**
     * Fills in boundaries just according to numbers
     * Does not take into consideration already placed cells
     */
    public boolean generateBounds() {
        boolean changed = false;
        boolean currentChanged;
        for (int i = 0; i < height; i++) {
            currentChanged = generateBounds(false, i);
            if (currentChanged) {
                crossCompleted(false, i, false);
                crossCompleted(false, i, true);
            }
            changed = currentChanged || changed;
        }
        crossFullLines();
        for (int i = 0; i < width; i++) {
            currentChanged = generateBounds(true, i);
            if (currentChanged) {
                crossCompleted(true, i, false);
                crossCompleted(true, i, true);
            }
            changed = currentChanged || changed;
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
        for (int i = 0; i < height; i++) {
            if (leftClues.isComplete(i)) continue;
            changed = crossCompleted(false, i, false) || changed;
            changed = crossCompleted(false, i, true) || changed;
        }

        for (int i = 0; i < width; i++) {
            if (upperClues.isComplete(i)) continue;
            changed = crossCompleted(true, i, false) || changed;
            changed = crossCompleted(true, i, true) || changed;
        }
        return changed;
    }

    public boolean isCross(int x, int y) {
        return grid[y][x].isCross();
    }

    private boolean crossCompleted(boolean column, int index, boolean backwards) {

        boolean changed = false;
        int increment = backwards ? -1 : 1;
        GridField currentField;
        Clues cluesUsed = column ? upperClues : leftClues;
        int fieldCounter = backwards ? column ? height - 1 : width - 1 : 0;
        int clueCounter = backwards ? cluesUsed.getClueLength(index) - 1 : 0;
        ClueField currentClue = cluesUsed.getClue(index, clueCounter);
        boolean stuffed = true;

        while (currentClue.isDone()) {
            fieldCounter = backwards ? currentClue.getLowerEnd() - 1 : currentClue.getHigherEnd() + 1;
            clueCounter += increment;
            currentClue = cluesUsed.getClue(index, clueCounter);
            if (currentClue == null) {
                return changed;
            }
        }

        int oldFieldCounter = fieldCounter;
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
                        getField(column, index, fieldCounter + i * increment).setColor(currentClue.getColor());
                    }
                    setCompletedClue(column, index, fieldCounter, clueCounter);
                    changed = true;
                    fieldCounter += currentClue.getHowMany() * increment;
                    clueCounter += increment;
                    currentClue = cluesUsed.getClue(index, clueCounter);
                    if (currentClue == null) break;
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
                    if (lc != null && length == lc.getHowMany()) {
                        setCompletedClue(column, index, fieldCounter, longestClue);
                        fieldCounter = backwards ? currentClue.getLowerEnd() - 1 : currentClue.getHigherEnd() + 1;
                        currentClue = cluesUsed.getClue(index, longestClue);
                        changed = true;
                    } else {
                        fieldCounter += increment * length;
                    }
                } else {
                    fieldCounter += increment;
                }
            }
            currentField = getField(column, index, fieldCounter);
        }
        return changed;
    }

    private void setCompletedClue(boolean column, int index, int cell, int clueIndex) {
        int higherEnd = cell;
        int lowerEnd = cell;
        Clues cluesUsed = column ? upperClues : leftClues;
        ClueField currentClue = cluesUsed.getClue(index, clueIndex);
        if (currentClue.isDone()) {
            //LOGGER.warning("Trying to set clue that is already complete... Skipping");
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
        } else {
            //It is the last clue
            int max = column ? height : width;
            crossBetween(column, index, higherEnd, max);
        }
        if (previousClue != null) {
            if (previousClue.isDone()) {
                crossBetween(column, index, lowerEnd, previousClue.getHigherEnd());
            } else if (previousClue.getColor() == color) {
                setFieldCross(column, index, lowerEnd);
            }
        } else {
            //It is the last clue
            crossBetween(column, index, lowerEnd, -1);
        }
        lowerEnd++;
        higherEnd--;

        currentClue.setDone(lowerEnd, higherEnd);
        for (int i = lowerEnd; i <= higherEnd; i++) {
            getField(column, index, i).setLocked(true);
        }
    }

    /**
     * Crosses all between index1 and index2, including index1 if they are not the same
     */
    private boolean crossBetween(boolean column, int index, int index1, int index2) {
        if (index1 == index2) return false;
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
        int max = backwards ? 0 : column ? height - 1 : width - 1;
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
        int max = backwards ? 0 : column ? height - 1 : width - 1;
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
                return fieldCounter - increment * (clue.getHowMany() - 1);
            }

            fieldCounter += increment;
            currentField = getField(column, index, fieldCounter);
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
                positions[0] = generateNextPosition(column, 0, index, currClue, false);
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

        int boundary = backwards ? 0 : column ? height : width;
        GridField field;
        while ((backwards ? (i >= boundary) : (i < boundary)) && counter < clue.getHowMany()) {
            field = getField(column, index, i);

            if (!field.isCross()) counter++;
            if (backwards) i--;
            else i++;
        }

        if (backwards ? i >= 0 : i < boundary) {
            field = getField(column, index, i);
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

    private String getColInStringDetailed(int col) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < upperClues.getClueLength(col); i++) {
            sb.append(upperClues.getClue(col, i).getColor()).append("|").append(upperClues.getClue(col, i).getHowMany()).append(" ");
        }

        sb.append(" = ");
        for (int i = 0; i < height; i++) {
            if (grid[i][col].isCross()) {
                sb.append("-");
            } else
                sb.append(grid[i][col].getColor());
        }
        return sb.toString();
    }

    private String getRowInStringDetailed(int row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < leftClues.getClueLength(row); i++) {
            sb.append(leftClues.getClue(row, i).getColor()).append("|").append(leftClues.getClue(row, i).getHowMany()).append(" ");
        }

        sb.append(" = ");
        for (int i = 0; i < width; i++) {
            if (grid[row][i].isCross()) {
                sb.append("-");
            } else
                sb.append(grid[row][i].getColor());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            if (i != 0) sb.append('\n');
            for (int j = 0; j < width; j++) {
//                if (grid[i][j].isCross()) sb.append('+');
//                else sb.append(grid[i][j].getColor());
                sb.append(grid[i][j].getColor());
            }
        }
        sb.append('\n');
        return sb.toString();
    }

    public void setCross(int x, int y) {
        grid[y][x].crossOut();
    }

    public boolean solve() {
        boolean changed;
        do {
            changed = generateBounds();
            //printDifference();
            if (isComplete()) return true;
            boolean currChanged;
//            currChanged = fillObvious();
            //          if (currChanged) LOGGER.info("fillObvious() changed something!");
            //        changed = currChanged || changed;
            //printDifference();
            currChanged = crossCompleted();
//            if (currChanged) LOGGER.info("crossCompleted() changed something!");
            changed = currChanged || changed;
            changed = fillSingles() || changed;
            currChanged = crossLines();
//            if (currChanged) LOGGER.info("crossLines() changed something!");
            changed = currChanged || changed;
            crossFullLines();
            //printDifference();
            //System.out.println(this);
            if (isComplete()) return true;
        } while (changed);
        checkSolution();
        return false;
    }

    /**
     * @return heuristically best variable to change
     */
    public CSPVariable getNextVariable() {
        //TODO: IMPLEMENT
        return null;
    }

    /**
     * Fills tooth of same color when there is only one clue that has that color
     */
    private boolean fillSingles() {
        boolean changed = false;
        for (int i = 0; i < height; i++) {
            if (leftClues.isComplete(i)) continue;
            changed = fillSingles(false, i) || changed;
            changed = fillSingles(false, i) || changed;
        }

        for (int i = 0; i < width; i++) {
            if (upperClues.isComplete(i)) continue;
            changed = fillSingles(true, i) || changed;
            changed = fillSingles(true, i) || changed;
        }
        return changed;
    }

    private boolean fillSingles(boolean column, int index) {
        Clues cluesUsed = column ? upperClues : leftClues;
        int fieldCounter = 0;
        boolean changed = false;

        Collection<ClueField> singleColors = cluesUsed.getSingleColors(index);

        for (ClueField clueField : singleColors) {
            while (getField(column, index, fieldCounter).getColor() != clueField.getColor()) {
                fieldCounter = findNextBlock(column, index, fieldCounter);
                if (fieldCounter < 0) break;
                if (getField(column, index, fieldCounter).getColor() != clueField.getColor()) {
                    fieldCounter += howLongIsColorChain(column, index, false, fieldCounter);
                }
            }
            if (fieldCounter < 0) {
                fieldCounter = 0;
                continue;
            }
            int max = getLastCellWithColor(column, index, fieldCounter, clueField.getColor(), clueField.getHowMany());
            if (max > fieldCounter) {
                for (int i = fieldCounter; i <= max; i++) {
                    changed = getField(column, index, i).setColor(clueField.getColor()) || changed;
                    getField(column, index, i).setLocked(true);
                }
            }

            fieldCounter = 0;
        }

        return changed;
    }

    private int getLastCellWithColor(boolean column, int index, int start, char color, int max) {
        GridField currField;
        for (int i = start + max; i > start; i--) {
            currField = getField(column, index, i);
            if (currField == null) continue;
            if (currField.getColor() == color) {
                return i;
            }
        }
        return -1;
    }

    private boolean crossLines(boolean column, int index) {
        boolean changed = false;
        Clues cluesUsed = column ? upperClues : leftClues;
        int clueCounter = 0;
        int fieldCounter = 0;
        ClueField currentClue = cluesUsed.getClue(index, clueCounter);
        GridField currentField;
        int[] cluesCoords = new int[cluesUsed.getClueLength(index)];
        while (currentClue != null) {
            fieldCounter = findNextBlock(column, index, fieldCounter);
            currentField = getField(column, index, fieldCounter);
            if (currentField == null || currentField.getColor() != currentClue.getColor()) return false;
            int size = getBlockSize(column, index, fieldCounter);
            if (size != currentClue.getHowMany()) return false;
            cluesCoords[clueCounter] = fieldCounter;
            fieldCounter += size;
            clueCounter++;
            currentClue = cluesUsed.getClue(index, clueCounter);
        }

        for (int i = 0; i < cluesCoords.length; i++) {
            changed = lockClue(column, index, i, cluesCoords[i]) || changed;
        }
        return changed;
    }

    private int findNextBlock(boolean column, int index, int start) {
        GridField field = getField(column, index, start);
        while (field.getColor() == '_') {
            field = getField(column, index, ++start);
            if (field == null) return -1;
        }
        return start;
    }

    /**
     * Returns size of block of specified color... Including spaces.
     */
    private int getBlockSize(boolean column, int index, int start) {
        int counter = 1;
        char color = getField(column, index, start).getColor();
        GridField currField = getField(column, index, start + counter);
        while (currField != null && currField.getColor() == color) {
            counter++;
            currField = getField(column, index, start + counter);
        }
        return counter;
    }

    private boolean crossLines() {
        boolean changed = false;
        for (int i = 0; i < height; i++) {
            if (!leftClues.isComplete(i))
                changed = crossLines(false, i) || changed;
        }
        for (int i = 0; i < width; i++) {
            if (!upperClues.isComplete(i)) {
                changed = crossLines(true, i) || changed;
            }
        }
        return changed;
    }

    public boolean isComplete() {
        return leftClues.isComplete() && upperClues.isComplete();
    }

    /**
     * Locks specified clue and cells surrounding it if it is nesessary
     *
     * @param fieldCounter anywhere in clues color field
     */
    private boolean lockClue(boolean column, int index, int clueCounter, int fieldCounter) {
        boolean changed = false;
        Clues cluesUsed = column ? upperClues : leftClues;
        char color = getField(column, index, fieldCounter).getColor();
        boolean upper = true;
        boolean lower = true;
        int lowerOffset = fieldCounter, upperOffset = fieldCounter;
        int offset = 0;
        GridField currField;
        while (upper || lower) {
            if (upper) {
                currField = getField(column, index, fieldCounter + offset);
                if (currField == null || currField.getColor() != color) {
                    upper = false;
                    upperOffset = fieldCounter + offset - 1;
                } else {
                    currField.setLocked(true);
                }
            }
            if (lower) {
                currField = getField(column, index, fieldCounter - offset);
                if (currField == null || currField.getColor() != color) {
                    lower = false;
                    lowerOffset = fieldCounter - offset + 1;
                } else {
                    currField.setLocked(true);
                }
            }
            offset++;
        }
        ClueField currentClue = cluesUsed.getClue(index, clueCounter);
        if (currentClue.isDone()) {
            if (currentClue.getHigherEnd() != upperOffset || currentClue.getLowerEnd() != lowerOffset) {
                LOGGER.severe("Somebody set this clue incorrectly! " + currentClue + " on line: " + getInStringDetailed(column, index));
                cluesUsed.getClue(index, clueCounter).setDone(lowerOffset, upperOffset);
                changed = true;
            }
        } else {
            cluesUsed.getClue(index, clueCounter).setDone(lowerOffset, upperOffset);
            changed = true;
        }

        ClueField nextClue = cluesUsed.getClue(index, clueCounter + 1);
        if (nextClue != null) {
            if (nextClue.isDone()) {
                if (nextClue.getLowerEnd() > upperOffset + 1) {
                    changed = crossBetween(column, index, upperOffset + 1, nextClue.getLowerEnd()) || changed;
                }
            } else if (nextClue.getColor() == color) {
                GridField toCross = getField(column, index, upperOffset + 1);
                if (toCross != null) changed = toCross.crossOut() || changed;
            }
        } else {
            changed = crossBetween(column, index, column ? height - 1 : width - 1, upperOffset) || changed;
        }
        nextClue = cluesUsed.getClue(index, clueCounter - 1);
        if (nextClue != null) {
            if (nextClue.isDone()) {
                if (nextClue.getHigherEnd() < lowerOffset - 1) {
                    changed = crossBetween(column, index, lowerOffset - 1, nextClue.getHigherEnd()) || changed;
                }
            } else if (nextClue.getColor() == color) {
                GridField toCross = getField(column, index, lowerOffset - 1);
                if (toCross != null) changed = toCross.crossOut() || changed;
            }
        } else {
            changed = crossBetween(column, index, 0, lowerOffset) || changed;
        }
        return changed;
    }

    private String getInStringDetailed(boolean column, int index) {
        if (column) {
            return getColInString(index);
        } else return getRowInString(index);
    }

    private void crossFullLines() {
        for (int y = 0; y < height; y++) {
            if (leftClues.isComplete(y)) continue;

            ArrayList<Integer> lowers = new ArrayList<>();
            ArrayList<Integer> highers = new ArrayList<>();
            boolean full = true;
            char lastColor = 0;
            boolean inColor = false;
            for (int x = 0; x < width; x++) {
                if (!grid[y][x].isLocked()) {
                    full = false;
                    break;
                } else if (inColor) {
                    if (grid[y][x].isCross()) {
                        highers.add(x - 1);
                        inColor = false;
                        lastColor = '_';
                    } else if (grid[y][x].getColor() != lastColor) {
                        highers.add(x - 1);
                        lowers.add(x);
                        lastColor = grid[y][x].getColor();
                    }
                } else if (!grid[y][x].isCross()) {
                    inColor = true;
                    lowers.add(x);
                    lastColor = grid[y][x].getColor();
                }
            }

            if (full) {
                if (highers.size() < lowers.size()) {
                    highers.add(width - 1);
                }
                leftClues.setCluesDone(y, lowers, highers);
            }
        }

        for (int x = 0; x < width; x++) {
            if (upperClues.isComplete(x)) continue;

            ArrayList<Integer> lowers = new ArrayList<>();
            ArrayList<Integer> highers = new ArrayList<>();
            boolean full = true;
            char lastColor = 0;
            boolean inColor = false;
            for (int y = 0; y < height; y++) {
                if (!grid[y][x].isLocked()) {
                    full = false;
                    break;
                } else if (inColor) {
                    if (grid[y][x].isCross()) {
                        highers.add(y - 1);
                        inColor = false;
                        lastColor = '_';
                    } else if (grid[y][x].getColor() != lastColor) {
                        highers.add(y - 1);
                        lowers.add(y);
                        lastColor = grid[y][x].getColor();
                    }
                } else if (!grid[y][x].isCross()) {
                    inColor = true;
                    lowers.add(y);
                    lastColor = grid[y][x].getColor();
                }
            }

            if (full) {
                if (highers.size() < lowers.size()) {
                    highers.add(height - 1);
                }
                upperClues.setCluesDone(x, lowers, highers);
            }
        }


    }

    public boolean checkSolution() {
        if (solution == null) return true;

        int stringCounter = 0;
        for (int y = 0; y < height; y++) {
            String line = getRowInString(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) != solution.charAt(stringCounter++)) {
                    if (!grid[y][x].isSpace()) {
                        LOGGER.severe("Invalid solution! Expected: " + solution.charAt(stringCounter - 1) + ", got: " +
                                line.charAt(x) + ", at x=" + x + ", y=" + y);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void printDifference() {
        if (solution == null) return;
        if (!checkSolution()) return;

        int counter = 0;
        int stringCounter = 0;
        for (int y = 0; y < height; y++) {
            String line = getRowInString(y);
            int currCounter = 0;
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) != solution.charAt(stringCounter++)) {
                    sb.append('X');
                    counter++;
                    currCounter++;
                } else {
                    sb.append(line.charAt(x));
                }
            }
            sb.append("    ").append(currCounter);
            System.out.println(sb.toString());
        }
        System.out.println("DIFFERENCE: " + counter);
        System.out.println("\n-----------\n");
    }

    public void loadSolution(String string) {
        solution = string;
    }

    public boolean isValid() {
        boolean valid = true;


    }

    private boolean isValid(boolean column, int index) {
        Clues cluesUsed = column ? upperClues : leftClues;
        ClueField currentClue = cluesUsed.getClue(index, 0);
        GridField currentField = getField(column, index, 0);
        int fieldCounter = 0;
        int clueCounter = 0;

        while (currentClue != null) {
            int newFieldCounter = findNextBlock(column, index, fieldCounter);
            if (newFieldCounter == -1) return canItFit(column, index, fieldCounter, clueCounter);
            fieldCounter = newFieldCounter;
            currentField = getField(column, index, fieldCounter);
            while (currentClue.getColor() != currentField.getColor()) {
                //TODO: THIS IS AVSOLUTELY BADLY!!! HNGUOBEUO
            }
        }

    }
}
