package student;

import java.util.Queue;

/**
 * Created by Hermes235 on 9.4.2017.
 */
public class CSPVariableClue {
    private final ClueField clueField;
    private final Queue<Integer> domain = new FIFOQueue<Integer>();
    private final int line;
    private final int index;

    public CSPVariableClue(ClueField clueField, int line, int index) {
        this.clueField = clueField;
        this.line = line;
        this.index = index;
    }

    public int pollDomain() {
        return domain.poll();
    }

    public void addToDomain(int index) {
        domain.offer(index);
    }

    public ClueField getClueField() {
        return clueField;
    }

    public int getLine() {
        return line;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CSPVariableClue{");
        sb.append("clueField=").append(clueField);
        sb.append(", domain=").append(domain);
        sb.append(", line=").append(line);
        sb.append(", index=").append(index);
        sb.append('}');
        return sb.toString();
    }

    public int domainSize() {
        return domain.size();
    }
}
