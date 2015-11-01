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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

/**
 *
 * @author Boyofthefuture
 */
public class SocialActivenessController {
    public HashMap<String, Breakdown> generateAwarenessReport(String onlyDate, String macAddress, String errors){
        System.out.println("meme");
        //THIS METHOD is going to store first for the user all his apps and THEN check for social
        //DATE and MACADD have already been checked before
        //--------------------------------------------HERE ONWARDS IS THE ONLINE BREAKDOWN----------------------------------------------------------------
        AppUsageDAO aDao = new AppUsageDAO();
        //Retrieve the results to be passed back. The method ONLY gets the apps based on school. This method still has to sort and find the top-K.
        ArrayList<AppUsage> aList = aDao.getUserAppsForSocial(onlyDate, macAddress);
        Iterator<AppUsage> iter = aList.iterator();
        //Ordered by appId.
        HashMap<App, Long> storage = new HashMap<>();
        //This is for JSON. Breakdown used so can store either arraylist or string
        HashMap<String, Breakdown> jsonMap = new HashMap<>();
        //This is for the arraylist of JSON results
        ArrayList<HashMap<String, Breakdown>> jsonResults = new ArrayList<>();
        //This is for the Overall hashmap of results
        HashMap<String, Breakdown> overallMap = new HashMap<>();
        //The method below can replicate that of Top-K
        //Overall total 
        long overall = 0;
        //prev time
        long prevTime = -1;
        //This is the total time thus far for the same appId
        long total = 0;
        //Current appid being processed. Stores appId as it is unique and we cant compare apps
        App thatApp = null;
        //This is the diff of the timing
        long diff = 0;
        //Gets Ready the end of the period of interest
        Date date = Utility.parseOnlyDate(onlyDate);
        long endDateTime = date.getTime() + (60*60*24*1000); //Adds up till the end of the day
        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered.
        
        while(iter.hasNext()){
            AppUsage aUsage = iter.next();
            //Current App
            App app = aUsage.getApp();
            //Converts to date from string
            Date appDate = Utility.parseDate(aUsage.getTimestamp());
            //This takes the date (Date) inside the system and places it into a date object
            long time = appDate.getTime();
            //When prevTime is more than time, it signals that it is a new user.
            if(thatApp == null){
                //Instantiates for first time running.
                thatApp = app;
                prevTime = appDate.getTime();
            }
            //WE TAKE THE DIFFERENCE FIRST. Why? Because no matter what is the next app, we take the diff first then we will check whether to continue.
            
            //time difference between prevTime and time
            diff = time - prevTime;
            //If the difference is more than 120s, sets as 10s
            if(diff > 120000){
                diff = 10000;
            }
            total += diff;
            //This is a duplicate if it entered the if loop.
            prevTime = time;
            if(!app.equals(thatApp)){
                //This means it is not the first instance
                //Before reset, stores into HashMap the current data values. AppId is stored before time for retrieval
                //IMMEDIATELY REMOVES THOSE THAT ARE NOT SOCIAL.
                if(thatApp.getAppCategory().equals("Social")){
                    overall += total;
                    storage.put(thatApp, total);
                }
                //Now checks the treemap whether it exists this new appId
                total = 0;
                if(storage.containsKey(app)){
                    total = storage.get(app);
                }
                overall -= total; //Everytime it is retrieved, overall can minus to continue to keep track.
                //Resets total if it is a new appId. sets the prevTime immedaitely so that the diff is 0;
                thatApp = app;
            }
        }
        //This is for the last app.
        //Ensures that it is also the number of seconds from end of day
        if(thatApp != null){
            diff = endDateTime - prevTime;
            if(diff > 120000){
                diff = 10000; //Can just be 10000 because there is NOT a subsequent update and therefore assume 10000
            }
            total += diff;
            if(thatApp.getAppCategory().equals("Social")){
                overall += total;
                storage.put(thatApp, total);
            }
        }
        //Swapping is so that the top values can be on top. additionally, change them into percentages.
       //From here, starts to get the top few 
        DecimalFormat df = new DecimalFormat("#");
        ArrayList<Long> valuesArr = new ArrayList<Long>(storage.values());
        Collections.sort(valuesArr);
        for(int i = valuesArr.size() - 1; i >= 0 ; i+=0){//reverse checking
            Iterator<App> iterApp = storage.keySet().iterator();
            while(iterApp.hasNext() && i >= 0){
                App app = iterApp.next();
                long time = storage.get(app);
                if(time == valuesArr.get(i)){
                    //helps to round off the percentage
                    String strPercent = df.format((double) time/overall * 100);
                    jsonMap.put("app-name", new Breakdown(app.getAppName()));
                    jsonMap.put("percent", new Breakdown("" + Utility.parseInt(strPercent)));
                    jsonResults.add(jsonMap);
                    jsonMap = new HashMap<>();
                    i--;
                }
            }
        }
        //Final addition for overall for Part 1 of Social Category
        //Makes overall into seconds, then concats to a string to add to breakdown so that it can be stored. breakdown is used to simul arraylist and string
        overallMap.put("total-social-app-usage-duration", new Breakdown("" + (overall/1000)));
        overallMap.put("individual-social-app-usage", new Breakdown(jsonResults));
        
        //--------------------------------------------HERE ONWARDS IS THE PHYSICAL BREAKDOWN----------------------------------------------------------------
        //----------------------------------------PART I: GROUPING OF LOCATION AND TIME -- USER.--------------------------------------------------------------------
        prevTime = 0;
        //First, gets the User's locationUsage
        LocationUsageDAO luDao = new LocationUsageDAO();
        ArrayList<LocationUsage> locList = luDao.retrieveUserLocationUsage(onlyDate, macAddress);
        //This is where we introduce Activeness entity. Activeness keeps track of location, start and end time.        
        Iterator<LocationUsage> locIter = locList.iterator();
        //This keeps track of the startdate in millisecs for storing
        long startDateSecs = 0;
        //Instantiates location
        Location loc = null;
        //This is for the overall time in SIS
        overall = 0;
        //This hashmap is for the User
        HashMap<Location, ArrayList<Activeness>> userList = new HashMap<>(); //The arraylist will make sure the numbers are in order
        //Can reuse app variables because they are stored already
        while(locIter.hasNext()){
            LocationUsage lu = locIter.next();
            //Gets the date from the LocationUsage
            Date locDate = Utility.parseDate(lu.getTimestamp());
            Location l = lu.getLocation();
            //This takes the date (Date) inside the system and places it into a date object
            long time = locDate.getTime();
            //For first time instantiation
            if(loc == null){
                //Instantiates for first time running.
                loc = l;
                startDateSecs = time;
            }else{
                //Groups by locations:
                diff = time - prevTime;
                if(diff > 300000){
                    diff = 300000;
                }
                total += diff;
            }
            //If similar, dont need reinstantiate
            if(!loc.equals(l)){
                //Checks if the arraylist exists.
                ArrayList<Activeness> activeArr= new ArrayList<>();
                if(userList.containsKey(loc)){
                    activeArr = userList.get(loc);
                }
                activeArr.add(new Activeness(startDateSecs, (total + startDateSecs), macAddress, loc));
                userList.put(loc, activeArr);
                overall += total; //adds to overall time
                total = 0;
                loc = l;
                //Stores the start for the next location
                startDateSecs = time;
                //Don't retrieve activeness cause each activeness is unique
            }
            //Needed because of startDateSecs, so pushed here.
            prevTime = time;
        }
        
        if(loc != null){
            //Ensures that it is also the number of seconds from end of day
            diff = endDateTime - prevTime;
            if(diff > 300000){
                diff = 300000; //Can just be 300000 because there is NOT a subsequent update and therefore assume 300000
            }
            total += diff;
            //Checks if it exists already.
            ArrayList<Activeness> activeArr= new ArrayList<>();
            if(userList.containsKey(loc)){
                activeArr = userList.get(loc);
            }
            activeArr.add(new Activeness(startDateSecs, (total + startDateSecs), macAddress, loc));
            userList.put(loc, activeArr);
            overall += total; //adds to overall time
            System.out.println("haha");
            //--------------------------------------PART II: GROUPING OF LOCATION AND TIME -- INDIVIDUALS.------------------------------------------------------------
            //Do the same thing for each user again, however, this time, toss users out who dont fit the criteria.
            ArrayList<LocationUsage> luList = luDao.retrievePeopleExceptUserLocationUsage(onlyDate, macAddress);
            Iterator<LocationUsage> iterLoc = luList.iterator();
            //THIS IS THE FINAL ARRAYLIST FOR OVERLAP. Here tallies ALL the overlaps with the user
            ArrayList<Activeness> userOverlapList = new ArrayList<>();
            //This arraylist is to group by those items for a user
            ArrayList<Activeness> singleList = new ArrayList<>();
            //This list keeps track of the final list of overlaps between individuals. 
            loc = null;
            //keeps track of current individual
            String macAdd = null;
            total = 0;
            //Goes through the entire list by person. Arranged by person, macadd, and compares immediately to throw.
            while(iterLoc.hasNext()){
                LocationUsage lUsage = iterLoc.next();
                //Gets the date from the LocationUsage
                Date locDate = Utility.parseDate(lUsage.getTimestamp());
                Location l = lUsage.getLocation();
                //This takes the date (Date) inside the system and places it into a date object
                long time = locDate.getTime();
                //MacAddress tallying
                String currentMacAdd = lUsage.getMacAddress();

                //For first time instantiation and for new macaddresses
                if(loc == null){
                    //Instantiates for first time running.
                    loc = l;
                    macAdd = currentMacAdd;
                    startDateSecs = time;
                }else if(macAdd.equals(currentMacAdd)){
                    diff = time - prevTime;
                    if(time > 300000){
                        diff = 300000;
                    }
                    total += diff;
                    if(!loc.equals(l)){
                        //Creates a new activeness based on what has been calculated
                        Activeness ac = new Activeness(startDateSecs, (total + startDateSecs), macAddress, loc);
                        //Checks if the location is an overlap to see if it is worth to be stored.
                        overlapUser(userList, singleList, ac, loc);
                        //Resets for next location
                        total = 0;
                        loc = l;
                        //Stores the start for the next location
                        startDateSecs = time;
                    }
                //here checks for new user. Therefore, dont even bother checking if locations are similar
                }else{
                    diff = endDateTime - prevTime;
                    if(diff > 300000){
                        diff = 300000; //Can just be 300000 because there is NOT a subsequent update and therefore assume 300000
                    }
                    total += diff;
                    //Creates a new activeness based on what has been calculated
                    Activeness ac = new Activeness(startDateSecs, (total + startDateSecs), macAddress, loc);
                    //This method is below. it helps to tally all the overlaps of a single user. Once overlap is completed, we can start checking for overlap within the 
                    //userOverlapList
                    overlapUser(userList, singleList, ac, loc);
                    //This part here checks for overlap with the masterlist.
                    for(Activeness temp: singleList){
                        //The equals method checks for overlap, and therefore if there is an overlap, it will return.
                        Iterator<Activeness> iterA = userOverlapList.iterator();
                        while(iterA.hasNext()){
                            Activeness temporary = iterA.next();
                            if(temp.equals(temporary)){
                                temp = temp.combine(temporary);
                                iterA.remove();
                            }
                        }
                        userOverlapList.add(temp);
                    }
                    //Resets for next location IF it is the second situation that it enters
                    total = 0;
                    loc = l;
                    //Stores the start for the next location
                    startDateSecs = time;
                    macAdd = currentMacAdd;
                }
                //At the end of it, sets it to prevTime
                prevTime = time;
            }
            if(loc != null){
                //Do outside the while loop for the last addition
                diff = endDateTime - prevTime;
                if(diff > 300000){
                    diff = 300000; //Can just be 300000 because there is NOT a subsequent update and therefore assume 300000
                }
                total += diff;
                //Creates a new activeness based on what has been calculated
                Activeness ac = new Activeness(startDateSecs, (total + startDateSecs), macAddress, loc);
                //This method is below. it helps to tally all the overlaps of a single user. Once overlap is completed, we can start checking for overlap within the 
                //userOverlapList
                overlapUser(userList, singleList, ac, loc);
                //This part here checks for overlap with the masterlist.
                for(Activeness temp: singleList){
                    //The equals method checks for overlap, and therefore if there is an overlap, it will return.
                    Iterator<Activeness> iterA = userOverlapList.iterator();
                    while(iterA.hasNext()){
                        Activeness temporary = iterA.next();
                        if(temp.equals(temporary)){
                            temp = temp.combine(temporary);
                            iterA.remove();
                        }
                    }
                    userOverlapList.add(temp);
                }
            }
            //--------------------------------------PART III: PUTTING THE PUZZLE PIECES TOGETHER.------------------------------------------------------------
            //Looking at the userOverlapList, the entire list with overlaps have been accounted for. Adds to the arraylists/hashmaps for JSON
            long group = 0;
            for(Activeness active: userOverlapList){
                group += active.getTime();
                System.out.println(group);
            }
            System.out.println(overall);
            String groupPercent = df.format((double) group/overall * 100);
            System.out.println(((double) group)/overall);
            long soloPercent = 100 - Utility.parseInt(groupPercent);
            overallMap.put("total-time-spent-in-sis", new Breakdown("" + overall/1000));
            overallMap.put("group-percent", new Breakdown("" + groupPercent));
            overallMap.put("solo-percent", new Breakdown("" + soloPercent));
        }else{
            overallMap.put("total-time-spent-in-sis", new Breakdown("" + 0));
            overallMap.put("group-percent", new Breakdown("" + 0));
            overallMap.put("solo-percent", new Breakdown("" + 0));
        }
        
        return overallMap;
    }

