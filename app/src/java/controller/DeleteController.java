/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.ConnectionManager;
import dao.LocationDAO;
import dao.LocationUsageDAO;
import dao.UserDAO;
import dao.Utility;
import entity.LocationUsage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Shuwen
 */
public class DeleteController {
  
    public ArrayList<LocationUsage> delete(String macAdd, String startDate, String endDate, String startTime, String endTime, String locId, String semanticPl, ArrayList<String> error) throws SQLException {
        ArrayList<LocationUsage> deleted = null;
        Connection conn = ConnectionManager.getConnection();
        conn.setAutoCommit(false);
        String errors = "";
    
        LocationUsageDAO luDao = new LocationUsageDAO();
        
        //Starts the checking here
        //START DATE VALIDATION
        Date dateFormattedStart = null;      
        if (startDate == null) {
            errors += ", missing startdate";
        } else if (startDate.length() == 0){
            errors += ", blank startdate";
        } else {
            if(startDate.length() != 10){
                errors += ", invalid startdate";
            }else{
                if(startTime != null && startTime.length() != 0){
                    startDate += " " + startTime + ":00";
                }else{
                    startDate += " 00:00:00";
                }
                dateFormattedStart = Utility.parseDate(startDate);  
                if(dateFormattedStart == null){
                    startDate += ", invalid startdate";
                }else{
                    startDate = Utility.formatDate(dateFormattedStart);
                    if(!Utility.checkOnlyDate(startDate)){
                        errors += ", invalid startdate";
                    } else{
                        if ((startTime != null && startTime.length() != 0) && startDate.length() != 19 || !Utility.checkDate(startDate)) { // if they are of the wrong length
                            errors += ", invalid starttime";
                        }
                    }
                }
            }
        } 
        
        //END DATE VALIDATION
        Date dateFormattedEnd = null;      
        if (endDate != null) {
            if (endDate.length() == 0){
                errors += ", blank enddate";
            } else {
                if(endDate.length() != 10){
                    errors += ", invalid enddate";
                }else{
                    if(endTime != null && endTime.length() != 0){
                        endDate += " " + endTime + ":00";
                    }else{
                        endDate += " 00:00:00";
                    }
                    dateFormattedEnd = Utility.parseDate(endDate);  
                    if(dateFormattedEnd == null){
                        endDate += ", invalid enddate";
                    }else{
                        endDate = Utility.formatDate(dateFormattedEnd);
                        if(!Utility.checkOnlyDate(endDate)){
                            errors += ", invalid enddate";
                        } else{
                            if ((endTime != null && endTime.length() != 0) && endDate.length() != 19 || !Utility.checkDate(endDate)) { // if they are of the wrong length
                                errors += ", invalid endtime";
                            }
                        }
                    }
                }
            } 
        }
        
        if(dateFormattedStart != null && dateFormattedEnd != null && dateFormattedStart.after(dateFormattedEnd)){
            errors += ", invalid starttime";
        }

        //MACADDRESS VALIDATION - This one COULD be input as the login person is admin, and therefore not retrieve the user's own macadd like activeness
        if(macAdd != null && macAdd.length() != 0){
            if (!Utility.checkHexadecimal(macAdd)) {
                errors += ", invalid mac address";
                
            //Retrieves the Userlist to check the macAdd
            }else{
                UserDAO userDao = new UserDAO();
                if(!userDao.checkMacAdd(conn, macAdd)){
                    errors += ", invalid mac address";
                    
                }                
            }
        }
        
        //Location id validation
        String place = null;
        int locationId = Utility.parseInt(locId);
        if(locId != null && locId.length() != 0){
            if(locationId < 0){
                errors += ", invalid location-id";
                
                //Here, have to call for locationIdList
            } else{
                LocationDAO lDao = new LocationDAO();
                place = lDao.checkLocationId(conn, locationId);
                if (place == null) {
                    errors += ", invalid location-id";       
                }
            }
        }
        
        //SEMANTIC PLACE VALIDATION
        if(semanticPl != null && semanticPl.length() > 7){
            String school = semanticPl.substring(0, 7); //SMUSISL or SMUSISB
            int levelNum = Utility.parseInt(semanticPl.substring(7, 8));//1-5

            if (!(school.equals("SMUSISL") || school.equals("SMUSISB")) || levelNum < 1 || levelNum > 5) {
                errors += ", invalid semantic place";
            }
        } else {
            errors += ", invalid semantic place";
        }
        
        if(errors.length() == 0){
            //System.out.println(macAdd);
            
            deleted = luDao.delete(conn, macAdd, startDate, endDate, locationId, semanticPl);
            //System.out.println(macAdd);
        }else{
            error.add(errors.substring(2));
            //System.out.println(errors);
        }
        return deleted;
    }
        //    errMap.put("Number of Valid records deleted: " + (index - notFound - 1));
//                errMap.put("Number of Valid records not found in the database: " + notFound);

}   
    


