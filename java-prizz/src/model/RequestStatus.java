package model;

/**
 * Enum representing every possible lifecycle state of a ServiceRequest.
 *
 * <p>Demonstrates: Java Enum type, state-machine modeling.</p>
 *
 * <b>Viva tip:</b> An enum is a special class whose instances are fixed
 * compile-time constants.  Using it here prevents invalid status strings
 * like "opn" or "done" from ever entering the system.
 */
public enum RequestStatus {
    DRAFT,           // Saved but not yet posted
    OPEN,            // Posted and accepting bids
    BIDDING_ACTIVE,  // Bidding window is live
    ACCEPTED,        // A bid has been accepted
    IN_PROGRESS,     // Service is being delivered
    COMPLETED,       // Job done, payment released
    CANCELLED        // Request withdrawn by customer
}
