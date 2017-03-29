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
    private char[][] grid;

    public Grid(LeftClues leftClues, UpperClues upperClues) {
        this.leftClues = leftClues;
        this.upperClues = upperClues;
        grid = new char[leftClues.size()][upperClues.size()];
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
        int[] positions = new int[leftClues.getClueLength(row)];
        char lastColor = 0;
        int lastPosition = 0;
        for (int i = 0; i < positions.length; i++) {
            ClueField currClue = leftClues.getClue(row, i);
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

        lastPosition = grid[0].length - 1;
        for (int i = positions.length - 1; i >= 0; i--) {
            ClueField currClue = leftClues.getClue(row, i);
            if (i != positions.length - 1) {
                if (currClue.getColor() == lastColor)
                    lastPosition -= 1;
            }

            int currentColoredCell = lastPosition - currClue.getHowMany() + 1;
            if (positions[i] >= currentColoredCell)
                colorRowBetween(row, positions[i], currentColoredCell, currClue.getColor());
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
            grid[row][i] = color;
        }
    }

    private void colorColBetween(int col, int start, int end, char color) {
        if (start > end) {
            LOGGER.warning("Start was bigger than end... skipping!");
            return;
        }

        for (int i = start; i <= end; i++) {
            grid[i][col] = color;
        }
    }

    //TODO:MERGE INTO THIS
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
    }

    /**
     * Generates bounds for a specified column... Then it writes it into the grid
     */
    private void generateBoundsCol(int col) {
        int[] positions = new int[upperClues.getClueLength(col)];
        char lastColor = 0;
        int lastPosition = 0;
        for (int i = 0; i < positions.length; i++) {
            ClueField currClue = upperClues.getClue(col, i);
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

        lastPosition = grid.length - 1;
        for (int i = positions.length - 1; i >= 0; i--) {
            ClueField currClue = upperClues.getClue(col, i);
            if (i != positions.length - 1) {
                if (currClue.getColor() == lastColor)
                    lastPosition -= 1;
            }

            int currentColoredCell = lastPosition - currClue.getHowMany() + 1;
            if (positions[i] >= currentColoredCell)
                colorColBetween(col, positions[i], currentColoredCell, currClue.getColor());
            lastColor = currClue.getColor();
            lastPosition = currentColoredCell - 1;
        }
    }

    public char getCell(int x, int y) {
        return grid[y][x];
    }

    public void setCell(int x, int y, char color) {
        if (!colors.contains(color)) {
            LOGGER.warning("Trying to set color " + color + ", that is unknown... Doing so...");
            colors.add(color);
        }

        grid[y][x] = color;
    }


}
