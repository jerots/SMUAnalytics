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

    public ArrayList<HashMap<String, String>> getTopKApp(int topK, String school, String strDate, String endDate, String errors) {

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
        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered.
        while(iter.hasNext()){
            AppUsage aUsage = iter.next();
            //Converts to date from string
            Date appDate = Utility.parseDate(aUsage.getTimestamp());
            //This takes the date (Date) inside the system and places it into a date object
            long time = appDate.getTime();
            //When prevTime is more than time, it signals that it is a new user.
            if(appId <= 0 || prevTime > time){
                //Instantiates for first time running.
                appId = aUsage.getAppId();
                prevTime = appDate.getTime();
            }
            //WE TAKE THE DIFFERENCE FIRST. Why? Because no matter what is the next app, we take the diff first then we will check whether to continue.
            
            //time difference between prevTime and time
            long diff = time - prevTime;
            //If the difference is more than 120s, sets as 10s
            if(diff > 120000){
                diff = 10000;
            }
            total += diff;
            //This is a duplicate if it entered the if loop.
            prevTime = time;
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
                prevTime = appDate.getTime();
            }
            if(!iter.hasNext()){
                total += 10000;
                storage.put(appId, total);
            }
        }
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
        //Ordered by appId. This is to collect the apps that are currently being used by the user.
        HashMap<Integer, Long> storage = new HashMap<>();
        //Stores the app that is currently being tracked.
        Iterator<AppUsage> iter = aList.iterator();
        //prev time
        long prevTime = -1;
        //tracks the appId
        int appId = 0;
        //This is the total time thus far for the same appId
        long total = 0;
        //Current macaddress being processed
        String macAdd = null;
        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered. 
        while(iter.hasNext()){
            AppUsage aUsage = iter.next();
            //Converts to date from timestamp
            Date appDate = Utility.parseDate(aUsage.getTimestamp());
            //This takes the date (Date) inside the system and places it into a date object
            long time = appDate.getTime();
            //Instantiates when it is a new app and ensures that is is not a new app
            if(appId <= 0 || prevTime > time){
                //Instantiates for first time running.
                if(appId <= 0){
                    appId = aUsage.getAppId();
                    macAdd = aUsage.getMacAddress();
                }
                prevTime = appDate.getTime();
            }
            long diff = time - prevTime;
            //If the difference is more than 120s, sets as 10s
            if(diff > 120000){
                diff = 10000;
            }
            total += diff;
            prevTime = time;
            String currentMacAdd = aUsage.getMacAddress();
            if(aUsage.getAppId() != appId || !macAdd.equals(currentMacAdd)){
                //This means it is not the first instance
                //Before reset, stores into TreeSet the current data values. Time is stored before appId so that it can be sorted
                storage.put(appId, total);
                total = 0;
                //Now checks the treemap whether it exists this new appId. However, it is a new macAdd, then makes sure that macAdd is reset.
                if(currentMacAdd.equals(macAdd)){ //This is for the first condition pass. 
                    if(storage.containsKey(appId)){
                        total = storage.get(appId);
                    }
                    //Here starts pulling out the appId to make sure that they are of the right category
                }else{
                    Iterator<Integer> iterId = storage.keySet().iterator();
                    //This is to track userTotal
                    long userTotal = 0;
                    while(iterId.hasNext()){
                        int iterAppId = iterId.next();
                        if(link.get(iterAppId).equals(cat)){ //This means they are of the right category
                            userTotal += storage.get(iterAppId);
                        }
                    }
                    userTime.put(userTotal, macAdd);
                    macAdd = currentMacAdd;
                    //Resets the storage
                    storage = new HashMap<>();
                }
                //Resets total if it is a new appId. sets the prevTime immedaitely so that the diff is 0;
                appId = aUsage.getAppId();
                prevTime = appDate.getTime();
            }
            if(!iter.hasNext()){
                total += 10000;
                storage.put(appId, total);
                Iterator<Integer> iterId = storage.keySet().iterator();
                //This is to track userTotal
                long userTotal = 0;
                while(iterId.hasNext()){
                    int iterAppId = iterId.next();
                    if(link.get(iterAppId).equals(cat)){ //This means they are of the right category
                        userTotal += storage.get(iterAppId);
                    }
                }
                userTime.put(userTotal, macAdd);
            }
        }
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
    
        public ArrayList<HashMap<String, String>> getTopkSchool(String entry, String cat, String strDate, String endDate){
        AppUsageDAO aDao = new AppUsageDAO();
        //This hashmap stores the link between macadd and student name
        HashMap<String, String> schoolList = new HashMap<>();
        //Retrieve the results to be passed back
        ArrayList<AppUsage> aList= aDao.getSchoolsByCategory(schoolList, cat, strDate, endDate);
        //Stores the current time associated with the student. TreeMap to find the highest number.
        TreeMap<Long, String> schoolTime = new TreeMap<>();
        
        Iterator<AppUsage> iter = aList.iterator();
        
        //changes the String entry into an int
        int topK = Utility.parseInt(entry);
        
        //current time. Long as it is in millisecs
        long time = -1;
        //prev time
        long prevTime = -1;
        //time difference between prevTime and time
        long diff = -1;
        //This is the total time thus far for the same appId
        long total = -1;
        //Current appid being processed
        String macAdd = null;
        //School checker
        String school = null;
        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered.
        while(iter.hasNext()){
            AppUsage aUsage = iter.next();
            //Converts to date from timestamp
            Date appDate = Utility.parseDate(aUsage.getTimestamp());
            //If macAdd is null, that would mean that school is null too, and no need to check
            if(macAdd == null || !aUsage.getMacAddress().equals(macAdd)){
                //Checks if it is still the same school
                String email = schoolList.get(macAdd);
                String currentSchool = email.substring(email.indexOf("@"), email.indexOf("."));
                
                //This means that it is not the first instance
                if(macAdd != null && !currentSchool.equals(school)){
                    //Before reset, stores into TreeSet the current data values. Time is stored before macAdd so that it can be sorted
                    schoolTime.put(total, currentSchool);
                }
                //Resets total if it is a new appId. sets the prevTime immedaitely so that the diff is 0;
                macAdd = aUsage.getMacAddress();
                prevTime = appDate.getTime();
                //If it is still the same, still reinstantiate, in case that school is null. Not to slow down to put another if.
                school = currentSchool;
            }
            //This takes the date (Date) inside the system and places it into a date object
            time = appDate.getTime();
            diff = time - prevTime;
            //If the difference is more than 120s, sets as 10s
            if(diff > 120000){
                diff = 10000;
            }
            total += diff;
            //This is a duplicate if it entered the if loop.
            prevTime = time;
        }
        //Resets previous time
        prevTime = 0;
        //This one is to keep track of the keys (time)
        Iterator<Long> kIter = schoolTime.descendingMap().keySet().iterator();
        //Converts to iterator to go through values (user mac add)
        Iterator<String> vIter = schoolTime.descendingMap().values().iterator();
        //This keeps track of the rank
        int num = 0;
        //ArrayList to store the variables
        ArrayList<HashMap<String, String>> uList = null;
        //This final portion goes through the treeset and takes the biggest top-K
        while(vIter.hasNext() && topK >0){
            school = vIter.next();
            //Time now tracks the current time associated with the appid. Both iterators should have the same size
            time = kIter.next();

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
            kDetails.put("duration", String.valueOf(time));
            
            uList.add(kDetails);
            topK--;
        }
        return uList;
    }
}
