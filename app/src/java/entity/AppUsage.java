package entity;

public class AppUsage {
    private String timestamp;
    private String macAddress;
    private int appId;

    public AppUsage(String timestamp, String macAddress, int appId) {
        this.timestamp = timestamp;
        this.macAddress = macAddress;
        this.appId = appId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getAppId() {
        return appId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }


    
    
    
}
