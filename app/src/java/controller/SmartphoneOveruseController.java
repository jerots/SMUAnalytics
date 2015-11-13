package controller;

import dao.AppUsageDAO;
import dao.Utility;
import entity.App;
import entity.AppUsage;
import entity.User;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

/**
 * SmartphoneOveruseController controls all actions related to SmartphoneOveruse
 * functionality
 */
public class SmartphoneOveruseController {

    /**
     * Retrieves a TreeMap object for the usages of the logged in user
     * @param user The datetime input by user
     * @param startDate The startdate for the usage range input by the user 
     * @param endDate The enddate for the usage range input by the user 
     * @return A TreeMap object that contains the semantic place and its usages.
     */
    public TreeMap<String, String> generateReport(User user, Date startDate, Date endDate) {
        AppUsageDAO auDAO = new AppUsageDAO();
        TreeMap<String, String> result2 = new TreeMap<String, String>();
        ArrayList<AppUsage> appUsageList = auDAO.retrieveByUser(user.getMacAddress(), startDate, endDate);
        App app = null;
        Date oldTime = null;
        boolean isGame = false;
        long days = Utility.daysBetweenDates(startDate, endDate);

        if (appUsageList.size() > 0) {
            AppUsage firstAU = appUsageList.get(0);
            app = firstAU.getApp();
            oldTime = firstAU.getDate();
            String appCat = app.getAppCategory().toLowerCase();
            if (appCat.equals("games")) {
                isGame = true;
            }
        }
        long totalAppUsage = 0;
        long gameUsage = 0;

        //CALCULATE APPUSAGE TIME AND GAMING TIME
        for (int i = 1; i < appUsageList.size(); i++) {

            AppUsage au = appUsageList.get(i);
            app = au.getApp();
            Date newTime = au.getDate();

            long difference = Utility.secondsBetweenDates(oldTime, newTime);
            if (isGame) {
                if (difference <= 120) {
                    gameUsage += difference;
                    totalAppUsage += difference;
                } else {
                    gameUsage += 10;
                    totalAppUsage += 10;
                }
            } else {
                if (difference <= 120) {
                    totalAppUsage += difference;
                } else {
                    totalAppUsage += 10;
                }
            }
            String category = app.getAppCategory().toLowerCase();

            isGame = category.equals("games");
            oldTime = newTime;

        }
        if (appUsageList.size() > 0) {
            long difference = Utility.secondsBetweenDates(oldTime, endDate);
            if (isGame) {
                if (difference <= 120) {
                    gameUsage += difference;
                    totalAppUsage += difference;
                } else {
                    gameUsage += 10;
                    totalAppUsage += 10;
                }
            } else {
                if (difference <= 120) {
                    totalAppUsage += difference;
                } else {
                    totalAppUsage += 10;
                }
            }
        }

        //CALCULATE SMARTPHONE ACCESS FREQUENCY
        Date nextHour = Utility.getNextHour(startDate);
        double frequency = 0;
        if (appUsageList.size() > 0) {
            AppUsage firstAU = appUsageList.get(0);
            oldTime = firstAU.getDate();
        }
        Boolean appearedBefore = false;

        for (int i = 1; i < appUsageList.size(); i++) {

            AppUsage au = appUsageList.get(i);
            Date newTime = au.getDate();
            if (newTime.before(nextHour)) {
                appearedBefore = true;
                long difference = Utility.secondsBetweenDates(oldTime, newTime);
                if (difference > 120) {
                    frequency++;
                }
            } else {
                if (appearedBefore) {
                    frequency++;
                } else {
                    i--;
                }
                nextHour = Utility.getNextHour(nextHour);
            }
            oldTime = newTime;
        }
        if (appUsageList.size() > 0) {
            if (oldTime.before(nextHour)) {
                frequency += 1;

            } else {
                frequency += 2;
            }
        }

        //Calculate average across days
        totalAppUsage /= days;
        gameUsage /= days;
        frequency /= (days * 24.0);
        //divide from seconds into hours

//		result2.put("usage-duration", "" + totalAppUsage / 60 / 60);
//		result2.put("gaming-duration", "" + gameUsage / 60 / 60);
        result2.put("usage-duration", "" + totalAppUsage);
        result2.put("gaming-duration", "" + gameUsage);
        DecimalFormat df = new DecimalFormat("0.00");
        result2.put("accessfrequency", "" + df.format(frequency));

        String appUsageCat = "";
        if (totalAppUsage < 3 * 60 * 60) {
            appUsageCat = "Light";
        } else if (totalAppUsage < 5 * 60 * 60) {
            appUsageCat = "Moderate";
        } else if (totalAppUsage >= 5 * 60 * 60) {
            appUsageCat = "Severe";
        }

        String gamingCat = "";
        if (gameUsage < 1 * 60 * 60) {
            gamingCat = "Light";
        } else if (gameUsage < 2 * 60 * 60) {
            gamingCat = "Moderate";
        } else if (gameUsage >= 2 * 60 * 60) {
            gamingCat = "Severe";
        }

        String freqCat = "";
        if (frequency < 3) {
            freqCat = "Light";
        } else if (frequency < 5) {
            freqCat = "Moderate";
        } else if (frequency >= 5) {
            freqCat = "Severe";
        }

        result2.put("usage-category", appUsageCat);
        result2.put("gaming-category", "" + gamingCat);
        result2.put("accessfrequency-category", "" + freqCat);

        //CALCULATE INDEX
        String overuseIndex = "";
        if (appUsageCat.equals("Severe") || gamingCat.equals("Severe") || freqCat.equals("Severe")) {
            overuseIndex = "Overusing";
        } else if (appUsageCat.equals("Light") && gamingCat.equals("Light") && freqCat.equals("Light")) {
            overuseIndex = "Normal";
        } else {
            overuseIndex = "ToBeCautious";
        }
        result2.put("overuse-index", overuseIndex);

        return result2;
    }
}
