package exception;

/**
 * Thrown by DAO classes when a JDBC operation fails.
 *
 * <p>Demonstrates: Exception chaining — wraps a low-level SQLException
 * into a domain-meaningful exception so callers never need to import java.sql.</p>
 */
public class DatabaseOperationException extends PrizzAppException {

    private static final long serialVersionUID = 1L;

    public DatabaseOperationException(String message) {
        super("PRIZZ_ERR_DB", message, null);
    }

    public DatabaseOperationException(String message, Throwable cause) {
        super("PRIZZ_ERR_DB", message, cause);
    }
}
