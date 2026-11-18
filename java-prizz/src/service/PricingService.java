package service;

/**
 * Calculates adjusted pricing based on urgency tier and geographic zone.
 *
 * <p>Demonstrates: Multi-dimensional primitive arrays (double[][]),
 * array traversal with nested for-loops, boundary validation.</p>
 *
 * <b>Viva tip:</b> The 2D array rateMultipliers[row][col] is a core Java
 * concept.  Row = urgency level, Column = zone type.  Multiply base budget
 * by the looked-up multiplier to get the surge price.
 */
public class PricingService {

    /*
     * Rows  → Urgency Tier : 0 = Standard | 1 = Urgent | 2 = Emergency 24/7
     * Columns → Zone       : 0 = Metro    | 1 = Suburban | 2 = Remote Industrial
     */
    private final double[][] rateMultipliers;

    /** Tier labels used only for display — not part of the calculation. */
    private static final String[] TIER_LABELS = {
        "0 (Standard)  ", "1 (Urgent)    ", "2 (Emergency) "
    };

    public PricingService() {
        this.rateMultipliers = new double[][] {
            { 1.00, 1.15, 1.30 }, // Standard
            { 1.25, 1.40, 1.65 }, // Urgent
            { 1.75, 1.95, 2.25 }  // Emergency
        };
    }

    /**
     * Returns the surge-adjusted budget for a given tier and zone.
     *
     * @param baseBudget  Customer's original budget (dollars)
     * @param urgencyTier Row index 0–2
     * @param zoneIndex   Column index 0–2
     * @return Adjusted budget after applying the multiplier
     * @throws IllegalArgumentException if indices are out of range
     */
    public double calculateAdjustedBudget(double baseBudget, int urgencyTier, int zoneIndex) {
        if (urgencyTier < 0 || urgencyTier >= rateMultipliers.length) {
            throw new IllegalArgumentException("Invalid urgency tier: " + urgencyTier);
        }
        if (zoneIndex < 0 || zoneIndex >= rateMultipliers[0].length) {
            throw new IllegalArgumentException("Invalid zone index: " + zoneIndex);
        }
        return baseBudget * rateMultipliers[urgencyTier][zoneIndex];
    }

    /** Returns the full 2D multiplier array (for read-only access). */
    public double[][] getRateMultipliers() { return rateMultipliers; }

    /** Prints the matrix to the console in a formatted table. */
    public void displayMatrix() {
        System.out.println("\n--- Dynamic Tariff & Surge Multiplier Matrix (2D Array) ---");
        System.out.println("Tier \\ Zone          Metro       Suburban      Remote");
        for (int r = 0; r < rateMultipliers.length; r++) {
            System.out.printf("%s:  ", TIER_LABELS[r]);
            for (int c = 0; c < rateMultipliers[r].length; c++) {
                System.out.printf("  %6.2fx    ", rateMultipliers[r][c]);
            }
            System.out.println();
        }
    }
}
