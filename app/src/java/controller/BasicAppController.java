/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.AppUsageDAO;
import dao.UserDAO;
import entity.AppUsage;
import entity.Breakdown;
import entity.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

/**
 *
 * @author jeremyongts92
 */
public class BasicAppController {

    public Breakdown generateReport(Date startDate, Date endDate, ArrayList<User> userList) {
		//TreeMap<String, int[]> result = new TreeMap<String, int[]>();

        //	result.put("intense-count", new int[2]);
        //	result.put("normal-count", new int[2]);
        //	result.put("mild-count", new int[2]);
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

        if (userList == null) {
            userList = auDAO.retrieveUsers(startDate, endDate);
        }

        //System.out.println("userList size: " + userList.size());
        for (int i = 0; i < userList.size(); i++) {

            User currUser = userList.get(i);
            //System.out.println("MAC: " + currentMac);
            ArrayList<AppUsage> userUsage = auDAO.retrieveByUser(currUser.getMacAddress(), startDate, endDate);
            //System.out.println("usageList size: " + userUsage.size());

            double totalSeconds = 0;

            Date nextDay = new Date(startDate.getTime() + 60 * 60 * 1000 * 24);

            Date oldTime = null;
            if (userUsage.size() > 0) {
                oldTime = userUsage.get(0).getDate();

            }

            for (int j = 1; j < userUsage.size(); j++) {
                AppUsage au = userUsage.get(j);
                Date newTime = au.getDate();

                if (newTime.before(nextDay)) {

                    //difference between app usage timing
                    long difference = (newTime.getTime() - oldTime.getTime()) / 1000;

                    //If difference less than/equal 2 minutes
                    if (difference <= 2 * 60) {
                        // add difference to totalSeconds if <= 2 mins
                        totalSeconds += difference;
                    } else {
                        // add 10sec to totalSeconds if > 2 mins
                        totalSeconds += 10;
                    }

                } else {  // NEW TIMING AFTER NEXT HOUR

                    totalSeconds += (nextDay.getTime() - oldTime.getTime()) / 1000;
                    nextDay = new Date(nextDay.getTime() + 60 * 60 * 1000);
                }

                oldTime = newTime;

            }

            //System.out.println(oldTime);
            if (oldTime.before(nextDay)) {
                //System.out.println("before totalSeconds" + totalSeconds);
                long difference = (nextDay.getTime() - oldTime.getTime()) / 1000;
                if (difference <= 120) {
                    totalSeconds += difference;
                } else {
                    totalSeconds += 10;
                }
                //System.out.println("after totalSeconds" + totalSeconds);
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

        double totalCount = mildCount + normalCount + intenseCount;

        int mildPercent = (int) Math.round(mildCount / totalCount * 100);
        int normalPercent = (int) Math.round(normalCount / totalCount * 100);
        int intensePercent = (int) Math.round(intenseCount / totalCount * 100);

        intenseMap.put("intense-count", new Breakdown("" + intenseCount));
        intenseMap.put("intense-percent", new Breakdown("" + intensePercent));

        normalMap.put("normal-count", new Breakdown("" + normalCount));
        normalMap.put("normal-percent", new Breakdown("" + normalPercent));

        mildMap.put("mild-count", new Breakdown("" + mildCount));
        mildMap.put("mild-percent", new Breakdown("" + mildPercent));

        return result;
    }

    public Breakdown generateReportByDemo(Date startDate, Date endDate, String[] demoArr) {
        Breakdown result = new Breakdown();
        UserDAO userDAO = new UserDAO();
        AppUsageDAO auDAO = new AppUsageDAO();

        //Retrieve whole userList, from startDate to endDate
        ArrayList<User> userList = auDAO.retrieveUsers(startDate, endDate);

        //Instantiating variables
        ArrayList<ArrayList<User>> prevLists = new ArrayList<ArrayList<User>>();

        //Start prevLists with entire user list.
        prevLists.add(userList);

        Breakdown prevBreak = result;

        //for each demographic (e.g. year,gender, school)
        for (int d = 0; d < demoArr.length; d++) {

            //Get demo as String
            String demo = demoArr[d];

            //Instantiating variables
            ArrayList<ArrayList<User>> filteredLists = new ArrayList<ArrayList<User>>();
            int breakIndex = 0;

            //for each userList from prevLists
            for (int i = 0; i < prevLists.size(); i++) {
                ArrayList<User> prevList = prevLists.get(i);

                HashMap<String, Breakdown> currMap = new HashMap<String, Breakdown>();

//                ArrayList<HashMap<String, Breakdown>> breakdown = prevBreak.getBreakdown();
//                while (breakdown.size() > 0 && breakdown.get(i).get("breakdown") != null) {
//                    breakdown = breakdown.get(i).get("breakdown").getBreakdown();
//				Breakdown currBreak = breakdown.get(i).get("breakdown");
//				while (currBreak != null){
//					currBreak = currBreak.getBreakdown().get(i).get("breakdown");
//                }
                //generate list of userlists by demographic and generate report for each
                    filteredLists = filterDemo(demo, prevList, startDate, endDate, prevBreak, (d + 1) == demoArr.length, d);


            }
            prevLists = filteredLists;

        }

        return result;
    }

    public ArrayList<ArrayList<User>> filterDemo(String demo, ArrayList<User> userList, Date startDate, Date endDate, Breakdown breakdown, boolean lastDemo, int tier) {

        ArrayList<ArrayList<User>> result = new ArrayList<ArrayList<User>>();
        UserDAO userDAO = new UserDAO();
        ArrayList<String> type = new ArrayList<String>();

        if (demo.equals("year")) {

            //2011,2012,2013,2014,2015
            ArrayList<String> years = userDAO.getYears();

            for (String year : years) {
                ArrayList<User> toParse = (ArrayList<User>) userList.clone();
                Iterator<User> iter = toParse.iterator();
                while (iter.hasNext()) {
                    User user = iter.next();
                    if (!user.getYear().equals(year)) {
                        iter.remove();
                    }
                }
                result.add(toParse);
                type.add(year);
            }

        } else if (demo.equals("school")) {

            // business, accountancy, sis, economics, law, socsc
            ArrayList<String> schools = userDAO.getSchools();

            for (String school : schools) {
                ArrayList<User> toParse = (ArrayList<User>) userList.clone();
                Iterator<User> iter = toParse.iterator();
                while (iter.hasNext()) {
                    User user = iter.next();
                    if (!user.getSchool().equals(school)) {
                        iter.remove();
                    }
                }
                result.add(toParse);
                type.add(school);
            }

        } else if (demo.equals("gender")) {
            // M and F

            String[] genders = {"M", "F"};

            for (String gender : genders) {
                //Take previous list and clone it
                ArrayList<User> toParse = (ArrayList<User>) userList.clone();

                //For each user in prevList
                Iterator<User> iter = toParse.iterator();
                while (iter.hasNext()) {
                    User user = iter.next();
                    if (!user.getGender().toUpperCase().equals(gender)) {
                        iter.remove();
                    }
                }
                result.add(toParse);
                type.add(gender);
            }

        }

        //Put mini-reports into Breakdown
        //For each userList in result
        for (int i = 0; i < result.size(); i++) {
            ArrayList<User> filteredList = result.get(i);

            //map of current sub-demo (e.g. "M", "F")
            HashMap<String, Breakdown> newMap = new HashMap<String, Breakdown>();

            //put in type (e.g. year, 2011)
            System.out.println(demo);
            newMap.put(demo, new Breakdown(type.get(i)));

            //generate report
            Breakdown report = generateReport(startDate, endDate, filteredList);

            Breakdown toPut = breakdown;

            for (int t = tier; t > 0; t--) {
                System.out.println("ENTERED TIER " + t);
                toPut = breakdown.getBreakdown().get(i).get("breakdown");
                if (toPut == null) {
                    toPut = new Breakdown();
                    breakdown.getBreakdown().get(i).put("breakdown", new Breakdown());
                }
            }
            if (lastDemo) {
                newMap.put("breakdown", report);
            }

            //put in count (e.g. count, 10)
            int total = 0;
            ArrayList<HashMap<String, Breakdown>> innerList = report.getBreakdown();
            total += Integer.parseInt(innerList.get(0).get("intense-count").getMessage());
            total += Integer.parseInt(innerList.get(1).get("normal-count").getMessage());
            total += Integer.parseInt(innerList.get(2).get("mild-count").getMessage());
            newMap.put("count", new Breakdown("" + total));

            toPut.addInList(newMap);
        }
        return result;
    }
}
