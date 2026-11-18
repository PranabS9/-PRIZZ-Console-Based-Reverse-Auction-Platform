package exception;

/**
 * Root checked exception for all domain-specific errors in PRIZZ.
 *
 * <p>Demonstrates: Custom Exception creation, exception hierarchy,
 * constructor chaining (this / super), error codes.</p>
 *
 * <b>Viva tip:</b> By extending Exception (not RuntimeException) this is
 * a "checked" exception — callers MUST either catch it or declare it with
 * "throws", which makes error paths explicit and visible.
 */
public class PrizzAppException extends Exception {

    private static final long serialVersionUID = 1L;

    /** Short machine-readable error code (e.g. "PRIZZ_ERR_DB"). */
    private final String errorCode;

    public PrizzAppException(String message) {
        super(message);
        this.errorCode = "PRIZZ_ERR_GENERIC";
    }

    public PrizzAppException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PRIZZ_ERR_CAUSE";
    }

    public PrizzAppException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }

    @Override
    public String toString() {
        return String.format("[%s] %s", errorCode, getMessage());
    }
}
