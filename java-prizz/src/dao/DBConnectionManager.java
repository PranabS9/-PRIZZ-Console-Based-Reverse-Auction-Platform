package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton that manages the SQLite JDBC connection and auto-creates tables.
 *
 * <p>Demonstrates: Singleton design pattern (double-checked locking),
 * JDBC DriverManager, DDL execution (CREATE TABLE IF NOT EXISTS),
 * volatile keyword for thread-safe singleton.</p>
 *
 * <b>Viva tip:</b> Singleton ensures only ONE connection manager exists
 * in the whole application.  "volatile" makes the instance variable
 * visible across threads without stale cache reads.
 */
public class DBConnectionManager {

    /** SQLite database file path (relative to working directory). */
    private static final String DB_URL = "jdbc:sqlite:prizz_database.db";

    /** The single shared instance — volatile for thread safety. */
    private static volatile DBConnectionManager instance;

    /** Private constructor prevents outside instantiation. */
    private DBConnectionManager() {
        initializeTables();
    }

    /**
     * Returns the single shared instance, creating it lazily on first call.
     * Uses double-checked locking for thread-safe singleton creation.
     *
     * @return the singleton DBConnectionManager
     */
    public static DBConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DBConnectionManager.class) {
                if (instance == null) {
                    instance = new DBConnectionManager();
                }
            }
        }
        return instance;
    }

    /**
     * Opens and returns a fresh JDBC Connection to the SQLite database.
     * Callers should use try-with-resources to close it automatically.
     *
     * @return a live JDBC Connection
     * @throws SQLException if the driver is unavailable or the file is locked
     */
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            // SQLite JDBC driver not found — operations will fail gracefully
        }
        return DriverManager.getConnection(DB_URL);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Creates all required tables if they do not already exist.
     * Called once when the singleton is first constructed.
     *
     * <b>Viva tip:</b> "CREATE TABLE IF NOT EXISTS" is idempotent — safe to
     * run on every startup without duplicating data.
     */
    private void initializeTables() {
        String createUsers =
            "CREATE TABLE IF NOT EXISTS users (" +
            "  user_id        INTEGER PRIMARY KEY," +
            "  name           TEXT    NOT NULL," +
            "  email          TEXT    NOT NULL," +
            "  role           TEXT    NOT NULL," +
            "  wallet_balance REAL    DEFAULT 0.0," +
            "  rating         REAL    DEFAULT 5.0," +
            "  loyalty_points INTEGER DEFAULT 0" +
            ")";

        String createRequests =
            "CREATE TABLE IF NOT EXISTS service_requests (" +
            "  request_id  INTEGER PRIMARY KEY," +
            "  customer_id INTEGER NOT NULL," +
            "  title       TEXT    NOT NULL," +
            "  description TEXT," +
            "  budget      REAL    DEFAULT 0.0," +
            "  status      TEXT    DEFAULT 'OPEN'" +
            ")";

        String createBookings =
            "CREATE TABLE IF NOT EXISTS bookings (" +
            "  booking_id        INTEGER PRIMARY KEY," +
            "  request_id        INTEGER NOT NULL," +
            "  customer_id       INTEGER NOT NULL," +
            "  provider_id       INTEGER NOT NULL," +
            "  final_amount      REAL    DEFAULT 0.0," +
            "  payment_completed INTEGER DEFAULT 0" +   // 0 = false, 1 = true (SQLite)
            ")";

        try (Connection conn = getConnection();
             Statement  stmt = conn.createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(createRequests);
            stmt.execute(createBookings);
            System.out.println("[DB] Tables verified / created successfully.");
        } catch (SQLException e) {
            System.err.println("[DB WARNING] Could not initialize tables: " + e.getMessage());
        }
    }
}
