package student;

/**
 * Created by lactosis on 8.4.17.
 */
public class CSP {
    private final Grid grid;
    boolean solved = false;

    public CSP(Grid grid) {
        this.grid = grid;
    }

    public void solveIt() {
        if (solved) return;
        solve();
        if (!solved) System.out.println("null");
    }

    private void solve() {
        if (grid.isComplete()) {
            solved = true;
            System.out.println(grid + "\n");
            return;
        }

        CSPVariable nextVariable = grid.getNextVariable();

        while (nextVariable.getDomain().size() > 0) {
            grid.save();
            nextVariable.getField().setColorLocked(nextVariable.getDomain().poll());
            if (grid.solve()) System.out.println(grid + "\n");
            if (grid.isValid()) {
                solve();
            }
            grid.load();
        }

    }

}
