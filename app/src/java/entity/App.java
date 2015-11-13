package entity;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ASUS-PC
 */
/**
 * App represents the available app in the Application
 */
public class App implements Comparable<App> {

    private int appId;
    private String appName;
    private String appCategory;

    /**
     * Creates an App object with the specified appId, name, appCategory
     *
     * @param appId The app id of the app
     * @param appName The name of app
     * @param appCategory The category the app belongs to
     */
    public App(int appId, String appName, String appCategory) {
        this.appId = appId;
        this.appName = appName;
        this.appCategory = appCategory;
    }

    /**
     * Get the App id of the app
     *
     * @return the appId
     */
    public int getAppId() {
        return appId;
    }

    /**
     * Get the App name of the app
     *
     * @return the appName
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Get the App category of the app
     *
     * @return the appCategory
     */
    public String getAppCategory() {
        return appCategory;
    }

    /**
     * Set the app id
     *
     * @param appId The appId of the app
     */
    public void setAppId(int appId) {
        this.appId = appId;
    }

    /**
     * Set the app name
     *
     * @param appName of the app
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * Set the app category
     *
     * @param appCategory of the app
     */
    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
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
        final App other = (App) obj;
        return this.appId == other.appId;
    }

    /**
     * Compare if both objects are equal
     *
     * @param o The object to compare
     * @return -1 if Integer less than the argument, 0 if Integer equals to the argument, 1 if Integer more than the argument
     */
    @Override
    //NOT IMPLEMENTED
    public int compareTo(App o) {
        return appName.compareTo(o.appName);
    }

    /**
     * Retrieve the Hashcode
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.appId;
        return hash;
    }
}
