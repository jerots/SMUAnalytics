package controller;

import dao.AppUsageDAO;
import dao.Utility;
import entity.AppUsage;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ASUS-PC
 */
public class TopkController {
    //This method gets the top few Apps based on AppUsage time given a school, and their rank.
    public ArrayList<HashMap<String, String>> getTopkApp(int topK, String school, String strDate, String endDate, String errors){
        AppUsageDAO aDao = new AppUsageDAO();
        //Stores the current time associated with the appid. TreeMap to find the highest number.
        TreeMap<Long, Integer> appTime = new TreeMap<>();
        //This hashmap is for appId/appName associate (appId is the primary key so must do this)
        HashMap<Integer, String> hMap = new HashMap<>();
        //Retrieve the results to be passed back. The method ONLY gets the apps based on school. This method still has to sort and find the top-K.
        ArrayList<AppUsage> aList = aDao.getAppsBySchool(hMap, school, strDate, endDate);
        Iterator<AppUsage> iter = aList.iterator();
        //Ordered by appId.
        HashMap<Integer, Long> storage = new HashMap<>();
        
        //prev time
        long prevTime = -1;
        //This is the total time thus far for the same appId
        long total = 0;
        //Current appid being processed
        int appId = 0;
        //This is the diff of the timing
        long diff = 0;
        //Stores the MacAdd for comparison
        String macAdd = null;
        //Gets Ready the end of the date for the period of interest
        Date date = Utility.parseOnlyDate(endDate);
        long endDateTime = date.getTime() + (60*60*24*1000); //This is the amount of millisecs in a day. You want to include the entire last day
        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered.
        while(iter.hasNext()){
            AppUsage aUsage = iter.next();
            //Converts to date from string
            Date appDate = Utility.parseDate(aUsage.getTimestamp());
            //This takes the date (Date) inside the system and places it into a date object
            long time = appDate.getTime();
            String currentMacAdd = aUsage.getMacAddress();
            //Compares to check if it is a new user
            if(appId <= 0){
                //Instantiates for first time running.
                appId = aUsage.getAppId();
                prevTime = appDate.getTime();
                macAdd = currentMacAdd;
            }else if(macAdd.equals(currentMacAdd)){
                //WE TAKE THE DIFFERENCE FIRST. Why? Because no matter what is the next app, we take the diff first then we will check whether to continue.
                //time difference between prevTime and time
                diff = time - prevTime;
                //If the difference is more than 120s, sets as 10s
                if(diff > 120000){
                    diff = 10000;
                }
                total += diff;
            }else{ //For mac add not similar
                diff = endDateTime - prevTime;
                if(diff > 120000){
                    diff = 10000;
                }
                total += diff;
                macAdd = currentMacAdd;
            }
            //This if loop is at the end since we are calculating by macAdd
            if(aUsage.getAppId() != appId){
                //This means it is not the first instance
                //Before reset, stores into TreeSet the current data values. Time is stored before appId so that it can be sorted
                storage.put(appId, total);
                //Now checks the treemap whether it exists this new appId
                total = 0;
                if(storage.containsKey(appId)){
                    total = storage.get(appId);
                }
                //Resets total if it is a new appId. sets the prevTime immedaitely so that the diff is 0;
                appId = aUsage.getAppId();
            }
            prevTime = time;
        }
        //Do outside the while loop for the final addition
        diff = endDateTime - prevTime;
        if(diff > 120000){
            diff = 10000;
        }
        total += diff;
        storage.put(appId, total);
        //Finally, changes the treeset over. Method for this in utility. They are together in the same place no matter how you iterate.
        Iterator<Integer> swapKey = storage.keySet().iterator();
        Iterator<Long> swapValue = storage.values().iterator();
        while(swapKey.hasNext()){
            appTime.put(swapValue.next(), swapKey.next());
        }
        //Resets previous time
        prevTime = 0;
        //This one is to keep track of the keys (time)
        Iterator<Long> kIter = appTime.descendingMap().keySet().iterator();
        //Converts to iterator to go through values
        Iterator<Integer> vIter = appTime.descendingMap().values().iterator();
        //This keeps track of the rank
        int num = 0;
        //ArrayList to store the variables and return
        ArrayList<HashMap<String, String>> returnList = new ArrayList<>();
        //This final portion goes through the treeset and takes the biggest top-K
        while(vIter.hasNext() && topK > 0){
            appId = vIter.next();
            //Time now tracks the current time associated with the appid. Both iterators should have the same size
            long time = kIter.next();
            //This will immedaitely go to the appId/appName hashmap to retrieve the appname associated
            String appName = hMap.get(appId);
            if(time != prevTime){
                //prevTime now tracks to compare
                prevTime = time;
                //Gets the size of the prev arraylist
                int size = returnList.size();
                //Adds current size to num
                num = size;
            }
            //Stashes all the information into a hashmap, for JSON
            HashMap<String, String> kDetails = new HashMap<>();
            kDetails.put("rank", String.valueOf(num + 1));
            kDetails.put("app-name", appName);
            kDetails.put("duration", String.valueOf(time/1000));
            
            returnList.add(kDetails);
            topK--;
        }
        if(returnList.size() < topK){
            errors += "not enough data";
        }else if(returnList.isEmpty()){
            errors += "there is no data";
        }
        return returnList;
    }
    //This method gets the students with most usage based on AppUsage time given an AppCategory.
    public ArrayList<HashMap<String, String>> getTopkStudents(int topK, String cat, String strDate, String endDate, String errors){
        AppUsageDAO aDao = new AppUsageDAO();
        //This hashmap stores the link between appid and appcategory
        HashMap<Integer, String> link = new HashMap<>();
        //This hashmap stores the link between macadd and student name
        HashMap<String, String> linkMac = new HashMap<>();
        //Retrieve the results to be passed back 
        ArrayList<AppUsage> aList= aDao.getStudentsByCategory(link, linkMac, strDate, endDate);
        //Stores the current time associated with the student. TreeMap to find the highest number.
        TreeMap<Long, String> userTime = new TreeMap<>();
        //Stores the app that is currently being tracked.
        Iterator<AppUsage> iter = aList.iterator();
        //prev time
        long prevTime = -1;
        //tracks the appId for the final add
        int appId = 0;
        //This is the diff of the timing
        long diff = 0;
        //Current macaddress being processed
        String macAdd = null;
        //Tracks the total of the current user
        long userTotal = 0;
        //Gets Ready the end of the date for the period of interest
        Date date = Utility.parseOnlyDate(endDate);
        long endDateTime = date.getTime() + (60*60*24*1000);
        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered. 
        while(iter.hasNext()){
            AppUsage aUsage = iter.next();
            //Converts to date from timestamp
            Date appDate = Utility.parseDate(aUsage.getTimestamp());
            //This takes the date (Date) inside the system and places it into a date object
            long time = appDate.getTime();
            String currentMacAdd = aUsage.getMacAddress();
            appId = aUsage.getAppId();
            //Instantiates for first time running.
            if(macAdd == null){
                macAdd = currentMacAdd;
                //Can skip all the rest since its the first line
            }else if(macAdd.equals(currentMacAdd)){
                //Accounts for normal practices. This is only for macAdd that equals. For mac add that don't, minus end of day
                diff = time - prevTime;
                //If the difference is more than 120s, sets as 10s
                if(diff > 120000){
                    diff = 10000;
                }
                if(link.get(appId).equals(cat)){ //This means they are of the right category. Removes those that are not.
                    userTotal += diff;
                }
            }else{
                diff = endDateTime - prevTime;
                //If the difference is more than 120s, sets as 10s
                if(diff > 120000){
                    diff = 10000;
                }
                //Dont need to check if Appid equates. Waste of logic space. Auto stores for user already.
                //Before reset, stores into TreeSet the current data values. 
                if(link.get(appId).equals(cat)){ //This means they are of the right category. Removes those that are not.
                    userTotal += diff;
                }
                //Places into a treemap to sort out
                userTime.put(userTotal, macAdd);
                macAdd = currentMacAdd;
                userTotal = 0;
            }
            prevTime = time;
        }
        //Places outside so dont have to keep checking. Method for last item
        //Ensures that it is also the number of seconds from end of day
        diff = endDateTime - prevTime;
        if(diff > 120000){
            diff = 10000; //Can just be 10000 because there is NOT a subsequent update and therefore assume 10000
        }
        if(link.get(appId).equals(cat)){ //This means they are of the right category. Removes those that are not.
            userTotal += diff;
        }
        userTime.put(userTotal, macAdd);
        //Resets previous time
        prevTime = 0;
        //This one is to keep track of the keys (time)
        Iterator<Long> kIter = userTime.descendingMap().keySet().iterator();
        //Converts to iterator to go through values (user mac add)
        Iterator<String> vIter = userTime.descendingMap().values().iterator();
        //This keeps track of the rank
        int num = 0;
        //ArrayList to store the variables and return
        ArrayList<HashMap<String, String>> uList = new ArrayList<>();
        //This final portion goes through the treeset and takes the biggest top-K
        while(vIter.hasNext() && topK >0){
            macAdd = vIter.next();
            //Time now tracks the current time associated with the appid. Both iterators should have the same size
            long time = kIter.next();
            //This will immedaitely go to the appId/App hashmap to retrieve the appname associated
            String name = linkMac.get(macAdd);
            if(time != prevTime){
                //prevTime now tracks to compare
                prevTime = time;
                //Gets the size of the prev arraylist
                int size = uList.size();
                //Adds current size to num
                num = size;
            }
            //Stashes all the information into a hashmap, for JSON
            HashMap<String, String> kDetails = new HashMap<>();
            kDetails.put("rank", String.valueOf(num + 1));
            kDetails.put("name", name);
            kDetails.put("mac-address", macAdd);
            kDetails.put("duration", String.valueOf(time/1000));
            uList.add(kDetails);
            topK--;
        }
        if(uList.size() < topK){
            errors += "not enough data";
        }else if(uList.isEmpty()){
            errors += "there is no data";
        }
        return uList;
    }
    
