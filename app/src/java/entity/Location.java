package entity;

public class Location implements Comparable<Location>{

    private int locationId;
    private String semanticPlace;
    private Location location;

    public Location(int locationId, String semanticPlace) {
        this.locationId = locationId;
        this.semanticPlace = semanticPlace;
    }
    
    public Location(Location location, String semanticPlace) {
        this.location = location;
        this.semanticPlace = semanticPlace;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + this.locationId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        return this.locationId == other.locationId;
    }
    
    //NOT IMPLEMENTED
    @Override
    public int compareTo(Location o) {
        return this.compareTo(o);
    } 
}
