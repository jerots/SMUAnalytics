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
  
    public int delete(String macAdd, String startDate, String endDate, String locId, String semanticPl, String errors) throws SQLException, IOException {
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
            
        } else {
            if(startDate.length() == 10){
                startDate += " 00:00:00";
            }else{
                startDate += ":00";
            }
            if ((startDate.length() != 19 || !Utility.checkDate(startDate))) { // if they are of the wrong length
                errors += ", invalid startdate";
            }else{
                dateFormattedStart = Utility.parseDate(startDate);
                startDate = Utility.formatDate(dateFormattedStart);
            }
        }

        //END DATE VALIDATION
        if(endDate != null && endDate.length() != 0){
            if (endDate.length() == 0) {
                errors += ", blank enddate";
                
            } else {
                if(endDate.length() == 10){
                    endDate += " 00:00:00";
                }else{
                    endDate += ":00";
                }
                if ((endDate.length() != 19 || !Utility.checkDate(endDate))) { // if they are of the wrong length or wrong checkDate
                    errors += ", invalid enddate";
                }else{
                    Date dateFormattedEnd = Utility.parseDate(endDate);
                    if(dateFormattedStart != null && dateFormattedEnd != null && dateFormattedStart.after(dateFormattedEnd)){
                        errors += ", invalid startdate";
                    }
                    endDate = Utility.formatDate(dateFormattedEnd);
                }
            }
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
    


