package tests;

import org.junit.After;
import org.junit.Before;
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
    private Grid testGrid1 = null;

    @Before
    public void setUp() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();
        lc.addLineOfClues("B,1,B,1");
        lc.addLineOfClues("C,1");
        lc.addLineOfClues("C,3");
        lc.addLineOfClues("B,1,C,2");
        uc.addLineOfClues("C,1");
        uc.addLineOfClues("B,1,C,1,B,1");
        uc.addLineOfClues("C,3");
        uc.addLineOfClues("B,1,C,1");
        testGrid1 = new Grid(lc, uc);
    }

    @Test
    public void getField() throws Exception {
        testGrid1.generateBounds();
        for (int i = 1; i < 4; i++) {
            assertEquals('C', testGrid1.getField(true, 2, i).getColor());
        }
        assertEquals('B', testGrid1.getField(false, 3, 1).getColor());
        assertEquals('C', testGrid1.getField(false, 3, 2).getColor());
        assertEquals('_', testGrid1.getField(true, 1, 1).getColor());
    }

    @org.junit.Test
    public void generateBoundsSimple() throws Exception {
        LeftClues lc = new LeftClues();
        lc.addClueBack(0, new ClueField('B', 2));
        lc.addClueBack(1, new ClueField('B', 2));
        UpperClues uc = new UpperClues();
        uc.addClueBack(0, new ClueField('B', 2));
        uc.addClueBack(1, new ClueField('B', 2));
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
        lc.addClueBack(0, new ClueField('B', 1));
        lc.addClueBack(0, new ClueField('B', 1));
        lc.addClueBack(1, new ClueField('C', 2));
        lc.addClueBack(2, new ClueField('C', 1));
        lc.addClueBack(2, new ClueField('S', 2));
        uc.addClueBack(0, new ClueField('S', 1));
        uc.addClueBack(0, new ClueField('C', 1));
        uc.addClueBack(0, new ClueField('B', 1));
        uc.addClueBack(1, new ClueField('S', 1));
        uc.addClueBack(1, new ClueField('C', 1));
        uc.addClueBack(2, new ClueField('C', 1));
        uc.addClueBack(2, new ClueField('B', 1));
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

    @Test
    public void generateBoundsHard() throws Exception {
        testGrid1.generateBounds();
        assertEquals("____", testGrid1.getRowInString(0));
        assertEquals("__C_", testGrid1.getRowInString(1));
        assertEquals("_CC_", testGrid1.getRowInString(2));
        assertEquals("_BC_", testGrid1.getRowInString(3));
    }
}