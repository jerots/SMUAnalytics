/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ASUS-PC
 */
public class AppUsage {
    private String timestamp;
    private String macAddress;
    private String appId;

    public AppUsage(String timeStap, String macAddress, String appId) {
        this.timestamp = timeStap;
        this.macAddress = macAddress;
        this.appId = appId;
    }

    public String getTimeStap() {
        return timestamp;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getAppId() {
        return appId;
    }
    
    
    
}
