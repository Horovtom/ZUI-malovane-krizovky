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
