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
public class topkAppController {

    public ArrayList<HashMap<String, String>> getTopKApp(String entry, String school, String strDate, String endDate) {

        ArrayList<HashMap<String, String>> resultList = new ArrayList<>();
        AppUsageDAO aDao = new AppUsageDAO();

        TreeMap<Long, Integer> appTime = new TreeMap<>();

        HashMap<Integer, String> hMap = new HashMap<>();

        ArrayList<AppUsage> aList = aDao.getAppsBySchool(hMap, school, strDate, endDate);

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
        long total = 0;
        //Current appid being processed
        int appId = 0;
        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered.
        while (iter.hasNext()) {
            AppUsage aUsage = iter.next();
            //Converts to date from string
            Date appDate = Utility.parseDate(aUsage.getTimestamp());
            if (aUsage.getAppId() != appId) {
                //This means it is not the first instance
                if (appId != 0) {
                    //Before reset, stores into TreeSet the current data values. Time is stored before appId so that it can be sorted
                    appTime.put(total, appId);
                }
                //Resets total if it is a new appId. sets the prevTime immedaitely so that the diff is 0;
                appId = aUsage.getAppId();
                total = 0;
                prevTime = appDate.getTime();

            }
            //This takes the date (Date) inside the system and places it into a date object
            time = appDate.getTime();
            diff = time - prevTime;
            //If the difference is more than 120s, sets as 10s
            if (diff > 120000) {
                diff = 10000;
            }
            total += diff;
            prevTime = time;
        }

        prevTime = 0;

        Iterator<Long> kIter = appTime.descendingMap().keySet().iterator();

        Iterator<Integer> vIter = appTime.descendingMap().values().iterator();

        int num = 0;

        ArrayList<HashMap<String, String>> returnList = null;

        while (vIter.hasNext() && topK > 0) {
            appId = vIter.next();

            time = kIter.next();

            String appName = hMap.get(appId);
            System.out.println(appId + " " + time);
            if (time != prevTime) {

                prevTime = time;

                int size = returnList.size();

                num = size;
            }
            HashMap<String, String> kDetails = new HashMap<>();
            kDetails.put("rank", String.valueOf(num + 1));
            kDetails.put("app-name", appName);
            kDetails.put("duration", String.valueOf(time));

            returnList.add(kDetails);
            topK--;
        }
        return resultList;
    }

    public ArrayList<HashMap<String, String>> getTopKAppStudents(String entry, String cat, String strDate, String endDate) {
        AppUsageDAO aDao = new AppUsageDAO();
        //This hashmap stores the link between macadd and student name
        HashMap<String, String> link = new HashMap<>();
        //Retrieve the results to be passed back
        ArrayList<AppUsage> aList = aDao.getStudentsByCategory(link, cat, strDate, endDate);
        //Stores the current time associated with the student. We use a TreeMap to find the highest number.
        TreeMap<Long, String> userTime = new TreeMap<>();

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
        //Uses an iterator for the ArrayList for more accurate gets. This works as they are ordered.
        while (iter.hasNext()) {
            AppUsage aUsage = iter.next();
            //Converts to date from timestamp
            Date appDate = Utility.parseDate(aUsage.getTimestamp());
            if (macAdd == null || !aUsage.getMacAddress().equals(macAdd)) {
                //This means that it is not the first instance
                if (macAdd != null) {
                    //Before reset, stores into TreeSet the current data values. Time is stored before macAdd so that it can be sorted
                    userTime.put(total, macAdd);
                }
                //Resets total if it is a new appId. sets the prevTime immedaitely so that the diff is 0;
                macAdd = aUsage.getMacAddress();
                total = -1;
                prevTime = appDate.getTime();
            }
            //This takes the date (Date) inside the system and places it into a date object
            time = appDate.getTime();
            diff = time - prevTime;
            //If the difference is more than 120s, sets as 10s
            if (diff > 120000) {
                diff = 10000;
            }
            total += diff;
            //This is a duplicate if it entered the if loop.
            prevTime = time;
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
        ArrayList<HashMap<String, String>> uList = null;
        //This final portion goes through the treeset and takes the biggest top-K
        while (vIter.hasNext() && topK > 0) {
            macAdd = vIter.next();
            time = kIter.next();
            String name = link.get(macAdd);

            if (time != prevTime) {
                prevTime = time;
                int size = uList.size();
                num = size;
            }

            HashMap<String, String> kDetails = new HashMap<>();
            kDetails.put("rank", String.valueOf(num + 1));
            kDetails.put("name", name);
            kDetails.put("mac-address", macAdd);
            kDetails.put("duration", String.valueOf(time));

            uList.add(kDetails);
            topK--;
        }
        return uList;
    }
}
