/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import entity.*;
import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.HashMap;

/**
 *
 * @author ASUS-PC
 */
public class LocationUsageDAO {

    private HashMap<String, LocationUsage> locationList;
    private HashMap<String, Integer> duplicate;

    public LocationUsageDAO() {
        duplicate = new HashMap<>();
        locationList = new HashMap<>();
    }

    public int[] insert(CSVReader reader, HashMap<Integer, String> errMap) throws IOException, SQLException {
        Connection conn = ConnectionManager.getConnection();
        conn.setAutoCommit(false);
        int index = 2;
        String sql = "insert into locationusage (timestamp, macaddress, locationid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?) ON DUPLICATE KEY UPDATE locationid = "
                + "VALUES(locationid);";
        PreparedStatement stmt = conn.prepareStatement(sql);

        String[] arr = null;
        while ((arr = reader.readNext()) != null) {
            //retrieving per row
            boolean err = false;

            //check timestamp
            String date = Utility.parseString(arr[0]);
            if (date == null || !Utility.checkDate(date)) {
                String errorMsg = errMap.get(index);
                if (errorMsg == null) {
                    errMap.put(index, "invalid timestamp");
                } else {
                    errMap.put(index, errorMsg + "," + "invalid timestamp");
                }
                err = true;
            }

            //check macAdd
            String macAdd = Utility.parseString(arr[1]);
            if (macAdd == null) {
                String errorMsg = errMap.get(index);
                if (errorMsg == null) {
                    errMap.put(index, "mac address cannot be blank");
                } else {
                    errMap.put(index, errorMsg + "," + "mac address cannot be blank");
                }
                err = true;
            }
            if (macAdd != null && !Utility.checkHexadecimal(macAdd)) {
                String errorMsg = errMap.get(index);
                if (errorMsg == null) {
                    errMap.put(index, "invalid mac address");
                } else {
                    errMap.put(index, errorMsg + "," + "invalid mac address");
                }
                err = true;
            }

            //check appid
            int locationId = Utility.parseInt(arr[2]);
            if (locationId <= 0) {
                String errorMsg = errMap.get(index);
                if (errorMsg == null) {
                    errMap.put(index, "location id cannot be blank");
                } else {
                    errMap.put(index, errorMsg + "," + "location id cannot be blank");
                }
                err = true;
            } else {
                String query = "select locationid from location where locationid = ?;";
                PreparedStatement pStmt = conn.prepareStatement(query);
                pStmt.setInt(1, locationId);
                ResultSet rs = pStmt.executeQuery();
                if(!rs.next()) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "invalid location");
                    } else {
                        errMap.put(index, errorMsg + "," + "invalid location");
                    }
                    err = true;
                }
                pStmt.close();
            }
            
            if (!err) {
                if (duplicate.containsKey(date + macAdd)) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "duplicate row " + duplicate.get(date + macAdd));
                    } else {
                        errMap.put(index, errorMsg + "," + "duplicate row " + duplicate.get(date + macAdd));
                    }
                }
                duplicate.put(date + macAdd, index);
                //add to list
                stmt.setString(1, date);
                stmt.setString(2, macAdd);
                stmt.setInt(3, locationId);
                stmt.addBatch();
            }
            index++;

        }
        //insert into tables

        int[] updateCounts = stmt.executeBatch();
        conn.commit();
        
        //close
        reader.close();
        ConnectionManager.close(conn, stmt);
        return updateCounts;
    }

    public int[] add(CSVReader reader, HashMap<Integer, String> errMap) throws IOException, SQLException {
        int[] updateCounts = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            int index = 2;
            String sql = "insert into locationusage (timestamp, macaddress, locationid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?);";
            PreparedStatement stmt = conn.prepareStatement(sql);

            String[] arr = null;
            while ((arr = reader.readNext()) != null) {
                //retrieving per row
                boolean err = false;

                //check timestamp
                String date = Utility.parseString(arr[0]);
                if (date == null || !Utility.checkDate(date)) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "invalid timestamp");
                    } else {
                        errMap.put(index, errorMsg + "," + "invalid timestamp");
                    }
                    err = true;
                }

                //check macAdd
                String macAdd = Utility.parseString(arr[1]);
                if (macAdd == null) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "mac address cannot be blank");
                    } else {
                        errMap.put(index, errorMsg + "," + "mac address cannot be blank");
                    }
                    err = true;
                }
                if (macAdd != null && !Utility.checkHexadecimal(macAdd)) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "invalid mac address");
                    } else {
                        errMap.put(index, errorMsg + "," + "invalid mac address");
                    }
                    err = true;
                }

                //check appid
                int locationId = Utility.parseInt(arr[2]);
                if (locationId <= 0) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "location id cannot be blank");
                    } else {
                        errMap.put(index, errorMsg + "," + "location id cannot be blank");
                    }
                    err = true;
                }else{
                    String query = "select locationid from location where locationid = ?;";
                    PreparedStatement pStmt = conn.prepareStatement(query);
                    pStmt.setInt(1, locationId);
                    ResultSet rs = pStmt.executeQuery();
                    if (!rs.next()) {
                        String errorMsg = errMap.get(index);
                        if (errorMsg == null) {
                            errMap.put(index, "invalid location");
                        } else {
                            errMap.put(index, errorMsg + "," + "invalid location");
                        }
                        err = true;
                    }
                    pStmt.close();
                }

                if (!err) {
                    if (duplicate.containsKey(date + macAdd)) {
                        String errorMsg = errMap.get(index);
                        if (errorMsg == null) {
                            errMap.put(index, "duplicate row " + duplicate.get(date + macAdd));
                        } else {
                            errMap.put(index, errorMsg + "," + "duplicate row " + duplicate.get(date + macAdd));
                        }
                    }
                    duplicate.put(date + macAdd, index);
                    locationList.put(date + macAdd, new LocationUsage(date, macAdd, locationId));
                }
                index++;

            }
            ArrayList<LocationUsage> locList = (ArrayList<LocationUsage>) locationList.values();
            
            try {
                for (LocationUsage loc : locList) {
                    stmt.setString(1, loc.getTimestamp());
                    stmt.setString(2, loc.getMacAddress());
                    stmt.setInt(3, loc.getLocationId());
                    stmt.addBatch();
                }
                //closing
                if (stmt != null) {
                    stmt.executeBatch();
                    conn.commit();
                }
            } catch (BatchUpdateException e) {
                updateCounts = e.getUpdateCounts();
                for (int i = 0; i < updateCounts.length; i++) {
                    if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                        // This method retrieves the row fail, and then searches the locationid corresponding and then uses the duplicate HashMap to find the offending row.
                        int row = duplicate.get(locList.get(i).getTimestamp() + locList.get(i).getMacAddress());
                        String errorMsg = errMap.get(row);
                        if (errorMsg == null) {
                            errMap.put(row, "duplicate row ");
                        } else {
                            errMap.put(row, errorMsg + "," + "duplicate row ");
                        }
                    }
                }
            }
            reader.close();
            ConnectionManager.close(conn, stmt);
            
        } catch (NullPointerException e) {

        }
        return updateCounts;
    }

    public int[] delete(CSVReader reader, HashMap<Integer, String> errMap) throws IOException, SQLException {
        int[] toReturn = new int[2];
        int index = 2; //counts the row of the record.
        int notFound = 0;
        try {
            
            Connection conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "delete from locationusage where timestamp = STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and macaddress = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            String[] arr = null;
            while ((arr = reader.readNext()) != null) {
                //retrieving per row
                boolean err = false;

                //check timestamp
                String date = Utility.parseString(arr[0]);
                if (date == null || !Utility.checkDate(date)) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "invalid timestamp");
                    } else {
                        errMap.put(index, errorMsg + "," + "invalid timestamp");
                    }
                    err = true;
                }

                //check macAdd
                String macAdd = Utility.parseString(arr[1]);
                if (macAdd == null) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "mac address cannot be blank");
                    } else {
                        errMap.put(index, errorMsg + "," + "mac address cannot be blank");
                    }
                    err = true;
                }
                if (macAdd != null && !Utility.checkHexadecimal(macAdd)) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "invalid mac address");
                    } else {
                        errMap.put(index, errorMsg + "," + "invalid mac address");
                    }
                    err = true;
                }

                if (!err) {
                    stmt.setString(1, date);
                    stmt.setString(2, macAdd);
                    stmt.addBatch();
                }
                if (stmt != null) {
                    int[] updateCounts = stmt.executeBatch();
                    conn.commit();
                    for (int i : updateCounts) {
                        if (i == 0) {
                            notFound++;
                        }
                    }
                }
                index++;
            }
            
            reader.close();
            ConnectionManager.close(conn, stmt);
          
        } catch (NullPointerException e) {

        }
        toReturn[0] = index;
        toReturn[1] = notFound;
        return toReturn;
    }

    public int delete(String macAdd, String startDate, String endDate) throws IOException, SQLException {
        int[] errors = null;
        try {
            Connection conn = ConnectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "delete from locationusage where timestamp BETWEEN STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s')"
                    + " and macaddress = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);

            boolean err = false;

            if (!err) {
                stmt.setString(1, startDate + "00:00:00");
                stmt.setString(2, endDate + "23:59:59");
                stmt.setString(3, macAdd);
                stmt.addBatch();
            }
            if (stmt != null) {
                errors = stmt.executeBatch();
                conn.commit();
            }
            ConnectionManager.close(conn, stmt);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return errors[0];
    }

    public ArrayList<LocationUsage> retrieve(java.util.Date date, String loc) {
        ArrayList<LocationUsage> result = new ArrayList<LocationUsage>();
        try {
            String sql = "SELECT timestamp, macaddress, lu.locationid \n"
                    + "FROM (\n"
                    + "SELECT MAX(TIMESTAMP) as timestamp, macaddress, locationid FROM locationusage\n"
                    + "WHERE timestamp >= STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') \n"
                    + "AND timestamp < STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') \n"
                    + "group by macaddress\n"
                    + ") as lu,\n"
                    + "location l\n"
                    + "WHERE \n"
                    + "lu.locationid = l.locationid\n"
                    + "AND semanticplace = ? \n"
                    + ";";

            Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            Date before = new java.sql.Date(date.getTime() - 900000);
            Date after = new java.sql.Date(date.getTime());
            ps.setString(1, Utility.formatDate(before)); //15 minutes before
            ps.setString(2, Utility.formatDate(after));
            ps.setString(3, loc);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp(1);
                String macAddress = rs.getString(2);
                String locationId = rs.getString(3);

                LocationUsage curr = new LocationUsage(Utility.formatDate(new Date(timestamp.getTime())), macAddress, Integer.parseInt(locationId));
                result.add(curr);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

}
