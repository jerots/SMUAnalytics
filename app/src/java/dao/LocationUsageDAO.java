/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.csvreader.CsvReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import entity.*;
import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.HashMap;
import java.util.TreeMap;

/**
 *
 * @author ASUS-PC
 */
public class LocationUsageDAO {

    private TreeMap<String, LocationUsage> locationList;
    private TreeMap<String, Integer> duplicate;

    public LocationUsageDAO() {
        duplicate = new TreeMap<>();
        locationList = new TreeMap<>();
    }

    public int[] insert(CsvReader reader, TreeMap<Integer, String> errMap, Connection conn, HashMap<Integer, String> locationIdList) throws IOException {
        int[] updateCounts = {};
        try {
            int index = 2;

            String sql = "insert into locationusage values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?) ON DUPLICATE KEY UPDATE locationid = "
                    + "VALUES(locationid);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            reader.readHeaders();
            while (reader.readRecord()) {
                //retrieving per row
                boolean err = false;
                String errorMsg = errMap.get(index);
                if (errorMsg == null) {
                    errorMsg = "";
                }
                //check timestamp
                String date = Utility.parseString(reader.get("timestamp"));
                if (date == null) {
                    errorMsg += ",invalid timestamp";
                    err = true;
                }

                //check macAdd
                String macAdd = Utility.parseString(reader.get("mac-address"));
                if (macAdd == null) {
                    errorMsg += ",mac address cannot be blank";
                    err = true;
                } else if (!Utility.checkHexadecimal(macAdd)) {

                    errorMsg += ",invalid mac address";
                    err = true;
                }

                //check locid
                int locationId = Utility.parseInt(reader.get("location-id"));
                if (locationId <= 0) {
                    errorMsg += ",location id cannot be blank";
                    err = true;
                } else if (!locationIdList.containsKey(locationId)) {

                    errorMsg += ",invalid location";
                    err = true;

                }

                if (!err) {
                    String key = date + macAdd;
                    Integer exisMac = duplicate.get(key);
                    if (exisMac != null) {

                        errMap.put(index, "duplicate row " + exisMac);

                    }
                    duplicate.put(key, index);
                    //add to list
                    stmt.setString(1, date);
                    stmt.setString(2, macAdd);
                    stmt.setInt(3, locationId);
                    stmt.addBatch();
                } else {

                    errMap.put(index, errorMsg.substring(1));
                }
                index++;
//			if (index % 10000 == 0){
//				stmt.executeBatch();
//			}

            }
            //insert into tables

            updateCounts = stmt.executeBatch();
            conn.commit();
            stmt.close();
        } catch (SQLException e) {

        }
        return updateCounts;
    }

