/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.AppDAO;
import dao.AppUsageDAO;
import entity.App;
import entity.AppUsage;
import entity.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ASUS-PC
 */
public class SmartphoneOveruseController {

    public HashMap<String, String> generateReport(User user, Date startDate, Date endDate) {
        HashMap<String, String> result = new HashMap<String, String>();

        //to store the overuse index value
        HashMap<String, Integer> overuseIndex = new HashMap<String, Integer>();

        //add the different indexes
        overuseIndex.put("severeUsage", 5);
        overuseIndex.put("severeGaming", 3);
        overuseIndex.put("severeFrequency", 0);
        overuseIndex.put("moderateUsage", 2);
        overuseIndex.put("moderateGaming", 1);
        overuseIndex.put("moderateFrequency", 0);
        overuseIndex.put("lightUsage", 5);
        overuseIndex.put("lightGaming", 3);
        overuseIndex.put("lightFrequency", 0);

        AppUsageDAO auDAO = new AppUsageDAO();

        ArrayList<AppUsage> appUsageList = auDAO.retrieveByUser(user.getMacAddress(), startDate, endDate);
        AppDAO aDao = new AppDAO();

        ArrayList<AppUsage> gameList = new ArrayList<AppUsage>();
        ArrayList<AppUsage> generalList = new ArrayList<AppUsage>();
        HashMap<Date, ArrayList<AppUsage>> generalMap = new HashMap<Date, ArrayList<AppUsage>>();
        HashMap<Date, ArrayList<AppUsage>> gameMap = new HashMap<Date, ArrayList<AppUsage>>();
        App app = null;
        for (int i = 0; i < appUsageList.size(); i++) {
            int appId = appUsageList.get(i).getAppId();
            app = aDao.retrieveAppbyId(appId);
            AppUsage au = appUsageList.get(i);
            //if games
            String category = app.getAppCategory().toLowerCase();
            if (category.equals("games")) {
                Date date = appUsageList.get(i).getDate();

                //sort by day
                if (gameMap.containsKey(date)) {
                    gameList = gameMap.get(date);
                    gameList.add(appUsageList.get(i));
                } else {
                    gameList.add(appUsageList.get(i));
                    gameMap.put(date, gameList);
                }
            } else {
                //
                Date date = appUsageList.get(i).getDate();

                //sort by day
                if (generalMap.containsKey(date)) {
                    generalList = generalMap.get(date);
                    generalList.add(appUsageList.get(i));
                } else {
                    generalList.add(appUsageList.get(i));
                    generalMap.put(date, generalList);
                }
            }
            int dailySmartphoneUsage = calculateAverage(generalMap);
            int dailyGamingDuration = calculateAverage(gameMap);
        }
        return result;
    }

    public int calculateAverage(HashMap<Date, ArrayList<AppUsage>> dayMap) {
        ArrayList<AppUsage> dayList = new ArrayList<AppUsage>();

        Iterator<ArrayList<AppUsage>> dayIter = dayMap.values().iterator();
        long totalUsageTime = 0;

        while (dayIter.hasNext()) {
            dayList = dayIter.next();
            //after we get each list of appUsage sort by date
            //compare time for each day
            long diffSeconds = 0;

            //for each list
            for (int i = 0; i < dayList.size(); i++) {

                //***get the duration
                if (i != dayList.size() - 1) {
                    AppUsage firstAppUsage = dayList.get(i);
                    AppUsage secondAppUsage = dayList.get(i + 1);
                    //time
                    long diffInMilliSec = secondAppUsage.getDate().getTime() - firstAppUsage.getDate().getTime();
                    diffSeconds = diffInMilliSec / 1000;
                } //for the last record, need use 00:00:00 to minus
                else {
                    AppUsage firstAppUsage = dayList.get(i);
                    Date date = firstAppUsage.getDate();
                    int second = date.getHours() * 3600 + date.getMinutes() * 60 + date.getSeconds();
                    int defaultSecond = 23 * 3600 + 59 * 60 + 60;
                    diffSeconds = defaultSecond - second;
                }
                if (diffSeconds > 120) {
                    diffSeconds = 10;
                }
                totalUsageTime += diffSeconds;
            }
        }

        int averageUsageTimeInHours = (int) (totalUsageTime / 3600) / (dayMap.size());
        return averageUsageTimeInHours;
    }

}
