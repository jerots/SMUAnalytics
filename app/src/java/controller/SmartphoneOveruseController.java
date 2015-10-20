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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ASUS-PC
 */
public class SmartphoneOveruseController {

    public TreeMap<String, String> generateReport(User user, Date startDate, Date endDate) {
        TreeMap<String, String> result = new TreeMap<String, String>();
        AppUsageDAO auDAO = new AppUsageDAO();

        ArrayList<AppUsage> appUsageList = auDAO.retrieveByUser(user.getMacAddress(), startDate, endDate);
        AppDAO aDao = new AppDAO();
        ArrayList<AppUsage> gameList = new ArrayList<AppUsage>();
        ArrayList<AppUsage> generalList = new ArrayList<AppUsage>();
        TreeMap<Date, ArrayList<AppUsage>> generalMap = new TreeMap<Date, ArrayList<AppUsage>>();
        TreeMap<Date, ArrayList<AppUsage>> gameMap = new TreeMap<Date, ArrayList<AppUsage>>();
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
        }
        TreeMap<String, Integer> overuseIndexMap = new TreeMap<String, Integer>();
        overuseIndexMap.put("Severe", 0);
        overuseIndexMap.put("Moderate", 0);
        overuseIndexMap.put("Light", 0);
        String dailyUsageIndex = "";

        long dailySmartphoneUsage = calculateAverage(generalMap);
        //GENERAL
        if (generalMap.size() == 0) {
            dailySmartphoneUsage = 0;
        }

        if (dailySmartphoneUsage >= 5) {
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
//PHONE ACCESS FREQUENCY
        int numofSession = 0;
        if (generalMap.size() > 0) {
            TreeMap<String, Integer> phonesessionmap = sortPhoneSessionByHour(generalMap);
            numofSession = calculatefrequencyAccess(phonesessionmap);
        }

        String frequencyUsageIndex = "";
        if (numofSession > 5) {
            frequencyUsageIndex = "Severe";
            int count = overuseIndexMap.get(frequencyUsageIndex);
            overuseIndexMap.put(frequencyUsageIndex, count + 1);
            result.put("frequency", "" + frequencyUsageIndex + "," + numofSession);
        } else if (numofSession <= 3) {
            frequencyUsageIndex = "Light";
            int count = overuseIndexMap.get(frequencyUsageIndex);
            overuseIndexMap.put(frequencyUsageIndex, count + 1);
            result.put("frequency", "" + frequencyUsageIndex + "," + numofSession);
        } else {
            frequencyUsageIndex = "Moderate";
            int count = overuseIndexMap.get(frequencyUsageIndex);
            overuseIndexMap.put(frequencyUsageIndex, count + 1);
            result.put("frequency", "" + frequencyUsageIndex + "," + numofSession);
        }
        //FOR GAMING ONLY
        Long dailyGamingDuration = calculateAverage(gameMap);

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

            result.put("gaming", "" + gamingUsageIndex + "," + dailyGamingDuration);
        } else {

            gamingUsageIndex = "Moderate";
            int count = overuseIndexMap.get(gamingUsageIndex);
            overuseIndexMap.put(gamingUsageIndex, count + 1);
            result.put("gaming", "" + gamingUsageIndex + "," + dailyGamingDuration);
        }

        //3rd one here
        //combination
        String overallIndex = "";

        if (overuseIndexMap.get(
                "Severe") >= 1) {
            overallIndex = "Overusing";
        } else if (overuseIndexMap.get(
                "Light") == 3) {
            overallIndex = "Normal";
        } else {
            overallIndex = "ToBeCautious";
        }

        result.put(
                "overuseindex", overallIndex);

