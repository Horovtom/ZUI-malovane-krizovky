package tests;

import org.junit.Before;
import org.junit.Test;
import student.*;

import java.io.*;
import java.util.ArrayList;

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
    public void test_Stingray() throws Exception {
        CSPMain.reset();
        CSPMain.setSolution(new BufferedReader(new FileReader(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/Stingray.out.txt"))));
        CSPMain.parseInput(new FileInputStream(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/Stingray.txt")));
        ArrayList<String> result = CSPMain.getResult();
        assertEquals(1, result.size());
        String resultString = result.get(0);
        StringReader reader = new StringReader(resultString);
        BufferedReader bufferedReader = new BufferedReader(reader);
        BufferedReader expected = new BufferedReader(new FileReader(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/Stingray.out.txt")));
        String line;
        while (bufferedReader.ready() && expected.ready()) {
            line = expected.readLine();
            assertEquals(line, bufferedReader.readLine());
        }
        assertFalse(expected.ready());
        assertTrue(bufferedReader.readLine() == null);
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

    @Test
    public void home10x10() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();
        uc.addLineOfClues("B,6");
        uc.addLineOfClues("B,7");
        uc.addLineOfClues("B,8");
        uc.addLineOfClues("B,9");
        uc.addLineOfClues("B,5,B,3");
        uc.addLineOfClues("B,5,B,3");
        uc.addLineOfClues("B,9");
        uc.addLineOfClues("B,8");
        uc.addLineOfClues("B,7");
        uc.addLineOfClues("B,6");

        lc.addLineOfClues("B,2");
        lc.addLineOfClues("B,4");
        lc.addLineOfClues("B,6");
        lc.addLineOfClues("B,8");
        lc.addLineOfClues("B,10");
        lc.addLineOfClues("B,4,B,4");
        lc.addLineOfClues("B,4,B,4");
        lc.addLineOfClues("B,10");
        lc.addLineOfClues("B,10");
        lc.addLineOfClues("B,10");


        Grid grid = new Grid(lc, uc);
        grid.loadSolution(
                "____BB____" +
                        "___BBBB___" +
                        "__BBBBBB__" +
                        "_BBBBBBBB_" +
                        "BBBBBBBBBB" +
                        "BBBB__BBBB" +
                        "BBBB__BBBB" +
                        "BBBBBBBBBB" +
                        "BBBBBBBBBB" +
                        "BBBBBBBBBB");

        grid.solve();

        assertEquals("____BB____", grid.getRowInString(0));
        assertEquals("___BBBB___", grid.getRowInString(1));
        assertEquals("__BBBBBB__", grid.getRowInString(2));
        assertEquals("_BBBBBBBB_", grid.getRowInString(3));
        assertEquals("BBBBBBBBBB", grid.getRowInString(4));
        assertEquals("BBBB__BBBB", grid.getRowInString(5));
        assertEquals("BBBB__BBBB", grid.getRowInString(6));
        assertEquals("BBBBBBBBBB", grid.getRowInString(7));
        assertEquals("BBBBBBBBBB", grid.getRowInString(8));
        assertEquals("BBBBBBBBBB", grid.getRowInString(9));

    }

    @Test
    public void testLeafManual() throws Exception {
        String result = "_________B\n" +
                "___BBBB_BB\n" +
                "__B___BB__\n" +
                "_BBBBBB_B_\n" +
                "B____BB_B_\n" +
                "BBBBB_B_B_\n" +
                "B__BB_B_B_\n" +
                "BBB_B_BB__\n" +
                "B_BBBBB___\n" +
                "BB________";

        StringReader sr = new StringReader(result);
        BufferedReader br = new BufferedReader(sr);

        assertEquals("____BBBBBB", br.readLine());
        assertEquals("___B_B_B_B", br.readLine());
        assertEquals("__BB_B_BB_", br.readLine());
        assertEquals("_B_B_BB_B_", br.readLine());
        assertEquals("_B_B_BBBB_", br.readLine());
        assertEquals("_B_BB___B_", br.readLine());
        assertEquals("_BBBBBBBB_", br.readLine());
        assertEquals("__B____B__", br.readLine());
        assertEquals("_B_BBBB___", br.readLine());
        assertEquals("BB________", br.readLine());
    }

    @Test
    public void leaf10x10() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();
        uc.addLineOfClues("B,1");
        uc.addLineOfClues("B,4,B,2");
        uc.addLineOfClues("B,1,B,2");
        uc.addLineOfClues("B,6,B,1");
        uc.addLineOfClues("B,1,B,2,B,1");
        uc.addLineOfClues("B,5,B,1,B,1");
        uc.addLineOfClues("B,1,B,2,B,1,B,1");
        uc.addLineOfClues("B,3,B,1,B,2");
        uc.addLineOfClues("B,1,B,5");
        uc.addLineOfClues("B,2");

        lc.addLineOfClues("B,6");
        lc.addLineOfClues("B,1,B,1,B,1,B,1");
        lc.addLineOfClues("B,2,B,1,B,2");
        lc.addLineOfClues("B,1,B,1,B,2,B,1");
        lc.addLineOfClues("B,1,B,1,B,4");
        lc.addLineOfClues("B,1,B,2,B,1");
        lc.addLineOfClues("B,8");
        lc.addLineOfClues("B,1,B,1");
        lc.addLineOfClues("B,1,B,4");
        lc.addLineOfClues("B,2");

        Grid grid = new Grid(lc, uc);
        grid.loadSolution("____BBBBBB___B_B_B_B__BB_B_BB__B_B_BB_B__B_B_BBBB__B_BB___B__BBBBBBBB___B____B___B_BBBB___BB________");

        CSPMain.setGrid(grid);
        CSPMain.solve();
        //assertFalse(grid.solve());


        ArrayList<String> result = CSPMain.getResult();
        assertFalse(result == null);

        assertEquals(1, result.size());
        StringReader sr = new StringReader(result.get(0));
        BufferedReader br = new BufferedReader(sr);

        assertEquals("____BBBBBB", br.readLine());
        assertEquals("___B_B_B_B", br.readLine());
        assertEquals("__BB_B_BB_", br.readLine());
        assertEquals("_B_B_BB_B_", br.readLine());
        assertEquals("_B_B_BBBB_", br.readLine());
        assertEquals("_B_BB___B_", br.readLine());
        assertEquals("_BBBBBBBB_", br.readLine());
        assertEquals("__B____B__", br.readLine());
        assertEquals("_B_BBBB___", br.readLine());
        assertEquals("BB________", br.readLine());
    }

    @Test
    public void test_tobaccoPipeManual() throws Exception {
    }

    @Test
    public void test_tobaccoPipe() throws Exception {
        CSPMain.reset();
        CSPMain.setSolution(new BufferedReader(new FileReader(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/tobaccoPipe.out.txt"))));
        CSPMain.parseInput(new FileInputStream(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/tobaccoPipe.txt")));
        ArrayList<String> result = CSPMain.getResult();
        assertEquals(1, result.size());
        String resultString = result.get(0);
        StringReader reader = new StringReader(resultString);
        BufferedReader bufferedReader = new BufferedReader(reader);
        BufferedReader expected = new BufferedReader(new FileReader(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/tobaccoPipe.out.txt")));
        String line;
        while (bufferedReader.ready() && expected.ready()) {
            line = expected.readLine();
            assertEquals(line, bufferedReader.readLine());
        }
        assertFalse(expected.ready());
        assertTrue(bufferedReader.readLine() == null);
    }

    @Test
    public void test_csp_example() throws Exception {
        CSPMain.reset();
        CSPMain.setSolution(new BufferedReader(new FileReader(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/csp_example.txt.out.txt"))));
        CSPMain.parseInput(new FileInputStream(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/csp_example.txt")));
        String result = CSPMain.getResult().get(0);
        assertFalse(result == null);
        StringReader reader = new StringReader(result);
        BufferedReader bufferedReader = new BufferedReader(reader);
        System.out.println("Got: \n" + result + "\nShould be:");
        BufferedReader expected = new BufferedReader(new FileReader(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/csp_example.txt.out.txt")));
        String line;
        while (bufferedReader.ready() && expected.ready()) {
            line = expected.readLine();
            System.out.println(line);
            assertEquals(line, bufferedReader.readLine());
        }
        assertFalse(expected.ready());
        assertTrue(bufferedReader.readLine() == null);
    }

    @Test
    public void MANUAL_test_SimpleMultiSol() throws Exception {
        CSPMain.parseInput(new FileInputStream(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/SimpleMultiline.txt")));
        CSPMain.printResult();
    }

    @Test
    public void test_input() throws Exception {
        //TODO: IMPLEMENT
    }

    @Test
    public void test_isValidSimple() throws Exception {
        LeftClues lc = new LeftClues();
        lc.addLineOfClues("B,1,B,2");
        UpperClues uc = new UpperClues();
        uc.addLineOfClues("B,1");
        uc.addLineOfClues("B,1");
        uc.addLineOfClues("B,1");
        uc.addLineOfClues("B,1");
        uc.addLineOfClues("B,1");
        uc.addLineOfClues("B,1");
        Grid grid = new Grid(lc, uc);
        grid.setCell(1, 0, 'B');
        grid.setCell(3, 0, 'B');
        assertTrue(grid.isValid(false, 0));
        grid.setCell(0, 0, 'B');
        assertFalse(grid.isValid(false, 0));
    }

    @Test
    public void test_isValidIntermediate() throws Exception {
        LeftClues lc = new LeftClues();
        lc.addLineOfClues("B,2,Y,1");
        UpperClues uc = new UpperClues();
        uc.addLineOfClues("B,1");
        uc.addLineOfClues("B,1");
        uc.addLineOfClues("B,1");
        uc.addLineOfClues("B,1");
        uc.addLineOfClues("B,1");
        Grid grid = new Grid(lc, uc);
        assertTrue(grid.isValid(false, 0));
        grid.setCell(3, 0, 'B');
        assertTrue(grid.isValid(false, 0));
        grid.setCell(4, 0, 'B');
        assertFalse(grid.isValid(false, 0));
        grid.setCell(4, 0, 'Y');
        assertTrue(grid.isValid(false, 0));
    }

    @Test
    public void test_krtek() throws Exception {
        CSPMain.setSolution(new BufferedReader(new FileReader(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/krtek.txt.out.txt"))));
        CSPMain.parseInput(new FileInputStream(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/krtek.txt")));
        String result = CSPMain.getResult().get(0);
        assertFalse(result == null);
        StringReader reader = new StringReader(result);
        BufferedReader bufferedReader = new BufferedReader(reader);
        System.out.println("Got: \n" + result + "\nShould be:");
        BufferedReader expected = new BufferedReader(new FileReader(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/krtek.txt.out.txt")));
        String line;
        while (bufferedReader.ready() && expected.ready()) {
            line = expected.readLine();
            System.out.println(line);
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                assertTrue(line == null || line.equals(""));
            }
            assertEquals(line, readLine);
        }
        assertFalse(expected.ready());
        assertTrue(bufferedReader.readLine() == null);
    }

    @Test
    public void test_dino() throws Exception {
        CSPMain.setSolution(new BufferedReader(new FileReader(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/dino.out.txt"))));
        CSPMain.parseInput(new FileInputStream(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/dino.txt")));
        String result = CSPMain.getResult().get(0);
        assertFalse(result == null);
        StringReader reader = new StringReader(result);
        BufferedReader bufferedReader = new BufferedReader(reader);
        System.out.println("Got: \n" + result + "\nShould be:");
        BufferedReader expected = new BufferedReader(new FileReader(new File("/home/lactosis/Documents/Programming/Java/JAG/ZUI-malovane-krizovky/src/student/examples/dino.out.txt")));
        String line;
        while (bufferedReader.ready() && expected.ready()) {
            line = expected.readLine();
            System.out.println(line);
            assertEquals(line, bufferedReader.readLine());
        }
        assertFalse(expected.ready());
        assertTrue(bufferedReader.readLine() == null);
    }

    @Test
    public void coloredSimple10x10() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();

        lc.addLineOfClues("B,10");
        lc.addLineOfClues("B,2,B,3,Y,2,B,1");
        lc.addLineOfClues("B,2,B,3,Y,2,B,1");
        lc.addLineOfClues("B,3,B,6");
        lc.addLineOfClues("S,1,L,4,S,5");
        lc.addLineOfClues("S,10");
        lc.addLineOfClues("S,7,Y,3");
        lc.addLineOfClues("Y,10");

        uc.addLineOfClues("B,4,S,3,Y,1");
        uc.addLineOfClues("B,4,L,1,S,2,Y,1");
        uc.addLineOfClues("B,1,B,1,L,1,S,2,Y,1");
        uc.addLineOfClues("B,1,L,1,S,2,Y,1");
        uc.addLineOfClues("B,4,L,1,S,2,Y,1");
        uc.addLineOfClues("B,4,S,3,Y,1");
        uc.addLineOfClues("B,4,S,3,Y,1");
        uc.addLineOfClues("B,1,Y,2,B,1,S,2,Y,2");
        uc.addLineOfClues("B,1,Y,2,B,1,S,2,Y,2");
        uc.addLineOfClues("B,4,S,2,Y,2");

        Grid grid = new Grid(lc, uc);
        grid.loadSolution("BBBBBBBBBBBB__BBBYYBBB__BBBYYBBBB_BBBBBBSLLLLSSSSSSSSSSSSSSSSSSSSSSYYYYYYYYYYYYY");
        grid.solve();

        assertEquals("BBBBBBBBBB", grid.getRowInString(0));
        assertEquals("BB__BBBYYB", grid.getRowInString(1));
        assertEquals("BB__BBBYYB", grid.getRowInString(2));
        assertEquals("BBB_BBBBBB", grid.getRowInString(3));
        assertEquals("SLLLLSSSSS", grid.getRowInString(4));
        assertEquals("SSSSSSSSSS", grid.getRowInString(5));
        assertEquals("SSSSSSSYYY", grid.getRowInString(6));
        assertEquals("YYYYYYYYYY", grid.getRowInString(7));

    }

    @Test
    public void androidWithEmptyLine5x5() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();

        lc.addLineOfClues("G,3");
        lc.addLineOfClues("G,1,Y,1,G,1,Y,1,G,1");
        lc.addLineOfClues("G,5");
        lc.addLineOfClues("");
        lc.addLineOfClues("G,5");

        uc.addLineOfClues("G,2,G,1");
        uc.addLineOfClues("G,1,Y,1,G,1,G,1");
        uc.addLineOfClues("G,3,G,1");
        uc.addLineOfClues("G,1,Y,1,G,1,G,1");
        uc.addLineOfClues("G,2,G,1");

        Grid grid = new Grid(lc, uc);

        grid.solve();

        assertEquals("_GGG_", grid.getRowInString(0));
        assertEquals("GYGYG", grid.getRowInString(1));
        assertEquals("GGGGG", grid.getRowInString(2));
        assertEquals("_____", grid.getRowInString(3));
        assertEquals("GGGGG", grid.getRowInString(4));
    }

    @Test
    public void brazilFlag19x13() throws Exception {
        LeftClues lc = new LeftClues();
        UpperClues uc = new UpperClues();

        lc.addLineOfClues("G,19");
        lc.addLineOfClues("G,19");
        lc.addLineOfClues("G,9,Y,1,G,9");
        lc.addLineOfClues("G,7,Y,5,G,7");
        lc.addLineOfClues("G,6,Y,2,B,3,Y,2,G,6");
        lc.addLineOfClues("G,4,Y,3,B,1,B,3,Y,3,G,4");
        lc.addLineOfClues("G,2,Y,5,B,3,B,1,Y,5,G,2");
        lc.addLineOfClues("G,4,Y,3,B,5,Y,3,G,4");
        lc.addLineOfClues("G,6,Y,2,B,3,Y,2,G,6");
        lc.addLineOfClues("G,7,Y,5,G,7");
        lc.addLineOfClues("G,9,Y,1,G,9");
        lc.addLineOfClues("G,19");
        lc.addLineOfClues("G,19");


        uc.addLineOfClues("G,13");
        uc.addLineOfClues("G,13");
        uc.addLineOfClues("G,6,Y,1,G,6");
        uc.addLineOfClues("G,6,Y,1,G,6");
        uc.addLineOfClues("G,5,Y,3,G,5");

        uc.addLineOfClues("G,5,Y,3,G,5");
        uc.addLineOfClues("G,4,Y,5,G,4");
        uc.addLineOfClues("G,3,Y,2,B,3,Y,2,G,3");
        uc.addLineOfClues("G,3,Y,1,B,1,B,3,Y,1,G,3");
        uc.addLineOfClues("G,2,Y,2,B,5,Y,2,G,2");

        uc.addLineOfClues("G,3,Y,1,B,2,B,2,Y,1,G,3");
        uc.addLineOfClues("G,3,Y,2,B,3,Y,2,G,3");
        uc.addLineOfClues("G,4,Y,5,G,4");
        uc.addLineOfClues("G,5,Y,3,G,5");
        uc.addLineOfClues("G,5,Y,3,G,5");

        uc.addLineOfClues("G,6,Y,1,G,6");
        uc.addLineOfClues("G,6,Y,1,G,6");
        uc.addLineOfClues("G,13");
        uc.addLineOfClues("G,13");


        Grid grid = new Grid(lc, uc);
        grid.loadSolution("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGYGGGGGGGGGGGGGGGGYYYYYGGGGGGGGGGGGGYYBBBYYGGGGGGGGGGYYYB_BBBYYYGGGGGGYYYYYBBB_BYYYYYGGGGGGYYYBBBBBYYYGGGGGGGGGGYYBBBYYGGGGGG" +
                "GGGGGGGYYYYYGGGGGGGGGGGGGGGGYGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
        grid.solve();

        assertEquals("GGGGGGGGGGGGGGGGGGG", grid.getRowInString(0));
        assertEquals("GGGGGGGGGGGGGGGGGGG", grid.getRowInString(1));
        assertEquals("GGGGGGGGGYGGGGGGGGG", grid.getRowInString(2));
        assertEquals("GGGGGGGYYYYYGGGGGGG", grid.getRowInString(3));
        assertEquals("GGGGGGYYBBBYYGGGGGG", grid.getRowInString(4));
        assertEquals("GGGGYYYB_BBBYYYGGGG", grid.getRowInString(5));
        assertEquals("GGYYYYYBBB_BYYYYYGG", grid.getRowInString(6));
        assertEquals("GGGGYYYBBBBBYYYGGGG", grid.getRowInString(7));
        assertEquals("GGGGGGYYBBBYYGGGGGG", grid.getRowInString(8));
        assertEquals("GGGGGGGYYYYYGGGGGGG", grid.getRowInString(9));
        assertEquals("GGGGGGGGGYGGGGGGGGG", grid.getRowInString(10));
        assertEquals("GGGGGGGGGGGGGGGGGGG", grid.getRowInString(11));
        assertEquals("GGGGGGGGGGGGGGGGGGG", grid.getRowInString(11));
    }

}