package entity;

public class LocationUsage {

    private String timestamp;
    private String macAddress;//user obj
    private int locationId;//location obj

    public LocationUsage(String timestamp, String macAddress, int locationId) {
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

    public int getLocationId() {
        return locationId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }


    
    
    
}
