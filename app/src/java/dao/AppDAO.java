package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ASUS-PC
 */
public class AppDAO {


    public AppDAO() {
    }

    public int[] insert(CSVReader reader, HashMap<Integer, String> errMap) throws IOException, SQLException {
        Connection conn = ConnectionManager.getConnection();
        conn.setAutoCommit(false);
        String sql = "insert into app values(?,?,?) ON DUPLICATE KEY UPDATE appname = appname, appcategory = appcategory;";
        PreparedStatement stmt = conn.prepareStatement(sql);
        String[] arr = null;
        //*
        int index = 2;
        while ((arr = reader.readNext()) != null) {
            boolean err = false;

            int appId = Utility.parseInt(arr[0]);
            if (appId <= 0) {
                
                String errorMsg = errMap.get(index);
                if (errorMsg == null){
                    errMap.put(index, "invalid app id");
                } else {
                    errMap.put(index, errorMsg + "," + "invalid app id");
                }
                
                
                err = true;
            }

            String name = Utility.parseString(arr[1]);
            name = name.replace("\"", "");
            if (name == null) {
                
                String errorMsg = errMap.get(index);
                if (errorMsg == null){
                    errMap.put(index, "name cannot be blank");
                } else {
                    errMap.put(index, errorMsg + "," + "name cannot be blank");
                }
                err = true;
            }

            String cat = Utility.parseString(arr[2]);
            cat = cat.replace("\"", "");

            if (cat == null) {
                
                String errorMsg = errMap.get(index);
                if (errorMsg == null){
                    errMap.put(index, "category cannot be blank");
                } else {
                    errMap.put(index, errorMsg + "," + "category cannot be blank");
                }
                
                err = true;

            }

            if (!Utility.checkCategory(cat)) {
                
                String errorMsg = errMap.get(index);
                if (errorMsg == null){
                    errMap.put(index, "invalid category");
                } else {
                    errMap.put(index, errorMsg + "," + "invalid category");
                }
                err = true;
            }

            if (!err) {
                //insert into tables
                stmt.setInt(1, appId);
                stmt.setString(2, name);
                stmt.setString(3, cat);
                stmt.addBatch();
            }
            index++;
        }
            //closing

        int[] updatedRecords = stmt.executeBatch();
        conn.commit();

        reader.close();
        ConnectionManager.close(conn, stmt);
        
        
        return updatedRecords;
    }
}
