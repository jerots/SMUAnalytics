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
import java.util.TreeMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ASUS-PC
 */
public class SmartphoneOveruseController {

    public TreeMap<String, String> generateReport(User user, Date startDate, Date endDate) {
        TreeMap<String, String> result = new TreeMap<String, String>();

        //to store the overuse index value
        TreeMap<String, Integer> overuseIndex = new TreeMap<String, Integer>();

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
        System.out.println("AppUsageLIse size=" + appUsageList.size());
        ArrayList<AppUsage> gameList = new ArrayList<AppUsage>();
        ArrayList<AppUsage> generalList = new ArrayList<AppUsage>();
        TreeMap<Date, ArrayList<AppUsage>> generalMap = new TreeMap<Date, ArrayList<AppUsage>>();
        TreeMap<Date, ArrayList<AppUsage>> gameMap = new TreeMap<Date, ArrayList<AppUsage>>();
        App app = null;
        for (int i = 0; i < appUsageList.size(); i++) {
            System.out.println("22222222222222222222222222");
            int appId = appUsageList.get(i).getAppId();

            app = aDao.retrieveAppbyId(appId);
            System.out.println(appId);
            AppUsage au = appUsageList.get(i);

            //if games
            String category = app.getAppCategory().toLowerCase();
            System.out.println("category =====" + category);
            if (category.equals("games")) {
                Date date = appUsageList.get(i).getDate();

                //sort by day
                if (gameMap.containsKey(date)) {
                    System.out.println("444444444444444444444");
                    gameList = gameMap.get(date);
                    gameList.add(appUsageList.get(i));
                } else {
                    System.out.println("goes in herreee!");
                    gameList.add(appUsageList.get(i));
                    gameMap.put(date, gameList);
                    System.out.println("gameMapsizeFF=======" + gameMap.size());
                }
            } else {
                //

                System.out.println("555555MUAHAHAHAH555555555555");
                Date date = appUsageList.get(i).getDate();

                //sort by day
                if (generalMap.containsKey(date)) {
                    generalList = generalMap.get(date);
                    generalList.add(appUsageList.get(i));
                    System.out.println("GGGGGGGGGGGGGGGEEEEEEEEEEEEEEEZZZZZZZ");
                } else {
                    generalList.add(appUsageList.get(i));
                    generalMap.put(date, generalList);
                    System.out.println("HHHHHHHHHHHHHHEEEEEEEEEEEEEEERRRRRRRREEEEE");
                }
            }
        }
        TreeMap<String, Integer> overuseIndexMap = new TreeMap<String, Integer>();
        String dailyUsageIndex = "";
        if (generalMap.size() > 0) {
            double dailySmartphoneUsage = calculateAverage(generalMap);

            if (dailySmartphoneUsage >= 5) {
                System.out.println("77777777777777777777777777777");
                dailyUsageIndex = "Severe";

                int count = overuseIndexMap.get(dailyUsageIndex);
                overuseIndexMap.put(dailyUsageIndex, count + 1);
                result.put("usage", "" + dailyUsageIndex + "," + dailySmartphoneUsage);
            } else if (dailySmartphoneUsage < 3) {
                dailyUsageIndex = "Light";
                int count = overuseIndexMap.get(dailyUsageIndex);
                overuseIndexMap.put(dailyUsageIndex, count + 1);
                result.put("usage", "" + dailyUsageIndex + "," + dailySmartphoneUsage);
            } else {
                dailyUsageIndex = "Moderate";
                int count = overuseIndexMap.get(dailyUsageIndex);
                overuseIndexMap.put(dailyUsageIndex, count + 1);
                result.put("usage", "" + dailyUsageIndex + "," + dailySmartphoneUsage);
            }
        }
        if (gameMap.size() > 0) {
            System.out.println("gameMapsize = " + gameMap.size());
            long dailyGamingDuration = calculateAverage(gameMap);
            System.out.println("YEOW LEONG SAYS COME OUT" +dailyGamingDuration);
            String gamingUsageIndex = "";
            if (dailyGamingDuration >= 2) {
                gamingUsageIndex = "Severe";
                int count = overuseIndexMap.get(gamingUsageIndex);
                overuseIndexMap.put(gamingUsageIndex, count + 1);
                result.put("gaming", "" + gamingUsageIndex + "," + dailyGamingDuration);
            } else if (dailyGamingDuration < 1) {
                gamingUsageIndex = "Light";
                int count = overuseIndexMap.get(gamingUsageIndex);
                overuseIndexMap.put(gamingUsageIndex, count + 1);
                System.out.print(+overuseIndexMap.size());
                result.put("gaming", "" + gamingUsageIndex + "," + dailyGamingDuration);
            } else {
                gamingUsageIndex = "Moderate";
                int count = overuseIndexMap.get(gamingUsageIndex);
                overuseIndexMap.put(gamingUsageIndex, count + 1);
                result.put("gaming", "" + gamingUsageIndex + "," + dailyGamingDuration);
            }
        }

        System.out.println("888888888888888888888888888");
        //3rd one here
        //combination
        String overallIndex = "";
        if (overuseIndexMap.get("Severe") >= 1) {
            overallIndex = "Overusing";
        } else if (overuseIndexMap.get("Light") == 3) {
            overallIndex = "Normal";
        } else {
            overallIndex = "ToBeCautious";
        }
        result.put("overuseindex", overallIndex);
        System.out.println("999999999999999999999999999999999999");
        return result;
    }

