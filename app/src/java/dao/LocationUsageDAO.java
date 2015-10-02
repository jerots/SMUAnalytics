/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

/**
 *
 * @author ASUS-PC
 */
public class LocationUsageDAO {

    private ArrayList<String> unsuccessful = new ArrayList<>();
    private ArrayList<LocationUsage> locationUsageList = new ArrayList<>();

    public void insert(LocationDAO lDao, UserDAO uDao, ZipInputStream zis) throws IOException, SQLException {
        Connection conn = ConnectionManager.getConnection();
        PreparedStatement stmt = null;
        conn.setAutoCommit(false);
        Scanner sc = new Scanner(zis).useDelimiter(",|\r\n");
        sc.nextLine(); //flush title
        String sql = "insert into locationusage (timestamp, macaddress, locationid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?);";
        stmt = conn.prepareStatement(sql);

        while (sc.hasNextLine()) {
            String currLine = sc.nextLine();
            String[] arr = currLine.split(",");
            //retrieving per row

            boolean err = false;

            //check timestamp
            java.util.Date dateCheck = Utility.parseDate(arr[0]);
            String date = Utility.formatDate(dateCheck);
            if (date == null) {
                err = true;
                unsuccessful.add("invalid timestamp");
            }

            //check macAdd
            String macAdd = Utility.parseString(arr[1]);
            if (macAdd == null) {
                unsuccessful.add("mac add cannot be blank");
                err = true;
            }
            if (!Utility.checkHexadecimal(macAdd)) {
                unsuccessful.add("invalid mac address");
                err = true;
            }

            //check appid
            int locationId = Utility.parseInt(arr[2]);
            if (locationId <= 0) {
                unsuccessful.add("location id cannot be blank");
                err = true;
            }

            if (!lDao.hasLocationId(locationId)) {
                unsuccessful.add("invalid location");
                err = true;
            }

            if (!err) {
                //add to list
                stmt.setString(1, date);
                stmt.setString(2, macAdd);
                stmt.setInt(3, locationId);
                stmt.addBatch();
            }

        }
        //insert into tables
        for (String s: unsuccessful) { 
            System.out.println(s);
        }

        //closing
        if (stmt != null) {
            stmt.executeBatch();
            conn.commit();
        }
        if (sc != null) {
            sc.close();
        }
        ConnectionManager.close(conn,stmt);
    }
}
