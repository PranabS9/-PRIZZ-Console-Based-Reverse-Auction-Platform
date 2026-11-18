package model;

/**
 * Abstract base class for all users in the PRIZZ system.
 *
 * <p>Demonstrates: Abstraction, Encapsulation, Inheritance (parent class),
 * Wrapper classes (Integer, Double).</p>
 *
 * <b>Viva tip:</b> This class cannot be instantiated directly — it forces
 * every subclass (Customer, ServiceProvider) to implement getProfileSummary().
 */
public abstract class User {

    private Integer userId;
    private String  name;
    private String  email;
    private String  role;          // "CUSTOMER" or "PROVIDER"
    private Double  walletBalance; // Current account balance in dollars

    /**
     * Constructs a User with the given details.
     *
     * @param userId        Unique numeric identifier
     * @param name          Full display name
     * @param email         Contact email address
     * @param role          Role label (CUSTOMER / PROVIDER)
     * @param walletBalance Starting wallet balance; defaults to 0.0 if null
     */
    public User(Integer userId, String name, String email, String role, Double walletBalance) {
        this.userId        = userId;
        this.name          = name;
        this.email         = email;
        this.role          = role;
        this.walletBalance = (walletBalance != null) ? walletBalance : 0.0;
    }

    /**
     * Returns a one-line summary of this user's profile.
     * Each subclass implements this differently — that is Polymorphism.
     *
     * @return formatted profile string
     */
    public abstract String getProfileSummary();

    // ---- Getters and Setters ----

    public Integer getUserId()                     { return userId; }
    public void    setUserId(Integer userId)        { this.userId = userId; }

    public String  getName()                       { return name; }
    public void    setName(String name)            { this.name = name; }

    public String  getEmail()                      { return email; }
    public void    setEmail(String email)          { this.email = email; }

    public String  getRole()                       { return role; }
    public void    setRole(String role)            { this.role = role; }

    public Double  getWalletBalance()              { return walletBalance; }
    public void    setWalletBalance(Double b)      { this.walletBalance = b; }

    @Override
    public String toString() {
        return String.format("User[ID=%d, Name='%s', Role='%s', Balance=$%.2f]",
                userId, name, role, walletBalance);
    }
}
