/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.AppDAO;
import dao.AppUsageDAO;
import entity.App;
import entity.AppUsage;
import entity.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author ASUS-PC
 */
public class SmartphoneOveruseController {
    
    public HashMap <String, String> generateReport(User user, Date startDate, Date endDate){
       HashMap<String, String> result = new HashMap<String,String>();
       
       //to store the overuse index value
       HashMap <String, Integer> overuseIndex = new HashMap<String,Integer>();
       
       //add the different indexes
       overuseIndex.put("severeUsage",5);
       overuseIndex.put("severeGaming",3);
       overuseIndex.put("severeFrequency",0);
       overuseIndex.put("moderateUsage",2);
       overuseIndex.put("moderateGaming",1);
       overuseIndex.put("moderateFrequency",0);
       overuseIndex.put("lightUsage",5);
       overuseIndex.put("lightGaming",3);
       overuseIndex.put("lightFrequency",0);
       
       AppUsageDAO auDAO = new AppUsageDAO();
    
       ArrayList <AppUsage> appUsageList = auDAO.retrieveByUser(user.getMacAddress(), startDate, endDate);
       AppDAO aDao = new AppDAO();
       
       for(AppUsage au: appUsageList){
           int appId = au.getAppId();
           App app = aDao.retrieveAppbyId(appId);
           
           
           
           String category = app.getAppCategory().toLowerCase();
           if(category.equals("games")){
               
           }
          
       
           
       }
     
       return result;
       
       
    }   
    
}
