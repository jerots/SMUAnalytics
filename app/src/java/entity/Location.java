package entity;

public class Location {

    private int locationId;
    private String semanticPlace;

    public Location(int locationId, String semanticPlace) {
        this.locationId = locationId;
        this.semanticPlace = semanticPlace;
    }

    public int getLocationId() {
        return locationId;
    }

    public String getSemanticPlace() {
        return semanticPlace;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public void setSemanticPlace(String semanticPlace) {
        this.semanticPlace = semanticPlace;
    }

}
