package model;

/**
 * Represents a customer who posts service requests and accepts bids.
 *
 * <p>Demonstrates: Inheritance (extends User), Polymorphism (overrides getProfileSummary),
 * Comparable interface (natural ordering by userId).</p>
 */
public class Customer extends User implements Comparable<Customer> {

    private Integer loyaltyPoints; // Reward points earned per completed booking

    /**
     * Creates a Customer account.
     *
     * @param userId        Unique customer ID
     * @param name          Full name
     * @param email         Email address
     * @param walletBalance Starting wallet balance
     * @param loyaltyPoints Existing loyalty points; defaults to 0 if null
     */
    public Customer(Integer userId, String name, String email,
                    Double walletBalance, Integer loyaltyPoints) {
        super(userId, name, email, "CUSTOMER", walletBalance);
        this.loyaltyPoints = (loyaltyPoints != null) ? loyaltyPoints : 0;
    }

    public Integer getLoyaltyPoints()                   { return loyaltyPoints; }
    public void    setLoyaltyPoints(Integer pts)        { this.loyaltyPoints = pts; }

    /**
     * Returns a formatted one-line customer summary.
     * This method overrides the abstract method in User — demonstrating Polymorphism.
     */
    @Override
    public String getProfileSummary() {
        return String.format(
                "Customer Profile: %s (ID: %d) | Balance: $%.2f | Loyalty Points: %d",
                getName(), getUserId(), getWalletBalance(), loyaltyPoints);
    }

    /**
     * Natural ordering by user ID (ascending).
     * Needed for sorted collections (e.g. TreeSet, Collections.sort).
     */
    @Override
    public int compareTo(Customer other) {
        if (other == null) return 1;
        return this.getUserId().compareTo(other.getUserId());
    }
}
