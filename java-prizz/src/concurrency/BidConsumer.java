package concurrency;

import model.Bid;

import java.util.List;

/**
 * Consumes bids from the shared BidBuffer and stores them in a results list.
 *
 * <p>Demonstrates: Extending Thread class, thread join(), isInterrupted(),
 * synchronized block for thread-safe list access.</p>
 *
 * <b>Viva tip:</b> Two ways to create a thread — implement Runnable (see
 * BidProducer) OR extend Thread (this class).  Extending Thread is simpler
 * but locks you into single inheritance.  Both approaches are shown here
 * deliberately to demonstrate both options in the viva.
 */
public class BidConsumer extends Thread {

    private final BidBuffer buffer;
    private final List<Bid> storageSink;  // Shared list where consumed bids are stored

    /**
     * Creates a consumer thread with a recognisable name.
     *
     * @param buffer      Shared BidBuffer to consume bids from
     * @param storageSink Thread-safe list to collect consumed bids
     */
    public BidConsumer(BidBuffer buffer, List<Bid> storageSink) {
        super("BidConsumerWorker");
        this.buffer      = buffer;
        this.storageSink = storageSink;
    }

    /**
     * Continuously drains bids from the buffer until this thread is interrupted.
     * Uses a synchronized block to protect the shared storageSink list.
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Bid bid = buffer.consumeBid();
                synchronized (storageSink) {
                    storageSink.add(bid);  // Thread-safe write to shared list
                }
                Thread.sleep(60);  // Simulates processing time
            }
        } catch (InterruptedException e) {
            System.out.println("[CONSUMER] BidConsumer thread stopped cleanly.");
            Thread.currentThread().interrupt();
        }
    }
}
