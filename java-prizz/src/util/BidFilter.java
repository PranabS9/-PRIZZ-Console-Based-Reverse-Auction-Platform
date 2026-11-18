package util;

import model.Bid;

/**
 * Functional interface used to pass custom bid-filtering logic as a lambda.
 *
 * <p>Demonstrates: @FunctionalInterface annotation, Lambda expressions,
 * Functional programming style in Java.</p>
 *
 * <b>Viva tip:</b> Because this interface has exactly ONE abstract method,
 * it qualifies as a functional interface.  This lets you write:
 * {@code (bid, max) -> bid.getBidAmount() <= max}
 * and pass it as a BidFilter argument — no anonymous class needed.
 */
@FunctionalInterface
public interface BidFilter {

    /**
     * Evaluates whether a bid satisfies a given budget ceiling.
     *
     * @param bid       The candidate bid to evaluate
     * @param maxBudget The maximum allowed bid amount
     * @return true if the bid qualifies, false otherwise
     */
    boolean evaluate(Bid bid, double maxBudget);
}
