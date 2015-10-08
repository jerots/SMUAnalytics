/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.AppUsageDAO;
import entity.AppUsage;
import entity.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author jeremyongts92
 */
public class BasicAppController {

	public HashMap<String, Integer> generateReport(Date startDate, Date endDate) {
		HashMap<String, Integer> result = new HashMap<String, Integer>();

		result.put("intense-count", 0);
		result.put("normal-count", 0);
		result.put("mild-count", 0);

		AppUsageDAO auDAO = new AppUsageDAO();
		ArrayList<String> userList = auDAO.retrieveUsers(startDate, endDate);
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
				if (difference <= 120){
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
				System.out.println("WENT IN ZERO");
				if (totalHours < 1.0) {
					//MILD
					int count = result.get("mild-count");
					result.put("mild-count", count + 1);

				} else if (totalHours < 5.0) {
					//NORMAL
					int count = result.get("normal-count");
					result.put("normal-count", count + 1);

				} else {
					//INTENSE
					int count = result.get("intense-count");
					result.put("intense-count", count + 1);
					

				}
			}

		}

		return result;
	}

}
