package student;

import java.util.Queue;

/**
 * Created by lactosis on 8.4.17.
 */
public class CSPVariable {
    private final GridField field;
    private final Queue<Character> domain;

    public CSPVariable(GridField field, Queue<Character> domain) {
        this.field = field;
        this.domain = domain;
    }

    public Queue<Character> getDomain() {
        return domain;
    }

    public GridField getField() {
        return field;
    }
}
