/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.LocationDAO;
import dao.LocationUsageDAO;
import entity.LocationUsage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.TreeMap;

/**
 *
 * @author jeremyongts92
 */
public class HeatmapController {

	public TreeMap<String, ArrayList<LocationUsage>> generateHeatmap(Date datetime, String floor) {
		
		LocationUsageDAO luDAO = new LocationUsageDAO();
		
		//for each location, count unique users
		TreeMap<String, ArrayList<LocationUsage>> result = new TreeMap<String, ArrayList<LocationUsage>>();

		LocationDAO locDAO = new LocationDAO();
		
		//retrieve all the floors
		ArrayList<String> floorLocationList = locDAO.retrieve(floor);
		
		
		//for each location in the floor
		for (int i = 0; i < floorLocationList.size(); i++) { 
			String loc = floorLocationList.get(i);
			ArrayList<LocationUsage> luList = luDAO.retrieve(datetime, loc);
			
			result.put(loc, luList);
		}
		
		
		return result;
	}

}
