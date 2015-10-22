/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.AppDAO;
import dao.AppUsageDAO;
import dao.LocationUsageDAO;
import dao.UserDAO;
import entity.App;
import entity.AppUsage;
import entity.LocationUsage;
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
public class SocialActivenessController {

	public TreeMap<String, String> generateOnlineReport(Date startDate, Date endDate, String username) {

		//INITIATING
		UserDAO userDAO = new UserDAO();
		User user = userDAO.retrieve(username);
		String macaddress = user.getMacAddress();
		AppUsageDAO auDAO = new AppUsageDAO();
		AppDAO appDAO = new AppDAO();

		

		//get all appusage by the user in specified date
		ArrayList<AppUsage> auList = auDAO.retrieveByUser(macaddress, startDate, endDate);
		HashMap<Integer, Long> appMap = new HashMap<Integer, Long>();
		int prevAppId = -1;
		Date oldTime = null;
		
		
		//Initialise first variables
		if (auList.size() > 0) {
			AppUsage firstAU = auList.get(0);
			prevAppId = firstAU.getAppId();
			oldTime = firstAU.getDate();
		}

		
		//For each appusage by the user
		for (int i = 1; i < auList.size(); i++) {

			AppUsage au = auList.get(i);

			int appId = au.getAppId();
			Date newTime = au.getDate();
			long difference = (newTime.getTime() - oldTime.getTime()) / 1000;
			Long storedUsage = appMap.get(prevAppId);

			if (difference <= 120) {
				if (storedUsage == null) {
					storedUsage = new Long(0);
				}
				storedUsage += difference;
				appMap.put(prevAppId, storedUsage);
			} else {
				if (storedUsage == null) {
					storedUsage = new Long(0);
				}
				storedUsage += 10;
				appMap.put(prevAppId, storedUsage);

			}

			prevAppId = appId;
			oldTime = newTime;
		}
		
		//Calculate the timing for the last appUsage
		if (auList.size() > 0) {
			long difference = (endDate.getTime() - oldTime.getTime()) / 1000;
			Long storedUsage = appMap.get(prevAppId);

			if (difference <= 120) {
				if (storedUsage == null) {
					storedUsage = new Long(0);
				}
				storedUsage += difference;
				appMap.put(prevAppId, storedUsage);
			} else {
				if (storedUsage == null) {
					storedUsage = new Long(0);
				}
				storedUsage += 10;
				appMap.put(prevAppId, storedUsage);

			}

		}
		
		//Calculate total time usage of SOCIAL apps
		TreeMap<String, String> result = new TreeMap<String, String>();

		Iterator iter = appMap.keySet().iterator();
		double totalUsageTime = 0.0;
		while (iter.hasNext()) {
			int appId = (int) iter.next();
			App app = appDAO.retrieveAppbyId(appId);

			if (app.getAppCategory().equals("Social")) {

				totalUsageTime += appMap.get(appId);
			
			}

		}
		
		
		result.put("total-social-app-usage-duration", "" + (int) totalUsageTime);
		iter = appMap.keySet().iterator();
		while (iter.hasNext()) {
			int appId = (int) iter.next();
			App app = appDAO.retrieveAppbyId(appId);

			if (app.getAppCategory().equals("Social")) {

				double usageTime = Double.parseDouble("" + appMap.get(appId));
				long percentage = Math.round(usageTime / totalUsageTime * 100);
				result.put(app.getAppName(), "" + percentage);
			}

		}
		return result;

	}
	
	public TreeMap<String, String> generatePhysicalReport(Date startDate, Date endDate, String username) {
		
		//get logged-on user's locationUsage in the day
		LocationUsageDAO luDAO = new LocationUsageDAO();
		ArrayList<LocationUsage> luList = luDAO.retrieveByUser(username, startDate, endDate);
		
		
		
		return null;
	}

}