package entity;

import dao.Utility;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * AppUsage represents a User's usage of the Application
 */
public class AppUsage implements Comparable<AppUsage> {

    private String timestamp;
    private String macAddress;
    private int appId;
    private App app;

    /**
     * Creates an AppUsage object with the specified timestamp, macAddress,appId
     *
     * @param timestamp The time of use of the App
     * @param macAddress The mac address of the user
     * @param appId The appId which the user is using
     */
    public AppUsage(String timestamp, String macAddress, int appId) {
        this.timestamp = timestamp;
        this.macAddress = macAddress;
        this.appId = appId;
    }

    /**
     * Creates an AppUsage object with the specified timestamp, macAddress,appId
     *
     * @param timestamp The time of use of the App
     * @param macAddress The mac address of the user
     * @param appId The id of the app
     * @param app The App Object
     */
    public AppUsage(String timestamp, String macAddress, int appId, App app) {
        this.timestamp = timestamp;
        this.macAddress = macAddress;
        this.appId = appId;
        this.app = app;
    }

    /**
     * Set the App Object
     *
     * @param app the App object
     */
    public void setApp(App app) {
        this.app = app;
    }

    /**
     * Get the App Object
     *
     * @return the App object
     */
    public App getApp() {
        return app;
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
     * Get the timestamp in date of the usage
     *
     * @return the timestamp in date of the usage
     */
    public Date getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(timestamp, new ParsePosition(0));
        return date;
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
     * Get the app id of the usage
     *
     * @return the appId of the usage
     */
    public int getAppId() {
        return appId;
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
     * Set the app id of the app
     *
     * @param appId The appId of the app
     */
    public void setAppId(int appId) {
        this.appId = appId;
    }

    /**
     * Compare if both objects are equal
     *
     * @param o The object to compare
     * @return -1 if Integer less than the argument, 0 if Integer equals to the argument, 1 if Integer more than the argument
     */
    @Override
    public int compareTo(AppUsage o) {
        if (Utility.parseDate(timestamp).after(Utility.parseDate(o.timestamp))) {
            return -1;
        } else if (Utility.parseDate(timestamp).before(Utility.parseDate(o.timestamp))) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Retrieve the Hashcode
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.app);
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
        final AppUsage other = (AppUsage) obj;
        if (!Objects.equals(this.app, other.app)) {
            return false;
        }
        return true;
    }

}
