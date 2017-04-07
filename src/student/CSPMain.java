package student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Created by Hermes235 on 30.3.2017.
 */
public class CSPMain {
    private static final Logger LOGGER = Logger.getLogger(CSPMain.class.getName());

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
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

            Grid grid = new Grid(lc, uc);
            grid.solve();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
