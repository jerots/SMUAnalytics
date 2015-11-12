package controller;

import dao.LocationDAO;
import dao.LocationUsageDAO;
import entity.Location;
import entity.LocationUsage;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * HeatmapController controls all actions related to Heatmap functionality
 */
public class HeatmapController {

    /**
     * Retrieves a TreeMap object for the location usages in the semantic place
     * @param datetime The datetime input by user
     * @param floor The floor input by the user
     * @return A TreeMap object that contains the semantic place and its usages. 
     */

    public TreeMap<String, ArrayList<LocationUsage>> generateHeatmap(Date datetime, String floor) {

        LocationUsageDAO luDAO = new LocationUsageDAO();

        //for each location, count unique users
        TreeMap<String, ArrayList<LocationUsage>> result = new TreeMap<String, ArrayList<LocationUsage>>();

        LocationDAO locDAO = new LocationDAO();

        //retrieve all the floors
        ArrayList<String> floorLocationList = locDAO.retrieve(floor);
        HashMap<String, LocationUsage> luMap = luDAO.retrieveByFloor(datetime, floor);
        //get all locationUsage on this floor, latest timing
        //for each location in the floor
        for (int i = 0; i < floorLocationList.size(); i++) {
            String loc = floorLocationList.get(i);

            //instantiate arraylist
            ArrayList<LocationUsage> locList = new ArrayList<LocationUsage>();
            result.put(loc, locList);

            Iterator<String> keyIter = luMap.keySet().iterator();
            while (keyIter.hasNext()) {
                String macAddress = keyIter.next();
                LocationUsage lu = luMap.get(macAddress);
                Location location = lu.getLocation();

                if (loc.equals(location.getSemanticPlace())) {
                    locList.add(lu);
                }
            }
        }

        return result;
    }

}
