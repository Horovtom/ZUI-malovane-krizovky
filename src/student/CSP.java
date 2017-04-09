package student;

import java.util.ArrayList;

/**
 * Created by lactosis on 8.4.17.
 */
public class CSP {
    private final Grid grid;
    private ArrayList<String> solutions = new ArrayList<String>();

    public CSP(Grid grid) {
        this.grid = grid;
    }

    void solveIt() {
        if (solutions.size() > 0) return;
        solve();
        refactorSolutions();
    }

    private void refactorSolutions() {
        ArrayList<String> newSolutions = new ArrayList<String>();
        for (String solution : solutions) {
            if (!newSolutions.contains(solution))
                newSolutions.add(solution);
        }
        //solutions.stream().filter(solution -> !newSolutions.contains(solution)).forEach(newSolutions::add);
        solutions = newSolutions;
    }

    ArrayList<String> getSolutions() {
        return solutions;
    }

    private void solve() {
        if (grid.isComplete()) {
            solutions.add(grid.toString());
            return;
        }

        CSPVariable nextVariable = grid.getNextVariable();

        while (nextVariable.getDomain().size() > 0) {
            grid.save();
            nextVariable.getField().setColorLocked(nextVariable.getDomain().poll());
            if (grid.solve()) {
                solutions.add(grid.toString());
            } else if (grid.isValid()) {
                solve();
            }
            grid.load();
        }

    }

}
