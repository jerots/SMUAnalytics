package entity;

import java.util.Objects;

/**
 * Location represents the available location in the Application
 */
public class Location implements Comparable<Location> {

    private int locationId;
    private String semanticPlace;
    private Location location;

    /**
     * Creates a Location object with the specified locationId, semanticPlace
     *
     * @param locationId The location id of the location
     * @param semanticPlace The name of the location
     */
    public Location(int locationId, String semanticPlace) {
        this.locationId = locationId;
        this.semanticPlace = semanticPlace;
    }

    /**
     * Creates a Location object with the specified location object,
     * semanticPlace
     *
     * @param location The location object
     * @param semanticPlace The name of the location
     */
    public Location(Location location, String semanticPlace) {
        this.location = location;
        this.semanticPlace = semanticPlace;
    }

    /**
     * Set the location of the Location
     *
     * @param location The location object
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Get the location
     *
     * @return the Location Object
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get the location id of the Location
     *
     * @return the locationId of the location
     */
    public int getLocationId() {
        return locationId;
    }

    /**
     * Get the semantic place of the Location
     *
     * @return the semanticPlace of the location
     */
    public String getSemanticPlace() {
        return semanticPlace;
    }

    /**
     * Set the locationId
     *
     * @param locationId The id of the specific location
     */
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    /**
     * Set the Semantic Place
     *
     * @param semanticPlace The name of the specific location
     */
    public void setSemanticPlace(String semanticPlace) {
        this.semanticPlace = semanticPlace;
    }

    /**
     * Retrieve the Hashcode
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.semanticPlace);
        return hash;
    }

    /**
     * Compare if both objects are equal
     *
     * @param obj The object to compare
     * @return true if equals, false if otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        return this.semanticPlace.equals(other.semanticPlace);
    }

    /**
     * Compare two locations
     *
     * @return -1 if Integer is less than the argument, 0 if Integer is equals
     * to argument and 1 if Integer is more than argument
     */
    //NOT IMPLEMENTED
    @Override
    public int compareTo(Location o) {
        return this.compareTo(o);
    }
}
