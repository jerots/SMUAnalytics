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
        Scanner sc = new Scanner(zis).useDelimiter(",|\r\n");
        sc.nextLine(); //flush title

        while (sc.hasNext()) {
            //retrieving per row

            boolean err = false;

            //check timestamp
            Date date = Utility.parseDate(sc.next());
            if (date == null) {
                err = true;
                unsuccessful.add("invalid timestamp");
            }

            //check macAdd
            String macAdd = Utility.parseString(sc.next());
            if (macAdd == null) {
                unsuccessful.add("mac add cannot be blank");
                err = true;
            }
            if (!Utility.checkHexadecimal(macAdd)) {
                unsuccessful.add("invalid mac address");
                err = true;
            }

            if (uDao.hasMacAdd(macAdd)) {
                unsuccessful.add("no matching mac address");
                err = true;
            }

            //check appid
            int locationId = Utility.parseInt(sc.next());
            if (locationId <= 0) {
                unsuccessful.add("location id cannot be blank");
                err = true;
            }

            if (lDao.hasLocationId(locationId)) {
                unsuccessful.add("invalid location");
                err = true;
            }

            if (!err) {
                //add to list
                LocationUsage locationU = new LocationUsage(date, macAdd, locationId);
                locationUsageList.add(locationU);

            }

        }
        //insert into tables
        for (LocationUsage lUsage : locationUsageList) {
            String sql = "insert into app (timestamp, mac-address, location-id values(?,?,?))";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, lUsage.getTimestamp());
            stmt.setString(2, "\"" + lUsage.getMacAddress() + "\"");
            stmt.setInt(3, lUsage.getLocationId());
        }

        //adding to batch
        stmt.addBatch();

        //closing
        if (stmt != null) {
            stmt.executeBatch();
            conn.commit();
            stmt.close();
        }
        if (sc != null) {
            sc.close();
        }

    }
}
