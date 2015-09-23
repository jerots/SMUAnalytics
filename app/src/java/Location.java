/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ASUS-PC
 */
public class Location{

    private String locationId;
    private String semanticPlace;

    public Location(String locationId, String semanticPlace) {
        this.locationId = locationId;
        this.semanticPlace = semanticPlace;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getSemanticPlace() {
        return semanticPlace;
    }
}
