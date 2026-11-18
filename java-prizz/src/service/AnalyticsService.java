package service;

import model.Bid;
import util.BidFilter;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides Stream-based analytics and reporting over in-memory bid collections.
 *
 * <p>Demonstrates: Stream API — filter, map, reduce, collect,
 * Collectors.groupingBy, Collectors.averagingDouble, summaryStatistics,
 * distinct, sorted, Functional Interface (BidFilter lambda).</p>
 *
 * <b>Viva tip:</b> Every method here uses a Stream pipeline:
 * source → intermediate operations → terminal operation.
 * No loop is needed — the Stream handles iteration internally.
 */
public class AnalyticsService {

    /**
     * Filters bids using a custom lambda passed through the BidFilter
     * functional interface.
     *
     * @param bids      List of candidate bids
     * @param maxBudget The budget ceiling
     * @param filter    Lambda that decides whether a bid qualifies
     * @return List of qualifying bids
     */
    public List<Bid> filterBidsUnderCeiling(List<Bid> bids, double maxBudget, BidFilter filter) {
        return bids.stream()
                .filter(b -> filter.evaluate(b, maxBudget))
                .collect(Collectors.toList());
    }

    /**
     * Calculates total market volume using Stream reduce (fold left).
     *
     * @param bids All bids to sum
     * @return Sum of all bid amounts
     */
    public double calculateTotalBiddedVolume(List<Bid> bids) {
        return bids.stream()
                .map(Bid::getBidAmount)
                .reduce(0.0, Double::sum);
    }

    /**
     * Groups bids by their requestId and calculates the average price per request
     * using Collectors.groupingBy with a downstream averagingDouble collector.
     *
     * @param bids All bids
     * @return Map of requestId -> average bid amount
     */
    public Map<Integer, Double> calculateAverageBidPerRequest(List<Bid> bids) {
        return bids.stream()
                .collect(Collectors.groupingBy(
                        Bid::getRequestId,
                        Collectors.averagingDouble(Bid::getBidAmount)));
    }

    /**
     * Returns full summary statistics (count, min, max, avg) in one pass.
     *
     * @param bids All bids
     * @return DoubleSummaryStatistics object
     */
    public DoubleSummaryStatistics getBidStatistics(List<Bid> bids) {
        return bids.stream()
                .mapToDouble(Bid::getBidAmount)
                .summaryStatistics();
    }

    /**
     * Extracts unique proposal messages, sorted alphabetically,
     * demonstrating distinct() and sorted() intermediate operations.
     *
     * @param bids All bids
     * @return Sorted list of unique proposal strings
     */
    public List<String> extractUniqueProposals(List<Bid> bids) {
        return bids.stream()
                .map(Bid::getProposalMessage)
                .filter(msg -> msg != null && !msg.trim().isEmpty())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }
}
