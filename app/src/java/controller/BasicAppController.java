package controller;

import dao.AppDAO;
import dao.AppUsageDAO;
import dao.UserDAO;
import dao.Utility;
import entity.AppUsage;
import entity.Breakdown;
import entity.User;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

public class BasicAppController {

    public Breakdown generateReport(Date startDate, Date endDate, ArrayList<User> userList, double total) {

        //INTIATING VARIABLES
        Breakdown result = new Breakdown();

        HashMap<String, Breakdown> intenseMap = new HashMap<String, Breakdown>();
        HashMap<String, Breakdown> normalMap = new HashMap<String, Breakdown>();
        HashMap<String, Breakdown> mildMap = new HashMap<String, Breakdown>();

        int intenseCount = 0;
        int normalCount = 0;
        int mildCount = 0;

        result.addInList(intenseMap);
        result.addInList(normalMap);
        result.addInList(mildMap);

        AppUsageDAO auDAO = new AppUsageDAO();

        //If it is without demographic, userList is null
        if (userList == null) {
            userList = auDAO.retrieveUsers(startDate, endDate);
        }
        for (int i = 0; i < userList.size(); i++) {
            User currUser = userList.get(i);

            //get current user's app usge
            ArrayList<AppUsage> userUsage = auDAO.retrieveByUser(currUser.getMacAddress(), startDate, endDate);
            double totalSeconds = 0;

            Date nextDay = new Date(startDate.getTime() + 60 * 60 * 1000 * 24);

            Date oldTime = null;
            if (userUsage.size() > 0) {
                oldTime = userUsage.get(0).getDate();
                if(oldTime.after(nextDay)) {
                    nextDay = new Date(nextDay.getTime() + 60 * 60 * 1000 * 24);
                }
            }

            for (int j = 1; j < userUsage.size(); j++) {
                AppUsage au = userUsage.get(j);
                Date newTime = au.getDate();
                boolean beforeAppeared = false;
                if (newTime.before(nextDay)) {
                    beforeAppeared = true;
                    //difference between app usage timing
                    long difference = Utility.secondsBetweenDates(oldTime, newTime);

                    //If difference less than/equal 2 minutes
                    if (difference <= 2 * 60) {
                        // add difference to totalSeconds if <= 2 mins
                        totalSeconds += difference;
                    } else {
                        // add 10sec to totalSeconds if > 2 mins
                        totalSeconds += 10;
                    }

                } else {  // NEW TIMING AFTER NEXT DAY
                    nextDay = new Date(nextDay.getTime() + 60 * 60 * 1000 * 24);
                    
                    if (!beforeAppeared) {
                        long difference = Utility.secondsBetweenDates(oldTime, newTime);
                        if (difference <= 2 * 60) {
                            // add difference to totalSeconds if <= 2 mins
                            totalSeconds += difference;
                        } else {
                            // add 10sec to totalSeconds if > 2 mins
                            totalSeconds += 10;
                        }
                    }
                }

                oldTime = newTime;

            }

            if (oldTime.before(nextDay)) {
                long difference = Utility.secondsBetweenDates(oldTime, nextDay);
                if (difference < 10) {
                    totalSeconds += difference;
                } else {
                    totalSeconds += 10;
                }
            } else {
                totalSeconds += 10;
            }
            
            //DIVIDE TO GET INTO DAYS
            long days = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24) + 1;
            totalSeconds /= days;

            //FILTER USER'S USAGE INTO CATEGORIES
            double totalHours = totalSeconds / (60 * 60);

            if (totalHours > 0) {
                if (totalHours < 1.0) {
                    //MILD
                    mildCount++;

                } else if (totalHours < 5.0) {
                    //NORMAL
                    normalCount++;

                } else {
                    //INTENSE
                    intenseCount++;

                }
            }

        }
        //Calculate percentage
        if (total == -1) {
            total = mildCount + normalCount + intenseCount;

        }

        int mildPercent = (int) Math.round(mildCount / total * 100);
        int normalPercent = (int) Math.round(normalCount / total * 100);
        int intensePercent = (int) Math.round(intenseCount / total * 100);

        //Put in maps
        intenseMap.put("intense-count", new Breakdown("" + intenseCount));
        intenseMap.put("intense-percent", new Breakdown("" + intensePercent));

        normalMap.put("normal-count", new Breakdown("" + normalCount));
        normalMap.put("normal-percent", new Breakdown("" + normalPercent));

