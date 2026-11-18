package model;

import java.util.Date;

/**
 * Represents a confirmed contract created when a customer accepts a bid.
 *
 * <p>Demonstrates: Domain relational modeling, encapsulation, Boolean wrapper.</p>
 *
 * <b>Viva tip:</b> A Booking is created from a Bid. It links customer, provider,
 * and request together and tracks whether the payment has been released.
 */
public class Booking {

    private Integer bookingId;
    private Integer requestId;
    private Integer customerId;
    private Integer providerId;
    private Double  finalAmount;       // The agreed price (from the winning bid)
    private Boolean paymentCompleted;  // True once escrow is released
    private Date    bookedAt;

    /**
     * Creates a Booking with payment pending.
     *
     * @param bookingId   Unique booking identifier
     * @param requestId   The request being fulfilled
     * @param customerId  The customer paying
     * @param providerId  The provider delivering the service
     * @param finalAmount The agreed total amount; defaults to 0.0 if null
     */
    public Booking(Integer bookingId, Integer requestId, Integer customerId,
                   Integer providerId, Double finalAmount) {
        this.bookingId        = bookingId;
        this.requestId        = requestId;
        this.customerId       = customerId;
        this.providerId       = providerId;
        this.finalAmount      = (finalAmount != null) ? finalAmount : 0.0;
        this.paymentCompleted = false;  // Payment starts as pending
        this.bookedAt         = new Date();
    }

    // ---- Getters and Setters ----

    public Integer getBookingId()                              { return bookingId; }
    public void    setBookingId(Integer id)                    { this.bookingId = id; }

    public Integer getRequestId()                              { return requestId; }
    public void    setRequestId(Integer id)                    { this.requestId = id; }

    public Integer getCustomerId()                             { return customerId; }
    public void    setCustomerId(Integer id)                   { this.customerId = id; }

    public Integer getProviderId()                             { return providerId; }
    public void    setProviderId(Integer id)                   { this.providerId = id; }

    public Double  getFinalAmount()                            { return finalAmount; }
    public void    setFinalAmount(Double amount)               { this.finalAmount = amount; }

    public Boolean getPaymentCompleted()                       { return paymentCompleted; }
    public void    setPaymentCompleted(Boolean completed)      { this.paymentCompleted = completed; }

    public Date    getBookedAt()                               { return bookedAt; }
    public void    setBookedAt(Date date)                      { this.bookedAt = date; }

    @Override
    public String toString() {
        return String.format(
                "Booking #%d [Req #%d, Customer #%d, Provider #%d] -> $%.2f (Paid: %s)",
                bookingId, requestId, customerId, providerId, finalAmount, paymentCompleted);
    }
}
