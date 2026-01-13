package devOps.models;

import jakarta.persistence.*;

@Entity
public class LibraryEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    private String name;
    private String address;

    private double latitude;
    private double longitude;

    @Column(length = 2000)
    private String heuresOuverture;

    // Getters et Setters manuels
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getHeuresOuverture() { return heuresOuverture; }
    public void setHeuresOuverture(String heuresOuverture) { this.heuresOuverture = heuresOuverture; }
}
