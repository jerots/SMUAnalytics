package dao;

import com.csvreader.CsvReader;
import entity.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class AppUsageDAO {

    private TreeMap<String, Integer> duplicate;
    private TreeMap<String, AppUsage> appList;

    public AppUsageDAO() {
        appList = new TreeMap<>();
        duplicate = new TreeMap<>();
    }

    public int[] insert(CsvReader reader, TreeMap<Integer, String> errMap, Connection conn, HashMap<String, String> macList, HashMap<Integer, String> appIdList) throws IOException {
        int[] updatedRecords = {};
        try{
            int index = 2;
            String sql = "insert into appusage (timestamp, macaddress, appid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?) ON DUPLICATE KEY UPDATE appid "
                    + " = VALUES(appid);";
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
                if (date == null || !Utility.checkDate(date)) {
                    err = true;

                    errorMsg += ",invalid timestamp";

                }

                //check macAdd
                String macAdd = Utility.parseString(reader.get("mac-address"));
                if (macAdd == null) {
                    errorMsg += ",mac add cannot be blank";
                    err = true;
                } else if (!Utility.checkHexadecimal(macAdd)) {
                    errorMsg += ",invalid mac add";
                    err = true;
                } else if (!macList.containsKey(macAdd)) {
                    errorMsg += ",no matching mac address";
                    err = true;
                }

                //check appid
                int appId = Utility.parseInt(reader.get("app-id"));
                if (appId <= 0) {
                    errorMsg += ",app id cannot be blank";
                    err = true;
                } else if (!appIdList.containsKey(appId)) {
                    errorMsg += ",invalid app";
                    err = true;
                }

                if (!err) {

                    if (duplicate.containsKey(date + macAdd)) {
                        errMap.put(index, "duplicate row " + duplicate.get(date + macAdd));
                    }
                    duplicate.put(date + macAdd, index);
                    //add to list
                    stmt.setString(1, date);
                    stmt.setString(2, macAdd);
                    stmt.setInt(3, appId);
                    stmt.addBatch();
                    //insert into tables
                } else {

                    errMap.put(index, errorMsg.substring(1));
                }
                index++;
            }
            updatedRecords = stmt.executeBatch();
            conn.commit();
            stmt.close();
        }catch(SQLException e){
            
        }
        return updatedRecords;
    }

    public int add(CsvReader reader, TreeMap<Integer, String> errMap, Connection conn) throws IOException, SQLException {
        int updateCounts = 0;
        try {
            int index = 2;
            String sql = "insert into appusage (timestamp, macaddress, appid) values(STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s'),?,?);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            String query = null;
            PreparedStatement pStmt = null;
            ResultSet rs = null;
            reader.readHeaders();
            while (reader.readRecord()) {
                //retrieving per row
                boolean err = false;

                //check timestamp
                String date = Utility.parseString(reader.get("timestamp"));
                if (date == null || !Utility.checkDate(date)) {
                    err = true;
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "invalid timestamp");
                    } else {
                        errMap.put(index, errorMsg + "," + "invalid timestamp");
                    }
                }

                //check macAdd
                String macAdd = Utility.parseString(reader.get("mac-address"));
                if (macAdd == null) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "mac add cannot be blank");
                    } else {
                        errMap.put(index, errorMsg + "," + "mac add cannot be blank");
                    }
                    err = true;
                } else if (macAdd != null && !Utility.checkHexadecimal(macAdd)) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "invalid mac add");
                    } else {
                        errMap.put(index, errorMsg + "," + "invalid mac add");
                    }
                    err = true;
                } else {
                    query = "select macaddress from user where macaddress = ?;";
                    pStmt = conn.prepareStatement(query);
                    pStmt.setString(1, macAdd);

                    rs = pStmt.executeQuery();
                    if (!rs.next()) {
                        String errorMsg = errMap.get(index);
                        if (errorMsg == null) {
                            errMap.put(index, "no matching mac address");
                        } else {
                            errMap.put(index, errorMsg + "," + "no matching mac address");
                        }
                        err = true;
                    }
                    pStmt.close();
                }
                //check appid
                int appId = Utility.parseInt(reader.get("app-id"));
                if (appId <= 0) {
                    String errorMsg = errMap.get(index);
                    if (errorMsg == null) {
                        errMap.put(index, "app id cannot be blank");
                    } else {
                        errMap.put(index, errorMsg + "," + "app id cannot be blank");
                    }
                    err = true;
                } else {
                    query = "select appid from app where appid = ?;";
                    pStmt = conn.prepareStatement(query);
                    pStmt.setInt(1, appId);
                    rs = pStmt.executeQuery();
                    if (!rs.next()) {
                        String errorMsg = errMap.get(index);
                        if (errorMsg == null) {
                            errMap.put(index, "invalid app");
                        } else {
                            errMap.put(index, errorMsg + "," + "invalid app");
                        }
                        err = true;
                    }
                    pStmt.close();
                }

                if (!err) {
                    if (duplicate.containsKey(date + macAdd)) {
                        String errorMsg = errMap.get(index);
                        errMap.put(index, errorMsg + "," + "duplicate row " + duplicate.get(date + macAdd));
                    }
                    duplicate.put(date + macAdd, index);
                    appList.put(date + macAdd, new AppUsage(date, macAdd, appId));
                }
                index++;
            }
            ArrayList<AppUsage> appArray = new ArrayList<AppUsage>(appList.values());
            try {
                for (AppUsage app : appArray) {
                    stmt.setString(1, app.getTimestamp());
                    stmt.setString(2, app.getMacAddress());
                    stmt.setInt(3, app.getAppId());
                    stmt.addBatch();
                }
                //closing
                int[] updatedArr = stmt.executeBatch();
                for (int i : updatedArr) {
                    updateCounts += i;
                }

                conn.commit();

            } catch (BatchUpdateException e) {
                int[] updateArr = e.getUpdateCounts();
                for (int i = 0; i < updateArr.length; i++) {
                    if (updateArr[i] == Statement.EXECUTE_FAILED) {
                        // This method retrieves the row fail, and then searches the prikey corresponding and then uses the duplicate TreeMap to find the offending row.
                        String errorMsg = errMap.get(index);
                        if (errorMsg == null) {
                            errMap.put(index, "duplicate row " + duplicate.get(appArray.get(i).getTimestamp() + appArray.get(i).getMacAddress()));
                        } else {
                            errMap.put(index, errorMsg + "," + "duplicate row " + duplicate.get(appArray.get(i).getTimestamp() + appArray.get(i).getMacAddress()));
                        }
                    }

                    if (updateArr[i] >= 0) {
                        updateCounts += updateArr[i];
                    }
                }
            }

            reader.close();
            stmt.close();

        } catch (NullPointerException e) {
//            e.printStackTrace();
        }
        return updateCounts;
    }

    public ArrayList<User> retrieveUsers(Date startDate, Date endDate) {

        ArrayList<User> result = new ArrayList<User>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("SELECT au.macaddress, name, password, email, gender from appusage au, user u where "
                    + "au.macaddress = u.macaddress "
                    + "AND timestamp >= ? AND timestamp <= ? "
                    + "GROUP BY macaddress");
            ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());

            rs = ps.executeQuery();

            while (rs.next()) {
                String macAdd = rs.getString(1);
                String name = rs.getString(2);
                String password = rs.getString(3);
                String email = rs.getString(4);
                String gender = rs.getString(5);
                String cca = rs.getString(6);
                result.add(new User(macAdd, name, password, email, gender, cca));
            }

        } catch (SQLException e) {
        }finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }

    public ArrayList<String> retrieveUsers(Date startDate, Date endDate, String sql) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        ArrayList<String> result = new ArrayList<String>();

        try {

            conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement(sql);
            ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());

            rs = ps.executeQuery();

            while (rs.next()) {
                String macAdd = rs.getString(1);
                result.add(macAdd);
            }


        } catch (SQLException e) {

        }finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }

    public ArrayList<AppUsage> retrieveByUser(String macAdd, Date startDate, Date endDate) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<AppUsage> result = new ArrayList<AppUsage>();

        try {

            conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("SELECT * from appusage au, app a where "
                    + "timestamp >= ? AND timestamp <= ? "
					+ "AND au.appid = a.appid "
                    + "AND macaddress = ? order by timestamp");
            ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());
            ps.setString(3, macAdd);

            rs = ps.executeQuery();

            while (rs.next()) {

                String timestamp = rs.getString(1);
                String macaddress = rs.getString(2);
                int appid = rs.getInt(3);
				String appName = rs.getString(5);
				String appCat = rs.getString(6);
                result.add(new AppUsage(timestamp, macaddress, appid, new App(appid,appName, appCat)));

            }

        } catch (SQLException e) {
        }finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }

    public ArrayList<AppUsage> retrieveByUserHourly(String macAdd, Date startHour, Date endHour) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<AppUsage> result = new ArrayList<AppUsage>();

        try {
            conn = ConnectionManager.getConnection();

            ps = conn.prepareStatement("SELECT * from appusage where "
                    + "timestamp >= ? AND timestamp < ? "
                    + "AND macaddress = ? "
                    + "ORDER BY timestamp");

            ps.setString(1, new java.sql.Timestamp(startHour.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endHour.getTime()).toString());
            ps.setString(3, macAdd);

            rs = ps.executeQuery();

            while (rs.next()) {

                String timestamp = rs.getString(1);
                String macaddress = rs.getString(2);
                int appid = rs.getInt(3);
                result.add(new AppUsage(timestamp, macaddress, appid));
            }
        } catch (SQLException e) {
        }finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return result;
    }

    public ArrayList<User> retrieveUserByDemo(Date startHour, Date endHour, String[] demoArr) {

        ArrayList<User> result = new ArrayList<User>();
Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

             conn = ConnectionManager.getConnection();

             ps = conn.prepareStatement("SELECT au.macaddress, name, password, email, gender from appusage au, user u \n"
                    + "where au.macaddress = u.macaddress\n"
                    + "AND timestamp >= ? AND timestamp < ?\n"
                    + "AND email like ? \n"
                    + "AND gender like ?\n"
                    + "AND email like ?\n"
                    + "GROUP BY macaddress;");
            ps.setString(1, new java.sql.Timestamp(startHour.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endHour.getTime()).toString());

            String year = demoArr[0];
            String gender = demoArr[1];
            String school = demoArr[2];

            if (year.equals("NA")) {
                ps.setString(3, "%");
            } else {
                ps.setString(3, "%." + year + "@%.smu.edu.sg");
            }

            if (gender.equals("NA")) {
                ps.setString(4, "%");
            } else {
                ps.setString(4, gender.toLowerCase());
            }

            if (school.equals("NA")) {
                ps.setString(5, "%");
            } else {
                ps.setString(5, "%@" + school + ".smu.edu.sg");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                String macAdd = rs.getString(1);
                String name = rs.getString(2);
                String password = rs.getString(3);
                String email = rs.getString(4);
                String genderRes = rs.getString(5);
                String cca = rs.getString(6);
                result.add(new User(macAdd, name, password, email, genderRes, cca));
            }

        } catch (SQLException e) {
        } finally {
			ConnectionManager.close(conn, ps, rs);
		}

        return result;
    }

    public ArrayList<AppUsage> retrieveByAppCat(Date startHour, Date endHour, String macAdd, String appCat) {

        ArrayList<AppUsage> result = new ArrayList<AppUsage>();
Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

             conn = ConnectionManager.getConnection();

             ps = conn.prepareStatement("select * from appusage au, app a\n"
                    + "WHERE au.appid = a.appid\n"
                    + "AND timestamp >= ? AND timestamp <= ? \n"
                    + "AND appcategory = ? \n"
                    + "AND macaddress = ? \n"
                    + "ORDER BY timestamp;");

            ps.setString(1, new java.sql.Timestamp(startHour.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endHour.getTime()).toString());
            ps.setString(3, appCat);
            ps.setString(4, macAdd);

             rs = ps.executeQuery();

            while (rs.next()) {
                String timestamp = rs.getString(1);
                String macAddress = rs.getString(2);
                int appId = rs.getInt(3);
                result.add(new AppUsage(timestamp, macAddress, appId));
            }
            ConnectionManager.close(conn, ps, rs);

        } catch (SQLException e) {
        } finally {
			ConnectionManager.close(conn, ps, rs);
		}
        
        return result;
    }

    public ArrayList<AppUsage> getAppsBySchool(String school, Date startDate, Date endDate) {
        ArrayList<AppUsage> aList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //This has been changed to take into account that the next update is calculated as well.
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement("SELECT timestamp, appname, a.appid, u.macaddress, appcategory\n"
                    + "FROM appusage au, user u, app a\n"
                    + "WHERE timestamp >= ?\n"
                    + "AND timestamp <= ?\n"
                    + "AND au.macaddress = u.macaddress\n"
                    + "AND a.appid = au.appid\n"
                    + "AND u.email LIKE ? \n"
                    + "ORDER BY u.macaddress, timestamp;");

            ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());
            ps.setString(3, "%" + school + "%");

            rs = ps.executeQuery();
            while (rs.next()) {
                String timestamp = rs.getString(1);
                String appName = rs.getString(2);
                int appId = rs.getInt(3);
                String macAdd = rs.getString(4);
                String appCat = rs.getString(5);

                aList.add(new AppUsage(timestamp, macAdd, appId, new App(appId, appName, appCat)));
            }
            ConnectionManager.close(conn, ps);

        } catch (SQLException e) {
        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return aList;
    }

    public ArrayList<AppUsage> getStudentsByCategory(HashMap<String, String> priKMac, Date startDate, Date endDate) {
        ArrayList<AppUsage> aList = new ArrayList<>();

        String sql = "SELECT timestamp, u.name, u.macaddress, a.appid, a.appcategory, appname\n"
                + "FROM appusage au, user u, app a\n"
                + "WHERE timestamp >= ?\n"
                + "AND timestamp <= ?\n"
                + "AND au.macaddress = u.macaddress\n"
                + "AND a.appid = au.appid\n"
                + "ORDER BY u.macaddress, timestamp;";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        //Cannot send in category as must minus from the previous amount.
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());

            rs = ps.executeQuery();

            while (rs.next()) {
                String timeStamp = rs.getString(1);
                String name = rs.getString(2);
                String macAdd = rs.getString(3);
                int appId = rs.getInt(4);
                String category = rs.getString(5);
                String appName = rs.getString(6);

                aList.add(new AppUsage(timeStamp, macAdd, appId, new App(appId, appName, category)));
                priKMac.put(macAdd, name);
            }

        } catch (SQLException e) {

        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return aList;
    }

    public ArrayList<AppUsage> getSchoolsByCategory(HashMap<String, String> priKSch, Date startDate, Date endDate) {
        ArrayList<AppUsage> aList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT timestamp, u.macaddress, a.appid, appcategory, email, appname\n"
                + "FROM appusage au, user u, app a\n"
                + "WHERE au.macaddress = u.macaddress\n"
                + "AND timestamp >= ?\n"
                + "AND timestamp <= ?\n"
                + "AND a.appid = au.appid\n"
                + "ORDER BY CASE\n"
                + "WHEN u.email LIKE '%accountancy%' THEN 1\n"
                + "WHEN u.email LIKE '%economics%' THEN 2\n"
                + "WHEN u.email LIKE '%business%' THEN 3\n"
                + "WHEN u.email LIKE '%sis%' THEN 4\n"
                + "WHEN u.email LIKE '%law%' THEN 5\n"
                + "WHEN u.email LIKE '%socsc%' THEN 6\n"
                + "end,macaddress, timestamp;";

        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, new java.sql.Timestamp(startDate.getTime()).toString());
            ps.setString(2, new java.sql.Timestamp(endDate.getTime()).toString());

            rs = ps.executeQuery();
            //Need to retrieve category as you must take the first time of every new app to be accurate
            while (rs.next()) {
                String timeStamp = rs.getString(1);
                String macAdd = rs.getString(2);
                int appId = rs.getInt(3);
                String category = rs.getString(4);
                String email = rs.getString(5);
                String school = Utility.getSchool(email);
                String appName = rs.getString(6);

                aList.add(new AppUsage(timeStamp, macAdd, appId, new App(appId, appName, category)));
                priKSch.put(macAdd, school);
            }

        } catch (SQLException e) {

        } finally {
            ConnectionManager.close(conn, ps, rs);
        }
        return aList;
    }
}
