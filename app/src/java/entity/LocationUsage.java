package entity;

import java.sql.Date;

public class LocationUsage {

    private Date timestamp;
    private String macAddress;//user obj
    private int locationId;//location obj

    public LocationUsage(Date timestamp, String macAddress, int locationId) {
        this.timestamp = timestamp;
        this.macAddress = macAddress;
        this.locationId = locationId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }


    
    
    
}
