package tests;

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

    @Test
    public void fillObviousSimple() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();
        lc.addLineOfClues("B,2");
        lc.addLineOfClues("B,2");
        lc.addLineOfClues("B,1");
        uc.addLineOfClues("B,3");
        uc.addLineOfClues("B,2");
        Grid grid = new Grid(lc, uc);
        grid.setCell(0, 0, 'B');
        grid.fillObvious();
        assertEquals("BB",grid.getRowInString(0));
        assertEquals("BB",grid.getRowInString(1));
        assertEquals("B_",grid.getRowInString(2));
    }

    @Test
    public void fillObviousSimpler() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();
        lc.addLineOfClues("B,2");
        lc.addLineOfClues("B,3");
        lc.addLineOfClues("B,2");
        uc.addLineOfClues("B,2");
        uc.addLineOfClues("B,3");
        uc.addLineOfClues("B,2");
        Grid grid = new Grid(lc, uc);

        grid.fillObvious();
        assertEquals("___", grid.getRowInString(0));
        assertEquals("___", grid.getRowInString(1));
        assertEquals("___", grid.getRowInString(2));
    }

    @Test
    public void fillObviousBoolean() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();
        lc.addLineOfClues("B,2");
        lc.addLineOfClues("B,3");
        lc.addLineOfClues("B,2");
        uc.addLineOfClues("B,2");
        uc.addLineOfClues("B,3");
        uc.addLineOfClues("B,2");
        Grid grid = new Grid(lc, uc);
        grid.setCell(2, 0, 'B');
        grid.setCell(0, 1, 'B');
        grid.setCell(0, 2, 'B');
        boolean didAnything = grid.fillObvious();
        assertEquals("_BB", grid.getRowInString(0));
        assertEquals("BBB", grid.getRowInString(1));
        assertEquals("BB_", grid.getRowInString(2));
        assertTrue(didAnything);
        didAnything = grid.fillObvious();
        assertFalse(didAnything);
    }

    @Test
    public void fillObviousIntermediate() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();
        lc.addLineOfClues("Y,2,B,1");
        lc.addLineOfClues("B,1,B,1");
        lc.addLineOfClues("B,1,B,1");
        lc.addLineOfClues("B,1");
        lc.addLineOfClues("Y,3");
        uc.addLineOfClues("B,1");
        uc.addLineOfClues("Y,1,B,2,Y,1");
        uc.addLineOfClues("Y,1,Y,1");
        uc.addLineOfClues("B,3,Y,1");
        Grid grid = new Grid(lc, uc);
        grid.setCell(1, 0, 'Y');
        grid.setCell(3, 0, 'B');
        grid.setCell(1, 1, 'B');
        grid.setCell(2,4,'Y');
        grid.setCross(0, 0);
        grid.setCross(0,4);
        assertTrue(grid.fillObvious());
        assertEquals("_YYB", grid.getRowInString(0));
        assertEquals("_B_B", grid.getRowInString(1));
        assertEquals("_B_B", grid.getRowInString(2));
        assertEquals("____", grid.getRowInString(3));
        assertEquals("_YYY", grid.getRowInString(4));
    }

    @Test
    public void crossCompletedSimple() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();
        lc.addLineOfClues("B,2");
        lc.addLineOfClues("B,3");
        lc.addLineOfClues("B,1,B,1");
        uc.addLineOfClues("B,3");
        uc.addLineOfClues("B,2");
        uc.addLineOfClues("B,2");
        Grid grid = new Grid(lc, uc);
        grid.setCell(0, 0, 'B');
        grid.setCell(1, 0, 'B');
        grid.setCell(0, 1, 'B');
        grid.setCell(1, 1, 'B');
        grid.setCell(2, 1, 'B');
        grid.setCell(0, 2, 'B');
        grid.setCell(2, 2, 'B');
        assertTrue(grid.crossCompleted());
        assertTrue(grid.isCross(1, 2));
        assertTrue(grid.isCross(2, 0));
        assertFalse(grid.crossCompleted());

    }

    @Test
    public void crossCompletedIntermediate() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();
        for (int i = 0; i < 9; i++) {
            uc.addLineOfClues("B,1");
        }

        lc.addLineOfClues("B,1,B,1,B,1");
        lc.addLineOfClues("B,1,B,2,B,3");
        Grid grid = new Grid(lc, uc);
        grid.setCell(0, 1, 'B');
        grid.setCell(1, 0, 'B');
        grid.setCell(4, 0, 'B');
        grid.setCell(5, 1, 'B');
        grid.setCell(6, 1, 'B');
        grid.setCell(7, 1, 'B');
        grid.setCell(8, 0, 'B');
        assertTrue(grid.crossCompleted());
        assertTrue(grid.isCross(8, 1));
        assertTrue(grid.isCross(4, 1));
        assertFalse(grid.isCross(2, 1));
        assertTrue(grid.isCross(1,1));
    }
}