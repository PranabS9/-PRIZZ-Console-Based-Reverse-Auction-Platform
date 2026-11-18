package util;

import model.Bid;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles CSV export and import of Bid records using file streams.
 *
 * <p>Demonstrates: File I/O with try-with-resources (BufferedWriter, FileWriter,
 * BufferedReader, FileReader), Iterator pattern (while + readLine), exception
 * propagation (throws IOException).</p>
 *
 * <b>Viva tip:</b> try-with-resources (the parenthesized resource in "try")
 * automatically calls close() on the writer/reader — even if an exception is
 * thrown — so you can never forget to close a file.
 */
public class CsvFileHandler {

    /**
     * Writes a list of Bid objects to a CSV file.
     * Creates or overwrites the file at the given path.
     *
     * @param filePath Target file path (e.g. "java-prizz/data/bids.csv")
     * @param bids     List of bids to export
     * @throws IOException if the file cannot be written
     */
    public static void exportBidsToCsv(String filePath, List<Bid> bids) throws IOException {
        // try-with-resources closes the writer automatically
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("bidId,requestId,providerId,bidAmount,proposalMessage");
            writer.newLine();
            for (Bid bid : bids) {
                // Escape double-quotes inside the message field
                String safeMsg = bid.getProposalMessage().replace("\"", "\"\"");
                String row = String.format("%d,%d,%d,%.2f,\"%s\"",
                        bid.getBidId(), bid.getRequestId(), bid.getProviderId(),
                        bid.getBidAmount(), safeMsg);
                writer.write(row);
                writer.newLine();
            }
        }
    }

    /**
     * Reads all data rows from a CSV file, skipping the header.
     * Returns an empty list if the file does not exist.
     *
     * @param filePath Source file path
     * @return List of raw CSV line strings (one per data row)
     * @throws IOException if the file exists but cannot be read
     */
    public static List<String> readCsvRawLines(String filePath) throws IOException {
        List<String> records = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return records;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }  // Skip header row
                if (!line.trim().isEmpty()) records.add(line);
            }
        }
        return records;
    }
}
