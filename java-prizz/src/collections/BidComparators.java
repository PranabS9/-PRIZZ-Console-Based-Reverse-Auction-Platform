package collections;

import model.Bid;
import java.util.Comparator;

/**
 * Reusable Comparator constants for sorting Bid objects.
 *
 * <p>Demonstrates: Comparator interface, Lambda expressions,
 * Method references (Bid::getBidAmount), Comparator.comparing chaining.</p>
 *
 * <b>Viva tip:</b> Comparator.comparingDouble uses a method reference —
 * "Bid::getBidAmount" is shorthand for "b -> b.getBidAmount()".
 * These comparators can be passed to Collections.sort(), PriorityQueue, etc.
 */
public class BidComparators {

    /** Sorts by price ascending — cheapest bid wins the reverse auction. */
    public static final Comparator<Bid> BY_PRICE_ASC =
            Comparator.comparingDouble(Bid::getBidAmount);

    /** Sorts by price descending — most expensive first. */
    public static final Comparator<Bid> BY_PRICE_DESC =
            (b1, b2) -> Double.compare(b2.getBidAmount(), b1.getBidAmount());

    /** Sorts by submission time descending — most recent first. */
    public static final Comparator<Bid> BY_TIMESTAMP_DESC =
            Comparator.comparing(Bid::getSubmittedAt).reversed();

    /** Sorts by provider ID ascending. */
    public static final Comparator<Bid> BY_PROVIDER_ID =
            Comparator.comparingInt(Bid::getProviderId);
}