        mildMap.put("mild-count", new Breakdown("" + mildCount));
        mildMap.put("mild-percent", new Breakdown("" + mildPercent));

        return result;
    }

    public Breakdown generateReportByDemo(Date startDate, Date endDate, String[] demoArr) {

        //INSTANTIATING VARIABLES
        UserDAO userDAO = new UserDAO();
        AppUsageDAO auDAO = new AppUsageDAO();
        ArrayList<User> userList = auDAO.retrieveUsers(startDate, endDate);

        double total = userList.size();

        Breakdown result = new Breakdown();

        ArrayList<String> schools = userDAO.getSchools();
        ArrayList<String> years = userDAO.getYears();
        ArrayList<String> genders = userDAO.getGenders();
        ArrayList<String> ccas = userDAO.getCCAs();

        ArrayList<String> demo1List = new ArrayList<String>();
        ArrayList<String> demo2List = new ArrayList<String>();
        ArrayList<String> demo3List = new ArrayList<String>();
        ArrayList<String> demo4List = new ArrayList<String>();

        String demo1Type = "";
        String demo2Type = "";
        String demo3Type = "";
        String demo4Type = "";

        int demoCount = demoArr.length;
        if (demoCount > 0) {
            switch (demoArr[0]) {
                case "gender":
                    demo1List = genders;
                    demo1Type = "gender";
                    break;
                case "school":
                    demo1List = schools;
                    demo1Type = "school";
                    break;
                case "year":
                    demo1List = years;
                    demo1Type = "year";
                    break;
                case "cca":
                    demo1List = ccas;
                    demo1Type = "cca";
                    break;
            }
        }
        if (demoCount > 1) {
            switch (demoArr[1]) {
                case "gender":
                    demo2List = genders;
                    demo2Type = "gender";
                    break;
                case "school":
                    demo2List = schools;
                    demo2Type = "school";
                    break;
                case "year":
                    demo2List = years;
                    demo2Type = "year";
                    break;
                case "cca":
                    demo2List = ccas;
                    demo2Type = "cca";
                    break;
            }
        }
        if (demoCount > 2) {
            switch (demoArr[2]) {
                case "gender":
                    demo3List = genders;
                    demo3Type = "gender";
                    break;
                case "school":
                    demo3List = schools;
                    demo3Type = "school";
                    break;
                case "year":
                    demo3List = years;
                    demo3Type = "year";
                    break;
                case "cca":
                    demo3List = ccas;
                    demo3Type = "cca";
                    break;
            }
        }
        if (demoCount > 3) {
            switch (demoArr[3]) {
                case "gender":
                    demo4List = genders;
                    demo4Type = "gender";
                    break;
                case "school":
                    demo4List = schools;
                    demo4Type = "school";
                    break;
                case "year":
                    demo4List = years;
                    demo4Type = "year";
                    break;
                case "cca":
                    demo4List = ccas;
                    demo4Type = "cca";
                    break;
            }
        }

        //For each demo1
        for (String demo1 : demo1List) {
            HashMap<String, Breakdown> demo1Map = new HashMap<String, Breakdown>();
            ArrayList<User> demo1UserList = filterDemo(demo1, demo1Type, userList);
            demo1Map.put(demo1Type, new Breakdown(demo1));
            demo1Map.put("count", new Breakdown("" + demo1UserList.size()));
            Breakdown demo1bd = new Breakdown();
            demo1Map.put("breakdown", demo1bd);

            result.addInList(demo1Map);

            //For each demo2
            for (String demo2 : demo2List) {
                HashMap<String, Breakdown> demo2Map = new HashMap<String, Breakdown>();
                ArrayList<User> demo2UserList = filterDemo(demo2, demo2Type, demo1UserList);
                demo2Map.put(demo2Type, new Breakdown(demo2));
                demo2Map.put("count", new Breakdown("" + demo2UserList.size()));
                Breakdown demo2bd = new Breakdown();
                demo2Map.put("breakdown", demo2bd);

                demo1bd.addInList(demo2Map);

                //For each demo3
                for (String demo3 : demo3List) {
                    HashMap<String, Breakdown> demo3Map = new HashMap<String, Breakdown>();
                    ArrayList<User> demo3UserList = filterDemo(demo3, demo3Type, demo2UserList);
                    demo3Map.put(demo3Type, new Breakdown(demo3));
                    demo3Map.put("count", new Breakdown("" + demo3UserList.size()));
                    Breakdown demo3bd = new Breakdown();
                    demo3Map.put("breakdown", demo3bd);

                    demo2bd.addInList(demo3Map);

//					
                    //For each demo4
                    for (String demo4 : demo4List) {
                        HashMap<String, Breakdown> demo4Map = new HashMap<String, Breakdown>();
                        ArrayList<User> demo4UserList = filterDemo(demo4, demo4Type, demo3UserList);
                        demo4Map.put(demo4Type, new Breakdown(demo4));
                        demo4Map.put("count", new Breakdown("" + demo4UserList.size()));

                        demo3bd.addInList(demo4Map);

                        if (demoCount == 4) {
                            Breakdown demo4report = generateReport(startDate, endDate, demo4UserList, total);
                            demo4Map.put("breakdown", demo4report);
                        }
                    }

                    if (demoCount == 3) {
                        //generate report if last demo
                        Breakdown demo3report = generateReport(startDate, endDate, demo3UserList, total);
                        demo3Map.put("breakdown", demo3report);
                    }

                }

                if (demoCount == 2) {
                    //generate report if last demo
                    Breakdown demo2report = generateReport(startDate, endDate, demo2UserList, total);
                    demo2Map.put("breakdown", demo2report);

                }
            }

            if (demoCount == 1) {
                //generate report if last demo
                Breakdown demo1report = generateReport(startDate, endDate, demo1UserList, total);
                demo1Map.put("breakdown", demo1report);
            }
        }

        //CALCULATE PERCENTAGE
        if (demoCount > 0) {
            Breakdown bd1 = result;
            ArrayList<HashMap<String, Breakdown>> list1 = bd1.getBreakdown();

            generatePercentage(bd1, total);

        }

        if (demoCount > 1) {
            ArrayList<HashMap<String, Breakdown>> secondTier = result.getBreakdown();
            for (HashMap<String, Breakdown> secondMap : secondTier) {
                generatePercentage(secondMap.get("breakdown"), total);
            }

        }
        if (demoCount > 2) {
            ArrayList<HashMap<String, Breakdown>> secondTier = result.getBreakdown();
            for (HashMap<String, Breakdown> secondMap : secondTier) {
                ArrayList<HashMap<String, Breakdown>> thirdTier = secondMap.get("breakdown").getBreakdown();
                for (HashMap<String, Breakdown> thirdMap : thirdTier) {
                    generatePercentage(thirdMap.get("breakdown"), total);

                }

            }
        }
        if (demoCount > 3) {
            ArrayList<HashMap<String, Breakdown>> secondTier = result.getBreakdown();

            for (HashMap<String, Breakdown> secondMap : secondTier) {
                ArrayList<HashMap<String, Breakdown>> thirdTier = secondMap.get("breakdown").getBreakdown();
                for (HashMap<String, Breakdown> thirdMap : thirdTier) {
                    ArrayList<HashMap<String, Breakdown>> fourthTier = thirdMap.get("breakdown").getBreakdown();
                    for (HashMap<String, Breakdown> fourthMap : fourthTier) {
                        generatePercentage(fourthMap.get("breakdown"), total);
                    }
                }

            }
        }

        return result;
    }

    public ArrayList<User> filterDemo(String demo, String demoType, ArrayList<User> userList) {

        ArrayList<User> toParse = (ArrayList<User>) userList.clone();
        Iterator<User> iter = toParse.iterator();
        if (demoType.equals("year")) {

            //Filter by 2011,2012,2013,2014,2015
            while (iter.hasNext()) {
                User user = iter.next();
                if (!user.getYear().equals(demo)) {
                    iter.remove();
                }
            }
            return toParse;

        } else if (demoType.equals("school")) {

            // Filter by business, accountancy, sis, economics, law, socsc
            while (iter.hasNext()) {
                User user = iter.next();
                if (!user.getSchool().equals(demo)) {
                    iter.remove();
                }
            }
            return toParse;

        } else if (demoType.equals("gender")) {
            // Filter by M and F
            while (iter.hasNext()) {
                User user = iter.next();
                if (!user.getGender().toLowerCase().equals(demo)) {
                    iter.remove();
                }
            }
            return toParse;

        } else if (demoType.equals("cca")) {
            // Filter by CCAs
            while (iter.hasNext()) {
                User user = iter.next();
                if (!user.getCca().toLowerCase().equals(demo.toLowerCase())) {
                    iter.remove();
                }
            }

            return toParse;

        }
        return userList;

    }

    public void generatePercentage(Breakdown breakdown, double total) {

        ArrayList<HashMap<String, Breakdown>> list1 = breakdown.getBreakdown();
        for (HashMap<String, Breakdown> map : list1) {

            int count = Integer.parseInt(map.get("count").getMessage());
            int percent = (int) Math.round((count * 100) / total);
            map.put("percent", new Breakdown("" + percent));

        }

    }

    public TreeMap<String, Integer[]> generateAppCategory(Date startDate, Date endDate) {

        //Total Usage Time for each appid
        TreeMap<Integer, Double> appResult = new TreeMap<Integer, Double>();
        //Total Usage Time for each category
        TreeMap<String, Double> result = new TreeMap<String, Double>();
        //Total Usage Time and Percent for each category
        TreeMap<String, Integer[]> toResult = new TreeMap<String, Integer[]>();

        AppUsageDAO auDAO = new AppUsageDAO();
        ArrayList<User> userList = new ArrayList<User>();

        userList = auDAO.retrieveUsers(startDate, endDate);
        for (int i = 0; i < userList.size(); i++) {

            User currUser = userList.get(i);
            ArrayList<AppUsage> userUsage = auDAO.retrieveByUser(currUser.getMacAddress(), startDate, endDate);
            Date nextDay = new Date(startDate.getTime() + 60 * 60 * 1000 * 24);
            
            Date oldTime = null;
            if (userUsage.size() > 0) {
                oldTime = userUsage.get(0).getDate();
                if(oldTime.after(nextDay)) {
                    nextDay = new Date(nextDay.getTime() + 60 * 60 * 1000 * 24);
                }
            }

            for (int j = 1; j < userUsage.size(); j++) {
                AppUsage au = userUsage.get(j);
                Date newTime = au.getDate();

                //store oldTime appId
                int appId = userUsage.get(j - 1).getAppId();
                boolean beforeAppeared = false;
                if (newTime.before(nextDay)) {
                    beforeAppeared = true;
                    
                    //difference = usage time of the oldTime appId
                    double difference = Utility.secondsBetweenDates(oldTime, newTime);

                    //If difference less than/equal 2 minutes
                    if (difference <= 2 * 60) {
                        // add time to the appId
                        if (appResult.containsKey(appId)) {
                            double value = appResult.get(appId);
                            appResult.put(appId, (value + difference));
                        } else {
                            appResult.put(appId, difference);
                        }

                    } else {
                        // add 10sec to appid if > 2 mins
                        if (appResult.containsKey(appId)) {
                            double value = appResult.get(appId);
                            appResult.put(appId, (value + 10));
                        } else {
                            appResult.put(appId, 10.0);
                        }

                    }

                } else { 
                    nextDay = new Date(nextDay.getTime() + 60 * 60 * 1000 * 24);
                    
                    if (!beforeAppeared) {
                        double diff = Utility.secondsBetweenDates(oldTime, newTime);
                        //add time to the appid
                        if (diff <= 2 * 60) {
                        // add time to the appId
                            if (appResult.containsKey(appId)) {
                                double value = appResult.get(appId);
                                appResult.put(appId, (value + diff));
                            } else {
                                appResult.put(appId, diff);
                            }

                        } else {
                            // add 10sec to appid if > 2 mins
                            if (appResult.containsKey(appId)) {
                                double value = appResult.get(appId);
                                appResult.put(appId, (value + 10));
                            } else {
                                appResult.put(appId, 10.0);
                            }

                        }
                    }

                }

                oldTime = newTime;

            }
            
            //get the appId of the last user usage
            int lastAppId = userUsage.get(userUsage.size() - 1).getAppId();

            if (oldTime.before(nextDay)) {
                double difference = Utility.secondsBetweenDates(oldTime, nextDay);
                //add the time difference to last appId
                if (difference < 10) {
                    if (appResult.containsKey(lastAppId)) {
                        double value = appResult.get(lastAppId);
                        appResult.put(lastAppId, (value + difference));
                    } else {
                        appResult.put(lastAppId, difference);
                    }
                } else {
                    if (appResult.containsKey(lastAppId)) {
                        double value = appResult.get(lastAppId);
                        appResult.put(lastAppId, (value + 10));
                    } else {
                        appResult.put(lastAppId, 10.0);
                    }
                }
            } else {
                
                if (appResult.containsKey(lastAppId)) {
                    double value = appResult.get(lastAppId);
                    appResult.put(lastAppId, (value + 10));
                } else {
                    appResult.put(lastAppId, 10.0);
                }

            }
            

            //DIVIDE TO GET INTO DAYS
            long days = Utility.daysBetweenDates(startDate, endDate);

            AppDAO app = new AppDAO();

            //Retrieve appid in each category
            TreeMap<String, ArrayList<Integer>> appCategoryList = app.retrieveByCategory();
            Iterator<String> iter = appCategoryList.keySet().iterator();
            double totTime = 0.0;
            //Sum the total time by category
            while (iter.hasNext()) {
                String key = iter.next();
                //EACH CATEGORY
                ArrayList<Integer> innerList = appCategoryList.get(key);
                double totCatTime = 0.0;
                for (int j = 0; j < innerList.size(); j++) {
                    int appid = innerList.get(j);
                    double timePerApp = 0.0;

                    if (appResult.containsKey(appid)) {
                        timePerApp = appResult.get(appid);

                    }
                    totCatTime += timePerApp;
                }
                
                double avgCatTime = totCatTime / days;
                
                totTime += avgCatTime;
                result.put(key, avgCatTime);
            }

            Iterator<String> iterator = result.keySet().iterator();
            //Calculate the percentage for each category
            while (iterator.hasNext()) {

                String name = iterator.next();
                double duration = result.get(name);
                double percent = (duration / totTime) * 100;
                Integer[] arrToReturn = new Integer[2];
                
                arrToReturn[0] = Integer.valueOf(Math.round(duration)+ "");
                arrToReturn[1] = Integer.valueOf(Math.round(percent) + "");
                toResult.put(name, arrToReturn);

            }

        }
        ArrayList<String> catList = Utility.retrieveCategories();

        for (String cat : catList) {
            if (!toResult.containsKey(cat)) {
                Integer[] arrToReturn = new Integer[2];
                arrToReturn[0] = 0;
                arrToReturn[1] = 0;
                toResult.put(cat, arrToReturn);
            }
        }
        return toResult;
    }

    public Breakdown generateDiurnalReport(Date startDate, String[] demoArr) {

        Breakdown result = new Breakdown();
        AppUsageDAO auDAO = new AppUsageDAO();
        Date startHour = startDate;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:00");
        //for each hour (for 24 loop)
        for (int i = 0; i < 24; i++) {

            HashMap<String, Breakdown> miniMap = new HashMap<String, Breakdown>();
            result.addInList(miniMap);

            Date endHour = new Date(startHour.getTime() + 1000 * 60 * 60);
            miniMap.put("period", new Breakdown(sdf.format(startHour) + "-" + sdf.format(endHour)));

            //get number of targetted users
            Date endDate = new Date(startDate.getTime() + 1000 * 60 * 60 * 24);
            ArrayList<User> targetList = auDAO.retrieveUserByDemo(startDate, endDate, demoArr);
            int targetCount = targetList.size();
            //get userList for this hour, filtered by demo
            ArrayList<User> userList = auDAO.retrieveUserByDemo(startHour, endHour, demoArr);
            double secondsThisHour = 0;

            //for each user
            for (User user : userList) {

                //retrieve appUsageList
                ArrayList<AppUsage> auList = auDAO.retrieveByUserHourly(user.getMacAddress(), startHour, endHour);

                Date oldTime = null;
                if (auList.size() > 0) {
                    oldTime = auList.get(0).getDate();
                }

                //For each appusage in appUsageList
                for (int j = 1; j < auList.size(); j++) {
                    Date newTime = auList.get(j).getDate();

                    //calculate usageTime and add to secondsThisHour
                    //difference between app usage timing
                    long difference = Utility.secondsBetweenDates(oldTime, newTime);

                    //If difference less than/equal 2 minutes
                    if (difference <= 2 * 60) {
                        // add difference to totalSeconds if <= 2 mins
                        secondsThisHour += difference;
                    } else {
                        // add 10sec to totalSeconds if > 2 mins
                        secondsThisHour += 10;
                    }

                    oldTime = newTime;

                }
                //Add 10 seconds for the last appusage in the list
                if (auList.size() > 0) {
                    Date lastTime = auList.get(auList.size() - 1).getDate();

                    long difference = Utility.secondsBetweenDates(lastTime, endHour);

                    if (difference > 10) {
                        difference = 10;
                    }
                    secondsThisHour += difference;

                }

            }
            //divide by all users in this hour to get average usage time in this hour
            if (targetCount > 0) {
                secondsThisHour /= targetCount;

            }

            //store in breakdown
            long time = Math.round(secondsThisHour);
            miniMap.put("duration", new Breakdown("" + time));

            startHour = endHour;
        }

        return result;
    }

}
