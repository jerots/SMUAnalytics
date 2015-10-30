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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Shuwen
 */
public class DeleteController {
  
    public int delete(String macAdd, String startDate, String endDate, String startTime, String endTime, String locId, String semanticPl, String errors) throws SQLException, IOException {
        int deleted = 0;
        Connection conn = ConnectionManager.getConnection();
        conn.setAutoCommit(false);
    
        LocationUsageDAO luDao = new LocationUsageDAO();
        
        //Starts the checking here
        //START DATE VALIDATION
        Date dateFormattedStart = null;        
        if (startDate == null) {
            errors += ", missing startdate";
            
        } else if (startDate.length() == 0) {
            errors += ", blank startdate";
            dateFormattedStart = Utility.parseDate(startDate);
            if(startDate.length() != 10 || dateFormattedStart == null){
                errors += ", invalid startdate";
            }else{
                startDate = Utility.formatDate(dateFormattedStart);
                if(startDate == null || !Utility.checkOnlyDate(startDate)){
                    errors += ", invalid startdate";
                }
            }
        } 
        
        if(startTime != null && startTime.length() != 0){
            startDate += " " + startTime + ":00";
            if ((startDate.length() != 19 || !Utility.checkDate(startDate))) { // if they are of the wrong length
                errors += ", invalid starttime";
            }
        }else{
            startDate += " 00:00:00";
        }
        
        //END DATE VALIDATION
        Date dateFormattedEnd = null;
        if (endDate == null) {
            errors += ", missing enddate";
        } else if (endDate.length() == 0) {
            errors += ", blank enddate";
            dateFormattedEnd = Utility.parseDate(endDate);
            if(endDate.length() != 10 || dateFormattedEnd == null){
                errors += ", invalid enddate";
            }else{
                endDate = Utility.formatDate(dateFormattedEnd);
                if(endDate == null || !Utility.checkOnlyDate(endDate)){
                    errors += ", invalid enddate";
                }
            }
        } 
        
        if(endTime != null && endTime.length() != 0){
            endDate += " " + endTime + ":00";
            if ((endDate.length() != 19 || !Utility.checkDate(endDate))) { // if they are of the wrong length
                errors += ", invalid endtime";
            }
        }else{
            endDate += " 00:00:00";
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
        int locationId = Utility.parseInt(locId);
        if(locationId != 0){
            if(locationId < 0){
                errors += ", invalid location-id";
                
                //Here, have to call for locationIdList
            } else{
                LocationDAO lDao = new LocationDAO();
                if (!lDao.checkLocationId(conn, locationId)) {
                    errors += ", invalid location-id";       
                }
            }
        }
        
        //SEMANTIC PLACE VALIDATION
        if(semanticPl != null && semanticPl.length() != 0){
            String school = semanticPl.substring(0, 7); //SMUSISL or SMUSISB
            int levelNum = Utility.parseInt(semanticPl.substring(7, 8));//1-5

            if (!(school.equals("SMUSISL") || school.equals("SMUSISB")) || levelNum < 1 || levelNum > 5) {
                errors += ", invalid semantic place";
            }
        }
        
        if(errors.length() == 0){
            int[] delete = luDao.delete(conn, macAdd, startDate, endDate, locationId, semanticPl);
            deleted = delete[0];
        }else{
            errors.substring(2);
        }
        return deleted;
    }
        //    errMap.put("Number of Valid records deleted: " + (index - notFound - 1));
//                errMap.put("Number of Valid records not found in the database: " + notFound);

}   
    


