/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.AppUsageDAO;
import dao.LocationUsageDAO;
import dao.Utility;
import entity.Activeness;
import entity.App;
import entity.AppUsage;
import entity.Breakdown;
import entity.Location;
import entity.LocationUsage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

/**
 *
 * @author Boyofthefuture
 */
public class SocialActiveness {
    public HashMap<String, Breakdown> generateAwarenessReport(String onlyDate, String macAddress){
//        //THIS METHOD is going to store first for the user all his apps and THEN check for social
//        //DATE and MACADD have already been checked before
//        //--------------------------------------------HERE ONWARDS IS THE ONLINE BREAKDOWN----------------------------------------------------------------
//        AppUsageDAO aDao = new AppUsageDAO();
//        //Stores the current time associated with the appid. TreeMap to find the highest number.
//        TreeMap<Integer, String> appTime = new TreeMap<>();
//        //Retrieve the results to be passed back. The method ONLY gets the apps based on school. This method still has to sort and find the top-K.
//        ArrayList<AppUsage> aList = aDao.getUserAppsForSocial(onlyDate, macAddress);
//        Iterator<AppUsage> iter = aList.iterator();
//        //Ordered by appId.
//        HashMap<App, Long> storage = new HashMap<>();
//        //This is for JSON. Breakdown used so can store either arraylist or string
//        HashMap<String, Breakdown> jsonMap = new HashMap<>();
//        //This is for the arraylist of JSON results
//        ArrayList<HashMap<String, Breakdown>> jsonResults = new ArrayList<>();
//        //This is for the Overall hashmap of results
//        HashMap<String, Breakdown> overallMap = new HashMap<>();
//        
//        //The method below can replicate that of Top-K
//        //Overall total 
//        long overall = 0;
//        //prev time
//        long prevTime = -1;
//        //This is the total time thus far for the same appId
//        long total = 0;
//        //Current appid being processed. Stores appId as it is unique and we cant compare apps
//        App thatApp = null;
//        //This is the diff of the timing
//        long diff = 0;
//        //Gets Ready the end of the period of interest
//        Date date = Utility.parseOnlyDate(onlyDate);
//        long endDateTime = date.getTime() + (60*60*24*1000); //Adds up till the end of the day
//        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered.
//        while(iter.hasNext()){
//            AppUsage aUsage = iter.next();
//            //Current App
//            App app = aUsage.getApp();
//            //Converts to date from string
//            Date appDate = Utility.parseDate(aUsage.getTimestamp());
//            //This takes the date (Date) inside the system and places it into a date object
//            long time = appDate.getTime();
//            //When prevTime is more than time, it signals that it is a new user.
//            if(thatApp == null){
//                //Instantiates for first time running.
//                thatApp = app;
//                prevTime = appDate.getTime();
//            }
//            
//            //WE TAKE THE DIFFERENCE FIRST. Why? Because no matter what is the next app, we take the diff first then we will check whether to continue.
//            
//            //time difference between prevTime and time
//            diff = time - prevTime;
//            //If the difference is more than 120s, sets as 10s
//            if(diff > 120000){
//                diff = 10000;
//            }
//            total += diff;
//            //This is a duplicate if it entered the if loop.
//            prevTime = time;
//            if(!app.equals(thatApp)){
//                //This means it is not the first instance
//                //Before reset, stores into HashMap the current data values. AppId is stored before time for retrieval
//                //IMMEDIATELY REMOVES THOSE THAT ARE NOT SOCIAL.
//                if(thatApp.getAppCategory().equals("Social")){
//                    overall += total;
//                    storage.put(thatApp, total);
//                }
//                //Now checks the treemap whether it exists this new appId
//                total = 0;
//                if(storage.containsKey(app)){
//                    total = storage.get(app);
//                }
//                overall -= total; //Everytime it is retrieved, overall can minus to continue to keep track.
//                //Resets total if it is a new appId. sets the prevTime immedaitely so that the diff is 0;
//                thatApp = app;
//            }
//            //This is for the last app.
//            if(!iter.hasNext()){
//                //Ensures that it is also the number of seconds from end of day
//                diff = endDateTime - prevTime;
//                if(diff > 10000){
//                    diff = 10000; //Can just be 10000 because there is NOT a subsequent update and therefore assume 10000
//                }
//                total += diff;
//                storage.put(app, total);
//            }
//        }
//        //Finally, changes the treeset over. Method for this in utility. They are together in the same place no matter how you iterate.
//        //Swapping is so that the top values can be on top. additionally, change them into percentages.
//        Iterator<App> swapKey = storage.keySet().iterator();
//        Iterator<Long> swapValue = storage.values().iterator();
//        while(swapKey.hasNext()){
//            //While swapping, adds the total time to overall
//            total = swapValue.next();
//            //helps to round off the percentage
//            DecimalFormat df = new DecimalFormat("#");
//            //Percentage: 
//            String strPercent = df.format((double) total/overall * 100);
//            int percent = Utility.parseInt(strPercent);
//            //THE NEXT PART IS TO GET THE APPNAME
//            String name = swapKey.next().getAppName();
//            appTime.put(percent, name);
//        }
//        //After it has been sorted, take out again and start to store for JSON format
//        Iterator<Integer> jsonPercent = appTime.keySet().iterator();
//        Iterator<String> jsonName = appTime.values().iterator();
//        
//        while(jsonPercent.hasNext()){
//            jsonMap.put("app-name", new Breakdown(jsonName.next()));
//            jsonMap.put("percent", new Breakdown("" + jsonPercent.next()));
//            jsonResults.add(jsonMap);
//        }
//        
//        //Final addition for overall for Part 1 of Social Category
//        //Makes overall into seconds, then concats to a string to add to breakdown so that it can be stored. breakdown is used to simul arraylist and string
//        overallMap.put("total-social-app-usage-duration", new Breakdown("" + (overall/1000)));
//        overallMap.put("individual-social-app-usage", new Breakdown(jsonResults));
//        
//        //--------------------------------------------HERE ONWARDS IS THE PHYSICAL BREAKDOWN----------------------------------------------------------------
//        //First, gets the User's locationUsage
//        LocationUsageDAO luDao = new LocationUsageDAO();
//        ArrayList<LocationUsage> locList = luDao.retrieveUserLocationUsage(onlyDate, macAddress);
//        //PART I: GROUPING OF LOCATION AND TIME -- USER.
//        //This is where we introduce Activeness entity. Activeness keeps track of location, start and end time.        
//        Iterator<LocationUsage> locIter = locList.iterator();
//        //This keeps track of the startdate in millisecs for storing
//        long startDateSecs = 0;
//        //Instantiates location
//        Location loc = null;
//        //This is for the overall time in SIS
//        overall = 0;
//        //This hashmap is for the User
//        HashMap<Location, Activeness> userList = new HashMap<>();
//        //Can reuse app variables because they are stored already
//        while(locIter.hasNext()){
//            LocationUsage lu = locIter.next();
//            //Gets the date from the LocationUsage
//            Date locDate = Utility.parseDate(lu.getTimestamp());
//            Location l = lu.getLocation();
//            //This takes the date (Date) inside the system and places it into a date object
//            long time = locDate.getTime();
//            
//            //For first time instantiation
//            if(loc == null){
//                //Instantiates for first time running.
//                loc = l;
//                prevTime = time;
//                startDateSecs = time;
//            }
//            //Groups by locations:
//            diff = time - prevTime;
//            if(diff > 300000){
//                diff = 300000;
//            }
//            total += diff;
//            //If similar, dont need reinstantiate
//            if(!loc.equals(l)){
//                userList.put(loc, new Activeness(startDateSecs, total, macAddress, loc));
//                total = 0;
//                loc = l;
//                //Stores the start for the next location
//                startDateSecs = time;
//                //Don't retrieve activeness cause each activeness is unique
//            }
//            //Needed because of startDateSecs, so pushed here.
//            prevTime = time;
//            if(!iter.hasNext()){
//                //Ensures that it is also the number of seconds from end of day
//                diff = endDateTime - prevTime;
//                if(diff > 300000){
//                    diff = 300000; //Can just be 300000 because there is NOT a subsequent update and therefore assume 300000
//                }
//                total += diff;
//                userList.put(loc, new Activeness(startDateSecs, total, macAddress, loc));
//            }
//        }
        
        //PART II: GROUPING OF LOCATION AND TIME -- INDIVIDUALS.
        //NOTE: no location/semantic place is needed in Activeness as UserList has it already.
        
        return null;
    }
}
