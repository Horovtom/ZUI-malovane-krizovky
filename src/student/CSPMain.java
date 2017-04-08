package student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class CSPMain {
    private static final Logger LOGGER = Logger.getLogger(CSPMain.class.getName());
    private static Grid grid = null;
    private static String solution = null;

    public static void main(String[] args) {
        parseInput(System.in);
        printResult();
    }

    public static void parseInput(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        int numOfRows, numOfCols;
        try {
            String currentLine = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(currentLine, ",");
            numOfRows = Integer.parseInt(tokenizer.nextToken());
            numOfCols = Integer.parseInt(tokenizer.nextToken());

            LeftClues lc = new LeftClues();
            for (int i = 0; i < numOfRows; i++) {
                lc.addLineOfClues(reader.readLine());
            }
            UpperClues uc = new UpperClues();
            for (int i = 0; i < numOfCols; i++) {
                uc.addLineOfClues(reader.readLine());
            }

            grid = new Grid(lc, uc);
            if (solution != null) {
                grid.loadSolution(solution);
                solution = null;
            }
            long myTime = System.currentTimeMillis();

            grid.solve();

            System.out.println("Solved in: " + (System.currentTimeMillis() - myTime) + "ms.");
            //System.out.println(grid);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printResult() {
        if (grid == null) {
            LOGGER.severe("There is no grid instantiated!");
            return;
        }
        System.out.println(grid);
    }

    public static String getResult() {
        if (grid == null) return null;

        return grid.toString();
    }

    public static void setSolution(BufferedReader solution) {
        StringBuilder string = new StringBuilder();
        try {
            while (solution.ready()) {
                string.append(solution.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        CSPMain.solution = string.toString();
    }
}
