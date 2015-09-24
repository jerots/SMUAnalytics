package entity;

import java.sql.Date;

public class AppUsage {
    private Date timestamp;
    private String macAddress;
    private int appId;

    public AppUsage(Date timestamp, String macAddress, int appId) {
        this.timestamp = timestamp;
        this.macAddress = macAddress;
        this.appId = appId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getAppId() {
        return appId;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }


    
    
    
}
