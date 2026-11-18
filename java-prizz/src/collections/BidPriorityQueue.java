package collections;

import model.Bid;

import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Wraps Java's PriorityQueue to automatically rank bids from lowest to highest price.
 *
 * <p>Demonstrates: Collections Framework (PriorityQueue), Comparator ordering,
 * Iterator traversal pattern.</p>
 *
 * <b>Viva tip:</b> PriorityQueue does NOT guarantee sorted iteration order —
 * elements are only guaranteed to come out of poll() in sorted order.
 * That is why printAllRankedBids() uses an iterator (which shows internal
 * heap order, not strict sort order).
 */
public class BidPriorityQueue {

    private final PriorityQueue<Bid> priorityQueue;

    /** Creates a priority queue ordered by lowest price first. */
    public BidPriorityQueue() {
        this.priorityQueue = new PriorityQueue<>(BidComparators.BY_PRICE_ASC);
    }

    /** Adds a bid to the queue (null bids are silently ignored). */
    public void addBid(Bid bid) {
        if (bid != null) priorityQueue.offer(bid);
    }

    /** Removes and returns the lowest-priced bid, or null if empty. */
    public Bid pollWinningBid() { return priorityQueue.poll(); }

    /** Returns the lowest-priced bid without removing it, or null if empty. */
    public Bid peekWinningBid() { return priorityQueue.peek(); }

    public int     size()    { return priorityQueue.size(); }
    public boolean isEmpty() { return priorityQueue.isEmpty(); }

    /**
     * Iterates over all bids using an Iterator and prints each one.
     * Demonstrates the Iterator design pattern explicitly.
     */
    public void printAllRankedBids() {
        Iterator<Bid> iterator = priorityQueue.iterator();
        int rank = 1;
        while (iterator.hasNext()) {
            Bid b = iterator.next();
            System.out.println("  [Candidate #" + (rank++) + "] " + b);
        }
    }
}