    public long calculateAverage(TreeMap<Date, ArrayList<AppUsage>> dayMap) {
        ArrayList<AppUsage> dayList = new ArrayList<AppUsage>();
        System.out.println("dayMap size============= " + dayMap.size());
        
        Iterator<ArrayList<AppUsage>> dayIter = dayMap.values().iterator();
        long totalUsageTime = 0;
        System.out.println("HEYHEYHEYHEY222222222 ");
        while (dayIter.hasNext()) {
            dayList = dayIter.next();
            //after we get each list of appUsage sort by date
            //compare time for each day
            long diffSeconds = 0;
            System.out.println("HEYHEYHEYHEY 33333333333");
            //for each list
            for (int i = 0; i < dayList.size(); i++) {
                System.out.println("HEYHEYHEYHEY44444 ");
                //***get the duration
                if (i != dayList.size() - 1) {
                    AppUsage firstAppUsage = dayList.get(i);
                    AppUsage secondAppUsage = dayList.get(i + 1);
                    //time
                    System.out.println("HEYHEYHEYHEY 55555555555555 ");
                    long diffInMilliSec = secondAppUsage.getDate().getTime() - firstAppUsage.getDate().getTime();
                    diffSeconds = diffInMilliSec / 1000;
                    System.out.println("HEYHEYHEYHEY 6666666666666666666666");
                } //for the last record, need use 00:00:00 to minus
                else {
                    System.out.println("HEYHEYHEYHEY 7777777777777777777 ");
                    AppUsage firstAppUsage = dayList.get(i);
                    //this is the date we are tracking now
                    Date date = firstAppUsage.getDate();
                    long lastTime = firstAppUsage.getDate().getTime();
                    
                   // int second = date.getHours() * 3600 + date.getMinutes() * 60 + date.getSeconds();
                    long defaultSecond = 2400000;
                    diffSeconds = defaultSecond - second;
                    System.out.println("HEYHEYHEYHEY 8888888888888888");
                    
                    
                }
                if (diffSeconds > 120) {
                    diffSeconds = 10;
                    System.out.println("HEYHEYHEYHEY 9999999999999999999999");
                }
                System.out.println("BYEBEYBEYEBYE 111111111111111111111");
                totalUsageTime += diffSeconds;
                 System.out.println("YLYLYL TOTAL USAGE TIME" +totalUsageTime);
            }
        }
        System.out.println("BYEBEYBEYEBYE 2222222222222222222");
        //10
        long averageUsageTimeInHours = (long) (totalUsageTime / 3600) / (dayMap.size());
        System.out.println("ylylylyylyl");
        return averageUsageTimeInHours;
    }

}
