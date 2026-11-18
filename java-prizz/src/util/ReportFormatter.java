package util;

import model.Bid;
import model.ServiceRequest;

import java.util.List;

/**
 * Formats Bid and ServiceRequest data into human-readable console output.
 *
 * <p>Demonstrates: StringBuilder for efficient string construction,
 * String.format for printf-style layout, method design.</p>
 *
 * <b>Viva tip:</b> StringBuilder is preferred over "+" concatenation inside
 * loops because it avoids creating a new String object on every iteration.
 */
public class ReportFormatter {

    /**
     * Builds a formatted card displaying all details of a ServiceRequest.
     *
     * @param request The request to format
     * @return Multi-line formatted string
     */
    public static String buildRequestCard(ServiceRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append(String.format("  SERVICE REQUEST #%d: %s\n",
                request.getRequestId(), request.getTitle().toUpperCase()));
        sb.append("====================================================\n");
        sb.append(String.format("  Customer ID : %d\n",   request.getCustomerId()));
        sb.append(String.format("  Base Budget : $%.2f\n", request.getBudget()));
        sb.append(String.format("  Status      : %s\n",   request.getStatus()));
        sb.append(String.format("  Details     : %s\n",   request.getDescription()));
        sb.append("----------------------------------------------------\n");
        return sb.toString();
    }

    /**
     * Builds a formatted ASCII table of all bids in the list.
     *
     * @param bids The bids to tabulate
     * @return Multi-line formatted table string
     */
    public static String buildBidSummaryTable(List<Bid> bids) {
        StringBuilder sb = new StringBuilder();
        sb.append("+--------+------------+-------------+------------+--------------------------------------+\n");
        sb.append("| Bid ID | Request ID | Provider ID | Price ($)  | Proposal Description                 |\n");
        sb.append("+--------+------------+-------------+------------+--------------------------------------+\n");
        for (Bid b : bids) {
            String msg = b.getProposalMessage();
            if (msg != null && msg.length() > 36) msg = msg.substring(0, 33) + "...";
            sb.append(String.format("| %-6d | %-10d | %-11d | %-10.2f | %-36s |\n",
                    b.getBidId(), b.getRequestId(), b.getProviderId(),
                    b.getBidAmount(), msg));
        }
        sb.append("+--------+------------+-------------+------------+--------------------------------------+\n");
        return sb.toString();
    }
}
