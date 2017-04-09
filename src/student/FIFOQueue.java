package student;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Artificial Intelligence A Modern Approach (3rd Edition): pg 80.<br>
 * <br>
 * First-in, first-out or FIFO queue, which pops the oldest element of the
 * queue and keeps elements unique;
 *
 *
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 * Modification by:
 * @author Tomáš Hořovský
 */
public class FIFOQueue<E> extends LinkedList<E> implements Queue<E> {
    private static final long serialVersionUID = 1;

    public FIFOQueue() {
        super();
    }

    public FIFOQueue(Collection<? extends E> c) {
        super(c);
    }

    //
    // START-Queue
    public boolean isEmpty() {
        return 0 == size();
    }

    public E pop() {
        return poll();
    }

    public void push(E element) {
        this.addLast(element);
    }

    @Override
    public boolean offer(E e) {
        return !contains(e) && super.offer(e);
    }

    public Queue<E> insert(E element) {
        if (contains(element)) return this;

        if (offer(element)) {
            return this;
        }
        return null;
    }
    // END-Queue
    //
}