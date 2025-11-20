package devOps.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LibraryEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    private String environment;

    private double accessibilityScore;

    private double meteorologyScore;
}