    public int add(CsvReader reader, TreeMap<Integer, String> errMap, Connection conn) throws IOException {
        int updateCounts = 0;
        try {
            int index = 2;
            String sql = "insert into locationusage (timestamp, macaddress, locationid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            reader.readHeaders();
            while (reader.readRecord()) {
                //retrieving per row
                boolean err = false;

                //check timestamp
                String date = Utility.parseString(reader.get("timestamp"));
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
                String macAdd = Utility.parseString(reader.get("mac-address"));
                if (macAdd == null) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "mac address cannot be blank");
                    } else {
                        errMap.put(index, errorMsg + "," + "mac address cannot be blank");
                    }
                    err = true;
                } else if (!Utility.checkHexadecimal(macAdd)) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "invalid mac address");
                    } else {
                        errMap.put(index, errorMsg + "," + "invalid mac address");
                    }
                    err = true;
                }

                //check appid
                int locationId = Utility.parseInt(reader.get("location-id"));
                if (locationId == -1) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "location id cannot be blank");
                    } else {
                        errMap.put(index, errorMsg + "," + "location id cannot be blank");
                    }
                    err = true;

                    //IF LOCATION ID NOT BLANK
                } else {

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

                //IF ALL VALIDATIONS ARE PASSED
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

                //row number increased
                index++;

            }

            //CHECK FOR DUPLICATES IN DATABASE
            ArrayList<LocationUsage> locList = new ArrayList<LocationUsage>(locationList.values());

            try {
                for (LocationUsage loc : locList) {
                    stmt.setString(1, loc.getTimestamp());
                    stmt.setString(2, loc.getMacAddress());
                    stmt.setInt(3, loc.getLocationId());
                    stmt.addBatch();
                }
                int[] updatedArr = stmt.executeBatch();
                for (int i : updatedArr) {
                    updateCounts += i;
                }
                conn.commit();

                //CATCH WHEN THERE IS DUPLICATE
            } catch (BatchUpdateException e) {
                int[] updatedArr = e.getUpdateCounts();

                for (int i = 0; i < updatedArr.length; i++) {
                    if (updatedArr[i] == Statement.EXECUTE_FAILED) {
                        // This method retrieves the row fail, and then searches the locationid corresponding and then uses the duplicate TreeMap to find the offending row.
                        int row = duplicate.get(locList.get(i).getTimestamp() + locList.get(i).getMacAddress());
                        String errorMsg = errMap.get(row);
                        if (errorMsg == null) {
                            errMap.put(row, "duplicate row ");
                        } else {
                            errMap.put(row, errorMsg + "," + "duplicate row ");
                        }
                    }
                    if (updatedArr[i] >= 0) {

                        updateCounts += updatedArr[i];
                    }
                }
            }
            reader.close();
            stmt.close();
        } catch (SQLException e) {

        }
        return updateCounts;
    }

    public int[] delete(CsvReader reader, TreeMap<Integer, String> errMap, Connection conn) throws IOException {
        int[] toReturn = new int[2];
        int index = 2; //counts the row of the record.
        int notFound = 0;
        int found = 0;
        try {
            String sql = "delete from locationusage where timestamp = STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and macaddress = ?;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            reader.readHeaders();
            while (reader.readRecord()) {
                //retrieving per row
                boolean err = false;

                //check timestamp
                String date = Utility.parseString(reader.get("timestamp"));
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
                String macAdd = Utility.parseString(reader.get("mac-address"));
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
                    found++;
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

        } catch (SQLException e) {

        }
        toReturn[0] = found - notFound; //Valid Records which have successfully deleted rows in the database
        toReturn[1] = notFound; //Valid Records which are succesful but have not deleted rows in the database
        return toReturn;
    }

    public ArrayList<LocationUsage> delete(Connection conn, String macAdd, String startDate, String endDate, int locationId, String semanticPlace) throws SQLException {
        ArrayList<LocationUsage> lList = new ArrayList<LocationUsage>();
        int stringCount = 2;
        try {
            String sql = "SELECT lu.locationid, macaddress, lu.timestamp, semanticplace FROM locationusage lu, location l WHERE lu.locationid = l.locationid"
                    + " AND timestamp >= STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s')";

            if (endDate != null) {
                sql += " AND timestamp < STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s')";
            }
            if (locationId > 0) {
                sql += " AND lu.locationid = ?";
            } else if (semanticPlace != null) {
                sql += " AND semanticplace = ?";
            }
            if (macAdd != null) {
                sql += " AND macaddress = ?";
            }
            sql += " ;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, startDate);
            if (endDate != null) {
                stmt.setString(stringCount, endDate);
                stringCount++;
            }
            if (locationId > 0) {
                stmt.setInt(stringCount, locationId);
                stringCount++;
            } else if (semanticPlace != null) {
                stmt.setString(stringCount, semanticPlace);
                stringCount++;
            }
            if (macAdd != null) {
                stmt.setString(stringCount, macAdd);
            }
            stringCount = 2;
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int locId = rs.getInt(1);
                String macAddress = rs.getString(2);
                String timestamp = rs.getString(3);
                String semPlace = rs.getString(4);
                lList.add(new LocationUsage(timestamp, macAddress, new Location(locId, semPlace)));
            }
            rs.close();
            stmt.close();
            sql = "DELETE FROM locationusage WHERE timestamp >= STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s')";

            if (endDate != null) {
                sql += " AND timestamp < STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s')";
            }
            if (locationId > 0) {
                sql += " AND locationid = ?";
            } else if (semanticPlace != null) {
                sql += " AND locationid IN (SELECT locationid FROM location WHERE semanticplace = ?)";
            }
            if (macAdd != null) {
                sql += " AND macaddress = ?";
            }
            sql += " ;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, startDate);
            if (endDate != null) {
                ps.setString(stringCount, endDate);
                stringCount++;
            }
            if (locationId > 0) {
                ps.setInt(stringCount, locationId);
                stringCount++;
            } else if (semanticPlace != null) {
                ps.setString(stringCount, semanticPlace);
                stringCount++;
            }
            if (macAdd != null) {
                ps.setString(stringCount, macAdd);
            }
            ps.addBatch();
            ps.executeBatch();
            conn.commit();

            ConnectionManager.close(conn, stmt);

        } catch (NullPointerException e) {
            
        } catch (SQLException e) {
        }
        return lList;
    }

    public ArrayList<LocationUsage> retrieve(java.util.Date date, String loc) {
        ArrayList<LocationUsage> result = new ArrayList<LocationUsage>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
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

            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);

            Date before = new java.sql.Date(date.getTime() - 900000);
            Date after = new java.sql.Date(date.getTime());
            ps.setString(1, Utility.formatDate(before)); //15 minutes before
            ps.setString(2, Utility.formatDate(after));
            ps.setString(3, loc);

            rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp(1);
                String macAddress = rs.getString(2);
                String locationId = rs.getString(3);

                LocationUsage curr = new LocationUsage(Utility.formatDate(new Date(timestamp.getTime())), macAddress, Integer.parseInt(locationId));
                result.add(curr);

            }
            ConnectionManager.close(conn, ps, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }

    public HashMap<String, LocationUsage> retrieveByFloor(java.util.Date date, String floor) {
        HashMap<String, LocationUsage> result = new HashMap<String, LocationUsage>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "select * \n"
                    + "    from appusage au, locationusage lu,location l \n"
                    + "    where au.macaddress = lu.macaddress\n"
                    + "    and lu.locationid = l.locationid \n"
                    + "    and semanticplace like ?\n"
                    + "    AND lu.timestamp >= ? and lu.timestamp <= ?\n"
                    + "    and au.timestamp >= ? and au.timestamp <= ?\n"
                    + "    order by lu.macaddress, lu.timestamp;";

            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);

            Date before = new java.sql.Date(date.getTime() - (15 * 60 * 1000));
            Date after = new java.sql.Date(date.getTime());
            ps.setString(1, "SMUSIS" + floor + "%");
            ps.setString(2, Utility.formatDate(before)); //15 minutes before
            ps.setString(3, Utility.formatDate(after));
            ps.setString(4, Utility.formatDate(before)); //15 minutes before
            ps.setString(5, Utility.formatDate(after));

            rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp(4);
                String macAddress = rs.getString(5);
                int locationId = rs.getInt(6);
                String semanticplace = rs.getString(8);
                LocationUsage curr = new LocationUsage(Utility.formatDate(new Date(timestamp.getTime())), macAddress, new Location(locationId, semanticplace));
                result.put(macAddress, curr);

            }
            ConnectionManager.close(conn, ps, rs);
        } catch (SQLException e) {
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }

    public ArrayList<LocationUsage> retrieveByUser(String macAdd, java.util.Date startDate, java.util.Date endDate) {

        ArrayList<LocationUsage> result = new ArrayList<LocationUsage>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = " select * from locationusage\n"
                    + " WHERE macaddress = ? \n"
                    + " AND timestamp >= ? AND timestamp <= ? \n"
                    + " ORDER BY timestamp;";

            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, macAdd);
            ps.setString(2, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(3, new java.sql.Timestamp(endDate.getTime()).toString());

            rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp(1);
                String macAddress = rs.getString(2);
                String locationId = rs.getString(3);

                LocationUsage curr = new LocationUsage(Utility.formatDate(new Date(timestamp.getTime())), macAddress, Integer.parseInt(locationId));
                result.add(curr);

            }
            ConnectionManager.close(conn, ps, rs);
        } catch (SQLException e) {
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;

    }

    public void retrieve(java.util.Date startInLocation, java.util.Date endInLocation, int prevLocationId, String macaddress, ArrayList<LocationUsage> totalAUList) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "select * from locationusage\n"
                    + " WHERE timestamp >= ? AND timestamp < ? \n"
                    + " AND locationid = ? \n"
                    + " AND macaddress != ? \n"
                    + " ORDER BY timestamp\n"
                    + " ;";

            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, new java.sql.Timestamp(startInLocation.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endInLocation.getTime()).toString());
            ps.setInt(3, prevLocationId);
            ps.setString(4, macaddress);

            rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp(1);
                String macAddress = rs.getString(2);
                String locationId = rs.getString(3);

                LocationUsage curr = new LocationUsage(Utility.formatDate(new Date(timestamp.getTime())), macAddress, Integer.parseInt(locationId));
                totalAUList.add(curr);

            }
            ConnectionManager.close(conn, ps, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

    }
    
    public ArrayList<LocationUsage> retrieveUserLocationUsage(String date, String macAdd){
            ArrayList<LocationUsage> locList = new ArrayList<>();
           // This method gets a Single user's locationusage
            try{
                //Note the query is already tailored to just check or date
                String sql = "SELECT timestamp, semanticplace, l.locationid \n"
                        + "FROM location l, locationusage lu \n"
                        + "WHERE date(timestamp) = ?\n"
                        + "AND l.locationid = lu.locationid\n"
                        + "AND macaddress = ?\n"
                        + "ORDER BY timestamp;";
                
                Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
                
                ps.setString(1, date);
                ps.setString(2, macAdd);
                
                ResultSet rs = ps.executeQuery();
                
                while(rs.next()){
                    String timestamp = rs.getString(1);
                    String place = rs.getString(2);
                    int locId = rs.getInt(3);

                    //Location id is NOT impt when you have the place already.
                    locList.add(new LocationUsage(timestamp, macAdd, new Location(locId, place)));
                }
                rs.close();
                ps.close();
                conn.close();
                
            }catch (SQLException e) {
                e.printStackTrace();
            }
            return locList;
        }
        
        public ArrayList<LocationUsage> retrievePeopleExceptUserLocationUsage(String date, String macAddress){
            //This is to get EVERYONE's location usage
            ArrayList<LocationUsage> locList = new ArrayList<>();
            try{
                //Note the query is already tailored to just check or date
                String sql = "SELECT timestamp, semanticplace, macaddress, l.locationid \n"
                        + "FROM location l, locationusage lu \n"
                        + "WHERE date(timestamp) = ?\n"
                        + "AND macaddress != ?\n"
                        + "AND l.locationid = lu.locationid\n"
                        + "ORDER BY macaddress, timestamp\n";
                
                Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
                
                ps.setString(1, date);
                //Ensures that the User's macadd doesnt return
                ps.setString(2, macAddress);
                
                ResultSet rs = ps.executeQuery();
                
                while(rs.next()){
                    String timestamp = rs.getString(1);
                    String place = rs.getString(2);
                    String macAdd = rs.getString(3);
                    int locId = rs.getInt(4);
                    
                    //Location id is NOT impt when you have the place already.
                    locList.add(new LocationUsage(timestamp, macAdd, new Location(locId, place)));
                }
                rs.close();
                ps.close();
                conn.close();
                
            }catch (SQLException e) {
                e.printStackTrace();
            }
            return locList;
        }

}
