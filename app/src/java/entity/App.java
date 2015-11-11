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
public class App implements Comparable<App>{
    private int appId;
    private String appName;
    private String appCategory;
    
    public App(int appId, String appName, String appCategory) {
        this.appId = appId;
        this.appName = appName;
        this.appCategory = appCategory;
    }

    public int getAppId() {
        return appId;
    }

    public String getAppName() {
        return appName;
    }
    

    public String getAppCategory() {
        return appCategory;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

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

    @Override
    //NOT IMPLEMENTED
    public int compareTo(App o) {
        return appName.compareTo(o.appName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.appId;
        return hash;
    }
}
