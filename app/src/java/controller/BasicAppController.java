/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.AppUsageDAO;
import dao.UserDAO;
import entity.AppUsage;
import entity.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

/**
 *
 * @author jeremyongts92
 */
public class BasicAppController {

	public TreeMap<String, int[]> generateReport(Date startDate, Date endDate, String sql) {
		TreeMap<String, int[]> result = new TreeMap<String, int[]>();

		result.put("intense-count", new int[2]);
		result.put("normal-count", new int[2]);
		result.put("mild-count", new int[2]);

		AppUsageDAO auDAO = new AppUsageDAO();
		ArrayList<String> userList = new ArrayList<String>();
		if (sql == null) {
			userList = auDAO.retrieveUsers(startDate, endDate);
		} else {
			userList = auDAO.retrieveUsers(startDate, endDate, sql);
		}
		System.out.println("userList size: " + userList.size());
		for (int i = 0; i < userList.size(); i++) {

			String currentMac = userList.get(i);
			System.out.println("MAC: " + currentMac);
			ArrayList<AppUsage> userUsage = auDAO.retrieveByUser(currentMac, startDate, endDate);
			System.out.println("usageList size: " + userUsage.size());

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

			System.out.println(oldTime);
			if (oldTime.before(nextDay)) {
				System.out.println("before totalSeconds" + totalSeconds);
				long difference = (nextDay.getTime() - oldTime.getTime()) / 1000;
				if (difference <= 120) {
					totalSeconds += difference;
				} else {
					totalSeconds += 10;
				}
				System.out.println("after totalSeconds" + totalSeconds);
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
					int[] countArr = result.get("mild-count");

					int count = countArr[0];
					countArr[0] = count + 1;
					result.put("mild-count", countArr);

				} else if (totalHours < 5.0) {
					//NORMAL
					int[] countArr = result.get("normal-count");
					int count = countArr[0];
					countArr[0] = count + 1;
					result.put("normal-count", countArr);

				} else {
					//INTENSE
					int[] countArr = result.get("intense-count");
					int count = countArr[0];
					countArr[0] = count + 1;
					result.put("intense-count", countArr);

				}
			}

		}

		int[] mildArr = result.get("mild-count");
		int[] normalArr = result.get("normal-count");
		int[] intenseArr = result.get("intense-count");

		int mildCount = mildArr[0];
		int normalCount = normalArr[0];
		int intenseCount = intenseArr[0];

		double totalCount = mildCount + normalCount + intenseCount;

		mildArr[1] = (int) Math.round(mildCount / totalCount * 100);
		normalArr[1] = (int) Math.round(normalCount / totalCount * 100);
		intenseArr[1] = (int) Math.round(intenseCount / totalCount * 100);

		return result;
	}

	public TreeMap<String, TreeMap<String, int[]>> generateReportByOneDemo(Date startDate, Date endDate, String[] demoArr) {

		String demo = demoArr[0];
		TreeMap<String,TreeMap<String,int[]>> result = new TreeMap<String,TreeMap<String,int[]>>();

		UserDAO userDAO = new UserDAO();
		
		if (demo.equals("year")) {

			
			ArrayList<String> years = userDAO.getYears();
			//2011 to 2015 (inclusive)
			for (String year : years) {
				String sql = "SELECT au.macaddress from appusage au,user u where\n"
						+ " au.macaddress = u.macaddress\n"
						+ " AND timestamp >= ? AND timestamp <= ?\n"
						+ " AND email like '%."+ year +"@%'\n"
						+ " GROUP BY macaddress;";
				TreeMap<String, int[]> breakdown = generateReport(startDate,endDate, sql);
				result.put(year, breakdown);
				//year
				//count
				//percent
			}

		} else if (demo.equals("school")) {
			
			ArrayList<String> schools = userDAO.getSchools();
			
			for (String school : schools ){
				String sql = "SELECT au.macaddress from appusage au,user u where\n"
						+ " au.macaddress = u.macaddress\n"
						+ " AND timestamp >= ? AND timestamp <= ?\n"
						+ " AND email like '%@"+ school +".%'\n"
						+ " GROUP BY macaddress;";
				
				TreeMap<String, int[]> breakdown = generateReport(startDate, endDate,sql);
				result.put(school, breakdown);
				
				
			}
			
			// business, accountancy, sis, economics, law, socsc
		} else if (demo.equals("gender")) {
			// M and F

			String[] genders = {"M","F"};
			
			for (String gender : genders ){
				String sql = "SELECT au.macaddress from appusage au,user u where\n"
						+ " au.macaddress = u.macaddress\n"
						+ " AND timestamp >= ? AND timestamp <= ?\n"
						+ " AND gender = '" + gender + "'"
						+ " GROUP BY macaddress;";
				
				TreeMap<String, int[]> breakdown = generateReport(startDate, endDate,sql);
				result.put(gender, breakdown);
				
				
			}
			
		}

		return result;
	}

}
