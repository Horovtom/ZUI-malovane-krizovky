package tests;

import org.junit.Test;
import student.ClueField;
import student.Grid;
import student.LeftClues;
import student.UpperClues;

import static org.junit.Assert.*;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class GridTest {
    @org.junit.Test
    public void generateBoundsSimple() throws Exception {
        LeftClues lc = new LeftClues();
        lc.addClue(0, new ClueField('B', 2));
        lc.addClue(1, new ClueField('B', 2));
        UpperClues uc = new UpperClues();
        uc.addClue(0, new ClueField('B', 2));
        uc.addClue(1, new ClueField('B', 2));
        Grid grid = new Grid(lc, uc);
        grid.generateBounds();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                assertEquals('B', grid.getCell(i, j));
            }
        }
    }

    @Test
    public void generateBoundsMiddle() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();
        lc.addClue(0, new ClueField('B', 1));
        lc.addClue(0, new ClueField('B', 1));
        lc.addClue(1, new ClueField('C', 2));
        lc.addClue(2, new ClueField('C', 1));
        lc.addClue(2, new ClueField('S', 2));
        uc.addClue(0, new ClueField('S', 1));
        uc.addClue(0, new ClueField('C', 1));
        uc.addClue(0, new ClueField('B', 1));
        uc.addClue(1, new ClueField('S', 1));
        uc.addClue(1, new ClueField('C', 1));
        uc.addClue(2, new ClueField('C', 1));
        uc.addClue(2, new ClueField('B', 1));
        Grid grid = new Grid(lc, uc);
        grid.generateBounds();
        assertEquals('B', grid.getCell(0, 0));
        assertEquals('_', grid.getCell(1, 0));
        assertEquals('B', grid.getCell(2, 0));
        assertEquals('C', grid.getCell(0, 1));
        assertEquals('S', grid.getCell(0, 2));
        assertEquals('S', grid.getCell(1, 2));
        assertEquals('C', grid.getCell(2, 2));
        assertEquals('C', grid.getCell(1, 1));
        assertEquals('_', grid.getCell(2, 1));
    }

}