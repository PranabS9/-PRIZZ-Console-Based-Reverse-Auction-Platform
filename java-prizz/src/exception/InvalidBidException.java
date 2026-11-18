package exception;

/**
 * Thrown when a submitted bid violates business constraints
 * (e.g. bid amount is zero or negative).
 *
 * <p>Demonstrates: Domain-specific custom exception, parameter validation.</p>
 */
public class InvalidBidException extends PrizzAppException {

    private static final long serialVersionUID = 1L;

    public InvalidBidException(String message) {
        super("PRIZZ_ERR_INVALID_BID", message, null);
    }
}
