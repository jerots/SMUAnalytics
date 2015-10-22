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
import java.util.HashSet;
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

	public HashMap<String, String> generatePhysicalReport(Date startDate, Date endDate, String username) {

		//INSTANTIATING
		HashMap<String, String> result = new HashMap<String, String>();
		ArrayList<LocationUsage> totalLUList = new ArrayList<LocationUsage>();

		LocationUsageDAO luDAO = new LocationUsageDAO();
		UserDAO userDAO = new UserDAO();
		User user = userDAO.retrieve(username);
		String macaddress = user.getMacAddress();

		//get logged-on user's locationUsage in the day
		ArrayList<LocationUsage> luList = luDAO.retrieveByUser(macaddress, startDate, endDate);
		Date oldTime = null;
		int totalTime = 0;
		int prevLocationId = 0;
		Date startInLocation = null;

		//calculate time for the first locationUsage
		if (luList.size() > 0) {
			LocationUsage firstLU = luList.get(0);
			oldTime = firstLU.getDate();
			startInLocation = oldTime;
			prevLocationId = firstLU.getLocationId();

		}

		for (int i = 1; i < luList.size(); i++) {

			LocationUsage lu = luList.get(i);
			Date newTime = lu.getDate();
			int locationId = lu.getLocationId();

			if (prevLocationId != locationId) {
				Date endInLocation = oldTime;

				//GET ALL locationUsage in this location, time period EXCEPT user
				luDAO.retrieve(startInLocation, endInLocation, prevLocationId, macaddress, totalLUList);

				//reset variables
				prevLocationId = locationId;
				startInLocation = newTime;
			}

			long difference = newTime.getTime() - oldTime.getTime();

			if (difference <= 300) {
				totalTime += difference;
			} else {
				totalTime += 300;
			}
			prevLocationId = locationId;
			oldTime = newTime;

		}

		//Calculate the time for the last locationUsage
		if (luList.size() > 0) {
			long difference = endDate.getTime() - oldTime.getTime();
			if (difference <= 300) {
				totalTime += difference;
			} else {
				totalTime += 300;
			}
		}

		result.put("total-time-spent-in-sis", "" + totalTime);

		//CALCULATE group time
		//get all users in totalLUList
		HashSet<String> userMacList = new HashSet<String>();
		for (LocationUsage lu : totalLUList) {
			userMacList.add(lu.getMacAddress());
		}

		
		
		//CALCULATE user instances
		ArrayList<ArrayList<HashMap<String, Date>>> userInstances = new ArrayList<ArrayList<HashMap<String, Date>>>();
		for (String userMac : userMacList) {
			ArrayList<HashMap<String, Date>> userInstance = new ArrayList<HashMap<String, Date>>();

			//INITIALISE
			LocationUsage firstLU = totalLUList.get(0);
			Date oldFriendTime = firstLU.getDate();
			Date friendStartTime = oldFriendTime;
			for (int i = 1; i < totalLUList.size(); i++) {

				LocationUsage lu = totalLUList.get(i);
				Date newFriendTime = lu.getDate();

				long difference = newFriendTime.getTime() - oldFriendTime.getTime();

				if (difference > 300) {
					HashMap<String, Date> instanceRecord = new HashMap<String, Date>();
					instanceRecord.put("start", new Date(friendStartTime.getTime()));
					instanceRecord.put("end", new Date(oldFriendTime.getTime()));
					friendStartTime = newFriendTime;
					userInstance.add(instanceRecord);
				}

				oldFriendTime = newFriendTime;
			}
			HashMap<String, Date> instanceRecord = new HashMap<String, Date>();
			instanceRecord.put("start", new Date(friendStartTime.getTime()));
			instanceRecord.put("end", new Date(oldFriendTime.getTime()));
			userInstance.add(instanceRecord);

		}
		
		
		//COMPRESS INSTANCES
		
		ArrayList<HashMap<String,Date>> mainInstance = null;
		if (userInstances.size() > 0){
			mainInstance = userInstances.get(0);
		}
		
		for (ArrayList<HashMap<String,Date>> userInstance : userInstances){
			
			for (HashMap<String,Date> mainMap: mainInstance){
				
				for (HashMap<String,Date> userMap : userInstance){
					
					Date userStartDate = userMap.get("start");
					Date userEndDate = userMap.get("end");
					Date mainStart = mainMap.get("start");
					Date mainEnd = mainMap.get("end");
					
					
					
					
				}
				
				
			}
			
			
		}
		

//		if (totalLUList.size() > 0) {
//			LocationUsage firstLU = totalLUList.get(0);
//			oldFriendTime = firstLU.getDate();
//			firstFriendTime = oldFriendTime;
//		}
//
//		for (int i = 1; i < totalLUList.size(); i++) {
//
//			LocationUsage lu = totalLUList.get(i);
//			Date newFriendTime = lu.getDate();
//
//			long difference = newFriendTime.getTime() - oldFriendTime.getTime();
//
//		}
		return result;
	}

}
