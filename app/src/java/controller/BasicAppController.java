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
	
	
	public HashMap<String, String[]> generateReport (Date startDate, Date endDate){
		HashMap<String, String[]> result = new HashMap<String, String[]>();
		
		result.put("intense-count", new String[2]);
		result.put("normal-count", new String[2]);
		result.put("mild-count", new String[2]);
		
		
		AppUsageDAO auDAO = new AppUsageDAO();
		ArrayList<String> userList = auDAO.retrieveUsers(startDate, endDate);
		
		for (int i = 0 ; i < userList.size(); i ++){
			
			String currentMac = userList.get(i);
			
			ArrayList<AppUsage> userUsage = auDAO.retrieveByUser(currentMac, startDate, endDate);
			
			
		}
		
		
		
		
		
		return result;
	}
	
}