        //This method will check whether there is an overlap between this user and the current user in process.
    public void overlapUser(HashMap<Location, ArrayList<Activeness>> userList, ArrayList<Activeness> singleList, Activeness ac, Location loc){
        if(userList.containsKey(loc)){
            ArrayList<Activeness> activeList = userList.get(loc);
            //Runs through to find if there is an overlap. These arraylists will be pretty small, if any. Deletes all data if they are not longer than 5 mins.
            for(Activeness active: activeList){
                //This portion returns the period of overlap as an activeness. Maccaddress is of the individual overlapping
                Activeness overlap = active.overlap(ac);
                if(overlap != null){
                    //PART II.1, before that we have to work backwards to check if the user has 5minutes in total for an activity of overlap.
                    //First part is to store if the end date matches the start date.
                    if(singleList.size() > 0){ //Makes sure that there is something inside
                        Activeness single = singleList.get(singleList.size() - 1);
                        //Always checking the last one.
                        if(!single.continuation(overlap)){
                            //If it is NOT a continuation, you will check the entire makeup of the previous set to check if it is 5 minutes. If not, delete.
                            //Reverse to check the last few if they make up 5 minutes, if not DELETE.
                            Collections.reverse(singleList);
                            //First iterator is for removing, 2nd iterator is for checking
                            Iterator<Activeness> iterA = singleList.iterator();
                            long totalTime = 0;
                            //Activeness is for the prev one to check continuation
                            Activeness activity = null;
                            //int i is for the number of things that have been gone through.
                            int i = 0;
                            for(Activeness a: singleList){
                                if(activity != null){
                                    //Check if it is still a continuation.
                                    if(!a.continuation(activity) || totalTime >= 300000){
                                        break;//This is to save time going through the whole loop
                                    }
                                }
                                totalTime += a.getTime();
                                i++;
                                activity = a;
                            }
                            if(totalTime < 300000){
                                while(i > 0){
                                    //Deletes all records that have no continuation.
                                    iterA.next();
                                    iterA.remove();
                                    i--;
                                }
                            }
                            Collections.reverse(singleList); //Reverses back the list
                        }
                    }
                    singleList.add(overlap);
                }
            }
        }
    }
}
