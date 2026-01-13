package devOps.responses;

public class LibraryResponseDTO {

    private String name;
    private String address;
    private String heuresOuverture;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getHeuresOuverture() {
        return heuresOuverture;
    }
    public void setHeuresOuverture(String heuresOuverture) {
        this.heuresOuverture = heuresOuverture;
    }
}
