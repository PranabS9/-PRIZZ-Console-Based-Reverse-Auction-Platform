package concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Runs audit-logging tasks asynchronously on a background thread pool.
 *
 * <p>Demonstrates: ExecutorService (fixed thread pool), Runnable lambda,
 * Thread.currentThread().getName(), clean shutdown with awaitTermination.</p>
 *
 * <b>Viva tip:</b> ExecutorService manages a pool of worker threads.
 * submit() hands a task to any free thread — the main thread continues
 * immediately without waiting.  shutdown() + awaitTermination() ensures
 * all queued tasks finish before the JVM exits.
 */
public class AsyncAuditService {

    private final ExecutorService executorService;

    /**
     * Creates an audit service backed by a fixed-size thread pool.
     *
     * @param threadPoolSize Number of worker threads (typically 2–4)
     */
    public AsyncAuditService(int threadPoolSize) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * Submits an audit log entry to be written asynchronously.
     * Returns immediately — the actual logging happens on a background thread.
     *
     * @param eventName Short event label (e.g. "BID_SUBMITTED")
     * @param details   Descriptive payload string
     */
    public void submitAuditTask(String eventName, String details) {
        executorService.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.printf("[AUDIT] [%s] Event: '%s' | Details: %s%n",
                    threadName, eventName, details);
        });
    }

    /**
     * Initiates an orderly shutdown: waits up to 2 seconds for running tasks
     * to finish, then forces shutdown if any remain.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
