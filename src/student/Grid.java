package student;

import student.abstracts.Clues;

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

    public void fillObvious() {
        for (int i = 0; i < grid.length; i++) {
            fillObvious(false, i, false);
        }
        for (int i = 0; i < width; i++) {
            fillObvious(true, i, false);
        }
    }

    /**
     * Used only with {@link #fillObvious()}
     */
    private void fillObvious(boolean column, int index, boolean backwards) {
        int maximum = column? height : width;
        int currentCell = backwards? column? width : height : 0;
        Clues cluesUsed = column? upperClues : leftClues;
        int clueCounter = backwards? cluesUsed.getClueLength() - 1 : 0;
        boolean locked = true;
        ClueField currentClue = cluesUsed.getClue(index, clueCounter);
        int counter = currentClue.getHowMany();
        GridField currCell = getField(column, index, currentCell);

        if (currCell.isLocked()) {
            if (currCell.getColor() == currentClue.getColor()) {
                int offset = 0;
                while (getField(column, index, currentCell + offset).getColor() == currentClue.getColor() && counter > 0) {
                    offset = backwards? offset - 1 : offset + 1;
                    counter--;
                }
                while (counter > 0) {
                    if (getField(column, index, currentCell + offset).getColor() == '_') {
                        setField(column, index, currentCell + offset, currentClue.getColor());
                        counter--;
                    } else {
                        LOGGER.warning("This cell is already set to: " + getField(column, index, currentCell+offset).getColor() + ", when it should be: " + currentClue.getColor());
                        return;
                    }
                }
                if (counter == 0) {
                    char previousColor = currentClue.getColor();
                    currentClue = cluesUsed.getClue(index, ++clueCounter);
                    counter = currentClue.getHowMany();
                    if (currentClue.getColor() == previousColor) {
                        setField(column, index, currentCell + offset, '_');
                        currentCell = currentCell + offset + 1;
                    } else currentCell += offset;
                    locked = true;
                }

            } else if (currCell.isCross()) {
                //Skip all crosses
                while(currCell.isCross() && currentCell < maximum - 1) currCell = getField(column, index, currentCell + 1);

            }
        }

    }

    public void setField(boolean column, int index, int i, char color) {
        if (column) grid[i][index].setColor(color);
        else grid[index][i].setColor(color);
    }

    public GridField getField(boolean column, int rowIndex, int elemIndex) {
        if (column )return grid[elemIndex][rowIndex];
        else return grid[rowIndex][elemIndex];
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
    public void generateBounds() {
        for (int i = 0; i < height; i++) {
            generateBounds(false, i);
        }
        for (int i = 0; i < width; i++) {
            generateBounds(true, i);
        }
    }

    /**
     * Generates bounds for a specified column... Then it writes it into the grid
     * @param column Whether the specified index is relevant to row or column
     */
    private void generateBounds(boolean column, int index) {
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
                colorBetween(column, index, currentColoredCell, positions[i], currClue.getColor());
            lastColor = currClue.getColor();
            lastPosition = currentColoredCell - 1;
        }
    }

    private void colorBetween(boolean column, int index, int start, int end, char color) {
        if (start > end) {
            LOGGER.warning("Start was bigger than end... skipping!");
            return;
        }

        for (int i = start; i <= end; i++) {
            if (column) {
                grid[i][index].setColor(color);
                grid[i][index].setLocked(true);
            } else {
                grid[index][i].setColor(color);
                grid[index][i].setLocked(true);
            }
        }
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

        int boundary = column? height : width;
        GridField field;
        while (i < boundary && counter < clue.getHowMany()) {
            field = column? grid[i][index] : grid[index][i];

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
}
