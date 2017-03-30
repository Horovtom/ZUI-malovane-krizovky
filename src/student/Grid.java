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
            generateBoundsRow(i);
        }
        for (int i = 0; i < grid[0].length; i++) {
            generateBoundsCol(i);
        }
    }

    /**
     * Generates bounds for a specified row... Then it writes it into the grid
     */
    private void generateBoundsRow(int row) {
        int[] positions = generatePositionsArray(row, leftClues);
        char lastColor = 0;
        int lastPosition = grid[0].length - 1;
        for (int i = positions.length - 1; i >= 0; i--) {
            ClueField currClue = leftClues.getClue(row, i);
            if (i != positions.length - 1) {
                if (currClue.getColor() == lastColor)
                    lastPosition -= 1;
            }

            int currentColoredCell = lastPosition - currClue.getHowMany() + 1;
            if (positions[i] >= currentColoredCell)
                colorRowBetween(row, currentColoredCell, positions[i], currClue.getColor());
            lastColor = currClue.getColor();
            lastPosition = currentColoredCell - 1;
        }
    }

    private void colorRowBetween(int row, int start, int end, char color) {
        if (start > end) {
            LOGGER.warning("Start was bigger than end... skipping!");
            return;
        }
        for (int i = start; i <= end; i++) {
            grid[row][i].setColor(color);
            grid[row][i].setLocked(true);
        }
    }

    private void colorColBetween(int col, int start, int end, char color) {
        if (start > end) {
            LOGGER.warning("Start was bigger than end... skipping!");
            return;
        }

        for (int i = start; i <= end; i++) {
            grid[i][col].setColor(color);
            grid[i][col].setLocked(true);

        }
    }

    /**
     * Used with
     * {@link #generateBoundsCol(int)}
     * {@link #generateBoundsRow(int)}
     */
    private int[] generatePositionsArray(int col, Clues clues) {
        int[] positions = new int[clues.getClueLength(col)];
        char lastColor = 0;
        int lastPosition = 0;
        for (int i = 0; i < positions.length; i++) {
            ClueField currClue = clues.getClue(col, i);
            if (i == 0) {
                positions[0] = currClue.getHowMany() - 1;
                lastColor = currClue.getColor();
                lastPosition = currClue.getHowMany();
            } else {
                lastPosition = currClue.getColor() == lastColor ? lastPosition + 1 : lastPosition;
                positions[i] = lastPosition + currClue.getHowMany() - 1;
                lastPosition = positions[i] + 1;
                lastColor = currClue.getColor();
            }
        }
        return positions;
    }

    /**
     * Generates bounds for a specified column... Then it writes it into the grid
     */
    private void generateBoundsCol(int col) {
        int[] positions = generatePositionsArray(col, upperClues);

        int lastColor = 0;
        int lastPosition = grid.length - 1;
        for (int i = positions.length - 1; i >= 0; i--) {
            ClueField currClue = upperClues.getClue(col, i);
            if (i != positions.length - 1) {
                if (currClue.getColor() == lastColor)
                    lastPosition -= 1;
            }

            int currentColoredCell = lastPosition - currClue.getHowMany() + 1;
            if (positions[i] >= currentColoredCell)
                colorColBetween(col, currentColoredCell, positions[i], currClue.getColor());
            lastColor = currClue.getColor();
            lastPosition = currentColoredCell - 1;
        }
    }

    public char getCell(int x, int y) {
        return grid[y][x].getColor();
    }

    public void setCell(int x, int y, char color) {
        grid[y][x].setColor(color);
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
