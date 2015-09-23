/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ASUS-PC
 */
public class LocationUsage {

    private String timestamp;
    private String macAddress;
    private String locationId;

    public LocationUsage(String timestamp, String macAddress, String locationId) {
        this.timestamp = timestamp;
        this.macAddress = macAddress;
        this.locationId = locationId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getLocationId() {
        return locationId;
    }
    
    
    
}
