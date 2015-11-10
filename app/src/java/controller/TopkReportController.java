package controller;

import dao.AppUsageDAO;
import dao.Utility;
import entity.App;
import entity.AppUsage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * TopkReportController controls all actions related TopkReport related functionality
 */
public class TopkReportController {

    /**
     * Retrieves a ArrayList<HashMap<String, String>> object for the data that
     * belongs contains the Top-k most used apps(Given a school)
     *
     * @param topK The top-k apps for a given duration
     * @param school The top-k apps for a given school
     * @param strDate The top-k apps from the given start date
     * @param endDate The top-k apps until the given end date
     * @param errors The errors generated from the top-k apps
     * @return A sorted ArrayList of Hashmap objects that belongs contains the
     * Top-k most used apps(Given a school)
     */
    public ArrayList<HashMap<String, String>> getTopkApp(int topK, String school, Date strDate, Date endDate, String errors) {
        AppUsageDAO auDAO = new AppUsageDAO();

        /*Return an arraylist of AppUsage of interest (i.e. macAddress and timestamp related to the report), sorted by macAddress then timestamp */
        ArrayList<AppUsage> aList = auDAO.getAppsBySchool(school, strDate, endDate);
        Iterator<AppUsage> iter = aList.iterator();
        //Ordered by appId.
        HashMap<App, Long> storage = new HashMap<>();

        /* initialisation of computation data */
        /*preset time*/
        long prevTime = -1;

        /* total time, for the same app!*/
        long total = 0;
        /* difference between first and second app*/
        long diff = 0;

        /* initialisation of objects */
        /* Stores the app for comparison */
        App app = null;

        /* Stores the MacAdd for comparison */
        String macAdd = null;

        /* set end date to millisecs - until 0000 next day */
        long endDateTime = endDate.getTime();

        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered.
        while (iter.hasNext()) {
            AppUsage aUsage = iter.next();
            /* Converts to date from string */
            Date appDate = Utility.parseDate(aUsage.getTimestamp());
            //This takes the date (Date) inside the system and places it into a date object

            long time = appDate.getTime();
            String currentMacAdd = aUsage.getMacAddress();
            App currentApp = aUsage.getApp();
            //Compares to check if it is a new user
            if (app == null) {
                //Instantiates for first time running.
                app = currentApp;
                prevTime = appDate.getTime();
                macAdd = currentMacAdd;
            } else if (macAdd.equals(currentMacAdd)) {
                //WE TAKE THE DIFFERENCE FIRST. Why? Because no matter what is the next app, we take the diff first then we will check whether to continue.
                //time difference between prevTime and time
                diff = time - prevTime;
                //If the difference is more than 120s, sets as 10s
                if (diff > 120000) {
                    diff = 10000;
                }
                total += diff;
            } else { //For mac add not similar
                diff = endDateTime - prevTime;
                if (diff > 120000) {
                    diff = 10000;
                }
                total += diff;
                macAdd = currentMacAdd;
            }
            //This if loop is at the end since we are calculating by macAdd
            if (!currentApp.equals(app)) {
                //This means it is not the first instance
                //Before reset, stores into TreeSet the current data values. Time is stored before appId so that it can be sorted
                storage.put(app, total);
                //Now checks the treemap whether it exists this new appId
                total = 0;
                if (storage.containsKey(currentApp)) {
                    total = storage.get(currentApp);
                }
                //Resets total if it is a new appId. sets the prevTime immedaitely so that the diff is 0;
                app = currentApp;
            }
            prevTime = time;
        }
        if (macAdd != null) {
            //Do outside the while loop for the final addition
            diff = endDateTime - prevTime;
            if (diff > 120000) {
                diff = 10000;
            }
            total += diff;
            storage.put(app, total);
        }
        //ArrayList to store the variables and return
        ArrayList<HashMap<String, String>> returnList = new ArrayList<>();
        HashMap<String, String> kDetails = new HashMap<>();
        //From here, starts to get the top few 
        ArrayList<Long> valuesArr = new ArrayList<Long>(storage.values());
        Collections.sort(valuesArr);
        int kFound = 1;
        while (topK >= kFound && kFound <= valuesArr.size()) {
            Iterator<App> intIter = storage.keySet().iterator();
            while (intIter.hasNext()) {
                app = intIter.next();
                long time = storage.get(app);
                if (time == valuesArr.get(valuesArr.size() - kFound)) {
                    kDetails.put("rank", String.valueOf(kFound));
                    kDetails.put("app-name", app.getAppName());
                    kDetails.put("duration", String.valueOf(time / 1000));
                    returnList.add(kDetails);
                    kDetails = new HashMap<>();
                }
            }
            kFound = (returnList.size() + 1);
        }
        if (returnList.size() < topK) {
            errors += "not enough data";
        } else if (returnList.isEmpty()) {
            errors += "there is no data";
        }

        return returnList;
    }

