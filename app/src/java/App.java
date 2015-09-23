/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ASUS-PC
 */
public class App {

    private String appId;
    private String appName;
    private String appCategory;

    public App(String appId, String appName, String appCategory) {
        this.appId = appId;
        this.appName = appName;
        this.appCategory = appCategory;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppCategory() {
        return appCategory;
    }
    
    
    
    
}
