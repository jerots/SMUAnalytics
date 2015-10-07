package dao;

import entity.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class AppUsageDAO {

    private HashMap<String, Integer> duplicate;
    private HashMap<String, AppUsage> appList;
    private ArrayList<String> unsuccessful;

    public AppUsageDAO() {
        appList = new HashMap<>();
        duplicate = new HashMap<>();
        unsuccessful = new ArrayList<>();
    }

    public void insert(CSVReader reader) throws IOException, SQLException {
        Connection conn = ConnectionManager.getConnection();
        
       
        
        conn.setAutoCommit(false);
        int counter = 1;
        String sql = "insert into appusage (timestamp, macaddress, appid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?) ON DUPLICATE KEY UPDATE appid "
                + " = VALUES(appid);";
        PreparedStatement stmt = conn.prepareStatement(sql);

        String[] arr = null;
        while ((arr = reader.readNext()) != null) {
            //retrieving per row
            boolean err = false;
            boolean pass = false;

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

            String query = "select macaddress from user where macaddress = ?;";
            PreparedStatement pStmt = conn.prepareStatement(query);
            pStmt.setString(1, macAdd);

            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("macaddress") != null) {
                    pass = true;
                }
            }
            if (!pass) {
                unsuccessful.add("no matching mac address");
                err = true;
            }
            pass = false;
            pStmt.close();

            //check appid
            int appId = Utility.parseInt(arr[2]);
            if (appId <= 0) {
                unsuccessful.add("app id cannot be blank");
                err = true;
            }

            query = "select appid from app where appid = ?;";
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, appId);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt("appid") != 0) {
                    pass = true;
                }
            }
            if (!pass) {
                unsuccessful.add("invalid app");
                err = true;
            }
            pStmt.close();

            if (!err) {
                counter++;
                if (duplicate.containsKey(date + macAdd)) {
                    unsuccessful.add("duplicate row " + duplicate.get(date + macAdd));
                }
                duplicate.put(date + macAdd, counter);
                //add to list
                stmt.setString(1, date);
                stmt.setString(2, macAdd);
                stmt.setInt(3, appId);
                stmt.addBatch();
                //insert into tables
            }
        }
        stmt.executeBatch();
        conn.commit();
        
        //closing

        reader.close();
        ConnectionManager.close(conn, stmt);

    }

    public void add(CSVReader reader) throws IOException, SQLException {
        try {
            Connection conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            int counter = 1;
            String sql = "insert into appusage (timestamp, macaddress, appid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?);";
            PreparedStatement stmt = conn.prepareStatement(sql);

            String[] arr = null;
            while ((arr = reader.readNext()) != null) {
                //retrieving per row
                boolean err = false;
                boolean pass = false;

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

                String query = "select macaddress from user;";
                PreparedStatement pStmt = conn.prepareStatement(query);
                ResultSet rs = pStmt.executeQuery();
                while (rs.next()) {
                    if (!rs.getString("macaddress").equals(macAdd)) {
                        pass = true;
                    }
                }
                if (!pass) {
                    unsuccessful.add("no matching mac address");
                    err = true;
                }
                pass = false;
                pStmt.close();

                //check appid
                int appId = Utility.parseInt(arr[2]);
                if (appId <= 0) {
                    unsuccessful.add("app id cannot be blank");
                    err = true;
                }

                query = "select appid from app;";
                pStmt = conn.prepareStatement(query);
                rs = pStmt.executeQuery();
                while (rs.next()) {
                    if (rs.getInt("appid") == appId) {
                        pass = true;
                    }
                }
                if (!pass) {
                    unsuccessful.add("invalid app");
                    err = true;
                }
                pStmt.close();

                if (!err) {
                    counter++;
                    if (duplicate.containsKey(date + macAdd)) {
                        unsuccessful.add("duplicate row " + duplicate.get(date + macAdd));
                    }
                    duplicate.put(date + macAdd, counter);
                    appList.put(date + macAdd, new AppUsage(date, macAdd, appId));
                }
                ArrayList<AppUsage> appArray = (ArrayList<AppUsage>) appList.values();
                try {
                    for (AppUsage app : appArray) {
                        stmt.setString(1, app.getTimestamp());
                        stmt.setString(2, app.getMacAddress());
                        stmt.setInt(3, app.getAppId());
                        stmt.addBatch();
                    }
                    //closing
                    if (stmt != null) {
                        stmt.executeBatch();
                        conn.commit();
                    }
                } catch (BatchUpdateException e) {
                    int[] updateCounts = e.getUpdateCounts();
                    for (int i = 0; i < updateCounts.length; i++) {
                        if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                            // This method retrieves the row fail, and then searches the prikey corresponding and then uses the duplicate HashMap to find the offending row.
                            unsuccessful.add("duplicate row " + duplicate.get(appArray.get(i).getTimestamp() + appArray.get(i).getMacAddress()));
                        }
                    }
                }
            }
            reader.close();
            ConnectionManager.close(conn, stmt);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
