package model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a contractor or logistics provider who bids on service requests.
 *
 * <p>Demonstrates: Inheritance (extends User), Polymorphism,
 * Collections (Set&lt;String&gt; ensures each skill is unique — no duplicates).</p>
 */
public class ServiceProvider extends User {

    private Double      rating;   // Average star rating (1.0 – 5.0)
    private Set<String> skillSet; // Unique skill tags (e.g. "Crane Operation")

    /**
     * Creates a ServiceProvider account.
     *
     * @param userId        Unique provider ID
     * @param name          Full name
     * @param email         Email address
     * @param walletBalance Starting wallet balance
     * @param rating        Initial star rating; defaults to 5.0 if null
     */
    public ServiceProvider(Integer userId, String name, String email,
                           Double walletBalance, Double rating) {
        super(userId, name, email, "PROVIDER", walletBalance);
        this.rating   = (rating != null) ? rating : 5.0;
        this.skillSet = new HashSet<>();
    }

    public Double      getRating()          { return rating; }
    public void        setRating(Double r)  { this.rating = r; }
    public Set<String> getSkillSet()        { return skillSet; }

    /**
     * Adds a skill tag to this provider's profile.
     * HashSet silently ignores duplicate entries.
     *
     * @param skill Skill label to add (e.g. "Pallet Wrapping")
     */
    public void addSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty()) {
            this.skillSet.add(skill.trim());
        }
    }

    /**
     * Returns a formatted one-line provider summary.
     * Overrides the abstract method in User — demonstrating Polymorphism.
     */
    @Override
    public String getProfileSummary() {
        return String.format(
                "Provider Profile: %s (ID: %d) | Rating: %.1f* | Skills: %s",
                getName(), getUserId(), rating, skillSet);
    }
}
