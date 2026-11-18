package dao;

import exception.DatabaseOperationException;
import exception.InsufficientFundsException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Coordinates an atomic multi-table escrow payment transaction.
 *
 * <p>Demonstrates: JDBC Transaction Management — setAutoCommit(false),
 * commit(), rollback(), and proper resource cleanup in finally block.</p>
 *
 * <b>Viva tip:</b> Without setAutoCommit(false) each SQL statement commits
 * immediately.  Here we group debit + credit + booking-update into ONE
 * atomic unit so the database is never left in a half-updated state.
 */
public class TransactionManager {

    private final DBConnectionManager connectionManager;

    public TransactionManager() {
        this.connectionManager = DBConnectionManager.getInstance();
    }

    /**
     * Executes an escrow payment for a confirmed booking:
     * <ol>
     *   <li>Validates the customer has enough balance.</li>
     *   <li>Deducts the amount from the customer's wallet.</li>
     *   <li>Credits the amount to the provider's wallet.</li>
     *   <li>Marks the booking as payment_completed = true.</li>
     * </ol>
     * All four steps commit together or roll back together.
     *
     * @param bookingId  The booking being paid
     * @param customerId The paying customer
     * @param providerId The receiving provider
     * @param amount     The payment amount in dollars
     * @return true if the transaction committed successfully
     * @throws DatabaseOperationException if any SQL step fails
     * @throws InsufficientFundsException if the customer's balance is too low
     */
    public boolean executeBookingPayment(int bookingId, int customerId,
                                         int providerId, double amount)
            throws DatabaseOperationException, InsufficientFundsException {

        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);  // Begin transaction

            // Step 1 — Check customer balance
            double balance = fetchBalance(conn, customerId);
            if (balance < amount) {
                throw new InsufficientFundsException(
                        "Insufficient funds: Available $" + balance + ", Required $" + amount);
            }

            // Step 2 — Deduct from customer
            updateBalance(conn, customerId, -amount);

            // Step 3 — Credit to provider
            updateBalance(conn, providerId, +amount);

            // Step 4 — Mark booking as paid
            markBookingPaid(conn, bookingId);

            conn.commit();  // All steps succeeded — commit atomically
            return true;

        } catch (SQLException e) {
            rollback(conn);
            throw new DatabaseOperationException(
                    "Transaction failed and was rolled back: " + e.getMessage(), e);
        } finally {
            restoreAutoCommit(conn);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private double fetchBalance(Connection conn, int userId) throws SQLException {
        String sql = "SELECT wallet_balance FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("User not found: " + userId);
                return rs.getDouble("wallet_balance");
            }
        }
    }

    private void updateBalance(Connection conn, int userId, double delta) throws SQLException {
        String sql = "UPDATE users SET wallet_balance = wallet_balance + ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, delta);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    private void markBookingPaid(Connection conn, int bookingId) throws SQLException {
        String sql = "UPDATE bookings SET payment_completed = 1 WHERE booking_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        }
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); }
            catch (SQLException ex) { System.err.println("Rollback failed: " + ex.getMessage()); }
        }
    }

    private void restoreAutoCommit(Connection conn) {
        if (conn != null) {
            try { conn.setAutoCommit(true); conn.close(); }
            catch (SQLException e) { System.err.println("Error closing connection: " + e.getMessage()); }
        }
    }
}