    public ArrayList<HashMap<String, String>> getTopkSchool(int topK, String cat, String strDate, String endDate, String errors){
        AppUsageDAO aDao = new AppUsageDAO();
        //This hashmap stores the link between appId and appCat
        HashMap<Integer, String> appCat = new HashMap<>();
        //This hashmap stores the link between macadd and email
        HashMap<String, String> schoolList = new HashMap<>();
        //Retrieve the results to be passed back
        ArrayList<AppUsage> aList= aDao.getSchoolsByCategory(appCat, schoolList, strDate, endDate);
        //Stores the current time associated with the student. TreeMap to find the highest number.
        TreeMap<Long, String> schoolTime = new TreeMap<>();
        //Stores the app that is currently being tracked.
        Iterator<AppUsage> iter = aList.iterator();
       
        //prev time
        long prevTime = -1;
        //This is the diff of the timing
        long diff = 0;
        //Stores macadd to count end of the day
        String macAdd = null;
        //School checker
        String school = null;
        //Purpose of appId is for the final addition.
        int appId = 0;
        //This keeps track of the school total
        long schoolTotal = 0;
        //Gets Ready the end of the date for the period of interest
        Date date = Utility.parseOnlyDate(endDate);
        long endDateTime = date.getTime() + (60*60*24*1000);
        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered. 
        while(iter.hasNext()){
            AppUsage aUsage = iter.next();
            //Converts to date from timestamp
            Date appDate = Utility.parseDate(aUsage.getTimestamp());
            //This takes the date (Date) inside the system and places it into a date object
            long time = appDate.getTime();
            //Gets school from mac Address
            String currentMacAdd = aUsage.getMacAddress();
            String currentSchool = schoolList.get(currentMacAdd);
            appId = aUsage.getAppId();
            //Instantiates when it is a new app and ensures that is is not a new app. Checks for a new person as well.
            if(macAdd == null){
                //This is only for the first time to instantiate school.
                school = currentSchool;
                macAdd = currentMacAdd;
            }else if(currentMacAdd.equals(macAdd)){
                diff = time - prevTime;
                //If the difference is more than 120s, sets as 10s
                if(diff > 120000){
                    diff = 10000;
                }
                if(appCat.get(appId).equals(cat)){ //This means they are of the right category. Removes those that are not.
                    schoolTotal += diff;
                }
            }else{
                diff = endDateTime - prevTime;
                //If the difference is more than 120s, sets as 10s
                if(diff > 120000){
                    diff = 10000;
                }
                if(appCat.get(appId).equals(cat)){ //This means they are of the right category. Removes those that are not.
                    schoolTotal += diff;
                }
                //This can only occur when the individual changes
                if(!currentSchool.equals(school)){
                    //Places the school total for tracking
                    schoolTime.put(schoolTotal, school);
                    school = currentSchool;
                    schoolTotal = 0;
                }
                macAdd = currentMacAdd;
            }
            prevTime = time;
        }
        //handles the last instance
        //Ensures that it is also the number of seconds from end of day
        diff = endDateTime - prevTime;
        if(diff > 120000){
            diff = 10000; //Can just be 10000 because there is NOT a subsequent update and therefore assume 10000
        }
        if(appCat.get(appId).equals(cat)){ //This means they are of the right category
            schoolTotal += diff;
        }
        schoolTime.put(schoolTotal, school);
        
        //Resets previous time
        prevTime = 0;
        //This one is to keep track of the keys (time)
        Iterator<Long> kIter = schoolTime.descendingMap().keySet().iterator();
        //Converts to iterator to go through values (user mac add)
        Iterator<String> vIter = schoolTime.descendingMap().values().iterator();
        //This keeps track of the rank
        int num = 0;
        //ArrayList to store the variables
        ArrayList<HashMap<String, String>> uList = new ArrayList<>();
        //This final portion goes through the treeset and takes the biggest top-K
        while(vIter.hasNext() && topK >0){
            school = vIter.next();
            //Time now tracks the current time associated with the appid. Both iterators should have the same size
            long time = kIter.next();

            if(time != prevTime){
                //prevTime now tracks to compare
                prevTime = time;
                //Gets the size of the prev arraylist
                int size = uList.size();
                //Adds current size to num
                num = size;
            }
            //Stashes all the information into a hashmap, for JSON. School is used here cause it is UNIQUE.
            HashMap<String, String> kDetails = new HashMap<>();
            kDetails.put("rank", String.valueOf(num + 1));
            kDetails.put("school", school);
            kDetails.put("duration", String.valueOf(time/1000));
            
            uList.add(kDetails);
            topK--;
        }
        if(uList.size() < topK){
            errors += "not enough data";
        }else if(uList.isEmpty()){
            errors += "there is no data";
        }
        return uList;
    }
}
