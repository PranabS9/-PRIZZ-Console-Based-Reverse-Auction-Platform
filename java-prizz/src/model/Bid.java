package model;

import java.util.Date;

/**
 * Represents a price proposal submitted by a ServiceProvider for a ServiceRequest.
 *
 * <p>Demonstrates: Encapsulation, Comparable interface (natural ordering by price
 * ascending — lowest bid wins the reverse auction).</p>
 *
 * <b>Viva tip:</b> compareTo() makes Bid usable with PriorityQueue without
 * a separate Comparator.
 */
public class Bid implements Comparable<Bid> {

    private Integer bidId;
    private Integer requestId;
    private Integer providerId;
    private Double  bidAmount;        // The offered price
    private String  proposalMessage;  // Short pitch from the provider
    private Date    submittedAt;

    /**
     * Creates a Bid.
     *
     * @param bidId           Unique bid identifier
     * @param requestId       The request this bid is for
     * @param providerId      The provider who submitted this bid
     * @param bidAmount       Offered price; defaults to 0.0 if null
     * @param proposalMessage Short pitch message
     * @param submittedAt     Submission timestamp; defaults to now if null
     */
    public Bid(Integer bidId, Integer requestId, Integer providerId,
               Double bidAmount, String proposalMessage, Date submittedAt) {
        this.bidId           = bidId;
        this.requestId       = requestId;
        this.providerId      = providerId;
        this.bidAmount       = (bidAmount != null) ? bidAmount : 0.0;
        this.proposalMessage = proposalMessage;
        this.submittedAt     = (submittedAt != null) ? submittedAt : new Date();
    }

    // ---- Getters and Setters ----

    public Integer getBidId()                              { return bidId; }
    public void    setBidId(Integer bidId)                 { this.bidId = bidId; }

    public Integer getRequestId()                          { return requestId; }
    public void    setRequestId(Integer requestId)         { this.requestId = requestId; }

    public Integer getProviderId()                         { return providerId; }
    public void    setProviderId(Integer providerId)       { this.providerId = providerId; }

    public Double  getBidAmount()                          { return bidAmount; }
    public void    setBidAmount(Double bidAmount)          { this.bidAmount = bidAmount; }

    public String  getProposalMessage()                    { return proposalMessage; }
    public void    setProposalMessage(String msg)          { this.proposalMessage = msg; }

    public Date    getSubmittedAt()                        { return submittedAt; }
    public void    setSubmittedAt(Date submittedAt)        { this.submittedAt = submittedAt; }

    /**
     * Natural ordering: lowest price first (reverse auction — cheapest bid wins).
     */
    @Override
    public int compareTo(Bid other) {
        if (other == null) return -1;
        return Double.compare(this.bidAmount, other.bidAmount);
    }

    @Override
    public String toString() {
        return String.format("Bid #%d [Req #%d, Provider #%d] -> $%.2f (\"%s\")",
                bidId, requestId, providerId, bidAmount, proposalMessage);
    }
}
