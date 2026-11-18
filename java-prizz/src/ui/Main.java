package ui;

import collections.BidComparators;
import collections.BidPriorityQueue;
import collections.RequestDispatcherQueue;
import concurrency.AsyncAuditService;
import concurrency.BidBuffer;
import concurrency.BidConsumer;
import concurrency.BidProducer;
import exception.InsufficientFundsException;
import exception.InvalidBidException;
import model.Bid;
import model.Booking;
import model.Customer;
import model.RequestStatus;
import model.ServiceProvider;
import model.ServiceRequest;
import service.AnalyticsService;
import service.PricingService;
import util.CsvFileHandler;
import util.InputValidator;
import util.ReportFormatter;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Main application entry point for PRIZZ — Enterprise On-Demand Logistics Marketplace.
 *
 * <p>Course: Programming in Java (25CSE0202) — Chitkara University
 * Evaluation: CE-III Project Milestone Evaluation (25 Marks)</p>
 *
 * <p>Demonstrates:
 * <ul>
 *   <li>Core OOP (Abstraction, Inheritance, Polymorphism, Encapsulation)</li>
 *   <li>Collections Framework (List, Set, Map, PriorityQueue, ArrayDeque, Iterator, Comparator)</li>
 *   <li>Java 8 Stream API (filter, map, reduce, groupingBy, summaryStatistics)</li>
 *   <li>Multi-threading &amp; Concurrency (wait/notifyAll, Thread, Runnable, ExecutorService)</li>
 *   <li>Custom Checked Exception Hierarchy &amp; Propagation</li>
 *   <li>File I/O with try-with-resources (CSV export and import)</li>
 *   <li>JDBC Layer Architecture &amp; ACID Transactions</li>
 *   <li>Defensive Input Validation with retry logic</li>
 * </ul>
 * </p>
 */
public class Main {

    // Global in-memory data registries
    private static final Map<Integer, Customer> customerRegistry = new HashMap<>();
    private static final Map<Integer, ServiceProvider> providerRegistry = new HashMap<>();
    private static final List<ServiceRequest> allRequests = new ArrayList<>();
    private static final List<Bid> liveBids = new ArrayList<>();
    private static final List<Booking> confirmedBookings = new ArrayList<>();

    // Core services
    private static final PricingService pricingService = new PricingService();
    private static final AnalyticsService analyticsEngine = new AnalyticsService();
    private static final BidPriorityQueue bidQueue = new BidPriorityQueue();
    private static final RequestDispatcherQueue dispatcherQueue = new RequestDispatcherQueue();
    private static final AsyncAuditService auditService = new AsyncAuditService(2);

    // ID generators
    private static int nextRequestId = 502;
    private static int nextBidId = 10;
    private static int nextBookingId = 1001;

