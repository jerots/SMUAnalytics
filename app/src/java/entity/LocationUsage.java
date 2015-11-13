package entity;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AppUsage represents a User's usage of the Locations in the Application
 */
public class LocationUsage {

    private String timestamp;
    private String macAddress;//user
    private int locationId;//location
    private Location location;

    /**
     * Creates a LocationUsage object with the specified timestamp,
     * macAddress,locationId
     *
     * @param timestamp The time of use of the App
     * @param macAddress The mac address of the user
     * @param locationId The locationId which the user is at
     */
    public LocationUsage(String timestamp, String macAddress, int locationId) {
        this.timestamp = timestamp;
        this.macAddress = macAddress;
        this.locationId = locationId;
        location = null;
    }

    /**
     * Creates an LocationUsage object with the specified timestamp,
     * macAddress,locationId
     *
     * @param timestamp The time of use of the App
     * @param macAddress The mac address of the user
     * @param location The location Object
     */
    public LocationUsage(String timestamp, String macAddress, Location location) {
        this.timestamp = timestamp;
        this.macAddress = macAddress;
        this.location = location;
    }

    /**
     * Get the Location Object
     *
     * @return the Location object
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get the timestamp of the usage
     *
     * @return the timestamp of the usage
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Get the macAddress of the usage
     *
     * @return the macAddress of the usage
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Get the location id of the usage
     *
     * @return the locationId of the usage
     */
    public int getLocationId() {
        return locationId;
    }

    /**
     * Set the timestamp of the app
     *
     * @param timestamp The timestamp of the app
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Set the mac address of the user using the app
     *
     * @param macAddress The macAddress of the user using the app
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * Set the location id of the location
     *
     * @param locationId The locationId of the location
     */
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    /**
     * Get the timestamp in date of the usage
     *
     * @return the timestamp in date of the usage
     */
    public Date getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(timestamp, new ParsePosition(0));
        return date;
    }

}