    /**
     * Retrieves a ArrayList<HashMap<String, String>> object for the data that
     * belongs contains the Top-k student(Given a app category)
     *
     * @param topK The top-k students for a given duration
     * @param category The top-k students for a given category
     * @param strDate The top-k students from the given start date
     * @param endDate The top-k students until the given end date
     * @param errors The errors generated from the top-k students
     * @return A sorted ArrayList of Hashmap objects that belongs contains the
     * Top-k most used students(Given a app category)
     */
    public ArrayList<HashMap<String, String>> getTopkStudents(int topK, String cat, Date strDate, Date endDate, String errors) {
        AppUsageDAO aDao = new AppUsageDAO();
        //This hashmap stores the link between macadd and student name
        HashMap<String, String> linkMac = new HashMap<>();
        //Retrieve the results to be passed back 
        ArrayList<AppUsage> aList = aDao.getStudentsByCategory(linkMac, strDate, endDate);
        //Stores the current time associated with the student. TreeMap to find the highest number.
        TreeMap<String, Long> userTime = new TreeMap<>();
        //Stores the app that is currently being tracked.
        Iterator<AppUsage> iter = aList.iterator();
        //prev time
        long prevTime = -1;
        //tracks the appId for the final add
        App app = null;
        //This is the diff of the timing
        long diff = 0;
        //Current macaddress being processed
        String macAdd = null;
        //Tracks the total of the current user
        long userTotal = 0;
        //Gets Ready the end of the date for the period of interest
        long endDateTime = endDate.getTime();
        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered. 
        while (iter.hasNext()) {
            AppUsage aUsage = iter.next();
            //Converts to date from timestamp
            Date appDate = Utility.parseDate(aUsage.getTimestamp());
            //This takes the date (Date) inside the system and places it into a date object
            long time = appDate.getTime();
            String currentMacAdd = aUsage.getMacAddress();

            //Instantiates for first time running.
            if (macAdd == null) {
                macAdd = currentMacAdd;
                app = aUsage.getApp();
                //Can skip all the rest since its the first line
            } else if (macAdd.equals(currentMacAdd)) {
                //Accounts for normal practices. This is only for macAdd that equals. For mac add that don't, minus end of day
                diff = time - prevTime;
                //If the difference is more than 120s, sets as 10s
                if (diff > 120000) {
                    diff = 10000;
                }
                if (app.getAppCategory().toLowerCase().equals(cat)) { //This means they are of the right category. Removes those that are not.
                    userTotal += diff;
                }
            } else {
                diff = endDateTime - prevTime;
                //If the difference is more than 120s, sets as 10s
                if (diff > 120000) {
                    diff = 10000;
                }
                //Dont need to check if Appid equates. Waste of logic space. Auto stores for user already.
                //Before reset, stores into TreeSet the current data values. 
                if (app.getAppCategory().toLowerCase().equals(cat)) { //This means they are of the right category. Removes those that are not.
                    userTotal += diff;
                }
                //Places into a treemap to sort out
                userTime.put(macAdd, userTotal);
                macAdd = currentMacAdd;
                userTotal = 0;
            }
            app = aUsage.getApp();
            prevTime = time;
        }
        if (macAdd != null) {
            //Places outside so dont have to keep checking. Method for last item
            //Ensures that it is also the number of seconds from end of day
            diff = endDateTime - prevTime;
            if (diff > 120000) {
                diff = 10000; //Can just be 10000 because there is NOT a subsequent update and therefore assume 10000
            }
            if (app.getAppCategory().toLowerCase().equals(cat)) { //This means they are of the right category. Removes those that are not.
                userTotal += diff;
            }
            userTime.put(macAdd, userTotal);
        }
        //ArrayList to store the variables and return
        ArrayList<HashMap<String, String>> returnList = new ArrayList<>();
        HashMap<String, String> kDetails = new HashMap<>();
        //From here, starts to get the top few 
        ArrayList<Long> valuesArr = new ArrayList<Long>(userTime.values());
        Collections.sort(valuesArr);
        int kFound = 1;
        while (topK >= kFound && kFound <= valuesArr.size()) {
            Iterator<String> userIter = userTime.keySet().iterator();
            while (userIter.hasNext()) {
                macAdd = userIter.next();
                long time = userTime.get(macAdd);
                if (time == valuesArr.get(valuesArr.size() - kFound)) {
                    String name = linkMac.get(macAdd);
                    kDetails.put("rank", String.valueOf(kFound));
                    kDetails.put("name", name);
                    kDetails.put("mac-address", macAdd);
                    kDetails.put("duration", String.valueOf(time / 1000));
                    returnList.add(kDetails);
                    kDetails = new HashMap<>();
                }
            }
            kFound = (returnList.size() + 1);
        }
        if (returnList.size() < topK) {
            errors += "not enough data";
        } else if (returnList.isEmpty()) {
            errors += "there is no data";
        }
        System.out.println(returnList.size());
        return returnList;
    }

