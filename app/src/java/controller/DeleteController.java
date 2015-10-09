/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.LocationUsageDAO;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author Shuwen
 */
public class DeleteController {
  
    public int delete(String macAdd, String startDate, String endDate) throws SQLException, IOException {
        
        LocationUsageDAO luDao = new LocationUsageDAO();

        return luDao.delete(macAdd, startDate, endDate);
    }
    
//    errMap.put("Number of Valid records deleted: " + (index - notFound - 1));
//                errMap.put("Number of Valid records not found in the database: " + notFound);
}

