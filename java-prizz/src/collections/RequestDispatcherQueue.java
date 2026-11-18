package collections;

import model.ServiceRequest;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Double-ended queue that dispatches service requests in priority order.
 *
 * <ul>
 *   <li>Standard requests join the BACK of the queue (FIFO — first in, first out).</li>
 *   <li>Emergency requests jump to the FRONT (LIFO-style preemption).</li>
 * </ul>
 *
 * <p>Demonstrates: Collections Framework (Deque / ArrayDeque), synchronized
 * methods for basic thread safety, Queue vs Deque distinction.</p>
 *
 * <b>Viva tip:</b> ArrayDeque implements both Queue (FIFO) and Stack (LIFO)
 * interfaces.  addLast() = enqueue, addFirst() = push to front, pollFirst() = dequeue.
 */
public class RequestDispatcherQueue {

    private final Deque<ServiceRequest> dispatchQueue;

    public RequestDispatcherQueue() {
        this.dispatchQueue = new ArrayDeque<>();
    }

    /** Adds a standard request to the back of the queue (FIFO). */
    public synchronized void enqueueStandardRequest(ServiceRequest request) {
        dispatchQueue.addLast(request);
    }

    /** Pushes an emergency request to the front of the queue (preempts normal ones). */
    public synchronized void enqueueEmergencyRequest(ServiceRequest request) {
        dispatchQueue.addFirst(request);
    }

    /** Removes and returns the next request to be dispatched, or null if empty. */
    public synchronized ServiceRequest dispatchNext() {
        return dispatchQueue.pollFirst();
    }

    public synchronized int     pendingCount() { return dispatchQueue.size(); }
    public synchronized boolean hasPending()   { return !dispatchQueue.isEmpty(); }
}