        /**
     * Retrieves a ArrayList<HashMap<String, String>> object for the data that
     * belongs contains the Top-k most school(Given a app category)
     *
     * @param topK The top-k school for a given duration
     * @param category The top-k school for a given category
     * @param strDate The top-k school from the given start date
     * @param endDate The top-k school until the given end date
     * @param errors The errors generated from the top-k school
     * @return A sorted ArrayList of Hashmap objects that belongs contains the
     * Top-k most used school(Given a app category)
     */
    public ArrayList<HashMap<String, String>> getTopkSchool(int topK, String cat, Date strDate, Date endDate, String errors) {
        AppUsageDAO aDao = new AppUsageDAO();
        //This hashmap stores the link between macadd and email
        HashMap<String, String> schoolList = new HashMap<>();
        //Retrieve the results to be passed back
        ArrayList<AppUsage> aList = aDao.getSchoolsByCategory(schoolList, strDate, endDate);
        //Stores the current time associated with the student. TreeMap to find the highest number.
        TreeMap<String, Long> schoolTime = new TreeMap<>();
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
        App app = null;
        //This keeps track of the school total
        long schoolTotal = 0;
        //Gets Ready the end of the date for the period of interest
        long endDateTime = endDate.getTime();
        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered. 
        while (iter.hasNext()) {
            AppUsage aUsage = iter.next();
            //Converts to date from timestamp
            Date appDate = Utility.parseDate(aUsage.getTimestamp());
            //This takes the date (Date) inside the system and places it into a date object
            long time = appDate.getTime();
            //Gets school from mac Address
            String currentMacAdd = aUsage.getMacAddress();
            String currentSchool = schoolList.get(currentMacAdd);

            //Instantiates when it is a new app and ensures that is is not a new app. Checks for a new person as well.
            if (macAdd == null) {
                //This is only for the first time to instantiate school.
                school = currentSchool;
                macAdd = currentMacAdd;
                app = aUsage.getApp();
            } else if (currentMacAdd.equals(macAdd)) {
                diff = time - prevTime;
                //If the difference is more than 120s, sets as 10s
                if (diff > 120000) {
                    diff = 10000;
                }
                if (app.getAppCategory().toLowerCase().equals(cat)) { //This means they are of the right category. Removes those that are not.
                    schoolTotal += diff;
                }
            } else {
                diff = endDateTime - prevTime;
                //If the difference is more than 120s, sets as 10s
                if (diff > 120000) {
                    diff = 10000;
                }
                if (app.getAppCategory().toLowerCase().equals(cat)) { //This means they are of the right category. Removes those that are not.
                    schoolTotal += diff;
                }
                //This can only occur when the individual changes
                if (!currentSchool.equals(school)) {
                    //Places the school total for tracking
                    if (schoolTotal != 0) {
                        schoolTime.put(school, schoolTotal);
                    }
                    school = currentSchool;
                    schoolTotal = 0;
                }
                macAdd = currentMacAdd;
            }
            app = aUsage.getApp();
            prevTime = time;
        }
        if (macAdd != null) {
            //handles the last instance
            //Ensures that it is also the number of seconds from end of day
            diff = endDateTime - prevTime;
            if (diff > 120000) {
                diff = 10000; //Can just be 10000 because there is NOT a subsequent update and therefore assume 10000
            }
            if (app.getAppCategory().toLowerCase().equals(cat)) { //This means they are of the right category
                schoolTotal += diff;
            }
            if (schoolTotal != 0) {
                schoolTime.put(school, schoolTotal);
            }
        }
        //ArrayList to store the variables and return
        ArrayList<HashMap<String, String>> returnList = new ArrayList<>();
        HashMap<String, String> kDetails = new HashMap<>();
        //From here, starts to get the top few 
        ArrayList<Long> valuesArr = new ArrayList<Long>(schoolTime.values());
        Collections.sort(valuesArr);
        int kFound = 1;
        while (topK >= kFound && kFound <= valuesArr.size()) {
            Iterator<String> schIter = schoolTime.keySet().iterator();
            while (schIter.hasNext()) {
                school = schIter.next();
                long time = schoolTime.get(school);
                if (time == valuesArr.get(valuesArr.size() - kFound)) {
                    kDetails.put("rank", String.valueOf(kFound));
                    kDetails.put("school", school);
                    kDetails.put("duration", String.valueOf(time / 1000));
                    returnList.add(kDetails);
                    kDetails = new HashMap<>();
                }
            }
            kFound = (returnList.size() + 1);
        }
        if (returnList.size() < topK) {
            errors += "not enough data";
        } else if (returnList.isEmpty()) {
            errors += "there is no data";
        }
        System.out.println(returnList.size());
        return returnList;
    }
}
