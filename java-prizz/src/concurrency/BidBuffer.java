package concurrency;

import model.Bid;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A bounded, thread-safe buffer shared between BidProducer and BidConsumer threads.
 *
 * <p>Demonstrates: Synchronization (synchronized methods), Inter-thread
 * communication (wait() / notifyAll()), race condition prevention,
 * bounded buffer / producer-consumer pattern.</p>
 *
 * <b>Viva tip:</b> wait() releases the lock and sleeps the thread until
 * notifyAll() wakes it.  This prevents busy-waiting (spinning) and is the
 * classic solution to the producer-consumer problem.
 */
public class BidBuffer {

    private final Queue<Bid> buffer   = new LinkedList<>();
    private final int        capacity;

    /**
     * Creates a bounded buffer.
     *
     * @param capacity Maximum number of bids the buffer can hold at one time
     */
    public BidBuffer(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Adds a bid to the buffer.  If the buffer is full, the calling thread
     * sleeps (wait) until a consumer makes space.
     *
     * @param bid The bid to add
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized void produceBid(Bid bid) throws InterruptedException {
        while (buffer.size() == capacity) {
            wait();  // Buffer full — wait for a consumer to free space
        }
        buffer.offer(bid);
        System.out.printf("[PRODUCER] Added Bid #%d ($%.2f) | Buffer size: %d%n",
                bid.getBidId(), bid.getBidAmount(), buffer.size());
        notifyAll();  // Wake up any waiting consumers
    }

    /**
     * Removes and returns a bid from the buffer.  If the buffer is empty,
     * the calling thread sleeps (wait) until a producer adds something.
     *
     * @return The next bid in the buffer
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized Bid consumeBid() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait();  // Buffer empty — wait for a producer to add a bid
        }
        Bid bid = buffer.poll();
        System.out.printf("[CONSUMER] Took Bid #%d | Thread: [%s] | Remaining: %d%n",
                bid.getBidId(), Thread.currentThread().getName(), buffer.size());
        notifyAll();  // Wake up any waiting producers
        return bid;
    }

    public synchronized int getQueueSize() { return buffer.size(); }
}
