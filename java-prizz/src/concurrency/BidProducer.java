package concurrency;

import model.Bid;

import java.util.Date;

/**
 * Simulates a service provider submitting bids concurrently.
 *
 * <p>Demonstrates: Runnable interface (run() method), Thread.sleep(),
 * InterruptedException handling, concurrent bid generation.</p>
 *
 * <b>Viva tip:</b> Implementing Runnable (rather than extending Thread)
 * is preferred because Java supports only single inheritance — using
 * Runnable leaves the class free to extend another class if needed.
 */
public class BidProducer implements Runnable {

    private final BidBuffer buffer;
    private final int       requestId;
    private final int       totalBidsToGenerate;

    /**
     * Creates a producer that will generate a fixed number of bids.
     *
     * @param buffer              Shared BidBuffer to put bids into
     * @param requestId           The service request these bids are for
     * @param totalBidsToGenerate How many bids this producer will create
     */
    public BidProducer(BidBuffer buffer, int requestId, int totalBidsToGenerate) {
        this.buffer              = buffer;
        this.requestId           = requestId;
        this.totalBidsToGenerate = totalBidsToGenerate;
    }

    /**
     * Generates bids with random prices between $120 and $300, placing
     * each one into the shared buffer, with a short delay between each.
     */
    @Override
    public void run() {
        try {
            for (int i = 1; i <= totalBidsToGenerate; i++) {
                double price = 120.0 + (Math.random() * 180.0);
                Bid bid = new Bid(i, requestId, 200 + i, price,
                        "Provider Quote #" + i, new Date());
                buffer.produceBid(bid);
                Thread.sleep(80);  // Simulates thinking/network delay
            }
        } catch (InterruptedException e) {
            System.err.println("[PRODUCER] Interrupted — shutting down.");
            Thread.currentThread().interrupt();
        }
    }
}
