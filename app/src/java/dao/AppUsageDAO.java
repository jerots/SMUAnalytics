package dao;

import entity.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.*;
import com.opencsv.CSVReader;
import java.sql.ResultSet;

public class AppUsageDAO {

    private ArrayList<String> unsuccessful = new ArrayList<>();

    public AppUsageDAO() {
    }

    public void insert(AppDAO aDao, UserDAO uDao, CSVReader reader) throws IOException, SQLException {
        try{
            Connection conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "insert into appusage (timestamp, macaddress, appid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?) ON DUPLICATE KEY UPDATE appid "
                    + "= VALUES(appid);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            int counter = 0;
            String[] arr = null;
            while ((arr = reader.readNext()) != null) {
                //retrieving per row
                boolean err = false;
                //check timestamp
                java.util.Date format = Utility.parseDate(arr[0]);
                
                if (format == null) {
                    err = true;
                    unsuccessful.add("invalid timestamp");
                }
                String date = Utility.formatDate(format);
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

                if (!uDao.hasMacAdd(macAdd)) {
                    unsuccessful.add("no matching mac address");
                    err = true;
                }

                //check appid
                int appId = Utility.parseInt(arr[2]);
                if (appId <= 0) {
                    unsuccessful.add("app id cannot be blank");
                    err = true;
                }

                if (!aDao.hasAppId(appId)) {
                    unsuccessful.add("invalid app");
                    err = true;
                }

                if (!err) {
                    counter++;
                    //add to list
                    stmt.setString(1, date);
                    stmt.setString(2, macAdd);
                    stmt.setInt(3, appId);
                    stmt.addBatch();
                    //insert into tables
                }
            }
            //closing
            if (stmt != null) {
                int[] errors = stmt.executeBatch();
                System.out.println(errors.length);
                for(int i = 0; i < counter - errors.length; i++){
                    unsuccessful.add("duplicate row");
                    System.out.println(i);
                }
                conn.commit();
            }
            reader.close();
            ConnectionManager.close(conn, stmt);
        } catch (NullPointerException e) {
        }
    }
}
