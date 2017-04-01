package student;

import student.abstracts.Clues;

import java.util.logging.Logger;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class Grid {

    private static final Logger LOGGER = Logger.getLogger(Grid.class.getName());
    private LeftClues leftClues;
    private UpperClues upperClues;
    private GridField[][] grid;

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
    }

    private void setCompletedAsLocked() {
        for (int i = 0; i < grid.length; i++) {
            setCompletedAsLockedRow(i);
        }
        for (int i = 0; i < grid[0].length; i++) {
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
        for (int i = 0; i < grid.length; i++) {
            generateBounds(false, i);
        }
        for (int i = 0; i < grid[0].length; i++) {
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
        if (column) lastPosition = grid.length - 1;
        else lastPosition = grid[0].length - 1;

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

        int boundary = column? grid.length : grid[index].length;
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
        for (int i = 0; i < grid.length; i++) {
            sb.append(grid[i][col].getColor());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Grid{");
        for (int i = 0; i < grid.length; i++) {
            sb.append('\n');
            for (int j = 0; j < grid[0].length; j++) {
                sb.append(grid[i][j].getColor());
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
