package exception;

/**
 * Thrown when a customer's wallet balance is too low to cover
 * an escrow payment or booking transaction.
 *
 * <p>Demonstrates: Business-rule enforcement via exceptions,
 * exception propagation up the call stack.</p>
 */
public class InsufficientFundsException extends PrizzAppException {

    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(String message) {
        super("PRIZZ_ERR_INSUFFICIENT_FUNDS", message, null);
    }
}