        return result;
    }

    public long calculateAverage(TreeMap<Date, ArrayList<AppUsage>> dayMap) {

        if (dayMap.size() > 0) {
            ArrayList<AppUsage> dayList = new ArrayList<AppUsage>();
            Iterator<ArrayList<AppUsage>> dayIter = dayMap.values().iterator();
            long totalUsageTimeInMilliseconds = 0;
            while (dayIter.hasNext()) {
                dayList = dayIter.next();
                //after we get each list of appUsage sort by date
                //compare time for each day
                long diffSeconds = 0;
                //for each list

                for (int i = 0; i < dayList.size(); i++) {
                    //***get the duration
                    long diffInMilliSec = 0;
                    //if is not the last record
                    if (i != dayList.size() - 1) {
                        AppUsage firstAppUsage = dayList.get(i);
                        AppUsage secondAppUsage = dayList.get(i + 1);
                        //time
                        diffInMilliSec = secondAppUsage.getDate().getTime() - firstAppUsage.getDate().getTime();

                    } else {
                        //if is the last record,
                        //this is the date we are tracking now
                        //for the last record, need use 00:00:00 to minus
                        AppUsage firstAppUsage = dayList.get(i);
                        Date date = firstAppUsage.getDate();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                        String d1 = df.format(date);
                        long lastTime = firstAppUsage.getDate().getTime();

                        //End of day date
                        Date dateKey = new Date();
                        Set<Date> dateKeys = dayMap.keySet();
                        Calendar c = Calendar.getInstance();
                        for (Date d : dateKeys) {
                            String d2 = df.format(d);
                            if (d2.equals(d1)) {
                                dateKey = d;
                                c.setTime(d);
                                c.add(Calendar.DATE, 1);
                                c.set(Calendar.HOUR_OF_DAY, 0);
                                c.set(Calendar.MINUTE, 0);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.MILLISECOND, 0);

                                Date midnight = c.getTime();
                                long defaultMidnightComparison = midnight.getTime();
                                diffInMilliSec = defaultMidnightComparison - lastTime;
                            }
                        }

                    }
                    //for both last record and not last
                    if (diffInMilliSec > 120000) {
                        diffInMilliSec = 10000;
                        if (dayList.size() == 1) {
//*
                        }
                    }
                    totalUsageTimeInMilliseconds += diffInMilliSec;

                }
            }
            long averageUsageTimeInHours = (long) ((totalUsageTimeInMilliseconds / 60000) / 60) / (dayMap.size());

            return averageUsageTimeInHours;
        }
        return 0;
    }

    public TreeMap<String, Integer> sortPhoneSessionByHour(TreeMap<Date, ArrayList<AppUsage>> dayMap) {
        //set up the Map to return to UI based on the hours
        TreeMap<String, Integer> resultMap = new TreeMap<>();

        //store all appUsage according to the timing
        //12mn-12noon      
        resultMap.put("0", 0);
        resultMap.put("1", 0);
        resultMap.put("2", 0);
        resultMap.put("3", 0);
        resultMap.put("4", 0);
        resultMap.put("5", 0);
        resultMap.put("6", 0);
        resultMap.put("7", 0);
        resultMap.put("8", 0);
        resultMap.put("9", 0);
        resultMap.put("10", 0);
        resultMap.put("11", 0);
        //12noon-12mn
        resultMap.put("12", 0);
        resultMap.put("13", 0);
        resultMap.put("14", 0);
        resultMap.put("15", 0);
        resultMap.put("16", 0);
        resultMap.put("17", 0);
        resultMap.put("18", 0);
        resultMap.put("19", 0);
        resultMap.put("20", 0);
        resultMap.put("21", 0);
        resultMap.put("22", 0);
        resultMap.put("23", 0);

        if (dayMap.size() > 0) {
            ArrayList<AppUsage> dayList = new ArrayList<AppUsage>();
            Iterator<ArrayList<AppUsage>> dayIter = dayMap.values().iterator();
            long totalUsageTimeInMilliseconds = 0;
            while (dayIter.hasNext()) {
                dayList = dayIter.next();
                //after we get each list of appUsage sort by date
                //compare time for each day

                for (int i = 0; i < dayList.size(); i++) {
                    AppUsage firstAppUsage = dayList.get(i);

                    //chack if last record in the list
                    if (i == dayList.size() - 1) {

                    } else {

                        AppUsage secondAppUsage = dayList.get(i + 1);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                        //FIRST retrieved Usage
                        Date retrievedDate = firstAppUsage.getDate();
                        String dateTime = df.format(retrievedDate);
                        String datetime[] = dateTime.split(" ");
                        String date = datetime[0];
                        String time = datetime[1];
                        String timeArray[] = time.split(":");
                        String hour = timeArray[0];

                        //Second Retrieved usage
                        Date retrievedDate2 = secondAppUsage.getDate();
                        String dateTime2 = df.format(retrievedDate2);
                        String datetime2[] = dateTime.split(" ");
                        String date2 = datetime2[0];
                        String time2 = datetime2[1];
                        String timeArray2[] = time2.split(":");
                        String hour2 = timeArray2[0];

                        String hh = hour;
                        long diffInMilliSec = 0;
                        int countPhoneSessionInThatHour = 1;

                        if (!hour2.equals(hh)) {
                            int sessioncount = resultMap.get(hh);
                            sessioncount += 1;
                            resultMap.put(hh, sessioncount);

                            hh = hour2;
                            sessioncount = resultMap.get(hh);
                            sessioncount += 1;
                            resultMap.put(hh, sessioncount);

                        } else {

                            diffInMilliSec = secondAppUsage.getDate().getTime() - firstAppUsage.getDate().getTime();

                            if (diffInMilliSec > 120000) {
                                countPhoneSessionInThatHour++;
                            }
                            int sessioncount = resultMap.get(hh);
                            sessioncount += 1;
                            resultMap.put(hh, sessioncount);
                        }
                    }

                }

            }
        }
        return resultMap;
    }

    public int calculatefrequencyAccess(TreeMap<String, Integer> sortedMap) {
        Iterator<String> iter = sortedMap.keySet().iterator();
        int totalphonesession = 0;
        while (iter.hasNext()) {
            String hour = iter.next();
            totalphonesession += sortedMap.get(hour);
        }
        return totalphonesession;
    }
}
