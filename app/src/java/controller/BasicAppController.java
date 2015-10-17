/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.AppDAO;
import dao.AppUsageDAO;
import dao.UserDAO;
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

/**
 *
 * @author jeremyongts92
 */
public class BasicAppController {

	public Breakdown generateReport(Date startDate, Date endDate, ArrayList<User> userList) {

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
		System.out.println("USERLIST SIZE " + userList.size());
		for (int i = 0; i < userList.size(); i++) {
			User currUser = userList.get(i);

			//get current user's app usge
			ArrayList<AppUsage> userUsage = auDAO.retrieveByUser(currUser.getMacAddress(), startDate, endDate);
			double totalSeconds = 0;

			Date nextDay = new Date(startDate.getTime() + 60 * 60 * 1000 * 24);

			Date oldTime = null;
			if (userUsage.size() > 0) {
				oldTime = userUsage.get(0).getDate();

			}

			for (int j = 1; j < userUsage.size(); j++) {
				AppUsage au = userUsage.get(j);
				Date newTime = au.getDate();
				boolean beforeAppeared = false;
				if (newTime.before(nextDay)) {
					beforeAppeared = true;
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

				} else {  // NEW TIMING AFTER NEXT DAY
					if (beforeAppeared) {
						totalSeconds += (nextDay.getTime() - oldTime.getTime()) / 1000;

					}
					nextDay = new Date(nextDay.getTime() + 60 * 60 * 1000);
				}

				oldTime = newTime;

			}

			if (oldTime.before(nextDay)) {
				long difference = (nextDay.getTime() - oldTime.getTime()) / 1000;
				if (difference <= 120) {
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
		double totalCount = mildCount + normalCount + intenseCount;

		int mildPercent = (int) Math.round(mildCount / totalCount * 100);
		int normalPercent = (int) Math.round(normalCount / totalCount * 100);
		int intensePercent = (int) Math.round(intenseCount / totalCount * 100);

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
		Breakdown result = new Breakdown();

		ArrayList<String> schools = userDAO.getSchools();
		ArrayList<String> years = userDAO.getYears();
		ArrayList<String> genders = userDAO.getGenders();

		ArrayList<String> demo1List = new ArrayList<String>();
		ArrayList<String> demo2List = new ArrayList<String>();
		ArrayList<String> demo3List = new ArrayList<String>();

		String demo1Type = "";
		String demo2Type = "";
		String demo3Type = "";

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

					demo2bd.addInList(demo3Map);

					//generate report if last demo
					Breakdown demo3report = generateReport(startDate, endDate, demo3UserList);
					demo3Map.put("breakdown", demo3report);

				}

				if (demoCount == 2) {
					//generate report if last demo
					Breakdown demo2report = generateReport(startDate, endDate, demo2UserList);
					demo2Map.put("breakdown", demo2report);

				}
			}

			if (demoCount == 1) {
				//generate report if last demo
				Breakdown demo1report = generateReport(startDate, endDate, demo1UserList);
				demo1Map.put("breakdown", demo1report);
			}
		}

		//CALCULATE PERCENTAGE
		if (demoCount > 0) {
			generatePercentage(result);
		}
		if (demoCount > 1) {
			ArrayList<HashMap<String, Breakdown>> secondTier = result.getBreakdown();
			for (HashMap<String, Breakdown> secondMap : secondTier) {
				generatePercentage(secondMap.get("breakdown"));
			}
		}
		if (demoCount > 2) {
			ArrayList<HashMap<String, Breakdown>> secondTier = result.getBreakdown();
			for (HashMap<String, Breakdown> secondMap : secondTier) {
				ArrayList<HashMap<String, Breakdown>> thirdTier = secondMap.get("breakdown").getBreakdown();
				for (HashMap<String, Breakdown> thirdMap : thirdTier) {
					generatePercentage(thirdMap.get("breakdown"));

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
				if (!user.getGender().toUpperCase().equals(demo)) {
					iter.remove();
				}
			}
			return toParse;

		}
		return userList;

	}

	public void generatePercentage(Breakdown breakdown) {

		ArrayList<HashMap<String, Breakdown>> list1 = breakdown.getBreakdown();
		double total = 0.0;

		//Calculate the total
		for (HashMap<String, Breakdown> map : list1) {
			int count = Integer.parseInt(map.get("count").getMessage());
			total += count;
		}

		//Calculate the percentage and put in Breakdown
		for (HashMap<String, Breakdown> map : list1) {

			int count = Integer.parseInt(map.get("count").getMessage());
			int percent = (int) Math.round(count / total * 100);
			map.put("percent", new Breakdown("" + percent));

		}

	}

	public TreeMap<String, Double[]> generateAppCategory(Date startDate, Date endDate) {

		//Total Usage Time for each appid
		TreeMap<Integer, Double> appResult = new TreeMap<Integer, Double>();
		//Total Usage Time for each category
		TreeMap<String, Double> result = new TreeMap<String, Double>();
		//Total Usage Time and Percent for each category
		TreeMap<String, Double[]> toResult = new TreeMap<String, Double[]>();

		AppUsageDAO auDAO = new AppUsageDAO();
		ArrayList<User> userList = new ArrayList<User>();

		userList = auDAO.retrieveUsers(startDate, endDate);

		System.out.println("userList size: " + userList.size());
		for (int i = 0; i < userList.size(); i++) {

			User currUser = userList.get(i);
			ArrayList<AppUsage> userUsage = auDAO.retrieveByUser(currUser.getMacAddress(), startDate, endDate);

			Date nextDay = new Date(startDate.getTime() + 60 * 60 * 1000 * 24);

			Date oldTime = null;
			if (userUsage.size() > 0) {
				oldTime = userUsage.get(0).getDate();
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
					double difference = (newTime.getTime() - oldTime.getTime()) / 1000;

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

				} else {  // NEW TIMING AFTER NEXT HOUR
					if (beforeAppeared) {
						double diff = (nextDay.getTime() - oldTime.getTime()) / 1000;
						//add time to the appid
						if (appResult.containsKey(appId)) {
							double value = appResult.get(appId);
							appResult.put(appId, (value + diff));
						} else {
							appResult.put(appId, diff);
						}
					}
					nextDay = new Date(nextDay.getTime() + 60 * 60 * 1000);

				}

				oldTime = newTime;

			}
			//get the appId of the last user usage
			int lastAppId = userUsage.get(userUsage.size() - 1).getAppId();

			if (oldTime.before(nextDay)) {
				double difference = (nextDay.getTime() - oldTime.getTime()) / 1000;
				//add the time difference to last appId
				if (difference <= 120) {
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
			long days = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24) + 1;

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
				System.out.println("Category: " + key);
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
			DecimalFormat df = new DecimalFormat("####0.0");
			//Calculate the percentage for each category
			while (iterator.hasNext()) {

				String name = iterator.next();
				double duration = result.get(name);
				double percent = (duration / totTime) * 100;
				Double[] arrToReturn = new Double[2];
				arrToReturn[0] = Double.valueOf(df.format(duration));
				arrToReturn[1] = Double.valueOf(df.format(percent));
				toResult.put(name, arrToReturn);

			}

		}
		return toResult;
	}

	public Breakdown generateDiurnalReport(Date startDate, String[] demoArr) {

		Breakdown result = new Breakdown();
		AppUsageDAO auDAO = new AppUsageDAO();
		Date startHour = startDate;
		SimpleDateFormat sdf = new SimpleDateFormat("HH.00");
		//for each hour (for 24 loop)
		for (int i = 0; i < 24; i++) {

			HashMap<String, Breakdown> miniMap = new HashMap<String, Breakdown>();
			result.addInList(miniMap);

			Date endHour = new Date(startHour.getTime() + 1000 * 60 * 60);
			miniMap.put("period", new Breakdown(sdf.format(startHour) + "-" + sdf.format(endHour)));

			//get userList for this hour, filtered by demo
			ArrayList<User> userList = auDAO.retrieveUserByDemo(startHour, endHour, demoArr);

			int secondsThisHour = 0;

			//for each user
			for (User user : userList) {

				//retrieve appUsageList
				ArrayList<AppUsage> auList = auDAO.retrieveByUserHourly(user.getMacAddress(), startHour, endHour);

				Date oldTime = null;
				if (auList.size() > 0) {
					oldTime = auList.get(0).getDate();
				}

				for (int j = 1; j < auList.size(); j++) {
					Date newTime = auList.get(j).getDate();

					//calculate usageTime and add to secondsThisHour
					//difference between app usage timing
					long difference = (newTime.getTime() - oldTime.getTime()) / 1000;

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

				if (auList.size() > 0) {
					Date lastTime = auList.get(auList.size() - 1).getDate();

					long difference = endHour.getTime() - lastTime.getTime() / 1000;

					if (difference <= 2 * 60) {
						// add difference to totalSeconds if <= 2 mins
						secondsThisHour += difference;
					} else {
						// add 10sec to totalSeconds if > 2 mins
						secondsThisHour += 10;
					}

				}

			}
			int numUsers = userList.size();
			//divide by all users in this hour to get average usage time in this hour
			if (numUsers > 0) {
				secondsThisHour /= numUsers;

			}

			//store in breakdown
			miniMap.put("duration", new Breakdown("" + secondsThisHour));

			startHour = endHour;
		}

		return result;
	}

}
