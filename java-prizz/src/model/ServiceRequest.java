package model;

import java.util.Date;

/**
 * Represents a job posting created by a customer.
 *
 * <p>Demonstrates: Encapsulation (private fields with getters/setters),
 * Enum usage (RequestStatus), Wrapper classes (Integer, Double).</p>
 */
public class ServiceRequest {

    private Integer       requestId;
    private Integer       customerId;
    private String        title;
    private String        description;
    private Double        budget;
    private RequestStatus status;    // Current lifecycle state
    private Date          createdAt;

    /**
     * Creates a new service request in OPEN state.
     *
     * @param requestId   Unique request identifier
     * @param customerId  ID of the customer who posted this
     * @param title       Short job title
     * @param description Detailed job description
     * @param budget      Maximum budget the customer is willing to pay
     */
    public ServiceRequest(Integer requestId, Integer customerId,
                          String title, String description, Double budget) {
        this.requestId   = requestId;
        this.customerId  = customerId;
        this.title       = title;
        this.description = description;
        this.budget      = budget;
        this.status      = RequestStatus.OPEN;  // Default state on creation
        this.createdAt   = new Date();
    }

    // ---- Getters and Setters ----

    public Integer       getRequestId()                        { return requestId; }
    public void          setRequestId(Integer id)              { this.requestId = id; }

    public Integer       getCustomerId()                       { return customerId; }
    public void          setCustomerId(Integer id)             { this.customerId = id; }

    public String        getTitle()                            { return title; }
    public void          setTitle(String title)                { this.title = title; }

    public String        getDescription()                      { return description; }
    public void          setDescription(String desc)           { this.description = desc; }

    public Double        getBudget()                           { return budget; }
    public void          setBudget(Double budget)              { this.budget = budget; }

    public RequestStatus getStatus()                           { return status; }
    public void          setStatus(RequestStatus status)       { this.status = status; }

    public Date          getCreatedAt()                        { return createdAt; }
    public void          setCreatedAt(Date date)               { this.createdAt = date; }

    @Override
    public String toString() {
        return String.format("Request #%d: '%s' | Budget: $%.2f | Status: %s",
                requestId, title, budget, status);
    }
}