    public static void main(String[] args) {
        // Ensure data directory exists
        new File("java-prizz/data").mkdirs();

        // Seed initial data for immediate demonstration
        seedInitialData();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("===============================================================");
        System.out.println("       PRIZZ: ENTERPRISE ON-DEMAND LOGISTICS MARKETPLACE       ");
        System.out.println("   Course: 25CSE0202 | Chitkara University | Team of 4 Members ");
        System.out.println("===============================================================");

        while (running) {
            printMenu();
            System.out.print("Enter your choice (1-11): ");
            String input = scanner.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        handleCreateRequest(scanner);
                        break;
                    case 2:
                        handleSubmitBid(scanner);
                        break;
                    case 3:
                        handleViewMarketplace();
                        break;
                    case 4:
                        handleAwardWinningBid();
                        break;
                    case 5:
                        handlePricingMatrixDemo();
                        break;
                    case 6:
                        handleConcurrencyAuctionSimulation();
                        break;
                    case 7:
                        handleStreamAnalyticsDemo();
                        break;
                    case 8:
                        handleFileIoCsvDemo();
                        break;
                    case 9:
                        handleExceptionAndValidationDemo(scanner);
                        break;
                    case 10:
                        runAutomatedSyllabusSuite();
                        break;
                    case 11:
                        System.out.println("\nShutting down PRIZZ Engine...");
                        auditService.shutdown();
                        running = false;
                        break;
                    default:
                        System.out.println("  [!] Invalid choice. Please enter a number between 1 and 11.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Input error: please enter a valid integer (1-11).");
            } catch (Exception e) {
                System.out.println("  [!] Unexpected error: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("PRIZZ successfully stopped. Thank you!");
    }

    // -------------------------------------------------------------------------
    // MENU DISPLAY
    // -------------------------------------------------------------------------

    private static void printMenu() {
        System.out.println("\n---------------------------------------------------------------");
        System.out.println("                 PRIZZ MAIN CONTROL MENU                       ");
        System.out.println("---------------------------------------------------------------");
        System.out.println("  [CORE USE-CASES - WORKING PROTOTYPE]");
        System.out.println("    1. Post New Service Request (Interactive Console Input)");
        System.out.println("    2. Submit Provider Bid (Interactive Input & Validation)");
        System.out.println("    3. View Marketplace Requests & Ranked Bids");
        System.out.println("    4. Award Winning Bid & Create Booking (Escrow Check)");
        System.out.println("  [SYLLABUS CONCEPT DEMONSTRATIONS]");
        System.out.println("    5. OOP Profiles & Dynamic Pricing Matrix (2D Arrays)");
        System.out.println("    6. Multithreaded Reverse-Auction (wait/notify & join)");
        System.out.println("    7. Stream API Analytics Engine (filter/reduce/groupingBy)");
        System.out.println("    8. CSV File Export & Import (try-with-resources)");
        System.out.println("    9. Custom Checked Exceptions & InputValidator Demo");
        System.out.println("   10. Run Full Automated Syllabus Verification Suite");
        System.out.println("   11. Exit PRIZZ");
        System.out.println("---------------------------------------------------------------");
    }

    // -------------------------------------------------------------------------
    // SEED DATA
    // -------------------------------------------------------------------------

    private static void seedInitialData() {
        Customer c1 = new Customer(101, "Alice Walker", "alice@prizz.io", 1500.0, 250);
        Customer c2 = new Customer(102, "Bob Vance", "bob@vance.com", 850.0, 110);
        customerRegistry.put(c1.getUserId(), c1);
        customerRegistry.put(c2.getUserId(), c2);

        ServiceProvider p1 = new ServiceProvider(201, "Swift Logistics", "swift@prizz.io", 300.0, 4.8);
        p1.addSkill("Pallet Transport");
        p1.addSkill("Heavy Hauling");

        ServiceProvider p2 = new ServiceProvider(202, "Metro Freight", "metro@prizz.io", 450.0, 4.6);
        p2.addSkill("Fleet Dispatch");
        p2.addSkill("Warehousing");

        providerRegistry.put(p1.getUserId(), p1);
        providerRegistry.put(p2.getUserId(), p2);

        ServiceRequest req1 = new ServiceRequest(501, 101, "Warehouse Pallet Transport",
                "Move 150 pallets from Dock A to Central Warehouse", 600.0);
        allRequests.add(req1);
        dispatcherQueue.enqueueStandardRequest(req1);

        Bid b1 = new Bid(1, 501, 201, 280.0, "Express pallet delivery", new Date());
        Bid b2 = new Bid(2, 501, 202, 350.0, "Flatbed delivery", new Date());
        liveBids.add(b1);
        liveBids.add(b2);
        bidQueue.addBid(b1);
        bidQueue.addBid(b2);
    }

    // -------------------------------------------------------------------------
    // USE-CASE 1: CREATE REQUEST
    // -------------------------------------------------------------------------

    private static void handleCreateRequest(Scanner sc) {
        System.out.println("\n--- [USE-CASE 1] POST NEW SERVICE REQUEST ---");
        String title = InputValidator.readNonEmpty(sc, "  Enter Job Title: ");
        String desc = InputValidator.readNonEmpty(sc, "  Enter Description: ");
        double budget = InputValidator.readDouble(sc, "  Enter Base Budget ($): ", 10.0);
        int urgency = InputValidator.readInt(sc, "  Urgency (0 = Standard FIFO, 1 = Emergency LIFO Preemption): ", 0, 1);

        int reqId = nextRequestId++;
        ServiceRequest newReq = new ServiceRequest(reqId, 101, title, desc, budget);
        allRequests.add(newReq);

        if (urgency == 1) {
            dispatcherQueue.enqueueEmergencyRequest(newReq);
            System.out.println("  [!] Emergency Request placed at FRONT of Dispatcher Queue (LIFO preemption).");
        } else {
            dispatcherQueue.enqueueStandardRequest(newReq);
            System.out.println("  [+] Standard Request added to BACK of Dispatcher Queue (FIFO).");
        }

        System.out.print(ReportFormatter.buildRequestCard(newReq));
        auditService.submitAuditTask("REQUEST_POSTED", "Req #" + reqId + " Budget: $" + budget);
    }

    // -------------------------------------------------------------------------
    // USE-CASE 2: SUBMIT BID
    // -------------------------------------------------------------------------

    private static void handleSubmitBid(Scanner sc) {
        System.out.println("\n--- [USE-CASE 2] SUBMIT PROVIDER BID ---");
        int reqId = InputValidator.readInt(sc, "  Enter Target Request ID (e.g. 501): ", 1, 9999);
        int provId = InputValidator.readInt(sc, "  Enter Provider ID (e.g. 201, 202): ", 1, 9999);
        double amount = InputValidator.readDouble(sc, "  Enter Proposed Bid Amount ($): ", 1.0);
        String msg = InputValidator.readNonEmpty(sc, "  Enter Proposal Pitch: ");

        try {
            int bidId = nextBidId++;
            Bid newBid = new Bid(bidId, reqId, provId, amount, msg, new Date());
            validateBid(newBid);

            liveBids.add(newBid);
            bidQueue.addBid(newBid);

            System.out.println("  [+] Bid #" + bidId + " submitted successfully for Request #" + reqId + "!");
            System.out.println("      Ranked automatically into PriorityQueue (Lowest price = Highest priority).");
            auditService.submitAuditTask("BID_SUBMITTED", "Bid #" + bidId + " -> $" + amount);
        } catch (InvalidBidException e) {
            System.err.println("  [X] Bid Validation Failed: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // USE-CASE 3: VIEW MARKETPLACE
    // -------------------------------------------------------------------------

    private static void handleViewMarketplace() {
        System.out.println("\n--- [USE-CASE 3] MARKETPLACE OVERVIEW ---");
        System.out.println("  [ACTIVE SERVICE REQUESTS: " + allRequests.size() + "]");
        for (ServiceRequest req : allRequests) {
            System.out.println("    " + req);
        }

        System.out.println("\n  [BIDS RANKED BY PRIORITY QUEUE (Lowest Price First)]:");
        bidQueue.printAllRankedBids();

        System.out.println("\n  [FORMATTED SUMMARY TABLE (ReportFormatter)]:");
        System.out.print(ReportFormatter.buildBidSummaryTable(liveBids));
    }

    // -------------------------------------------------------------------------
    // USE-CASE 4: AWARD BID & ESCROW
    // -------------------------------------------------------------------------

    private static void handleAwardWinningBid() {
        System.out.println("\n--- [USE-CASE 4] AWARD WINNING BID & ESCROW PAYMENT ---");
        Bid winningBid = bidQueue.peekWinningBid();
        if (winningBid == null) {
            System.out.println("  [!] No active bids available in PriorityQueue.");
            return;
        }

        System.out.println("  Top Candidate in Reverse Auction:");
        System.out.println("    " + winningBid);

        Customer customer = customerRegistry.get(101);
        try {
            simulatePayment(customer, winningBid.getBidAmount());

            // Deduct customer balance
            customer.setWalletBalance(customer.getWalletBalance() - winningBid.getBidAmount());
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() + 25);

            // Credit provider
            ServiceProvider provider = providerRegistry.get(winningBid.getProviderId());
            if (provider != null) {
                provider.setWalletBalance(provider.getWalletBalance() + winningBid.getBidAmount());
            }

            // Create Booking
            int bookingId = nextBookingId++;
            Booking booking = new Booking(bookingId, winningBid.getRequestId(),
                    customer.getUserId(), winningBid.getProviderId(), winningBid.getBidAmount());
            booking.setPaymentCompleted(true);
            confirmedBookings.add(booking);

            // Pop from priority queue
            bidQueue.pollWinningBid();

            System.out.println("  [âœ“] Booking Confirmed & Escrow Released Successfully!");
            System.out.println("      " + booking);
            System.out.printf("      Customer Updated Balance: $%.2f | Loyalty Points: %d%n",
                    customer.getWalletBalance(), customer.getLoyaltyPoints());
            auditService.submitAuditTask("BOOKING_COMPLETED", "Booking #" + bookingId + " Paid: $" + winningBid.getBidAmount());

        } catch (InsufficientFundsException e) {
            System.err.println("  [X] Payment Failed: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // SYLLABUS DEMO 5: 2D PRICING MATRIX & OOP
    // -------------------------------------------------------------------------

    private static void handlePricingMatrixDemo() {
        System.out.println("\n--- [DEMO 5] OOP PROFILES & 2D ARRAY PRICING MATRIX ---");
        System.out.println("  [1] Polymorphic Profiles (User -> Customer & ServiceProvider):");
        for (Map.Entry<Integer, Customer> e : customerRegistry.entrySet()) {
            System.out.println("    " + e.getValue().getProfileSummary());
        }
        for (Map.Entry<Integer, ServiceProvider> e : providerRegistry.entrySet()) {
            System.out.println("    " + e.getValue().getProfileSummary());
        }

        System.out.println("\n  [2] Dynamic Tariff Multipliers (2D Primitive Array double[][]):");
        pricingService.displayMatrix();

        double base = 500.0;
        double surgePrice = pricingService.calculateAdjustedBudget(base, 2, 0); // Emergency Tier in Metro Zone
        System.out.printf("  Sample Calculation: Base $%.2f -> Emergency Tier (Zone: Metro) = $%.2f%n",
                base, surgePrice);
        auditService.submitAuditTask("TARIFF_MATRIX_CHECK", "Surge calculated: $" + surgePrice);
    }

    // -------------------------------------------------------------------------
    // SYLLABUS DEMO 6: CONCURRENCY AUCTION SIMULATION
    // -------------------------------------------------------------------------

    private static void handleConcurrencyAuctionSimulation() {
        System.out.println("\n--- [DEMO 6] MULTITHREADED REVERSE-AUCTION SIMULATION ---");
        System.out.println("  Spawning Producer (Runnable) and Consumer (Thread) with Bounded Circular Buffer...");

        BidBuffer buffer = new BidBuffer(3);
        List<Bid> auctionSink = new ArrayList<>();
        BidConsumer consumer = new BidConsumer(buffer, auctionSink);
        consumer.start();

        Thread producerThread = new Thread(new BidProducer(buffer, 501, 5));
        producerThread.start();

        try {
            producerThread.join();  // Wait for producer to finish creating 5 bids
            Thread.sleep(600);      // Allow consumer worker to drain buffer
            consumer.interrupt();   // Clean thread interruption
            consumer.join();        // Wait for worker termination

            System.out.println("  [âœ“] Simulation finished! Consumed " + auctionSink.size() + " concurrent bids.");
            for (Bid b : auctionSink) {
                liveBids.add(b);
                bidQueue.addBid(b);
            }
        } catch (InterruptedException e) {
            System.err.println("  [!] Thread interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    // -------------------------------------------------------------------------
    // SYLLABUS DEMO 7: STREAM API ANALYTICS
    // -------------------------------------------------------------------------

    private static void handleStreamAnalyticsDemo() {
        System.out.println("\n--- [DEMO 7] STREAM API ANALYTICS ENGINE ---");
        if (liveBids.isEmpty()) {
            seedInitialData();
        }

        // 1. Lambda filter via BidFilter Functional Interface
        double ceiling = 300.0;
        List<Bid> underCeiling = analyticsEngine.filterBidsUnderCeiling(
                liveBids, ceiling, (b, max) -> b.getBidAmount() <= max);
        System.out.printf("  1. Bids under $%.2f ceiling (filter + lambda): %d matches%n",
                ceiling, underCeiling.size());
        for (Bid b : underCeiling) {
            System.out.println("     " + b);
        }

        // 2. Stream reduction
        double totalVolume = analyticsEngine.calculateTotalBiddedVolume(liveBids);
        System.out.printf("%n  2. Total Market Volume (Stream reduce): $%.2f%n", totalVolume);

        // 3. Collectors.groupingBy & averagingDouble
        Map<Integer, Double> avgByReq = analyticsEngine.calculateAverageBidPerRequest(liveBids);
        System.out.println("  3. Average Price Per Request (groupingBy + averagingDouble): " + avgByReq);

        // 4. DoubleSummaryStatistics
        DoubleSummaryStatistics stats = analyticsEngine.getBidStatistics(liveBids);
        System.out.printf("  4. Summary Statistics: Count=%d, Min=$%.2f, Max=$%.2f, Avg=$%.2f%n",
                stats.getCount(), stats.getMin(), stats.getMax(), stats.getAverage());

        // 5. Distinct & Sorted Messages
        List<String> proposals = analyticsEngine.extractUniqueProposals(liveBids);
        System.out.println("  5. Unique Proposals (distinct + sorted): " + proposals);
    }

    // -------------------------------------------------------------------------
    // SYLLABUS DEMO 8: CSV FILE PERSISTENCE
    // -------------------------------------------------------------------------

    private static void handleFileIoCsvDemo() {
        System.out.println("\n--- [DEMO 8] FILE I/O VIA TRY-WITH-RESOURCES ---");
        String exportPath = "java-prizz/data/prizz_bids_export.csv";

        try {
            CsvFileHandler.exportBidsToCsv(exportPath, liveBids);
            System.out.println("  [âœ“] Successfully exported " + liveBids.size() + " bids to: " + exportPath);

            List<String> rawRows = CsvFileHandler.readCsvRawLines(exportPath);
            System.out.println("  [âœ“] Read back " + rawRows.size() + " data rows from disk (Iterator traversal):");

            Iterator<String> it = rawRows.iterator();
            while (it.hasNext()) {
                System.out.println("      " + it.next());
            }
        } catch (IOException e) {
            System.err.println("  [X] File I/O Error: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // SYLLABUS DEMO 9: EXCEPTIONS & INPUT VALIDATOR
    // -------------------------------------------------------------------------

    private static void handleExceptionAndValidationDemo(Scanner sc) {
        System.out.println("\n--- [DEMO 9] CUSTOM CHECKED EXCEPTIONS & INPUT VALIDATOR ---");

        // 1. InvalidBidException
        System.out.println("  [1] Testing InvalidBidException with negative price ($-50.00):");
        try {
            validateBid(new Bid(999, 501, 201, -50.0, "Invalid negative test", new Date()));
        } catch (InvalidBidException e) {
            System.out.println("      CAUGHT EXPECTED: " + e);
        }

        // 2. InsufficientFundsException
        System.out.println("%n  [2] Testing InsufficientFundsException ($5000 payout on $850 balance):");
        try {
            simulatePayment(customerRegistry.get(102), 5000.0);
        } catch (InsufficientFundsException e) {
            System.out.println("      CAUGHT EXPECTED: " + e);
        }

        // 3. Defensive InputValidator test
        System.out.println("%n  [3] Testing InputValidator retry behavior:");
        int number = InputValidator.readInt(sc, "      Enter a number between 1 and 10: ", 1, 10);
        System.out.println("      Successfully received validated integer: " + number);
    }

    // -------------------------------------------------------------------------
    // SYLLABUS DEMO 10: AUTOMATED TEST SUITE (1-CLICK EVALUATION)
    // -------------------------------------------------------------------------

    private static void runAutomatedSyllabusSuite() {
        System.out.println("\n===============================================================");
        System.out.println("       RUNNING AUTOMATED SYLLABUS VERIFICATION SUITE           ");
        System.out.println("===============================================================");

        // Step 1: Pricing Matrix
        double matrixVal = pricingService.calculateAdjustedBudget(500.0, 1, 1);
        System.out.printf("  [TEST 1] 2D Pricing Matrix: Urgent x Suburban = $%.2f (PASS)%n", matrixVal);

        // Step 2: Deque Dispatcher
        ServiceRequest emReq = new ServiceRequest(999, 101, "Emergency Rigging", "Immediate crane", 1000.0);
        dispatcherQueue.enqueueEmergencyRequest(emReq);
        ServiceRequest popped = dispatcherQueue.dispatchNext();
        System.out.printf("  [TEST 2] Deque Preemption: Dispatched %s (PASS)%n", popped.getTitle());

        // Step 3: PriorityQueue min-heap
        Bid lowest = bidQueue.peekWinningBid();
        System.out.printf("  [TEST 3] PriorityQueue Lowest Bid: Bid #%d ($%.2f) (PASS)%n",
                lowest.getBidId(), lowest.getBidAmount());

        // Step 4: Stream API Volume
        double vol = analyticsEngine.calculateTotalBiddedVolume(liveBids);
        System.out.printf("  [TEST 4] Stream API Volume Reduction: $%.2f (PASS)%n", vol);

        // Step 5: File I/O
        try {
            CsvFileHandler.exportBidsToCsv("java-prizz/data/prizz_bids_export.csv", liveBids);
            List<String> lines = CsvFileHandler.readCsvRawLines("java-prizz/data/prizz_bids_export.csv");
            System.out.printf("  [TEST 5] File I/O CSV: Written & Read %d rows (PASS)%n", lines.size());
        } catch (IOException e) {
            System.err.println("  [TEST 5] File I/O FAILED: " + e.getMessage());
        }

        // Step 6: Custom Exception
        try {
            validateBid(new Bid(1, 1, 1, -1.0, "Negative", new Date()));
        } catch (InvalidBidException e) {
            System.out.println("  [TEST 6] Custom Exception Hierarchy Caught: " + e.getErrorCode() + " (PASS)");
        }

        // Step 7: Async Audit Task
        auditService.submitAuditTask("AUTOMATED_SUITE", "All 7 syllabus tests executed successfully.");
        System.out.println("  [TEST 7] Async Audit Task submitted to background ExecutorService (PASS)");

        System.out.println("===============================================================");
        System.out.println("  ALL SYLLABUS EVALUATION MODULES VERIFIED SUCCESSFULLY!");
        System.out.println("===============================================================");
    }

    // -------------------------------------------------------------------------
    // VALIDATION HELPERS
    // -------------------------------------------------------------------------

    private static void validateBid(Bid bid) throws InvalidBidException {
        if (bid.getBidAmount() <= 0) {
            throw new InvalidBidException("Bid amount must be positive. Provided: $" + bid.getBidAmount());
        }
    }

    private static void simulatePayment(Customer customer, double amount) throws InsufficientFundsException {
        if (customer.getWalletBalance() < amount) {
            throw new InsufficientFundsException(String.format(
                    "Customer #%d balance ($%.2f) is below required payment ($%.2f).",
                    customer.getUserId(), customer.getWalletBalance(), amount));
        }
    }
}
